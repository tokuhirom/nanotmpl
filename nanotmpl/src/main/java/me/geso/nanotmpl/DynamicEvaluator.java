package me.geso.nanotmpl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import me.geso.nanotmpl.AST.Template;

import com.google.common.collect.Lists;

/**
 * A file object used to represent source coming from a string.
 */
class JavaSourceFromString extends SimpleJavaFileObject {
	/**
	 * The source code of this "file".
	 */
	final String code;

	/**
	 * Constructs a new JavaSourceFromString.
	 * 
	 * @param name
	 *            the name of the compilation unit represented by this file
	 *            object
	 * @param code
	 *            the source code for the compilation unit represented by this
	 *            file object
	 */
	JavaSourceFromString(String name, String code) {
		super(URI.create("string:///" + name.replace('.', '/')
				+ Kind.SOURCE.extension), Kind.SOURCE);
		this.code = code;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return code;
	}
}

/**
 * This is a dynamic evaluator class for nanotmpl. This is only for
 * debugging/testing environment.
 * 
 * This class compiles template file to java code, and compile it by javac, and
 * then evaluate. It's really slow process.
 * 
 * This class requires JDK. You need run this class on JDK.
 * 
 * DynamicEvaluator doesn't support to set non-built-in object to the field,
 * maybe.
 * 
 * @author tokuhirom
 * 
 */
public class DynamicEvaluator {
	private JavaCompiler javaCompiler;
	private static long classId;
	private Path workDir;
	private ClassLoader classLoader;
	private String packageName;

	public DynamicEvaluator(Path workDir, String packageName)
			throws MalformedURLException {
		this.javaCompiler = ToolProvider.getSystemJavaCompiler();

		this.packageName = packageName;
		this.workDir = workDir;
		this.classLoader = this.buildClassLoader();
	}

	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	private ClassLoader buildClassLoader() throws MalformedURLException {
		ClassLoader systemLoader = ClassLoader.getSystemClassLoader();
		URL[] urls = new URL[1];
		urls[0] = workDir.toUri().toURL();

		ClassLoader loader = new URLClassLoader(urls, systemLoader);
		return loader;
	}

	public boolean hasJavaCompiler() {
		return javaCompiler != null;
	}

	// This 'synchronized' affect to performance?
	protected String generateClassName() {
		synchronized (this) {
			return String.format("dynamicTemplate_%d", classId++);
		}
	}

	public String transpileString(String template, String className,
			Class<?> baseClass, Class<?> escapeClass, String fileName) {
		Template klass_obj = (new Parser()).parse(template);
		CodeGenerator codeGenerator = new CodeGenerator();
		return codeGenerator.generate(klass_obj, packageName, className,
				baseClass, escapeClass, fileName);
	}

	public Renderer compileString(String template, String className,
			Class<?> baseClass, Class<?> escapeClass, String fileName)
			throws Exception {
		String src = this.transpileString(template, className, baseClass,
				escapeClass, fileName);
		this.compileJavaCode(src, className);
		return this.loadClass(className);
	}

	protected Renderer loadClass(final String className) throws Exception {
		Class<?> clazz = Class.forName(packageName + "." + className, true,
				this.classLoader);
		if (clazz != null) {
			return new Renderer(clazz);
		} else {
			// TODO: better exception
			throw new Exception("Cannot get class by class loader");
		}
	}

	private void compileJavaCode(String src, String className) throws Exception {
		if (!this.hasJavaCompiler()) {
			// TODO: better exception
			throw new Exception("There is no java compiler");
		}

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager stdFileManager = javaCompiler
				.getStandardFileManager(diagnostics, null, null);
		Boolean ret = javaCompiler.getTask(null, stdFileManager, diagnostics,
				Arrays.asList("-d", workDir.toString()), null,
				Lists.newArrayList(new JavaSourceFromString(className, src)))
				.call();
		for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics
				.getDiagnostics()) {
			System.out.format("Error on line %d in %s%n",
					diagnostic.getLineNumber(), diagnostic.getSource().toUri());
		}

		stdFileManager.close();

		if (!ret) {
			// TODO: better exception
			System.err.println(src);
			throw new Exception("Cannot compile to .class");
		}
	}

	public Renderer compileString(String template, Class<?> baseClass,
			Class<?> escapeClass, String fileName) throws Exception {
		String className = this.generateClassName();
		// String packageName = "me.geso.nanotmpl.dynamicEvaluate";

		String src = this.transpileString(template, className, baseClass,
				escapeClass, fileName);
		this.compileJavaCode(src, className);
		return this.loadClass(className);
	}

	/**
	 * Wrapper class for rendering templates.
	 * 
	 * @author tokuhirom
	 * 
	 */
	public class Renderer {
		protected Class<?> clazz;
		protected Object instance;

		public Renderer(Class<?> clazz) throws InstantiationException,
				IllegalAccessException {
			this.clazz = clazz;
			this.instance = clazz.newInstance();
		}

		public void setAttribute(String name, Object value)
				throws NoSuchFieldException, SecurityException,
				IllegalArgumentException, IllegalAccessException {
			Field field = clazz.getField(name);
			field.set(this.instance, value);
		}

		public EncodedString render() throws NoSuchMethodException,
				SecurityException, IllegalAccessException,
				IllegalArgumentException, InvocationTargetException {
			Method render_ = clazz.getMethod("render");
			return (EncodedString) render_.invoke(this.instance);
		}

		public EncodedString render(List<Object> args)
				throws NoSuchMethodException, SecurityException,
				IllegalAccessException, IllegalArgumentException,
				InvocationTargetException {
			Method m = clazz.getMethod("render", List.class);
			return (EncodedString) m.invoke(this.instance, args);
		}

		public void setTemplateEngine(TemplateEngine engine)
				throws NoSuchMethodException, SecurityException,
				IllegalAccessException, IllegalArgumentException,
				InvocationTargetException {
			Method m = clazz.getDeclaredMethod("setTemplateEngine",
					TemplateEngine.class);
			m.invoke(this.instance, engine);
		}
	}

	public String getPackageName() {
		return this.packageName;
	}
}

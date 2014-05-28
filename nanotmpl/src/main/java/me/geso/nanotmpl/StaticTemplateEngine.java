package me.geso.nanotmpl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.reflect.ClassPath;

public class StaticTemplateEngine implements TemplateEngine {
	private ClassLoader classLoader;
	private String packageRoot;
	// filename -> class
	private Map<String, Class<?>> classMap;

	public StaticTemplateEngine(String packageRoot, ClassLoader classLoader)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException {
		this.classLoader = classLoader;
		this.packageRoot = packageRoot;
		this.classMap = this.aggregateClasses();
	}

	private Map<String, Class<?>> aggregateClasses() throws IOException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		// scans the class path used by class loader
		ClassPath classpath = ClassPath.from(classLoader);
		Map<String, Class<?>> cm = new HashMap<>();
		for (ClassPath.ClassInfo classInfo : classpath
				.getTopLevelClassesRecursive(this.packageRoot)) {
			Class<?> clazz = classInfo.load();
			Method m = clazz.getMethod("getFileName");
			String fileName = (String) m.invoke(null);
			cm.put(fileName, clazz);
		}
		return cm;
	}

	public EncodedString render(String filename, Object... args)
			throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Class<?> clazz = this.classMap.get(filename);
		if (clazz == null) {
			System.out.println(this.classMap);
			throw new RuntimeException("Unknown template: " + filename);
		}

		Object obj = clazz.newInstance();
		
		// call setManager(Manager manager)
		{
			Method m = clazz.getDeclaredMethod("setTemplateEngine", TemplateEngine.class);
			m.invoke(obj, this);
		}

		// call render()
		Method m = clazz.getMethod("render", List.class);
		return (EncodedString) m.invoke(obj, Arrays.asList(args));
	}
}

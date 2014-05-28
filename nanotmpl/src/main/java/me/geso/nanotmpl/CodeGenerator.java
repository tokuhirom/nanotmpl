package me.geso.nanotmpl;

import java.util.List;

import com.google.common.base.CaseFormat;

import me.geso.nanotmpl.AST.Attribute;
import me.geso.nanotmpl.AST.Node;
import me.geso.nanotmpl.AST.Template;

import org.apache.commons.lang3.StringEscapeUtils;

public class CodeGenerator {
	public String generate(Template klass_obj, String packageName,
			String className, Class<?> baseClass, Class<?> escapeClass,
			String sourceFileName) {
		String body = generateBody(klass_obj);

		StringBuilder ret = new StringBuilder();

		// Java doesn't support `#line`...
		// Then, we should switch to ASM.
		ret.append("package " + packageName + ";\n");
		ret.append("import java.util.List;\n");
		ret.append("import static " + escapeClass.getName() + ".escape;\n");
		ret.append("import me.geso.nanotmpl.EncodedString;\n");
		ret.append("import me.geso.nanotmpl.TemplateEngine;\n");
		ret.append("\n");
		for (String it : klass_obj.getImports()) {
			ret.append("import " + it + ";");
		}
		ret.append("\n");
		ret.append("@SuppressWarnings(\"unused\")\n");
		ret.append("public class " + className + " extends "
				+ baseClass.getName() + " {\n");

		ret.append("    private TemplateEngine _NT;\n");

		// Default constructor
		ret.append(String.format("    public %s() { }\n", className));

		// Constructor with initialization
		if (klass_obj.getAttributes().size() > 0) {
			ret.append(String.format("    public %s(\n", className));
			for (int i = 0; i < klass_obj.getAttributes().size(); i++) {
				final Attribute e = klass_obj.getAttributes().get(i);
				ret.append(String.format("      %s %s\n", e.getType(),
						e.getName()));
				if (i < klass_obj.getAttributes().size() - 1) {
					ret.append(",\n");
				}
			}
			ret.append("    ) {\n");
			for (Attribute e : klass_obj.getAttributes()) {
				ret.append(String.format("      this.%s=%s;\n", e.getName(),
						e.getName()));
			}
			ret.append("    }\n");
		}

		// setter and attributes.
		for (Attribute e : klass_obj.getAttributes()) {
			ret.append(String.format("  public %s %s;\n", e.getType(),
					e.getName()));
			ret.append(String.format(
					"  public void set%s(%s value) { this.%s=value; }",
					CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,
							e.getName()), e.getType(), e.getName()));
		}
		ret.append("\n");

		// File name getter.
		ret.append("  public static String getFileName() { return \""
				+ StringEscapeUtils.escapeJava(sourceFileName) + "\"; }");
		ret.append("\n");

		// Render templates.
		ret.append("  public EncodedString render()  throws Exception{ \n");
		ret.append("    StringBuilder _MT = new StringBuilder();\n");
		ret.append("      " + body + "\n");
		ret.append("    return new EncodedString(_MT.toString());\n");
		ret.append("  }\n");

		// public String render(List<Object> args);
		ret.append("  public EncodedString render(List<Object> args) throws Exception {\n");
		ret.append("    for (int i=0; i<args.size(); i++) {\n");
		for (Attribute e : klass_obj.getAttributes()) {
			ret.append(String.format("      this.%s = (%s)args.get(i);\n",
					e.getName(), e.getType()));
		}
		ret.append("    }\n");
		ret.append("    return this.render();\n");
		ret.append("  }\n");

		// Better exception throws...
		ret.append("  public EncodedString include(String file, Object... args) throws Exception {\n");
		ret.append("    return this._NT.render(file, args);\n");
		ret.append("  }\n");

		// public void setTemplateEnginer(TemplateEngine engine);
		ret.append("  public void setTemplateEngine(TemplateEngine engine) {\n");
		ret.append("    this._NT = engine;\n");
		ret.append("  }\n");

		ret.append("}\n");
		return ret.toString();
	}

	private String generateBody(Template klass) {
		StringBuilder buffer = new StringBuilder();
		// boolean last_was_code = false;
		for (List<Node> line : klass.getTree()) {
			// New line
			for (Node node : line) {
				switch (node.getState()) {
				case TEXT:
					String txt = node.getBody();
					buffer.append("_MT.append(\""
							+ StringEscapeUtils.escapeJava(txt) + "\");");
					break;
				case EXPR:
					String expr = node.getBody();
					buffer.append("_MT.append(escape(" + expr + "));");
					break;
				case CODE:
					buffer.append(node.getBody() + ";");
					// last_was_code = true;
					break;
				default:
					throw new RuntimeException("oops");
				}
			}
		}
		return buffer.toString();
	}
}

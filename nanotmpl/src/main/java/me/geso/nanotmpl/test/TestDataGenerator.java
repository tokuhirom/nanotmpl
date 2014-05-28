package me.geso.nanotmpl.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import me.geso.nanotmpl.CodeGenerator;
import me.geso.nanotmpl.Parser;
import me.geso.nanotmpl.AST.Template;
import me.geso.nanotmpl.escape.HTMLEscape;

public class TestDataGenerator {

	private static void render(String fileName) throws IOException {
		String base = (new File(fileName)).getName().replaceAll("\\.nt\\z", "");
		String dstFileName = String.format("src/test/java/com/example/view/"
				+ base + ".java");
		String packageName = "com.example.view";
		Class<?> escapeClass = HTMLEscape.class;
		String className = base;
		String src = new String(Files.readAllBytes(Paths.get(fileName)));
		Template klass_obj = (new Parser()).parse(src);
		CodeGenerator codeGenerator = new CodeGenerator();
		String compiled = codeGenerator.generate(klass_obj, packageName,
				className, Object.class, escapeClass, fileName);

		Files.write(Paths.get(dstFileName), compiled.getBytes());
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		for (String fileName : (new File("eg")).list()) {
			System.out.println("========> " + fileName + " <======");
			render("eg/" + fileName);
		}

	}

}

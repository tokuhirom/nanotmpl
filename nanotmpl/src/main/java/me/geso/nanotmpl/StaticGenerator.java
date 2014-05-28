package me.geso.nanotmpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import me.geso.nanotmpl.AST.Template;

public class StaticGenerator {
	public StaticGenerator() {
	}

	public static void main(final String[] args) throws IOException,
			ClassNotFoundException {
		String fileName = args[0];
		String packageName = args[1];
		String className = args[2];
		String escape = args[3];
		String dstfname = args[4];

		Class<?> escapeClass = Class
				.forName("me.geso.nanotmpl.escape." + escape);
		String src = new String(Files.readAllBytes(Paths.get(fileName)));
		Template klass_obj = (new Parser()).parse(src);
		CodeGenerator codeGenerator = new CodeGenerator();
		String compiled = codeGenerator.generate(klass_obj, packageName,
				className, Object.class, escapeClass, fileName);

		Files.write(Paths.get(dstfname), compiled.getBytes());
	}
}

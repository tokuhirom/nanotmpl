package me.geso.nanotmpl.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

import me.geso.nanotmpl.DynamicEvaluator;
import me.geso.nanotmpl.StaticTemplateEngine;
import me.geso.nanotmpl.escape.HTMLEscape;

import org.junit.Test;

public class StaticLoaderTest {
	private DynamicEvaluator eval;

	public StaticLoaderTest() throws Exception {
		Path workDir = Files.createTempDirectory("nanotmpl");
		String packageName = "me.geso.nanotmpl.dynamicEvaluate";
		this.eval = new DynamicEvaluator(workDir, packageName);
	}

	@Test
	public void test() throws Exception {
		/*
		 * String code = eval.transpileString("hogehoge", "fugafuga",
		 * Object.class, HTMLEscape.class, "hoge.nt");
		 */
		eval.compileString("hogehoge", Object.class, HTMLEscape.class,
				"hoge.nt");
		StaticTemplateEngine manager = buildManager();
		String got = manager.render("hoge.nt").toString();
		assertEquals("hogehoge", got);
	}

	@Test
	public void testInclude() throws Exception {
		eval.compileString("?= include(\"fuga.nt\")", Object.class,
				HTMLEscape.class, "root.nt");
		eval.compileString("fugafuga", Object.class, HTMLEscape.class,
				"fuga.nt");
		StaticTemplateEngine manager = buildManager();
		String got = manager.render("root.nt").toString();
		assertEquals("fugafuga", got);
	}

	@Test
	public void testFor() throws Exception {
		eval.compileString("? for (int i=0; i<3; i++) {\n" + "?= i\n" + "? }",
				Object.class, HTMLEscape.class, "root.nt");
		StaticTemplateEngine manager = buildManager();
		String got = manager.render("root.nt").toString();
		assertEquals("0\n1\n2\n", got);
	}

	@Test
	public void testLineEnd() throws Exception {
		eval.compileString("?= 5963", Object.class, HTMLEscape.class,
				"no-nl.nt");
		eval.compileString("?= 4649\n", Object.class, HTMLEscape.class, "nl.nt");

		StaticTemplateEngine manager = buildManager();

		assertEquals("5963", manager.render("no-nl.nt").toString());
		assertEquals("4649\n", manager.render("nl.nt").toString());
	}

	private StaticTemplateEngine buildManager() throws NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {
		return new StaticTemplateEngine(eval.getPackageName(),
				eval.getClassLoader());
	}
}

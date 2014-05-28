package me.geso.nanotmpl.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import me.geso.nanotmpl.DynamicEvaluator;
import me.geso.nanotmpl.DynamicEvaluator.Renderer;
import me.geso.nanotmpl.escape.HTMLEscape;
import me.geso.nanotmpl.escape.NoEscape;

import org.junit.Test;

public class DynamicTest {
	protected DynamicEvaluator evaluator;

	public DynamicTest() throws IOException {
		Path workDir = Files.createTempDirectory("nanotmpl");
		String packageName = "me.geso.nanotmpl.dynamicEvaluate";
		this.evaluator = new DynamicEvaluator(workDir, packageName);
	}

	@Test
	public void testHelloDynamic() throws Exception {
		Renderer renderer = evaluator.compileString("Hello", Object.class,
				HTMLEscape.class, "-");
		assertEquals("Hello", renderer.render().toString());
	}

	@Test
	public void testDynamicAttr() throws Exception {
		Renderer renderer = evaluator.compileString("@ String name\n"
				+ "Hello, <?= name ?>", Object.class, HTMLEscape.class, "-");
		renderer.setAttribute("name", "John");
		String got = renderer.render().toString();
		assertEquals("Hello, John", got);
	}

	@Test
	public void testDynamicNoEscape() throws Exception {
		Renderer renderer = evaluator.compileString("@ String name\n"
				+ "Hello, <?= name ?>", Object.class, NoEscape.class, "-");
		renderer.setAttribute("name", "John<B>");
		String got = renderer.render().toString();
		assertEquals("Hello, John<B>", got);
	}

	@Test
	public void testLineWithoutEcho() throws Exception {
		Renderer renderer = evaluator.compileString("@ String name\n"
				+ "? name=\"John\";\n" + "Hoh, <?= name ?>", Object.class,
				NoEscape.class, "-");
		String got = renderer.render().toString();
		assertEquals("Hoh, John", got);
	}

	// / ?=
	@Test
	public void testEscape() throws Exception {
		Renderer renderer = evaluator.compileString("@ String name\n"
				+ "?= name\n", Object.class, NoEscape.class, "-");
		renderer.setAttribute("name", "John");
		String got = renderer.render().toString();
		assertEquals("John\n", got);
	}
}

package me.geso.nanotmpl.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.List;

import me.geso.nanotmpl.Parser;
import me.geso.nanotmpl.AST.Node;
import me.geso.nanotmpl.AST.State;
import me.geso.nanotmpl.AST.Template;

import org.junit.Test;

public class ParserTest {

	@Test
	public final void test() throws IOException {
		Parser parser = new Parser();

		// Basic string
		{
			Template klass = parser.parse("heh");
			List<List<Node>> tree = klass.getTree();
			assertEquals(1, tree.size());
			assertEquals(1, tree.get(0).size());
			assertEquals(State.TEXT, tree.get(0).get(0).getState());
			assertEquals("heh", tree.get(0).get(0).getBody());
		}

		// Attributes
		{
			Template klass = parser.parse("@ String name");
			assertEquals(1, klass.getAttributes().size());
			assertEquals("name", klass.getAttributes().get(0).getName());
			assertEquals("String", klass.getAttributes().get(0).getType());
		}

		// eg/hello.nt
		{
			Template klass = parser.parse("hogehoge\n<?= 3+2 ?>");
			assertEquals(0, klass.getAttributes().size());
			assertEquals(2, klass.getTree().size());
			assertEquals(State.TEXT, klass.getTree().get(0).get(0)
					.getState());
			assertEquals(1, klass.getTree().get(1).size());
			assertEquals(State.EXPR, klass.getTree().get(1).get(0)
					.getState());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSplitLines() throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		Method method = Parser.class.getDeclaredMethod("splitLines",
				String.class);
		method.setAccessible(true);

		Parser parser = new Parser();
		assertEquals(3,
				((Deque<String>) method.invoke(parser, "hoge\nfuga")).size());
		assertEquals(4,
				((Deque<String>) method.invoke(parser, "hoge\nfuga\n"))
						.size());
	}

}

package me.geso.nanotmpl.test;

import static org.junit.Assert.assertEquals;
import me.geso.nanotmpl.EncodedString;

import org.junit.Test;

import com.example.view.escape;
import com.example.view.escape2;
import com.example.view.hello;
import com.example.view.person;
import com.example.view.variables;

/**
 * Test cases for static compile templates.
 * @author tokuhirom
 *
 */
public class ExampleTest {
	
	@Test
	public void testPerson() throws Exception {
		person view = new person();
		view.person = new Person("John", 43);
		assertEquals("Greeting: Hi I&#39;m John. I&#39;m 43.\n", view.render().toString());
	}
	
	@Test
	public void testPersonWithConstructor() throws Exception {
		person view = new person(new Person("John", 43));
		assertEquals("Greeting: Hi I&#39;m John. I&#39;m 43.\n", view.render().toString());
	}

	@Test
	public void testHello() throws Exception {
		hello view = new hello();
		assertEquals("hogehoge\n5", view.render().toString());
	}

	@Test
	public void testVariables() throws Exception {
		variables view = new com.example.view.variables();
		view.name = "John";
		assertEquals("hello, John\n", view.render().toString());
	}

	/**
	 * Escaped string won't escape.
	 * @throws Exception 
	 */
	@Test
	public void testEncodedString() throws Exception {
		escape2 view = new com.example.view.escape2();
		view.y = new EncodedString("<B>John</B>");
		assertEquals("Oh my <B>John</B>\n", view.render().toString());
	}

	/**
	 * HTMLEscape context will escape HTML automatically.
	 * @throws Exception 
	 */
	@Test
	public void testString() throws Exception {
		escape view = new com.example.view.escape();
		view.x = "<B>John</B>";
		assertEquals("&lt;B&gt;John&lt;/B&gt;\n", view.render().toString());
	}

}

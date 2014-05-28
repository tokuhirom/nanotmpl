package me.geso.nanotmpl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.geso.nanotmpl.AST.Node;
import me.geso.nanotmpl.AST.State;
import me.geso.nanotmpl.AST.Template;

public class Parser {

	private Deque<String> splitLines(String src) {
		Deque<String> lines = new ArrayDeque<String>();
		String[] splitted = src.split("\n");
		for (int i = 0; i < splitted.length; i++) {
			lines.add(splitted[i]);
			if ((i != splitted.length - 1)
					|| (src.charAt(src.length() - 1) == '\n')) {
				lines.add("\n");
			}
		}
		return lines;
	}

	public Template parse(String src) {
		Deque<String> lines = splitLines(src);
		Template klass = new Template();

		boolean multiline_expression = false;
		while (lines.size() > 0) {
			String line = lines.removeFirst();
			boolean newline = false;
			if (lines.size() > 0) {
				lines.removeFirst();
				newline = true;
			}

			{
				// `?= include("include/header.nt")`
				Pattern p = Pattern.compile("\\A\\?=\\s*(.*)\\z");
				Matcher m = p.matcher(line);
				if (m.find()) {
					List<Node> list = new ArrayList<>();
					list.add(new Node(State.EXPR, m.group(1)));
					if (newline) {
						list.add(new Node(State.TEXT, "\n"));
					}
					klass.getTree().add(list);
					multiline_expression = false;
					continue;
				}
			}

			{
				// `? imports me.geso.nanotmpl.test.Person`
				Pattern p = Pattern.compile("\\A\\?\\s+import\\s+(.*)\\z");
				Matcher m = p.matcher(line);
				if (m.find()) {
					klass.addImports(m.group(1));
					multiline_expression = false;
					continue;
				}
			}

			{
				// `? System.out.println("Hello")`
				Pattern p = Pattern.compile("\\A\\?\\s+(.*)\\z");
				Matcher m = p.matcher(line);
				if (m.find()) {
					List<Node> list = new ArrayList<Node>();
					list.add(new Node(State.CODE, m.group(1)));
					klass.getTree().add(list);
					multiline_expression = false;
					continue;
				}
			}

			{
				// `@ String name`
				Pattern p = Pattern.compile("\\A\\@\\s*(\\S+)\\s+(\\S+)");
				Matcher m = p.matcher(line);
				if (m.find()) {
					klass.getTree().add(new ArrayList<Node>());
					klass.addAttribute(m.group(1), m.group(2));
					continue;
				}
			}

			// TODO: escaped line ending
			{
				// Escaped line ending?
				Pattern p = Pattern.compile("(\\+)\\z");
				Matcher m = p.matcher(line);
				if (m.find()) {
					int length = m.group(1).length();
					if (length == 1) {
						// new line escaped.
						line.replaceAll("\\\\z", "");
					} else if (length >= 2) {
						// Backslash escaped
						line.replaceAll("\\\\\\z", "\\");
						line += "\n";
					}
				} else if (newline) {
					line += "\n";
				}
			}

			// ...
			// Mixed line
			// Instances of this (Pattern) class are immutable and are safe for
			// use by multiple concurrent threads. Instances of the Matcher
			// class are not safe for such use.
			// http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
			String re = String.format("(?:%s|%s|%s|%s)", Pattern.quote("<?="),
					Pattern.quote("<?#"), Pattern.quote("<?"),
					Pattern.quote("?>"));
			Pattern pat = Pattern.compile(String.format(
					"(%s|(?!%s)[^<\\?>]+|(?!%s).)", re, re, re));
			// "(%s|(?!%s)[^<\\?>]+|(?!%s).)", re, re, re));
			State state = State.TEXT;
			List<Node> tokens = new ArrayList<Node>();
			Matcher m = pat.matcher(line);
			while (m.find()) {
				for (int i = 0; i < m.groupCount(); i++) {
					String it = m.group(i);
					// Garbage
					if (it.equals("")) {
						continue;
					}

					if (it.equals("?>")) {
						state = State.TEXT;
						multiline_expression = false;
					} else if (it.equals("<?")) {
						state = State.CODE;
					} else if (it.equals("<?#")) {
						state = State.CMNT;
					} else if (it.equals("<?=")) {
						state = State.EXPR;
					} else {
						// value
						if (state == State.CMNT) {
							continue;
						}

						if (multiline_expression) {
							state = State.CODE;
						}
						if (state == State.EXPR) {
							multiline_expression = true;
						}

						// Store value
						tokens.add(new Node(state, it));
					}
				}
			}
			klass.getTree().add(tokens);
		}
		return klass;
	}
}

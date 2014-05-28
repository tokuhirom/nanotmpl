package me.geso.nanotmpl.AST;

import java.util.ArrayList;
import java.util.List;

public class Template {
	private List<List<Node>> tree;
	private List<Attribute> attributes;
	private List<String> imports;

	public Template() {
		this.tree = new ArrayList<List<Node>>();
		this.attributes = new ArrayList<Attribute>();
		this.imports = new ArrayList<String>();
	}

	public void addAttribute(String type, String name) {
		attributes.add(new Attribute(type, name));
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public List<List<Node>> getTree() {
		return this.tree;
	}

	public void addImports(String pkg) {
		this.imports.add(pkg);
	}

	public List<String> getImports() {
		return imports;
	}
}
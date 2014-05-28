package me.geso.nanotmpl.AST;

/**
 * Attribute for the template.
 * 
 * @author tokuhirom
 *
 */
public class Attribute {
	private String type;
	private String name;
	
	
	public Attribute(String type, String name) {
		this.type = type;
		this.setName(name);
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
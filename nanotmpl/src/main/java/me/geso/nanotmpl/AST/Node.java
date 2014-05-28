package me.geso.nanotmpl.AST;



public class Node {
	private State state;
	private String body;

	public Node(State state, String body) {
		this.state = state;
		this.body = body;
	}

	public State getState() {
		return state;
	}

	public String getBody() {
		return body;
	}

	public String toString() {
		return "(" + state + " " + body + ")";
	}
}
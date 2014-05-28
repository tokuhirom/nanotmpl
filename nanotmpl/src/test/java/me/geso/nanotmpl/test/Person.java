package me.geso.nanotmpl.test;

public class Person {
	private String name;
	private int age;
	
	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}
	
	public String greeting() {
		return String.format("Hi I'm %s. I'm %d.", name, age);
	}
}

package com.example.view;
import java.util.List;
import static me.geso.nanotmpl.escape.HTMLEscape.escape;
import me.geso.nanotmpl.EncodedString;
import me.geso.nanotmpl.TemplateEngine;

import me.geso.nanotmpl.test.Person;;
@SuppressWarnings("unused")
public class person extends java.lang.Object {
    private TemplateEngine _NT;
    public person() { }
    public person(
      Person person
    ) {
      this.person=person;
    }
  public Person person;
  public void setPerson(Person value) { this.person=value; }
  public static String getFileName() { return "eg/person.nt"; }
  public EncodedString render()  throws Exception{ 
    StringBuilder _MT = new StringBuilder();
      _MT.append("Greeting: ");_MT.append(escape( person.greeting() ));_MT.append("\n");
    return new EncodedString(_MT.toString());
  }
  public EncodedString render(List<Object> args) throws Exception {
    for (int i=0; i<args.size(); i++) {
      this.person = (Person)args.get(i);
    }
    return this.render();
  }
  public EncodedString include(String file, Object... args) throws Exception {
    return this._NT.render(file, args);
  }
  public void setTemplateEngine(TemplateEngine engine) {
    this._NT = engine;
  }
}

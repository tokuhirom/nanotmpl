package com.example.view;
import java.util.List;
import static me.geso.nanotmpl.escape.HTMLEscape.escape;
import me.geso.nanotmpl.EncodedString;
import me.geso.nanotmpl.TemplateEngine;


@SuppressWarnings("unused")
public class variables extends java.lang.Object {
    private TemplateEngine _NT;
    public variables() { }
    public variables(
      String name
    ) {
      this.name=name;
    }
  public String name;
  public void setName(String value) { this.name=value; }
  public static String getFileName() { return "eg/variables.nt"; }
  public EncodedString render()  throws Exception{ 
    StringBuilder _MT = new StringBuilder();
      _MT.append("hello, ");_MT.append(escape( name ));_MT.append("\n");
    return new EncodedString(_MT.toString());
  }
  public EncodedString render(List<Object> args) throws Exception {
    for (int i=0; i<args.size(); i++) {
      this.name = (String)args.get(i);
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

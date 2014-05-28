package com.example.view;
import java.util.List;
import static me.geso.nanotmpl.escape.HTMLEscape.escape;
import me.geso.nanotmpl.EncodedString;
import me.geso.nanotmpl.TemplateEngine;


@SuppressWarnings("unused")
public class escape2 extends java.lang.Object {
    private TemplateEngine _NT;
    public escape2() { }
    public escape2(
      EncodedString y
    ) {
      this.y=y;
    }
  public EncodedString y;
  public void setY(EncodedString value) { this.y=value; }
  public static String getFileName() { return "eg/escape2.nt"; }
  public EncodedString render()  throws Exception{ 
    StringBuilder _MT = new StringBuilder();
      _MT.append("Oh my ");_MT.append(escape( y ));_MT.append("\n");
    return new EncodedString(_MT.toString());
  }
  public EncodedString render(List<Object> args) throws Exception {
    for (int i=0; i<args.size(); i++) {
      this.y = (EncodedString)args.get(i);
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

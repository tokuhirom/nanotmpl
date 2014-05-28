import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import me.geso.nanotmpl.DynamicTemplateEngine;
import me.geso.nanotmpl.EncodedString;

public class Main {
    public static void main(String[] args) throws Exception {
        DynamicTemplateEngine tmpl = new DynamicTemplateEngine(Paths.get("/tmp/"));
        List<Path> includePath = new ArrayList<Path>();
        includePath.add(Paths.get("eg/"));
        tmpl.setIncludePath(includePath);
        EncodedString got = tmpl.render("hello.nt", "田中");
        System.out.println(got.toString());
    }
}

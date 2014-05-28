nanotmpl - Lightweight template engine for Java
===============================================

## Synopsis

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

## DESCRIPTION

This is a simple template engine ported from Perl5's Text::MicroTemplate.
It's really like JSP. But nanotmpl supports automatic type based escaping.

### intelligent

nanotmpl automatically escapes variables when and only when
necessary.


## TEMPLATE SYNTAX

The template language is Java itself!

    # output the result of expression with automatic escape
    <?= expr ?>             (tag style)
    ?= expr                 (per-line)

    # execute java code (tag style)
    <? foo() ?>
    ? foo()

    # comment (tag style)
    <?# comment ?>
    ?# comment

    # loops
    <ul>
    ? for (Object item: list) {
    <li><?= item ?></li>
    ? }
    </ul>

    # Define variables
    @ String name
    <?= name ?>

## Architecture

nanotmpl converts template files to Java code. And load it by custom class lodaer.
It means, nanotmpl compile your template file to JVM byte code. Then, it's really fast.

## LICENSE

    The MIT License (MIT)
    Copyright © 2014 Tokuhiro Matsuno, http://64p.org/ <tokuhirom@gmail.com>
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the “Software”), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.




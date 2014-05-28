package me.geso.nanotmpl.test;

import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import me.geso.nanotmpl.DynamicTemplateEngine;
import me.geso.nanotmpl.EncodedString;

import org.junit.Test;

import com.google.common.collect.Lists;

public class DynamicTemplateEngineTest {
	private DynamicTemplateEngine loader;

	public DynamicTemplateEngineTest() throws Exception {
		Path workDir = Files.createTempDirectory("nanotmpl");
		this.loader = new DynamicTemplateEngine(workDir);
		this.loader.setIncludePath(Lists.newArrayList(Paths.get("eg/")));
	}

	@Test
	public void test() throws Exception {
		EncodedString got = loader.render("hello.nt");
		assertEquals("hogehoge\n5", got.toString());
	}

	@Test
	public void testInclude() throws Exception {
		EncodedString got = loader.render("child.nt");
		assertEquals("I'm child.\nI'm parent.\n\n", got.toString());
	}

	@Test
	public void testJapanese() throws Exception {
		EncodedString got = loader.render("japanese.nt", "太郎");
		assertEquals("こんにちは太郎さん｡\n", got.toString());
	}
}

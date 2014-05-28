package me.geso.nanotmpl;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import me.geso.nanotmpl.DynamicEvaluator.Renderer;
import me.geso.nanotmpl.escape.HTMLEscape;

public class DynamicTemplateEngine implements TemplateEngine {
	private DynamicEvaluator eval;
	private static AtomicLong cnt = new AtomicLong();
	private CacheMode useCache = CacheMode.CHECK;
	// File name -> Class
	private Map<String, CacheEntry> cache = new HashMap<>();
	private List<Path> includePath;
	private Charset charset = Charset.forName("UTF-8");
	private Class<?> escapeClass = HTMLEscape.class;
	private Class<?> baseClass = Object.class;

	public enum CacheMode {
		NONE, CHECK, NO_CHECK
	};

	private class CacheEntry {
		public CacheEntry(long mtime, Renderer renderer) {
			this.mtime = mtime;
			this.renderer = renderer;
		}

		public long mtime;
		public Renderer renderer;
	}

	public DynamicTemplateEngine(Path workDir) throws MalformedURLException {
		eval = new DynamicEvaluator(workDir,
				"me.geso.nanotmpl.DynamicLoaderANON"
						+ cnt.getAndIncrement());
		includePath = new ArrayList<>();
	}

	public Renderer buildFile(String file) throws Exception {
		// return cached entry
		if (useCache == CacheMode.NO_CHECK) {
			CacheEntry cacheEntry = cache.get(file);
			if (cacheEntry != null) {
				return cacheEntry.renderer;
			}
		}

		// setup (filepath, st)
		File realFile = null;
		long mtime = 0;
		{
			File fileFile = new File(file);
			if (fileFile.isAbsolute()) {
				// absolute path
				realFile = fileFile;
				mtime = fileFile.lastModified();
			} else {
				// relative path, search "includePath"s
				for (Path inc : getIncludePath()) {
					realFile = inc.resolve(file).toFile();
					mtime = realFile.lastModified();
					if (mtime != 0) {
						break; // file exists.
					}
				}
			}
		}

		if (mtime == 0) {
			throw new RuntimeException(String.format(
					"could not find template file: %s (include_path: %s)",
					file, getIncludePath().toString()));
		}

		// return cached entry after comparing mtime
		{
			CacheEntry e = cache.get(realFile.getAbsolutePath());
			if (e != null && e.mtime == mtime) {
				return e.renderer;
			}
		}

		// read the file, parse, build, cache the entry if necessary, and return
		String template = new String(Files.readAllBytes(realFile.toPath()),
				charset);

		Renderer renderer = eval.compileString(template, baseClass,
				escapeClass, realFile.getAbsolutePath());
		renderer.setTemplateEngine(this);

		if (useCache == CacheMode.CHECK || useCache == CacheMode.NO_CHECK) {
			cache.put(file, new CacheEntry(mtime, renderer));
		}
	
		return renderer;
	}

	public EncodedString render(String filename, Object... args) throws Exception {
		Renderer renderer = this.buildFile(filename);
		return renderer.render(Arrays.asList(args));
	}

	public CacheMode getUseCache() {
		return useCache;
	}

	public void setUseCache(CacheMode useCache) {
		this.useCache = useCache;
	}

	public List<Path> getIncludePath() {
		return includePath;
	}

	public void setIncludePath(List<Path> includePath) {
		this.includePath = includePath;
	}
}

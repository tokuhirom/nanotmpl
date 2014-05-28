package me.geso.nanotmpl;

import java.lang.reflect.InvocationTargetException;

public interface TemplateEngine {

	public EncodedString render(String filename, Object... args)
			throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, Exception;
}

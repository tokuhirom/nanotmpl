package me.geso.nanotmpl.escape;

import me.geso.nanotmpl.EncodedString;

/**
 * nanotmpl evaluation context without escape.
 * It's useful for rendering text.
 * 
 * @author tokuhirom
 *
 */
public class NoEscape {

	static public String escape(final String value) {
		return value;
	}

	static public String escape(final EncodedString value) {
		return value.toString();
	}

	static public String escape(int value) {
		return ""+value;
	}

}

package me.geso.nanotmpl.escape;

import me.geso.nanotmpl.EncodedString;

import com.google.common.escape.CharEscaper;
import com.google.common.escape.Escapers;

public class HTMLEscape {
	private static final CharEscaper HTML_CONTENT_ESCAPER = (CharEscaper) Escapers
			.builder().addEscape('"', "&quot;")
			// Note: "&apos;" is not defined in HTML 4.01.
			.addEscape('\'', "&#39;").addEscape('&', "&amp;")
			.addEscape('<', "&lt;").addEscape('>', "&gt;").build();

	static public String escape(String str) {
		if (str == null) {
			return "(null)";
		} else {
			return HTML_CONTENT_ESCAPER.escape(str);
		}
	}

	static public String escape(EncodedString escaped) {
		return escaped.toString();
	}

	static public String escape(int i) {
		return "" + i;
	}
}

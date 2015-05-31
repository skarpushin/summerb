package org.summerb.springvmc;

public class Views {
	public static final String ERROR_UNEXPECTED = "common/errorUnexpected";
	public static final String ERROR_UNEXPECTED_CLARIFIED = "common/errorUnexpectedClarified";

	public static String redirect(String view) {
		return "redirect:/" + view;
	}

	public static String forward(String view) {
		return "forward:" + view;
	}
}

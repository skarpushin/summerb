package org.summerb.approaches.springmvc;

public class Views {
	public static final String ERROR_UNEXPECTED = "common/errorUnexpected";
	public static final String ERROR_UNEXPECTED_CLARIFIED = "common/errorUnexpectedClarified";
	public static final String ERROR_404 = "common/error404";

	public static String redirect(String view) {
		return "redirect:/" + view;
	}

	public static String forward(String view) {
		return "forward:" + view;
	}
}

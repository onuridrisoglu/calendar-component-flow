package org.vaadin.calendar;

public enum CalendarItemTheme {
	Blue("primary"), Green("primary success"), Red("primary error"), Black("primary contrast"), LightBlue(
			""), LightGreen("success"), LightRed("error"), Gray("contrast");

	private String themeName;

	private CalendarItemTheme(String name) {
		themeName = name;
	}

	public String getThemeName() {
		return themeName;
	}

}

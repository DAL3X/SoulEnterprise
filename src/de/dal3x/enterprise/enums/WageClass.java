package de.dal3x.enterprise.enums;

public enum WageClass {

	HIGH, MEDIUM, LOW;

	public static String translate(String wage) {
		if (wage.equalsIgnoreCase("Hoch")) {
			return "HIGH";
		}
		if (wage.equalsIgnoreCase("Mittel")) {
			return "MEDIUM";
		}
		if (wage.equalsIgnoreCase("Niedrig")) {
			return "LOW";
		}
		if (wage.equalsIgnoreCase("HIGH")) {
			return "Hoch";
		}
		if (wage.equalsIgnoreCase("MEDIUM")) {
			return "Mittel";
		}
		if (wage.equalsIgnoreCase("LOW")) {
			return "Niedrig";
		}
		return "";
	}
	
	public static boolean isWageClass(String wage) {
		for(WageClass wageClass : values()) {
			if(wageClass.toString().equalsIgnoreCase(wage)) {
				return true;
			}
		}
		return false;
	}
	
	public int getWorth() {
		if(this == WageClass.HIGH) {
			return 3;
		}
		if(this == WageClass.MEDIUM) {
			return 2;
		}
		if(this == WageClass.LOW) {
			return 1;
		}
		return 0;
	}
}

package de.dal3x.enterprise.enums;

public enum ProductionBranch {
	Blocks, Materials, Agriculture, Hunting;

	public String toString() {
		if (this == Blocks) {
			return "Blocks";
		}
		if (this == Materials) {
			return "Materials";
		}
		if (this == Agriculture) {
			return "Agriculture";
		} else {
			return "Hunting";
		}
	}
	
	public static boolean exists(String name) {
		if (name.equalsIgnoreCase("Blocks")) {
			return true;
		}
		if (name.equalsIgnoreCase("Materials")) {
			return true;
		}
		if (name.equalsIgnoreCase("Agriculture")) {
			return true;
		}
		if (name.equalsIgnoreCase("Hunting")) {
			return true;
		}
		return false;
	}

	public static String translate(String branch) {
		if (branch.equalsIgnoreCase("Blöcke")) {
			return "Blocks";
		}
		if (branch.equalsIgnoreCase("Materialien")) {
			return "Materials";
		}
		if (branch.equalsIgnoreCase("Landwirtschaft")) {
			return "Agriculture";
		}
		if (branch.equalsIgnoreCase("Jagen")) {
			return "Hunting";
		}
		if (branch.equalsIgnoreCase("Blocks")) {
			return "Blöcke";
		}
		if (branch.equalsIgnoreCase("Materials")) {
			return "Materialien";
		}
		if (branch.equalsIgnoreCase("Agriculture")) {
			return "Landwirtschaft";
		}
		if (branch.equalsIgnoreCase("Hunting")) {
			return "Jagen";
		}
		return "";
	}
}

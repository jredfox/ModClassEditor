package jredfox.mce.cfg;

public enum Opperation {
	BEFORE,
	AFTER;
	
	public static Opperation get(String v){
		return v.equals("before") ? BEFORE : AFTER;
	}
}

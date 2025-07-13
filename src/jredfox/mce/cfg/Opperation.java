package jredfox.mce.cfg;

public enum Opperation {
	BEFORE(false),
	AFTER(true),
	INSERT(true);
	
	private boolean after;
	private Opperation(boolean a)
	{
		this.after = a;
	}
	
	public boolean isAfter()
	{
		return this.after;
	}
	
	public static Opperation get(String v){
		return v.equals("before") ? BEFORE : AFTER;
	}
}

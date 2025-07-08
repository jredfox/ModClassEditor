package jredfox.mce.cfg;

public enum ShiftTo
{
	LINE,
	LABEL,
	EXACT;
	
	public static ShiftTo get(String v)
	{
		v = v.trim().toUpperCase();
		
		if(v.startsWith("LABEL"))
			return LABEL;
		else if(v.startsWith("EXACT") || v.startsWith("INSN"))
			return EXACT;
		
		return LINE;
	}
}
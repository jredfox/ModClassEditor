package jredfox.mce;

import org.objectweb.asm.Opcodes;

public enum DataType {
	
	CHAR("C", "", "", Opcodes.T_CHAR, Opcodes.CASTORE, false, 		"([CIC)V", "([CCIII)V", "([C[CI)V"),
	BOOLEAN("Z", "", "", Opcodes.T_BOOLEAN, Opcodes.BASTORE, false, "([ZIZ)V", "([ZZII)V", "([Z[ZI)V"),
	BYTE("B", "", "", Opcodes.T_BYTE, Opcodes.BASTORE, false, 		"([BIB)V", "([BBIII)V", "([B[BI)V"),
	SHORT("S", "", "", Opcodes.T_SHORT, Opcodes.SASTORE, false, 	"([SIS)V", "([SSIII)V", "([S[SI)V"),
	INT("I", "", "", Opcodes.T_INT, Opcodes.IASTORE, false,			"([III)V", "([IIIII)V", "([I[II)V"),
	LONG("J", "", "", Opcodes.T_LONG, Opcodes.LASTORE, false, 		"([JIJ)V", "([JJIII)V", "([J[JI)V"),
	FLOAT("F", "", "", Opcodes.T_FLOAT, Opcodes.FASTORE, false, 	"([FIF)V", "([FFIII)V", "([F[FI)V"),
	DOUBLE("D", "", "", Opcodes.T_DOUBLE, Opcodes.DASTORE, false, 	"([DID)V", "([DDIII)V", "([D[DI)V"),
	STRING("Ljava/lang/String;", "java/lang/String", "", -1, Opcodes.AASTORE, true, 									"([Ljava/lang/String;ILjava/lang/String;)V", "([Ljava/lang/String;Ljava/lang/String;II)V", "([Ljava/lang/String;[Ljava/lang/String;I)V"),
	WRAPPED_CHAR("Ljava/lang/Character;", "java/lang/Character", "(C)Ljava/lang/Character;", -1, Opcodes.AASTORE, true, "([Ljava/lang/Character;IC)V", "([Ljava/lang/Character;CIII)V", "([Ljava/lang/Character;[CI)V", "([Ljava/lang/Character;ILjava/lang/Character;)V", "", ""),
	WRAPPED_BOOLEAN("Ljava/lang/Boolean;", "java/lang/Boolean", "(Z)Ljava/lang/Boolean;", -1, Opcodes.AASTORE, true, 	"([Ljava/lang/Boolean;IZ)V", "([Ljava/lang/Boolean;ZII)V", "([Ljava/lang/Boolean;[ZI)V", "([Ljava/lang/Boolean;ILjava/lang/Boolean;)V", "", ""),
	WRAPPED_BYTE("Ljava/lang/Byte;", "java/lang/Byte", "(B)Ljava/lang/Byte;", -1, Opcodes.AASTORE, true, 				"([Ljava/lang/Byte;IB)V", "([Ljava/lang/Byte;BIII)V", "([Ljava/lang/Byte;[BI)V", "([Ljava/lang/Byte;ILjava/lang/Byte;)V", "", ""),
	WRAPPED_SHORT("Ljava/lang/Short;", "java/lang/Short", "(S)Ljava/lang/Short;", -1, Opcodes.AASTORE, true, 			"([Ljava/lang/Short;IS)V", "([Ljava/lang/Short;SIII)V", "([Ljava/lang/Short;[SI)V", "([Ljava/lang/Short;ILjava/lang/Short;)V", "", ""),
	WRAPPED_INT("Ljava/lang/Integer;", "java/lang/Integer", "(I)Ljava/lang/Integer;", -1, Opcodes.AASTORE, true, 		"([Ljava/lang/Integer;II)V", "([Ljava/lang/Integer;IIII)V", "([Ljava/lang/Integer;[II)V", "([Ljava/lang/Integer;ILjava/lang/Integer;)V", "", ""),
	WRAPPED_LONG("Ljava/lang/Long;", "java/lang/Long", "(J)Ljava/lang/Long;", -1, Opcodes.AASTORE, true, 				"([Ljava/lang/Long;IJ)V", "([Ljava/lang/Long;JIII)V", "([Ljava/lang/Long;[JI)V", "([Ljava/lang/Long;ILjava/lang/Long;)V", "", ""),
	WRAPPED_FLOAT("Ljava/lang/Float;", "java/lang/Float", "(F)Ljava/lang/Float;", -1, Opcodes.AASTORE, true, 			"([Ljava/lang/Float;IF)V", "([Ljava/lang/Float;FIII)V", "([Ljava/lang/Float;[FI)V", "([Ljava/lang/Float;ILjava/lang/Float;)V", "", ""),
	WRAPPED_DOUBLE("Ljava/lang/Double;", "java/lang/Double", "(D)Ljava/lang/Double;", -1, Opcodes.AASTORE, true, 		"([Ljava/lang/Double;ID)V", "([Ljava/lang/Double;DIII)V", "([Ljava/lang/Double;[DI)V", "([Ljava/lang/Double;ILjava/lang/Double;)V", "", ""),
	NULL(null, null, null, -1, -1, false, null, null, null);
	
	//java information
	public final String desc;
	public final String clazz;
	public final String descValueOf;
	public final int newArrayType;
	public final int arrayStore;
	public final boolean isWrapper;
	public final boolean isObject;
	public boolean hasIncrement;
	
	//ArrUtils information
	public final String descSet;
	public final String descFill;
	public final String descInsert;
	public final String descSetN;
	public final String descFillN;
	public final String descInsertN;
	
	private DataType(String desc, String clazz, String valueOfDesc,int newArrType, int store, boolean isObject, String setDesc, String fillDesc, String insertDesc)
	{
		this(desc, clazz, valueOfDesc,newArrType, store, isObject, setDesc, fillDesc, insertDesc, setDesc, fillDesc, insertDesc);
	}
	
	private DataType(String desc, String clazz, String valueOfDesc,int newArrType, int store, boolean isObject, String setDesc, String fillDesc, String insertDesc, String setDescN, String fillDescN, String insertDescN)
	{
		this.desc = desc;
		this.clazz = clazz;
		this.descValueOf = valueOfDesc;
		this.newArrayType = newArrType;
		this.arrayStore = store;
		this.isWrapper = valueOfDesc != null && !valueOfDesc.isEmpty();
		this.isObject = isObject;
		this.hasIncrement = fillDesc != null && fillDesc.endsWith("III)V");
		this.descSet = setDesc;
		this.descFill = fillDesc;
		this.descInsert = insertDesc;
		this.descSetN = setDescN;
		this.descFillN = fillDescN;
		this.descInsertN = insertDescN;
	}
	
	public String getDescSet(String val)
	{
		return (this.isWrapper && val == null) ? this.descSetN : this.descSet;
	}
	
	public String getDescFill(String val)
	{
		return (this.isWrapper && val == null) ? this.descFillN : this.descFill;
	}
	
	public String getDescInsert(String val)
	{
		return (this.isWrapper && val == null) ? this.descInsertN : this.descInsert;
	}
	
	public String getDescSet(boolean isNull)
	{
		return (this.isWrapper && isNull) ? this.descSetN : this.descSet;
	}
	
	public String getDescFill(boolean isNull)
	{
		return (this.isWrapper && isNull) ? this.descFillN : this.descFill;
	}
	
	public String getDescInsert(boolean isNull)
	{
		return (this.isWrapper && isNull) ? this.descInsertN : this.descInsert;
	}
	
	/**
	 * Get's the Primitive form of the Type
	 */
	public static DataType getPrimitive(DataType t)
	{
		switch(t)
		{
			case WRAPPED_BOOLEAN:
				return BOOLEAN;
			case WRAPPED_BYTE:
				return BYTE;
			case WRAPPED_SHORT:
				return SHORT;
			case WRAPPED_INT:
				return INT;
			case WRAPPED_LONG:
				return LONG;
			case WRAPPED_FLOAT:
				return FLOAT;
			case WRAPPED_DOUBLE:
				return DOUBLE;
			case WRAPPED_CHAR:
				return CHAR;
			default:
				break;
		}
		return t;
	}
	
	/**
	 * converts string to Type
	 */
	public static DataType getType(String type) 
	{
		boolean isWrapper = Character.isUpperCase(type.charAt(0));
		type = type.toLowerCase();
		if(type.equals("boolean"))
			return !isWrapper ? BOOLEAN : WRAPPED_BOOLEAN;
		else if(type.equals("byte"))
			return !isWrapper ? BYTE : WRAPPED_BYTE;
		else if(type.equals("short"))
			return !isWrapper ? SHORT : WRAPPED_SHORT;
		else if(type.equals("int") || type.equals("integer"))
			return !isWrapper ? INT : WRAPPED_INT;
		else if(type.equals("long"))
			return !isWrapper ? LONG : WRAPPED_LONG;
		else if(type.equals("float"))
			return !isWrapper ? FLOAT : WRAPPED_FLOAT;
		else if(type.equals("double"))
			return !isWrapper ? DOUBLE : WRAPPED_DOUBLE;
		else if(type.equals("string"))
			return STRING;
		else if(type.equals("char"))
			return CHAR;
		else if(type.equals("character"))
			return WRAPPED_CHAR;
		
		return NULL;
	}

}

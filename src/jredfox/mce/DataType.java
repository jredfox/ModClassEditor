package jredfox.mce;

import org.objectweb.asm.Opcodes;

public enum DataType {
	
	CHAR("C", "", "", "([CIC)V", "([CCIII)V", "([C[CI)V", Opcodes.T_CHAR, Opcodes.CASTORE, false),
	BOOLEAN("Z", "", "", "([ZIZ)V", "([ZZII)V", "([Z[ZI)V", Opcodes.T_BOOLEAN, Opcodes.BASTORE, false),
	BYTE("B", "", "", "([BIB)V", "([BBIII)V", "([B[BI)V", Opcodes.T_BYTE, Opcodes.BASTORE, false),
	SHORT("S", "", "", "([SIS)V", "([SSIII)V", "([S[SI)V", Opcodes.T_SHORT, Opcodes.SASTORE, false),
	INT("I", "", "", "([III)V", "([IIIII)V", "([I[II)V", Opcodes.T_INT, Opcodes.IASTORE, false),
	LONG("J", "", "", "([JIJ)V", "([JJIII)V", "([J[JI)V", Opcodes.T_LONG, Opcodes.LASTORE, false),
	FLOAT("F", "", "", "([FIF)V", "([FFIII)V", "([F[FI)V", Opcodes.T_FLOAT, Opcodes.FASTORE, false),
	DOUBLE("D", "", "", "([DID)V", "([DDIII)V", "([D[DI)V", Opcodes.T_DOUBLE, Opcodes.DASTORE, false),
	STRING("Ljava/lang/String;", "java/lang/String", "", "([Ljava/lang/String;ILjava/lang/String;)V", "([Ljava/lang/String;Ljava/lang/String;II)V", "([Ljava/lang/String;[Ljava/lang/String;I)V", -1, Opcodes.AASTORE, true),
	WRAPPED_CHAR("Ljava/lang/Character;", "java/lang/Character", "(C)Ljava/lang/Character;", "([Ljava/lang/Character;IC)V", "([Ljava/lang/Character;CIII)V", "([Ljava/lang/Character;[CI)V", -1, Opcodes.AASTORE, true),
	WRAPPED_BOOLEAN("Ljava/lang/Boolean;", "java/lang/Boolean", "(Z)Ljava/lang/Boolean;", "([Ljava/lang/Boolean;IZ)V", "([Ljava/lang/Boolean;ZII)V", "([Ljava/lang/Boolean;[ZI)V", -1, Opcodes.AASTORE, true),
	WRAPPED_BYTE("Ljava/lang/Byte;", "java/lang/Byte", "(B)Ljava/lang/Byte;", "([Ljava/lang/Byte;IB)V", "([Ljava/lang/Byte;BIII)V", "([Ljava/lang/Byte;[BI)V", -1, Opcodes.AASTORE, true),
	WRAPPED_SHORT("Ljava/lang/Short;", "java/lang/Short", "(S)Ljava/lang/Short;", "([Ljava/lang/Short;IS)V", "([Ljava/lang/Short;SIII)V", "([Ljava/lang/Short;[SI)V", -1, Opcodes.AASTORE, true),
	WRAPPED_INT("Ljava/lang/Integer;", "java/lang/Integer", "(I)Ljava/lang/Integer;", "([Ljava/lang/Integer;II)V", "([Ljava/lang/Integer;IIII)V", "([Ljava/lang/Integer;[II)V", -1, Opcodes.AASTORE, true),
	WRAPPED_LONG("Ljava/lang/Long;", "java/lang/Long", "(J)Ljava/lang/Long;", "([Ljava/lang/Long;IJ)V", "([Ljava/lang/Long;JIII)V", "([Ljava/lang/Long;[JI)V", -1, Opcodes.AASTORE, true),
	WRAPPED_FLOAT("Ljava/lang/Float;", "java/lang/Float", "(F)Ljava/lang/Float;", "([Ljava/lang/Float;IF)V", "([Ljava/lang/Float;FIII)V", "([Ljava/lang/Float;[FI)V", -1, Opcodes.AASTORE, true),
	WRAPPED_DOUBLE("Ljava/lang/Double;", "java/lang/Double", "(D)Ljava/lang/Double;", "([Ljava/lang/Double;ID)V", "([Ljava/lang/Double;DIII)V", "([Ljava/lang/Double;[DI)V", -1, Opcodes.AASTORE, true),
	NULL(null, null, null, null, null, null, -1, -1, false);
	
	public final String desc;
	public final String clazz;
	public final String descValueOf;
	public final String descSet;
	public final String descFill;
	public final String descInsert;
	public final int newArrayType;
	public final int arrayStore;
	public final boolean isWrapper;
	public final boolean isObject;
	public boolean hasIncrement;//whether or not the set method has an increment parameter
	
	private DataType(String desc, String clazz, String valueOfDesc, String setDesc, String fillDesc, String insertDesc,int newArrType, int store, boolean isObject)
	{
		this.desc = desc;
		this.clazz = clazz;
		this.descValueOf = valueOfDesc;
		this.descSet = setDesc;
		this.descFill = fillDesc;
		this.descInsert = insertDesc;
		this.newArrayType = newArrType;
		this.arrayStore = store;
		this.isWrapper = valueOfDesc != null && !valueOfDesc.isEmpty();
		this.isObject = isObject;
		this.hasIncrement = fillDesc != null && fillDesc.endsWith("III)V");
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

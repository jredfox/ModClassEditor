package jredfox.mce;

import org.objectweb.asm.Opcodes;

public enum DataType {
	
	BOOLEAN("Z", "", "", Opcodes.T_BOOLEAN, Opcodes.BASTORE, false),
	BYTE("B", "", "", Opcodes.T_BYTE, Opcodes.BASTORE, false),
	SHORT("S", "", "", Opcodes.T_SHORT, Opcodes.SASTORE, false),
	INT("I", "", "", Opcodes.T_INT, Opcodes.IASTORE, false),
	LONG("J", "", "", Opcodes.T_LONG, Opcodes.LASTORE, false),
	FLOAT("F", "", "", Opcodes.T_FLOAT, Opcodes.FASTORE, false),
	DOUBLE("D", "", "", Opcodes.T_DOUBLE, Opcodes.DASTORE, false),
	STRING("Ljava/lang/String;", "java/lang/String", "", -1, Opcodes.AASTORE, true),
	WRAPPED_BOOLEAN("Ljava/lang/Boolean;", "java/lang/Boolean", "(Z)Ljava/lang/Boolean;", -1, Opcodes.AASTORE, true),
	WRAPPED_BYTE("Ljava/lang/Byte;", "java/lang/Byte", "(B)Ljava/lang/Byte;", -1, Opcodes.AASTORE, true),
	WRAPPED_SHORT("Ljava/lang/Short;", "java/lang/Short", "(S)Ljava/lang/Short;", -1, Opcodes.AASTORE, true),
	WRAPPED_INT("Ljava/lang/Integer;", "java/lang/Integer", "(I)Ljava/lang/Integer;", -1, Opcodes.AASTORE, true),
	WRAPPED_LONG("Ljava/lang/Long;", "java/lang/Long", "(J)Ljava/lang/Long;", -1, Opcodes.AASTORE, true),
	WRAPPED_FLOAT("Ljava/lang/Float;", "java/lang/Float", "(F)Ljava/lang/Float;", -1, Opcodes.AASTORE, true),
	WRAPPED_DOUBLE("Ljava/lang/Double;", "java/lang/Double", "(D)Ljava/lang/Double;", -1, Opcodes.AASTORE, true),
	NULL(null, null, null, -1, -1, false);
	
	public final String desc;
	public final String clazz;
	public final String valueOfDesc;
	public final int newArrayType;
	public final int arrayStore;
	public final boolean isWrapper;
	public final boolean isObject;
	
	private DataType(String desc, String clazz, String valueOfDesc, int newArrType, int store, boolean isObject)
	{
		this.desc = desc;
		this.clazz = clazz;
		this.valueOfDesc = valueOfDesc;
		this.newArrayType = newArrType;
		this.arrayStore = store;
		this.isWrapper = valueOfDesc != null && !valueOfDesc.isEmpty();
		this.isObject = isObject;
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
		
		return NULL;
	}

}

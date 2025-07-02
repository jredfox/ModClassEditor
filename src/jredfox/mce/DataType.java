package jredfox.mce;

public enum DataType {
	
	BOOLEAN,
	BYTE,
	SHORT,
	INT,
	LONG,
	FLOAT,
	DOUBLE,
	STRING,
	WRAPPED_BOOLEAN,
	WRAPPED_BYTE,
	WRAPPED_SHORT,
	WRAPPED_INT,
	WRAPPED_LONG,
	WRAPPED_FLOAT,
	WRAPPED_DOUBLE,
	NULL;
	
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

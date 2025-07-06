package jredfox.mce;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Opcodes;

public class OpcodeHelper {
	
	public static final Map<String, Integer> opps;
	public static final Map<Integer, String> i2opps;
	public static final Set<Integer> BAD_CTR_OPCODES = new HashSet<Integer>();
	
	static
	{
		Map<String, Integer> o = new HashMap(256, 0.9f);
		Map<Integer, String> i = new HashMap(256, 0.9f);
		o.put("ASM4", 262144);
		o.put("ASM5", 327680);
		o.put("V1_1", 196653);
		o.put("V1_2", 46);
		o.put("V1_3", 47);
		o.put("V1_4", 48);
		o.put("V1_5", 49);
		o.put("V1_6", 50);
		o.put("V1_7", 51);
		o.put("V1_8", 52);
		o.put("ACC_PUBLIC", 1);
		o.put("ACC_PRIVATE", 2);
		o.put("ACC_PROTECTED", 4);
		o.put("ACC_STATIC", 8);
		o.put("ACC_FINAL", 16);
		o.put("ACC_SUPER", 32);
		o.put("ACC_SYNCHRONIZED", 32);
		o.put("ACC_VOLATILE", 64);
		o.put("ACC_BRIDGE", 64);
		o.put("ACC_VARARGS", 128);
		o.put("ACC_TRANSIENT", 128);
		o.put("ACC_NATIVE", 256);
		o.put("ACC_INTERFACE", 512);
		o.put("ACC_ABSTRACT", 1024);
		o.put("ACC_STRICT", 2048);
		o.put("ACC_SYNTHETIC", 4096);
		o.put("ACC_ANNOTATION", 8192);
		o.put("ACC_ENUM", 16384);
		o.put("ACC_MANDATED", 32768);
		o.put("ACC_DEPRECATED", 131072);
		o.put("T_BOOLEAN", 4);
		o.put("T_CHAR", 5);
		o.put("T_FLOAT", 6);
		o.put("T_DOUBLE", 7);
		o.put("T_BYTE", 8);
		o.put("T_SHORT", 9);
		o.put("T_INT", 10);
		o.put("T_LONG", 11);
		o.put("H_GETFIELD", 1);
		o.put("H_GETSTATIC", 2);
		o.put("H_PUTFIELD", 3);
		o.put("H_PUTSTATIC", 4);
		o.put("H_INVOKEVIRTUAL", 5);
		o.put("H_INVOKESTATIC", 6);
		o.put("H_INVOKESPECIAL", 7);
		o.put("H_NEWINVOKESPECIAL", 8);
		o.put("H_INVOKEINTERFACE", 9);
		o.put("F_NEW", -1);//Opcodes.
		o.put("F_FULL", 0);
		o.put("F_APPEND", 1);
		o.put("F_CHOP", 2);
		o.put("F_SAME", 3);
		o.put("F_SAME1", 4);
		o.put("TOP", 0);
		o.put("INTEGER", 1);
		o.put("FLOAT", 2);
		o.put("DOUBLE", 3);
		o.put("LONG", 4);
		o.put("NULL", 5);
		o.put("UNINITIALIZED_THIS", 6);
		o.put("NOP", 0);
		o.put("ACONST_NULL", 1);
		o.put("ICONST_M1", 2);
		o.put("ICONST_0", 3);
		o.put("ICONST_1", 4);
		o.put("ICONST_2", 5);
		o.put("ICONST_3", 6);
		o.put("ICONST_4", 7);
		o.put("ICONST_5", 8);
		o.put("LCONST_0", 9);
		o.put("LCONST_1", 10);
		o.put("FCONST_0", 11);
		o.put("FCONST_1", 12);
		o.put("FCONST_2", 13);
		o.put("DCONST_0", 14);
		o.put("DCONST_1", 15);
		o.put("BIPUSH", 16);
		o.put("SIPUSH", 17);
		o.put("LDC", 18);
		o.put("ILOAD", 21);
		o.put("LLOAD", 22);
		o.put("FLOAD", 23);
		o.put("DLOAD", 24);
		o.put("ALOAD", 25);
		o.put("IALOAD", 46);
		o.put("LALOAD", 47);
		o.put("FALOAD", 48);
		o.put("DALOAD", 49);
		o.put("AALOAD", 50);
		o.put("BALOAD", 51);
		o.put("CALOAD", 52);
		o.put("SALOAD", 53);
		o.put("ISTORE", 54);
		o.put("LSTORE", 55);
		o.put("FSTORE", 56);
		o.put("DSTORE", 57);
		o.put("ASTORE", 58);
		o.put("IASTORE", 79);
		o.put("LASTORE", 80);
		o.put("FASTORE", 81);
		o.put("DASTORE", 82);
		o.put("AASTORE", 83);
		o.put("BASTORE", 84);
		o.put("CASTORE", 85);
		o.put("SASTORE", 86);
		o.put("POP", 87);
		o.put("POP2", 88);
		o.put("DUP", 89);
		o.put("DUP_X1", 90);
		o.put("DUP_X2", 91);
		o.put("DUP2", 92);
		o.put("DUP2_X1", 93);
		o.put("DUP2_X2", 94);
		o.put("SWAP", 95);
		o.put("IADD", 96);
		o.put("LADD", 97);
		o.put("FADD", 98);
		o.put("DADD", 99);
		o.put("ISUB", 100);
		o.put("LSUB", 101);
		o.put("FSUB", 102);
		o.put("DSUB", 103);
		o.put("IMUL", 104);
		o.put("LMUL", 105);
		o.put("FMUL", 106);
		o.put("DMUL", 107);
		o.put("IDIV", 108);
		o.put("LDIV", 109);
		o.put("FDIV", 110);
		o.put("DDIV", 111);
		o.put("IREM", 112);
		o.put("LREM", 113);
		o.put("FREM", 114);
		o.put("DREM", 115);
		o.put("INEG", 116);
		o.put("LNEG", 117);
		o.put("FNEG", 118);
		o.put("DNEG", 119);
		o.put("ISHL", 120);
		o.put("LSHL", 121);
		o.put("ISHR", 122);
		o.put("LSHR", 123);
		o.put("IUSHR", 124);
		o.put("LUSHR", 125);
		o.put("IAND", 126);
		o.put("LAND", 127);
		o.put("IOR", 128);
		o.put("LOR", 129);
		o.put("IXOR", 130);
		o.put("LXOR", 131);
		o.put("IINC", 132);
		o.put("I2L", 133);
		o.put("I2F", 134);
		o.put("I2D", 135);
		o.put("L2I", 136);
		o.put("L2F", 137);
		o.put("L2D", 138);
		o.put("F2I", 139);
		o.put("F2L", 140);
		o.put("F2D", 141);
		o.put("D2I", 142);
		o.put("D2L", 143);
		o.put("D2F", 144);
		o.put("I2B", 145);
		o.put("I2C", 146);
		o.put("I2S", 147);
		o.put("LCMP", 148);
		o.put("FCMPL", 149);
		o.put("FCMPG", 150);
		o.put("DCMPL", 151);
		o.put("DCMPG", 152);
		o.put("IFEQ", 153);
		o.put("IFNE", 154);
		o.put("IFLT", 155);
		o.put("IFGE", 156);
		o.put("IFGT", 157);
		o.put("IFLE", 158);
		o.put("IF_ICMPEQ", 159);
		o.put("IF_ICMPNE", 160);
		o.put("IF_ICMPLT", 161);
		o.put("IF_ICMPGE", 162);
		o.put("IF_ICMPGT", 163);
		o.put("IF_ICMPLE", 164);
		o.put("IF_ACMPEQ", 165);
		o.put("IF_ACMPNE", 166);
		o.put("GOTO", 167);
		o.put("JSR", 168);
		o.put("RET", 169);
		o.put("TABLESWITCH", 170);
		o.put("LOOKUPSWITCH", 171);
		o.put("IRETURN", 172);
		o.put("LRETURN", 173);
		o.put("FRETURN", 174);
		o.put("DRETURN", 175);
		o.put("ARETURN", 176);
		o.put("RETURN", 177);
		o.put("GETSTATIC", 178);
		o.put("PUTSTATIC", 179);
		o.put("GETFIELD", 180);
		o.put("PUTFIELD", 181);
		o.put("INVOKEVIRTUAL", 182);
		o.put("INVOKESPECIAL", 183);
		o.put("INVOKESTATIC", 184);
		o.put("INVOKEINTERFACE", 185);
		o.put("INVOKEDYNAMIC", 186);
		o.put("NEW", 187);
		o.put("NEWARRAY", 188);
		o.put("ANEWARRAY", 189);
		o.put("ARRAYLENGTH", 190);
		o.put("ATHROW", 191);
		o.put("CHECKCAST", 192);
		o.put("INSTANCEOF", 193);
		o.put("MONITORENTER", 194);
		o.put("MONITOREXIT", 195);
		o.put("MULTIANEWARRAY", 197);
		o.put("IFNULL", 198);
		o.put("IFNONNULL", 199);
		opps = o;
		
		//Inverse the map for lookup later
		for (Map.Entry<String, Integer> entry : o.entrySet()) 
		{
			String k = entry.getKey();
			if(k.startsWith("V1_"))
				continue;
			i.put(entry.getValue(), entry.getKey());
		}
		i2opps = i;
		
		BAD_CTR_OPCODES.add(Opcodes.NEW);
		BAD_CTR_OPCODES.add(Opcodes.DUP);
		BAD_CTR_OPCODES.add(Opcodes.DUP_X1);
		BAD_CTR_OPCODES.add(Opcodes.DUP_X2);
		BAD_CTR_OPCODES.add(Opcodes.DUP2);
		BAD_CTR_OPCODES.add(Opcodes.DUP2_X1);
		BAD_CTR_OPCODES.add(Opcodes.DUP2_X2);
		BAD_CTR_OPCODES.add(Opcodes.SWAP);
	}
	
	public static int getOppcode(String opp)
	{
		Integer o = opps.get(opp.toUpperCase());
		return o != null ? o : MCEObj.parseInt(opp);
	}
	
	public static String getOppcodeName(int opp)
	{
		String s = i2opps.get(opp);
		return s != null ? s : "NOP";
	}
	
	public static boolean hasOpcode(String opp)
	{
		if(opps.containsKey(opp.toUpperCase()))
			return true;
		int i = MCEObj.parseInt(opp);
		return i != 0 && i2opps.containsKey(i);
	}
	
	/**
	 * loads this class if not already loaded
	 */
	public static void init()
	{
		
	}

}

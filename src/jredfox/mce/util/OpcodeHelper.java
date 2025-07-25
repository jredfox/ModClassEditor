package jredfox.mce.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Opcodes;

public class OpcodeHelper {
	
	public static final Map<String, Integer> opps;
	public static final Map<Integer, String> i2opps;
	
	public static final Set<Integer> BAD_CTR_OPCODES = new HashSet<Integer>(10, 0.9F);
	
	static
	{
		Map<String, Integer> o = new HashMap(512);//for some reason cacpity wants to be 512 no matter the load factor so I say 512 to begin with now
		Map<Integer, String> i = new HashMap(256, 0.9F);
		
		//start opcode info
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
		o.put("F_NEW", -1);
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

		//start opcode instructions
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
		o.put("LDC_W", 19);
		o.put("LDC2_W", 20);
		o.put("ILOAD", 21);
		o.put("LLOAD", 22);
		o.put("FLOAD", 23);
		o.put("DLOAD", 24);
		o.put("ALOAD", 25);
		o.put("ILOAD_0", 26);
		o.put("ILOAD_1", 27);
		o.put("ILOAD_2", 28);
		o.put("ILOAD_3", 29);
		o.put("LLOAD_0", 30);
		o.put("LLOAD_1", 31);
		o.put("LLOAD_2", 32);
		o.put("LLOAD_3", 33);
		o.put("FLOAD_0", 34);
		o.put("FLOAD_1", 35);
		o.put("FLOAD_2", 36);
		o.put("FLOAD_3", 37);
		o.put("DLOAD_0", 38);
		o.put("DLOAD_1", 39);
		o.put("DLOAD_2", 40);
		o.put("DLOAD_3", 41);
		o.put("ALOAD_0", 42);
		o.put("ALOAD_1", 43);
		o.put("ALOAD_2", 44);
		o.put("ALOAD_3", 45);
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
		o.put("ISTORE_0", 59);
		o.put("ISTORE_1", 60);
		o.put("ISTORE_2", 61);
		o.put("ISTORE_3", 62);
		o.put("LSTORE_0", 63);
		o.put("LSTORE_1", 64);
		o.put("LSTORE_2", 65);
		o.put("LSTORE_3", 66);
		o.put("FSTORE_0", 67);
		o.put("FSTORE_1", 68);
		o.put("FSTORE_2", 69);
		o.put("FSTORE_3", 70);
		o.put("DSTORE_0", 71);
		o.put("DSTORE_1", 72);
		o.put("DSTORE_2", 73);
		o.put("DSTORE_3", 74);
		o.put("ASTORE_0", 75);
		o.put("ASTORE_1", 76);
		o.put("ASTORE_2", 77);
		o.put("ASTORE_3", 78);
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
		o.put("WIDE", 196);
		o.put("MULTIANEWARRAY", 197);
		o.put("IFNULL", 198);
		o.put("IFNONNULL", 199);
		o.put("GOTO_W", 200);
		o.put("JSR_W", 201);
		opps = o;
		
		//Inverse the map for lookup later
		i.put(0, "NOP");
		i.put(1, "ACONST_NULL");
		i.put(2, "ICONST_M1");
		i.put(3, "ICONST_0");
		i.put(4, "ICONST_1");
		i.put(5, "ICONST_2");
		i.put(6, "ICONST_3");
		i.put(7, "ICONST_4");
		i.put(8, "ICONST_5");
		i.put(9, "LCONST_0");
		i.put(10, "LCONST_1");
		i.put(11, "FCONST_0");
		i.put(12, "FCONST_1");
		i.put(13, "FCONST_2");
		i.put(14, "DCONST_0");
		i.put(15, "DCONST_1");
		i.put(16, "BIPUSH");
		i.put(17, "SIPUSH");
		i.put(18, "LDC");
		i.put(19, "LDC_W");
		i.put(20, "LDC2_W");
		i.put(21, "ILOAD");
		i.put(22, "LLOAD");
		i.put(23, "FLOAD");
		i.put(24, "DLOAD");
		i.put(25, "ALOAD");
		i.put(26, "ILOAD_0");
		i.put(27, "ILOAD_1");
		i.put(28, "ILOAD_2");
		i.put(29, "ILOAD_3");
		i.put(30, "LLOAD_0");
		i.put(31, "LLOAD_1");
		i.put(32, "LLOAD_2");
		i.put(33, "LLOAD_3");
		i.put(34, "FLOAD_0");
		i.put(35, "FLOAD_1");
		i.put(36, "FLOAD_2");
		i.put(37, "FLOAD_3");
		i.put(38, "DLOAD_0");
		i.put(39, "DLOAD_1");
		i.put(40, "DLOAD_2");
		i.put(41, "DLOAD_3");
		i.put(42, "ALOAD_0");
		i.put(43, "ALOAD_1");
		i.put(44, "ALOAD_2");
		i.put(45, "ALOAD_3");
		i.put(46, "IALOAD");
		i.put(47, "LALOAD");
		i.put(48, "FALOAD");
		i.put(49, "DALOAD");
		i.put(50, "AALOAD");
		i.put(51, "BALOAD");
		i.put(52, "CALOAD");
		i.put(53, "SALOAD");
		i.put(54, "ISTORE");
		i.put(55, "LSTORE");
		i.put(56, "FSTORE");
		i.put(57, "DSTORE");
		i.put(58, "ASTORE");
		i.put(59, "ISTORE_0");
		i.put(60, "ISTORE_1");
		i.put(61, "ISTORE_2");
		i.put(62, "ISTORE_3");
		i.put(63, "LSTORE_0");
		i.put(64, "LSTORE_1");
		i.put(65, "LSTORE_2");
		i.put(66, "LSTORE_3");
		i.put(67, "FSTORE_0");
		i.put(68, "FSTORE_1");
		i.put(69, "FSTORE_2");
		i.put(70, "FSTORE_3");
		i.put(71, "DSTORE_0");
		i.put(72, "DSTORE_1");
		i.put(73, "DSTORE_2");
		i.put(74, "DSTORE_3");
		i.put(75, "ASTORE_0");
		i.put(76, "ASTORE_1");
		i.put(77, "ASTORE_2");
		i.put(78, "ASTORE_3");
		i.put(79, "IASTORE");
		i.put(80, "LASTORE");
		i.put(81, "FASTORE");
		i.put(82, "DASTORE");
		i.put(83, "AASTORE");
		i.put(84, "BASTORE");
		i.put(85, "CASTORE");
		i.put(86, "SASTORE");
		i.put(87, "POP");
		i.put(88, "POP2");
		i.put(89, "DUP");
		i.put(90, "DUP_X1");
		i.put(91, "DUP_X2");
		i.put(92, "DUP2");
		i.put(93, "DUP2_X1");
		i.put(94, "DUP2_X2");
		i.put(95, "SWAP");
		i.put(96, "IADD");
		i.put(97, "LADD");
		i.put(98, "FADD");
		i.put(99, "DADD");
		i.put(100, "ISUB");
		i.put(101, "LSUB");
		i.put(102, "FSUB");
		i.put(103, "DSUB");
		i.put(104, "IMUL");
		i.put(105, "LMUL");
		i.put(106, "FMUL");
		i.put(107, "DMUL");
		i.put(108, "IDIV");
		i.put(109, "LDIV");
		i.put(110, "FDIV");
		i.put(111, "DDIV");
		i.put(112, "IREM");
		i.put(113, "LREM");
		i.put(114, "FREM");
		i.put(115, "DREM");
		i.put(116, "INEG");
		i.put(117, "LNEG");
		i.put(118, "FNEG");
		i.put(119, "DNEG");
		i.put(120, "ISHL");
		i.put(121, "LSHL");
		i.put(122, "ISHR");
		i.put(123, "LSHR");
		i.put(124, "IUSHR");
		i.put(125, "LUSHR");
		i.put(126, "IAND");
		i.put(127, "LAND");
		i.put(128, "IOR");
		i.put(129, "LOR");
		i.put(130, "IXOR");
		i.put(131, "LXOR");
		i.put(132, "IINC");
		i.put(133, "I2L");
		i.put(134, "I2F");
		i.put(135, "I2D");
		i.put(136, "L2I");
		i.put(137, "L2F");
		i.put(138, "L2D");
		i.put(139, "F2I");
		i.put(140, "F2L");
		i.put(141, "F2D");
		i.put(142, "D2I");
		i.put(143, "D2L");
		i.put(144, "D2F");
		i.put(145, "I2B");
		i.put(146, "I2C");
		i.put(147, "I2S");
		i.put(148, "LCMP");
		i.put(149, "FCMPL");
		i.put(150, "FCMPG");
		i.put(151, "DCMPL");
		i.put(152, "DCMPG");
		i.put(153, "IFEQ");
		i.put(154, "IFNE");
		i.put(155, "IFLT");
		i.put(156, "IFGE");
		i.put(157, "IFGT");
		i.put(158, "IFLE");
		i.put(159, "IF_ICMPEQ");
		i.put(160, "IF_ICMPNE");
		i.put(161, "IF_ICMPLT");
		i.put(162, "IF_ICMPGE");
		i.put(163, "IF_ICMPGT");
		i.put(164, "IF_ICMPLE");
		i.put(165, "IF_ACMPEQ");
		i.put(166, "IF_ACMPNE");
		i.put(167, "GOTO");
		i.put(168, "JSR");
		i.put(169, "RET");
		i.put(170, "TABLESWITCH");
		i.put(171, "LOOKUPSWITCH");
		i.put(172, "IRETURN");
		i.put(173, "LRETURN");
		i.put(174, "FRETURN");
		i.put(175, "DRETURN");
		i.put(176, "ARETURN");
		i.put(177, "RETURN");
		i.put(178, "GETSTATIC");
		i.put(179, "PUTSTATIC");
		i.put(180, "GETFIELD");
		i.put(181, "PUTFIELD");
		i.put(182, "INVOKEVIRTUAL");
		i.put(183, "INVOKESPECIAL");
		i.put(184, "INVOKESTATIC");
		i.put(185, "INVOKEINTERFACE");
		i.put(186, "INVOKEDYNAMIC");
		i.put(187, "NEW");
		i.put(188, "NEWARRAY");
		i.put(189, "ANEWARRAY");
		i.put(190, "ARRAYLENGTH");
		i.put(191, "ATHROW");
		i.put(192, "CHECKCAST");
		i.put(193, "INSTANCEOF");
		i.put(194, "MONITORENTER");
		i.put(195, "MONITOREXIT");
		i.put(196, "WIDE");
		i.put(197, "MULTIANEWARRAY");
		i.put(198, "IFNULL");
		i.put(199, "IFNONNULL");
		i.put(200, "GOTO_W");
		i.put(201, "JSR_W");
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
		Integer o = opps.get(opp.trim().toUpperCase());
		return o != null ? o : MCEUtil.parseInt(opp);
	}
	
	public static boolean hasOpcode(String opp)
	{
		opp = opp.trim().toUpperCase();
		if(opps.containsKey(opp))
			return true;
		int i = MCEUtil.parseInt(opp);
		return i != 0 && i2opps.containsKey(i);
	}
	
	public static String getOppcodeInsnName(int opcode) {
		return i2opps.get(opcode);
	}
	
	/**
	 * loads this class if not already loaded
	 */
	public static void init()
	{
		
	}


}

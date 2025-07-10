package jredfox.mce.types;

/**
 * A Quick Way to determine what type the Insn is
 * @author jredfox
 */
public enum InsnTypes {
	
	/**
	 * Looks for a Specific Opcode regardless of type when searching
	 */
	Opcode,
	/**
	 * InsnNode pushes a single opcode insn
	 */
	InsnNode,
	/**
	 * Pushes  BIPUSH, SIPUSH or NEWARRAY
	 */
	IntInsnNode,
	/**
	 * Pushes A local Variable based on index
	 */
	VarInsnNode,
	/**
	 * Jumps to a Label If Condition
	 */
	JumpInsnNode,
	/**
	 * Pushes NEW, ANEWARRAY, CHECKCAST or INSTANCEOF
	 */
	TypeInsnNode,
	/**
	 * Pushes Integer past short, Float, Long, Double or String
	 */
	LdcInsnNode,
	/**
	 * GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD
	 */
	FieldInsnNode,
	/**
	 * INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE
	 */
	MethodInsnNode,
	/**
	 * Pushes a line number
	 * WARNING: NOT USED Line numbers are stripped or inconsistent based on ASM / Java Version especially on ASM 4 and below
	 * Even If you used index based lines the bytecode viewer makes it pretty difficult to figure out which lines are at what index
	 * Use {@link #LabelNode} indexed based instead. It's bytecode viewer friendly and will get consistent results as long as the use the original class for the indexes
	 */
	LineNumberNode,
	/**
	 * Pushes a Label
	 */
	LabelNode,
	/**
	 * Pushes an local variable Integer then increments it after i++ for example
	 */
	IincInsnNode,
	/**
	 * NULL or Unsupported Insn
	 */
	NULL

}

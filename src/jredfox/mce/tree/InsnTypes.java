package jredfox.mce.tree;

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
	 */
	LineNumberNode,
	/**
	 * Pushes a Label
	 */
	LabelNode,
	/**
	 * NULL or Unsupported Insn
	 */
	NULL

}

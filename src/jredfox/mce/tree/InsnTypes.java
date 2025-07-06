package jredfox.mce.tree;

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
	 * Pushes a line number
	 */
	LineNumberNode,
	/**
	 * Pushes a Label
	 */
	LabelNode,
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
	 * NULL or Unsupported Insn
	 */
	NULL

}

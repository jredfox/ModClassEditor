package jredfox.mce.tree;

import java.util.Map;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * Place Holder for MCE Looking for Opcodes only instead of a specific Insn
 * @author jredfox
 */
public class MCEOpcode extends AbstractInsnNode {
	
    public MCEOpcode(final int opcode) {
        super(opcode);
    }

    @Override
    public int getType() {
        return INSN;
    }
    
    @Override
    public void accept(final MethodVisitor mv) {
        mv.visitInsn(opcode);
    }

    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> labels) {
        return new MCEOpcode(opcode);
    }

}

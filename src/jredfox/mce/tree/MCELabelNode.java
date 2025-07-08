package jredfox.mce.tree;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LabelNode;

public class MCELabelNode extends LabelNode {
	
    public MCELabelNode()
    {
        super();
    }
	
    public MCELabelNode(final Label label)
    {
        super(label);
    }

}

package jredfox.mce.tree;

import org.objectweb.asm.tree.LabelNode;

public class MCEIndexLabel extends LabelNode {
	
	public int index;
	
	public MCEIndexLabel(int index)
	{
		super();
		this.index = index;
	}

}

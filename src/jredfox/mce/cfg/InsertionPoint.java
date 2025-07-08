package jredfox.mce.cfg;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.ralleytn.simple.json.JSONObject;

import jredfox.mce.tree.MCEIndexLabel;
import jredfox.mce.tree.MCEOpcode;
import jredfox.mce.types.InsnTypes;
import jredfox.mce.util.MCEUtil;
import jredfox.mce.util.OpcodeHelper;

public class InsertionPoint
{
	public static enum ShiftTo
	{
		LINE,
		LABEL,
		EXACT;
		
		public static ShiftTo get(String v)
		{
			v = v.trim().toUpperCase();
			
			if(v.startsWith("LABEL"))
				return LABEL;
			else if(v.startsWith("EXACT") || v.startsWith("INSN"))
				return EXACT;
			
			return LINE;
		}
	}
		
	/**
	 * Opperation must be "before" or "after"
	 */
	public Opperation opp = Opperation.AFTER;
	/**
	 * The ASM Injection Point when NON-NULL will be a specific injection point instead of before or after. The opp will combine with this
	 */
	public AbstractInsnNode point;
	/**
	 * The ASM Injection Point Cached Insn Type for easy switches later
	 */
	public InsnTypes type = InsnTypes.NULL;
	/**
	 * the index of the InjectionPoint's Occurance
	 */
	public int occurrence;
	/**
	 * Shift X Number of {@link #shiftTo} Instructions
	 */
	public int shift;
	/**
	 * The Insn Type we are shifting
	 */
	public ShiftTo shiftTo = ShiftTo.LINE;
	
	public InsertionPoint(JSONObject j)
	{
		Object o = j.containsKey("inject") ? j.get("inject") : j.get("insert");
		if(o instanceof JSONObject)
		{
			JSONObject oj = (JSONObject) o;
			this.parse(oj.getString("point"));
			if(this.type != InsnTypes.LabelNode)
				this.occurrence = MCEUtil.parseInt(MCEUtil.safeString(oj.getAsString("occurrence"), "0").replace("start", "0").replace("end", "-1"));
			this.shift =  MCEUtil.parseInt(MCEUtil.safeString(oj.getAsString("shift"), "0").replace("start", "0").replace("end", "-1"));
			if(oj.containsKey("shiftTo"))
				this.shiftTo = ShiftTo.get(oj.getAsString("shiftTo"));
		}
		else
			this.parse(MCEUtil.safeString((String) o));
		
		if(this.point != null)
			System.out.println("DEBUG InsertionPoint " + this);
	}
	
	public InsertionPoint(String p)
	{
		this.parse(p);
	}
	protected void parse(String p) 
	{
		p = MCEUtil.safeString(p, "after");
		String[] arr = p.split(",");
		if(arr.length == 0)
			return;
			
		String v0 = arr[0].trim().toLowerCase();
		int typeIndex = 0;
		String type;
		
		//If there is no ASM Injection point parse the opp and return
		if(arr.length == 1 && (v0.equals("before") || v0.equals("after")))
		{
			this.opp = Opperation.get(v0);
			return;
		}
		else if(v0.isEmpty())
		{
			if(arr.length > 1)
				System.err.println("Maulformed Line! Opperation or Type not Found: '" + p + "'");
			return;
		}
			
		if(v0.startsWith("before"))
		{
			this.opp = Opperation.BEFORE;
			if(v0.startsWith("before:"))
			{
				type = v0.substring(7).trim();
			}
			else
			{
				type = arr.length > 1 ? (arr[1].trim().toLowerCase()) : (v0);
				typeIndex++;
			}
			}
		else if(v0.startsWith("after"))
		{
			if(v0.startsWith("after:"))
			{
				type = v0.substring(6).trim();
			}
			else
			{
				type = arr.length > 1 ? (arr[1].trim().toLowerCase()) : (v0);
				typeIndex++;
			}
		}
		else
			type = v0;
			
		try
		{
			boolean nf = type.endsWith("node");
			if(nf && type.equals("insnnode") || type.equals("insn"))
			{
				this.point = new InsnNode(OpcodeHelper.getOppcode(arr[typeIndex + 1]));
				this.type = InsnTypes.InsnNode;
			}
			else if(nf && type.equals("intinsnnode") || type.equals("intinsn"))
			{
				int oc = OpcodeHelper.getOppcode(arr[typeIndex + 1]);
				int val = 0;
				//parse the val based on it's type. Insn supports Byte, Short except for when the Opcode NEWARRAY is called then it supports int
				switch(oc)
				{
					case Opcodes.BIPUSH:
						val = MCEUtil.parseByte(arr[typeIndex + 2]);
					break;
					
					case Opcodes.SIPUSH:
						val = MCEUtil.parseShort(arr[typeIndex + 2]);
					break;
					
					default:
						val = MCEUtil.parseInt(arr[typeIndex + 2]);
					break;
				}
				this.point = new IntInsnNode(oc, val);
				this.type = InsnTypes.IntInsnNode;
			}
			else if(nf && type.equals("varinsnnode") || type.equals("varinsn"))
			{
				this.point = new VarInsnNode(OpcodeHelper.getOppcode(arr[typeIndex + 1]), MCEUtil.parseInt(arr[typeIndex + 2]));
				this.type = InsnTypes.VarInsnNode;
			}
			else if(nf && type.equals("jumpinsnnode") || type.equals("jumpinsn"))
			{
				this.point = new JumpInsnNode(OpcodeHelper.getOppcode(arr[typeIndex + 1]), new LabelNode());
				this.type = InsnTypes.JumpInsnNode;
			}
			else if(nf && type.equals("typeinsnnode") || type.equals("typeinsn"))
			{
				this.point = new TypeInsnNode(OpcodeHelper.getOppcode(arr[typeIndex + 1]), MCEUtil.parseString(arr[typeIndex + 2]));
				this.type = InsnTypes.TypeInsnNode;
			}
			else if(nf && type.equals("ldcinsnnode") || type.equals("ldcinsn"))
			{
				String ldc = p.substring(p.toLowerCase().indexOf("ldcinsnnode"));
				ldc = ldc.substring(ldc.indexOf(',') + 1).trim();
				
				//Handle EMPTY String
				if(ldc.isEmpty())
				{
					this.point = new LdcInsnNode("");
				}
				else
				{
					//They didn't specify string but it's expected to be a string
					if(ldc.charAt(0) == '"')
					{
						this.point = new LdcInsnNode(MCEUtil.parseString(ldc));
					}
					else
					{
						String[] ldc_arr = MCEUtil.splitFirst(ldc, ',');
						String ldc_type = ldc_arr[0].trim().toUpperCase();
						String ldc_val = MCEUtil.parseString(ldc_arr[1]);
						if(ldc_type.equals("INT") || ldc_type.equals("INTEGER"))
							this.point = new LdcInsnNode(new Integer(MCEUtil.parseInt(ldc_val)));
						else if(ldc_type.equals("LONG"))
							this.point = new LdcInsnNode(new Long(MCEUtil.parseLong(ldc_val)));
						else if(ldc_type.equals("FLOAT"))
							this.point = new LdcInsnNode(new Float(MCEUtil.parseFloat(ldc_val)));
						else if(ldc_type.equals("DOUBLE"))
							this.point = new LdcInsnNode(new Double(MCEUtil.parseDouble(ldc_val)));
						else if(ldc_type.equals("STR") || ldc_type.equals("STRING"))
							this.point = new LdcInsnNode(ldc_val);
						else
							System.err.println("INVALID LdcInsnNode Type: '" + ldc_type + "'. Valid: [STRING, INTEGER, LONG, FLOAT, DOUBLE]");
					}
				}
				if(this.point != null)
					this.type = InsnTypes.LdcInsnNode;
			}
			else if(nf && type.equals("fieldinsnnode") || type.equals("fieldinsn"))
			{
				this.point = new FieldInsnNode(OpcodeHelper.getOppcode(arr[typeIndex + 1]), MCEUtil.parseString(arr[typeIndex + 2]), MCEUtil.parseString(arr[typeIndex + 3]), MCEUtil.parseString(arr[typeIndex + 4]));
				this.type = InsnTypes.FieldInsnNode;
			}
			else if(nf && type.equals("methodinsnnode") || type.equals("methodinsn"))
			{
				this.point = new MethodInsnNode(OpcodeHelper.getOppcode(arr[typeIndex + 1]), MCEUtil.parseString(arr[typeIndex + 2]), MCEUtil.parseString(arr[typeIndex + 3]), MCEUtil.parseString(arr[typeIndex + 4]));
				this.type = InsnTypes.MethodInsnNode;
			}
			else if(nf && type.equals("labelnode") || type.equals("label"))
			{
				int lindex = MCEUtil.parseInt(arr[typeIndex + 1]);
				this.point = new MCEIndexLabel(lindex);
				this.occurrence = lindex;
				this.type = InsnTypes.LabelNode;
				this.shiftTo = ShiftTo.LABEL;
			}
			else if(type.startsWith("label:"))
			{
				int lindex = MCEUtil.parseInt(type.substring(6));
				this.point = new MCEIndexLabel(lindex);
				this.occurrence = lindex;
				this.type = InsnTypes.LabelNode;
				this.shiftTo = ShiftTo.LABEL;
			}
			else if(type.equals("opcode"))
			{
				this.point = new MCEOpcode(OpcodeHelper.getOppcode(arr[typeIndex + 1]));
				this.type = InsnTypes.Opcode;
			}
			else
			{
				if(type.startsWith("line") || type.startsWith("label"))
					System.err.println("Missing ':' on Line or Label Injection Point! Line: '" + p + "'");
				else if(OpcodeHelper.hasOpcode(type))
					System.err.println("Invalid Injection Point String: '" + p + "'\nType is Missing! It Must be one of these Types:[MethodInsnNode, FieldInsnNode, InsnNode, Opcode, IntInsnNode, LdcInsnNode, VarInsnNode, TypeInsnNode, JumpInsnNode, LabelNode, LineNumberNode, LINE:<int>, LABEL:<int> ]");
				else
					System.err.println("Unsupported AbstractInsnNode for ASM Injection Point! \"" + type.toUpperCase() + "\"");
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.err.println("Error while parsing Injection Point(Insertion Point):" + p);
			e.printStackTrace();
		}
	}
		
	@Override
	public String toString()
	{
		return this.opp + "," + this.point + ", Index:" + this.occurrence + ", shift:" + this.shift + ", shiftTo:" + this.shiftTo;
	}
}
package jredfox.mce.types;

public class DataTypeContainer {
	
	public DataType type;
	public boolean isArr;
	public int arrDim;
	public String desc;
	
	public DataTypeContainer(String d, boolean desc)
	{
		boolean arr = d.startsWith("[");
		if(arr)
		{
			this.isArr = true;
			this.arrDim = d.lastIndexOf('[') + 1;
			this.type = desc ? DataType.fromDesc(d.replace("[", "")) : DataType.get(d.replace("[", ""));
		}
		else
		{
			this.isArr = false;
			this.type = desc ? DataType.fromDesc(d) : DataType.get(d);
		}
		this.desc = this.getDesc();
	}
	
	public DataTypeContainer()
	{
		this.type = DataType.NULL;
	}
	
	public DataTypeContainer(DataType t, boolean arr, int dimCount)
	{
		this.type = t;
		this.isArr = arr;
		this.arrDim = dimCount;
	}
	
	protected String getDesc() 
	{
		if(!this.isArr)
			return this.type.desc;
		
		StringBuilder sb = new StringBuilder();
		for(int i=0; i < this.arrDim; i++)
			sb.append('[');
		sb.append(this.type.desc);
		return sb.toString();
	}

}

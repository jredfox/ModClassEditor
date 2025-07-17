package jredfox.mce.types;

public class DataTypeContainer {
	
	public DataType type;
	public boolean isArr;
	public int arrDim;
	public String desc;
	public boolean isNULL;
	
	public DataTypeContainer(String d, boolean desc)
	{
		if(d.isEmpty())
		{
			this.type = DataType.NULL;
			this.desc = this.getDesc();
			this.isNULL = true;
			return;
		}
		
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
		this.isNULL = this.type == DataType.NULL;
	}
	
	public DataTypeContainer()
	{
		this.type = DataType.NULL;
	}
	
	public DataTypeContainer(DataType t, boolean arr, int dimCount, String desc, boolean isNULL)
	{
		this.type = t;
		this.isArr = arr;
		this.arrDim = dimCount;
		this.desc = desc;
		this.isNULL = isNULL;
	}

	public DataTypeContainer copy() 
	{
		return new DataTypeContainer(this.type, this.isArr, this.arrDim, this.desc, this.isNULL);
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

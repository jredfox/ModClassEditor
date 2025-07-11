/*
 * $Id: JSONArray.java,v 1.1 2006/04/15 14:10:48 platform Exp $
 * Created on 2006-4-10
 */
package org.ralleytn.simple.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;

import org.ralleytn.simple.json.internal.Util;


/**
 * A JSON array. JSONObject supports java.util.List interface.
 * 
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public class JSONArray extends JSONArrayList implements ICopy {
	
	private static final long serialVersionUID = 3957988303675231981L;
	
	/**
	 * Constructs an empty {@linkplain JSONArray}.
	 * @since 1.0.0
	 */
	public JSONArray() {}
	
	/**
	 * Constructs a {@linkplain JSONArray} with the elements of the given {@linkplain Collection}.
	 * @param collection the {@linkplain Collection}
	 * @since 1.0.0
	 */
	public JSONArray(Collection<?> collection){
		super(collection);
	}
	
	public JSONArray(int capacity)
	{
		super(capacity);
	}
	
	/**
	 * Constructs a {@linkplain JSONArray} with the elements of the given array.
	 * @param array the array
	 * @param <T> the array type
	 * @since 1.0.0
	 */
	public <T>JSONArray(T[] array) {
		for(T element : array) {
			this.add(JSONUtil.getValidJsonValue(element));
		}
	}
	
	/**
	 * Constructs a {@linkplain JSONArray} with the elements of the given array.
	 * @param array the array
	 * @since 1.0.0
	 */
	public JSONArray(byte[] array) {
		
		for(byte element : array) {
			
			this.add(element);
		}
	}
	
	/**
	 * Constructs a {@linkplain JSONArray} with the elements of the given array.
	 * @param array the array
	 * @since 1.0.0
	 */
	public JSONArray(boolean[] array) {
		
		for(boolean element : array) {
			
			this.add(element);
		}
	}
	
	/**
	 * Constructs a {@linkplain JSONArray} with the elements of the given array.
	 * @param array the array
	 * @since 1.0.0
	 */
	public JSONArray(char[] array) {
		
		for(char element : array) {
			
			this.add(element);
		}
	}
	
	/**
	 * Constructs a {@linkplain JSONArray} with the elements of the given array.
	 * @param array the array
	 * @since 1.0.0
	 */
	public JSONArray(short[] array) {
		
		for(short element : array) {
			
			this.add(element);
		}
	}
	
	/**
	 * Constructs a {@linkplain JSONArray} with the elements of the given array.
	 * @param array the array
	 * @since 1.0.0
	 */
	public JSONArray(int[] array) {
		
		for(int element : array) {
			
			this.add(element);
		}
	}
	
	/**
	 * Constructs a {@linkplain JSONArray} with the elements of the given array.
	 * @param array the array
	 * @since 1.0.0
	 */
	public JSONArray(long[] array) {
		
		for(long element : array) {
			
			this.add(element);
		}
	}
	
	/**
	 * Constructs a {@linkplain JSONArray} with the elements of the given array.
	 * @param array the array
	 * @since 1.0.0
	 */
	public JSONArray(float[] array) {
		
		for(float element : array) {
			
			this.add(element);
		}
	}
	
	/**
	 * Constructs a {@linkplain JSONArray} with the elements of the given array.
	 * @param array the array
	 * @since 1.0.0
	 */
	public JSONArray(double[] array) {
		
		for(double element : array) {
			
			this.add(element);
		}
	}
	
	/**
	 * Constructs a {@linkplain JSONArray} from a JSON string.
	 * @param json the JSON string
	 * @throws JSONParseException if the JSON is invalid
	 * @since 1.0.0
	 */
	public JSONArray(String json) throws JSONParseException {
		
		this(new JSONParser().parseJSONArray(json));
	}
	
	/**
	 * Constructs a {@linkplain JSONArray} from JSON data read from a {@linkplain Reader}.
	 * @param reader the {@linkplain Reader}
	 * @throws JSONParseException if the JSON is invalid
	 * @throws IOException if an I/O error occurred
	 * @since 1.0.0
	 */
	public JSONArray(Reader reader) throws JSONParseException, IOException {
		
		this(new JSONParser().parseJSONArray(reader));
	}
	
	/**
	 * Writes this {@linkplain JSONArray} as a JSON string on the given {@linkplain Writer}.
	 * @param writer the {@linkplain Writer}
	 * @throws IOException if an I/O error occurred
	 * @since 1.0.0
	 */
	public void write(Writer writer) throws IOException {
		
		Util.write(this, writer);
	}

	/**
	 * Converts this {@linkplain JSONArray} to a JSON string.
	 * @return this {@linkplain JSONArray} as a JSON string
	 * @since 1.0.0
	 */
	@Override
	public String toString() 
	{
		StringWriter writer = null;
		try
		{	
			writer = new StringWriter();
			Util.write(this, writer);
			return writer.toString();	
		} 
		catch(IOException exception) 
		{
			exception.printStackTrace();
		}
		finally
		{
			Util.close(writer);
		}
		
		return null;
	}
	
	/**
	 * Merge Two JSONArray with JSONObject support
	 * @jredfox added merge
	 */
	public void merge(JSONArray other)
	{
		int tsize = this.size();
		for(int i=0; i< other.size(); i++)
		{
			Object otherIndex = other.get(i);
			if(i < tsize)
			{
				Object thisIndex = this.get(i);
				if(thisIndex instanceof JSONArray && otherIndex instanceof JSONArray)
				{
					((JSONArray)thisIndex).merge((JSONArray) otherIndex);
				}
				else if(thisIndex instanceof JSONObject && otherIndex instanceof JSONObject)
				{
					((JSONObject)thisIndex).merge((JSONObject) otherIndex);
				}
				else
					this.set(i, otherIndex instanceof ICopy ? ((ICopy) otherIndex).copy() : otherIndex);
			}
			else
				this.add(otherIndex instanceof ICopy ? ((ICopy) otherIndex).copy() : otherIndex);
		}
	}
	
	/**
	 * @jredfox copy of JSONArray and all of it's children
	 */
	@Override
	public JSONArray copy()
	{
		JSONArray arr = new JSONArray(this.size() + 3);
		for(int i=0;i<this.size();i++)
		{
			Object o = this.get(i);
			if(o instanceof ICopy)
				o = ((ICopy) o).copy();
			arr.add(o);
		}
		return arr;
	}
	
	/**
	 * @jredfox Fixed equals
	 */
	@Override
	public boolean equals(Object object) 
	{
		return object instanceof JSONArray && this.size() == ((JSONArray)object).size() && super.equals(object);
	}
	
	/**
	 * Converts this {@linkplain JSONArray} to a XML string.
	 * @param rootName name of the root element
	 * @return this JSON array as a XML string
	 * @since 1.1.0
	 */
	public String toXML(String rootName) {
		
		StringBuilder builder = new StringBuilder();
		builder.append('<');
		builder.append(rootName);
		builder.append(" length=");
		builder.append(this.size());
		builder.append('>');
		
		for(Object element : this) {

				   if(element instanceof JSONObject) {builder.append(((JSONObject)element).toXML("item"));
			} else if(element instanceof JSONArray) {builder.append(((JSONArray)element).toXML("item"));
			} else {
					
				builder.append("<item>");
				
				if(element != null) {
					
					builder.append(String.valueOf(element));
				}
				
				builder.append("</item>");
			}
		}
		
		builder.append("</");
		builder.append(rootName);
		builder.append('>');
		
		return builder.toString();
	}

}

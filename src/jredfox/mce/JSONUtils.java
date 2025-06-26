package jredfox.mce;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONFormatter;
import org.ralleytn.simple.json.JSONObject;
import org.ralleytn.simple.json.JSONParseException;
import org.ralleytn.simple.json.JSONParser;
import org.ralleytn.simple.json.internal.Util;

public class JSONUtils {
	
	public static JSONObject getJson(File f) 
	{
		//Create Directory if it doesn't exist
		if(!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		
		FileReader r = null;
		try 
		{
			r = new FileReader(f);
			return (JSONObject) jsonParser.get().parse(r);
		} 
		catch (IOException io) 
		{
			io.printStackTrace();
		}
		catch(JSONParseException e)
		{
			e.printStackTrace();
		}
		finally
		{
			Util.close(r);
		}
		return null;
	}
	
	public static JSONArray getJsonArray(File f)
	{
		//Create Directory if it doesn't exist
		if(!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		
		BufferedReader r = null;
		try 
		{
			r = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8) );
			return (JSONArray) jsonParser.get().parse(r);
		}
		catch(IOException io)
		{
			io.printStackTrace();
		}
		catch (JSONParseException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			Util.close(r);
		}
		return null;
	}
	
	/**
	 * Java 6 compliant thread local variable
	 */
	public static final ThreadLocal<JSONParser> jsonParser = new ThreadLocal<JSONParser>()
	{
		@Override
		public JSONParser initialValue() 
		{
			return new JSONParser();
		}
	};
	
	/**
	 * Saves a JSONObject to the disk
	 * @param json
	 * @param file
	 */
	public static void save(JSONObject json, File file)
	{
		save(toPrettyFormat(json.toString()), file, true);
	}
	
	/**
	 * Saves a JSONArray to the disk
	 * @param json
	 * @param file
	 */
	public static void save(JSONArray json, File file)
	{
		save(toPrettyFormat(json.toString()), file, true);
	}
	
    /**
	 * Convert a JSON string to freindly printed version
	*/
	public static String toPrettyFormat(String strJson) 
	{
		return new JSONFormatter().format(strJson);
	}
	
	/**
	 * Overwrites entire file default behavior no per line modification removal/addition
	 */
	public static void save(String str, File f, boolean utf8)
	{
		//Create Directory if it doesn't exist
		if(!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		
		BufferedWriter writer = null;
		try
		{
			if(!utf8)
			{
				writer = new BufferedWriter(new FileWriter(f));
			}
			else
			{
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),StandardCharsets.UTF_8 ) );
			}
			
			writer.write(str + "\r\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(writer != null)
			{
				try
				{
					writer.close();
				}
				catch(Exception e)
				{
					System.out.println("Unable to Close OutputStream this is bad");
				}
			}
		}
	}

}

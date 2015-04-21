package net.eai.session;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;

import net.eai.util.memcached.*;

public class SessionManager {
	
	
	public static LinkedHashMap<String,String> getTalkSession(String project,String custID)
	{
		MemcacheManagerForGwhalin   memcached = MemcacheManagerForGwhalin.getInstance();
		
		String conid = custID + "@" + project;	    	
    	LinkedHashMap<String,String> datamap =  (LinkedHashMap<String,String>) memcached.get(conid);	
		return datamap;		
	}
	
	public static void updateTalkSesstion(String project,String custID,LinkedHashMap<String,String> obj,int time)
	{
		MemcacheManagerForGwhalin   memcached = MemcacheManagerForGwhalin.getInstance();
		
		String conid = custID + "@" + project;	    	
    	memcached.add(conid, obj,time);
    	
    	
	}
	
	public static LinkedHashMap<String,String> getCustProfile(String project,String custID)
	{
		MemcacheManagerForGwhalin   memcached = MemcacheManagerForGwhalin.getInstance();

		String conid = custID + "@" + project + "_profile";	
		System.out.println("read custProfile:" + conid );    	
    	LinkedHashMap<String,String> datamap =  (LinkedHashMap<String,String>) memcached.get(conid);	

		Gson gson = new Gson();
		String backstr = gson.toJson(datamap);
		System.out.println("write data:" + backstr);
		
		return datamap;		
	}
	
	public static void updateCustProfile(String project,String custID,LinkedHashMap<String,String> obj,int time)
	{
		MemcacheManagerForGwhalin   memcached = MemcacheManagerForGwhalin.getInstance();
		
		String conid = custID + "@" + project + "_profile";	    

		System.out.println("write custProfile:" + conid); 
		
		LinkedHashMap<String,String> oldObj =  (LinkedHashMap<String,String>)memcached.get(conid);

		if(oldObj !=null)
		{
			Iterator iter = oldObj.entrySet().iterator();
    		while (iter.hasNext()) {
    			Map.Entry entry = (Map.Entry) iter.next();           			
    			
        		String key = entry.getKey().toString();
        		String val = entry.getValue().toString();    
        		obj.put(key, val);   	      		
    		}
		}
		
		Gson gson = new Gson();
		String backstr = gson.toJson(obj);
		System.out.println("write data:" + backstr);
		
    	memcached.add(conid, obj,time);
    	
    	
	}
	
}

package net.eai.dev.entitycg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import net.eai.dev.Entity;
import net.eai.dev.EntityAttribute;
import net.eai.dev.EntityOperation;
import net.eai.dev.OperationParameter;
import net.eai.dev.StarUmlObjectContainer;
import net.eai.dev.UMLDependency;
import net.eai.dev.ioUtil;

public class FormPageEntity {
	private Entity m_entity;
	private String templatePath;
	private HashMap<String,Entity> depends = new HashMap<String,Entity>();
	
	public FormPageEntity(Entity entity){
		m_entity = entity;
		templatePath = m_entity.getTemplatePath();		
	}
	
	public Entity getM_entity() {
		return m_entity;
	}
	public void setM_entity(Entity m_entity) {
		this.m_entity = m_entity;
	}
	public HashMap<String,Entity> getDepends() {
		return depends;
	}
	public void setDepends(HashMap<String,Entity> depends) {
		this.depends = depends;
	}


	static public void create()
	{
		
	}
	
	

	private LinkedHashMap<String,Entity> getDepends(Entity theEntity)
	{
		LinkedHashMap<String,Entity> dependEntities = new LinkedHashMap<String,Entity>() ;
		if(theEntity.getOwnedElements() != null)
		{
			for(UMLDependency depend:theEntity.getOwnedElements())
			{
				String targetID = depend.getTarget().get$ref();
				Entity entity = (Entity) StarUmlObjectContainer.getObject(targetID) ;
				if(entity != null)
				{
					dependEntities.put(entity.getName(), entity);
				}
			}
		}

		HashMap<String,Entity> depends = m_entity.getDepends();

		Iterator<Entry<String, Entity>> entityIter = depends.entrySet().iterator();
		while (entityIter.hasNext()) {
			Entry<String, Entity> entry =  entityIter.next();      
			
    		String key = entry.getKey();
    		Entity val = entry.getValue();
    		dependEntities.put(key, val);
    		
		}
		
		return dependEntities;
	}
	

	private Entity findEntity(String targetEntityName)
	{
		LinkedHashMap<String,Entity> dependEntities = getDepends(m_entity);
		Iterator<Entry<String, Entity>> ApiIter = dependEntities.entrySet().iterator();
		while (ApiIter.hasNext()) {
			Entry<String, Entity> entry =  ApiIter.next();      
			
    		String apiName = entry.getKey();
    		Entity apiObject = entry.getValue();


    		if(apiName.equals(targetEntityName))
    			return apiObject;
    		/*
    		if("Api".equals(apiObject.getStereotype()))
    		{

    			LinkedHashMap<String,Entity> apisDepends = getDepends(apiObject);
    			Iterator<Entry<String, Entity>> entityIter = apisDepends.entrySet().iterator();
    			while (entityIter.hasNext()) {
    				Entry<String, Entity> entityEntry =  entityIter.next();      
    				
    	    		String entityName = entityEntry.getKey();
    	    		Entity entityObject = entityEntry.getValue();
    	    		
    	    		if(entityName.equals(targetEntityName))
    	    			return entityObject;
    			}
    		}*/
		}
		
		return null;
		
	}
	


	private void genOnChooseFun(String chooseEntity,EntityAttribute val,LinkedHashMap<String,String> choosens,String parentID)
	{
		String attName = val.getName();
		
		String theExtendedAtt = attName.substring(attName.indexOf("."),attName.length());
		
		String complexName = attName.replace(".","-");
		
		if(!choosens.containsKey(attName + ":" + chooseEntity))
		{
			choosens.put(attName + ":" + chooseEntity, "");
		}
		
		String oneCode = choosens.get(attName + ":" + chooseEntity);		
		oneCode += "\r\n\t\t\t$(\"#" + parentID + "-" + complexName + "\").val(data.data" + theExtendedAtt + "Name);";
		
		choosens.put(attName + ":" + chooseEntity, oneCode);
		
	}
	
	
	private void genRenderChooseFun(String chooseEntity,EntityAttribute val,LinkedHashMap<String,String> choosens,String parentID)
	{
		
	}


	public String initEntityForm(EntityOperation oneOp,String parentID)
	{
		String rules = "";
		String messages = "";		
		
		List<OperationParameter> parameters = oneOp.getParameters();
		
		//first para is the Entity name
		
		String EntityName = parameters.get(0).getName();
		Entity theEntity = findEntity(EntityName);
		String owner = null;
		String ownerVari = null;
		String ownerVariType = "int";
		
		if(parameters.size() > 1)
			owner = parameters.get(1).getName();
		if(parameters.size() > 2)
			ownerVari = parameters.get(2).getName();
			
		//get all the attributes
		boolean hasDoc = false;
		String DivSetting = "";
		String renderChoose = "";
		String chooseNameSetting = "";
		LinkedHashMap<String,String> onChooseFuns = new LinkedHashMap<String,String> ();

		LinkedHashMap<String,String> renderChooseFuncs = new LinkedHashMap<String,String> ();
		LinkedHashMap<String,String> renderDictionaries = new LinkedHashMap<String,String> ();
		
		for(EntityAttribute val : theEntity.getAttributes())
		{ 	
			String attName = val.getName();
			String attType = val.getTypeStr();
			
			if(val.getExtraAttribute("聚集类") != null)
			{
				owner = val.getExtraAttribute("聚集类");
				ownerVari = attName;
				ownerVariType = attType;
			}
			
			if(val.getExtraAttribute("显示名") != null/*!"n/a".equals(val.getExtraAttribute("显示名"))*/)
    		{   
				String showName = attName;
				
					
				if(val.getExtraAttribute("显示名") != null)
				{
					showName = val.getExtraAttribute("显示名");
				}
    			hasDoc = true;
    			Entity typeEntity = theEntity.getDepends(attType);
    			
    			String dictionary = val.getExtraAttribute("字典");
    			
    			if(typeEntity == null && dictionary == null){
    				if(!attName.contains(".")){
    					if(attType.equals("Date"))
    						DivSetting += "\r\n\t\t\t$(\"#" + parentID + "-" + attName + "\").val(data.data." + attName + ".substr(0,10));";
    					else
            				DivSetting += "\r\n\t\t\t$(\"#" + parentID + "-" + attName + "\").val(data.data." + attName + ");";
    						
    				}
        			if("id".equals(attName.toLowerCase()))
        				DivSetting += "\r\n\t\t\t$(\"#" + parentID + "-" + attName + "\").attr(\"disabled\",\"disabled\");";
    			}
    			else{
        			DivSetting += "\r\n\t\t\t$(\"#" + parentID + "-" + attName + "\").val(data.data." + attName + ");";
        			DivSetting += "\r\n\t\t\t$(\"#" + parentID + "-" + attName + "-name\").val(data.data." + attName + "Name);";
        			
        			
        			if(val.getMultiplicity() != null && !val.getMultiplicity().equals(""))
        			{
                		DivSetting += "\r\n\t\t\t$(\"#" + parentID + "-" + attName + "-ID\").val(data.data." + attName + ");";
            			DivSetting += "\r\n\t\t\tchooseSelect(\"#" + parentID + "-" + attName + "\",data.data." + attName + ");";        				
        			}
        			else
        			{
                		DivSetting += "\r\n\t\t\t$(\"#" + parentID + "-" + attName + "-ID\").val(data.data." + attName + "ID);";
            			DivSetting += "\r\n\t\t\tchooseSelect(\"#" + parentID + "-" + attName + "\",data.data." + attName + "ID);";    
        				
        			}
        			
        			String rederKey;
        			LinkedHashMap<String,String> renderMap;
        			
        			if(dictionary == null){
        				rederKey = typeEntity.getName();
        				renderMap = renderChooseFuncs;
        			}
        			else{
        				rederKey = dictionary;
        				renderMap = renderDictionaries;
        			}
        			
        			if(!renderMap.containsKey(rederKey))
        			{            			
        				renderMap.put(rederKey, parentID + "-" + attName);
        			}
        			else
        			{
        				String value = renderMap.get(rederKey);
        				value += " " + parentID + "-" + attName;
        				renderMap.put(rederKey,value);
        			}

        			chooseNameSetting += "\r\n\t\t\t$(\"#" + parentID + "-" + attName + "-ID\").val($(\"#" + parentID + "-" + attName + "\").val());";
        			chooseNameSetting += "\r\n\t\t\t$(\"#" + parentID + "-" + attName + "-name\").val($(\"#" + parentID + "-" + attName + "\").find(\"option:selected\").text());";
                	
        			
    			}
    			
    			if(attName.contains("."))
				{
					String theChoosen = attName.substring(0,attName.indexOf("."));
					EntityAttribute theAtt = theEntity.findAttributeByName(theChoosen);					
					genOnChooseFun(theAtt.getTypeStr(),val,onChooseFuns,parentID);
					continue;
				}
    			
    			if(!rules.isEmpty()){
    				rules += ",";
    				messages += ",";
    			}
				
				rules += "\r\n\t\t\t" + attName + ": \"required\"";				
    			messages += "\r\n\t\t\t" + attName + ": \"请输入" + showName + "\"";
    		}
		}
		


		String renderJs;
		if(owner != null){
			renderJs = ioUtil.readFile(templatePath + "/jsTemplates/initOwnedEntityForm.js");
			
			renderJs = renderJs.replace("@ownerEntity@", owner);

			if("int".equals(ownerVariType))
				renderJs = renderJs.replace("@ownerVari@", ownerVari);
			else
				renderJs = renderJs.replace("@ownerVari@", ownerVari + "ID");
		}
		else
			renderJs = ioUtil.readFile(templatePath + "/jsTemplates/initEntityForm.js");
			
		
		


		//render choose
		String renderChooseFuncCode = "";
		Iterator<Entry<String, String>> renderChooseIter = renderChooseFuncs.entrySet().iterator();
		while (renderChooseIter.hasNext()) {
			Entry<String, String> entry =  renderChooseIter.next();   
			String key = entry.getKey();
			String val = entry.getValue();
			
			String[] locations = val.split(" ");
			
			String rederCode = "";
			for(String oneLoc:locations)
			{
				rederCode += "\r\n\t\t\t$(\"#" + oneLoc + "\").children().remove();";
				rederCode += "\r\n\t\t\t$(\"#" + oneLoc + "\").append(chooseData);";
				rederCode += "\r\n\t\t\tchooseSelect(\"#" + oneLoc + "\",$(\"#" + oneLoc + "-ID\").val());";	
				   			
    			
				rederCode += "\r\n\t\t\t$(\"#" + oneLoc + "\").chosen(); ";
			}
			
			String oneRederChooseFunc = ioUtil.readFile(templatePath + "/jsTemplates/renderChoose.js");
			oneRederChooseFunc = oneRederChooseFunc.replace("@Entity@", key);		
			oneRederChooseFunc = oneRederChooseFunc.replace("@rederCode@", rederCode);			
			
			renderChoose += "\r\n\trenderChoose" + key + "(\"page\");";
			
			renderChooseFuncCode += oneRederChooseFunc;
		}
		
		//render dictionary
		Iterator<Entry<String, String>> renderDicIter = renderDictionaries.entrySet().iterator();
		while (renderDicIter.hasNext()) {
			Entry<String, String> entry =  renderDicIter.next();   
			String key = entry.getKey();
			String val = entry.getValue();	
			
			renderChoose += "\r\n\trenderDicChoose(\"" + val + "\",\"" + key + "\");";
		}
		
		
		
		
		
		renderJs = renderJs.replace("@renderChooseFuncs@", renderChooseFuncCode);

		renderJs = renderJs.replace("@Entity@", EntityName);
		renderJs = renderJs.replace("@DivSetting@",DivSetting);	
		renderJs = renderJs.replace("@rules@", rules);
		renderJs = renderJs.replace("@chooseNameSetting@", chooseNameSetting);
		renderJs = renderJs.replace("@messages@", messages);
		

		
		Iterator<Entry<String, String>> onChooseIter = onChooseFuns.entrySet().iterator();
		while (onChooseIter.hasNext()) {
			Entry<String, String> entry =  onChooseIter.next();   
			String choose = entry.getKey();
			String rederCode = entry.getValue();

			String theChoosen = choose.substring(0,choose.indexOf("."));
			String theChooseEntity = choose.substring(choose.indexOf(":") + 1 ,choose.length());
			
			

			String onChooseJs = ioUtil.readFile(templatePath + "/jsTemplates/onChooseEntity.js");
			onChooseJs = onChooseJs.replace("@Choose@", theChoosen);
			onChooseJs = onChooseJs.replace("@Entity@", theChooseEntity);
			onChooseJs = onChooseJs.replace("@DivSetting@", rederCode);
			
			renderJs += onChooseJs;
			
			renderChoose += "\r\n\t\t\t$(\"#page-" + theChoosen + "\").attr(\"onchange\",\"onChoose" + theChoosen + "(this.value)\");";
		}
		

		renderJs = renderJs.replace("@renderChoose@", renderChoose);
			
		return renderJs;
	}
	
	
	
	private LinkedHashMap<String,String> getEleAtts(Entity ele)
	{
		LinkedHashMap<String,String> atts = new LinkedHashMap<String,String>();
		
		for(EntityAttribute val : ele.getAttributes())
		{ 
			atts.put(val.getName(), val.getDefaultValue());
		}
		
		return atts;
	}
	
}

package net.eai.dev.entitycg;

import net.eai.app.system.Common;
import net.eai.dev.*;
import net.eai.util.NameConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApiEntity {


	private String templatePath;
	private Entity m_entity;
	private HashMap<String,Entity> depends = new HashMap<String,Entity>();
	private List<UMLDependency> ownedElements;
	private String documentation;
	private String devPackage;

	private List<Entity> generatedConnectorEntities = new ArrayList<Entity>();
	

	private String getImportsFromDoc()
	{
		String imports = "";
		
		// imports from depends
		if(ownedElements != null)
		{
			for(UMLDependency depend:ownedElements)
			{
				String targetID = depend.getTarget().get$ref();
				Entity entity = (Entity) StarUmlObjectContainer.getObject(targetID) ;
				if(entity != null)
				{
					String pack = entity.getDevPackage();
					entity.setDevPackage(pack);
					if(!pack.equals(this.getDevPackage()))
					{
						imports += "import " + pack + "." + entity.getName() + ";\n";
					}
				}
			}
		}
		
		if(this.documentation != null)
		{
			String lines[] = this.documentation.split("\n");
			for(String line:lines)
			{
				if(line.startsWith("import"))
				{
					imports += line + "\n";
				}
			}
		}
		
		return imports;
		
	}
	
	public ApiEntity (Entity entity)
	{
		m_entity = entity;
	}
	
	
	private void createN2NPage(String path)
	{	
		
		
	}

	public void createEntityPages(String path)
	{		

		List<EntityOperation> ops = m_entity.getOperations();
		for(EntityOperation oneOp:ops)
		{			
			if(oneOp.getName().startsWith("manage"))
			{
				String entityName = oneOp.getName().replace("manage", "");
				Entity entityObj = (Entity) StarUmlObjectContainer.getObjectByName(entityName) ;
				if(entityObj != null)
				{
					
					entityObj.setTemplatePath(this.getTemplatePath());
					//generate edit page
					PageEntityFactory factory = new PageEntityFactory(entityObj,null);
					PageEntity editPage = factory.genEditPageEntity(null);
					String code = editPage.genCode("page",editPage);
					String packPath = devPackage.substring(devPackage.lastIndexOf(".") +1);

					ioUtil.writeFileWithExistCode(path + "/../WebContent/"  + packPath + "/" +  entityObj.getName() + "EditPage.html",code);

					//generate table page
					PageEntity tablePageEntity = factory.genTablePageEntity();
					String tablePagecode = tablePageEntity.genCode("page",tablePageEntity);

					ioUtil.writeFileWithExistCode(path + "/../WebContent/" + packPath + "/" + entityObj.getName() + "TablePage.html",tablePagecode);
					

					
					//generate entities' functional pages
					List<EntityOperation> funcOps = entityObj.getOperations();
					for(EntityOperation oneFuncOp:funcOps)
					{			
						if(oneFuncOp.getName().startsWith("listOwned"))
						{
							String ownedEntityName =  oneFuncOp.getName().substring("listOwned".length());
							Entity targetObj =(Entity) StarUmlObjectContainer.getObjectByName(ownedEntityName) ;
							if(targetObj != null)
							{
								PageEntityFactory ownedObjectPageFactory = new PageEntityFactory(targetObj,factory.getPagePackage());
								PageEntity ownedPage = ownedObjectPageFactory.genOwnedTablePageEntity(targetObj,entityObj.getName());
								String ownedPagecode = ownedPage.genCode("page");
								ioUtil.writeFileWithExistCode(
										path + "/../WebContent/" + packPath + "/" + 
										entityObj.getName() + "Owned" + 
										targetObj.getName() + "Page.html",ownedPagecode);
								
								PageEntity ownedEditPage = ownedObjectPageFactory.genEditPageEntity(ownedEntityName);
								String ownedEditPagecode = ownedEditPage.genCode("page");
								ioUtil.writeFileWithExistCode(
										path + "/../WebContent/" + packPath + "/" + 
										entityObj.getName() + "Owned" + 
										targetObj.getName() + "EditPage.html",ownedEditPagecode);
								


							}
							
						}
					}

				//factory.outputPageUmlJs(path + "/../");

					
				}
			}
		}
		
		
		
		
	}
	
	public String genCode(String packageName)
	{
		String code = "";
		String imports = "";

		imports = getImportsFromDoc();
		
		String actionCode = ioUtil.readFile(templatePath + "/EntityManageAction.java"); 
		actionCode = actionCode.replace("@entity@", m_entity.getName());		
		actionCode = actionCode.replace("@package@", packageName);		

		String OpCode = genOperations();

		code =  actionCode.replace("@targetEntityCodes@", OpCode);
		code = code.replace("@imports@", imports);
		
			
		return code;
	}
	
	
	
	
	public String genStruts(String path,String packageName)
	{

		
		
		String strutsCode = "\t<package name=\"" + m_entity.getName() +"\" extends=\"struts-default\">\r\n";
		List<EntityOperation> ops = m_entity.getOperations();		
		for(EntityOperation oneOp:ops)
		{
			OperationParameter ret =  oneOp.getReturn();
			if(oneOp.getName().startsWith("manage"))
			{
				
				String entityName = oneOp.getName().replace("manage", "");
				String manageStruts = ioUtil.readFile(templatePath + "/manageStruts.xml");
				manageStruts = manageStruts.replace("@Entity@", entityName);
				manageStruts = manageStruts.replace("@ApiEntity@",  m_entity.getName());
				manageStruts = manageStruts.replace("@packageName@", packageName);
				strutsCode += manageStruts;
			}
			else
			{
				
				String opName = oneOp.getName();
				
				strutsCode += 
						"\t\t<action name=\"" + opName + "\" class=\"" + packageName + "." + m_entity.getName()
						+"\" method=\"" + opName + "Api\"></action>\r\n";
				
			}
		}
		
		
		for(Entity one:generatedConnectorEntities)
		{
			PersistEntity per = new PersistEntity(one);

			String entityName = per.getBaseEntity().getName();
			String manageStruts = ioUtil.readFile(templatePath + "/multipleAttStruts.xml");
			manageStruts = manageStruts.replace("@Entity@", entityName);
			manageStruts = manageStruts.replace("@ApiEntity@",  m_entity.getName());
			manageStruts = manageStruts.replace("@packageName@", packageName);
			strutsCode += manageStruts;
		}
		
		strutsCode += "\t</package>\r\n\r\n";
		
		return strutsCode;
	
	}
	
	
	
	public void genContracts(String path,String packageName)
	{
		String code = "";

		String imports = "";

		imports = getImportsFromDoc();
		
		
		
		if( m_entity.getDocumentation() != null)
			code = m_entity.getDocumentation() ;
				
		List<EntityOperation> ops = m_entity.getOperations();
		String getListResponseContract = ioUtil.readFile(templatePath + "/contractEntities/listEntitiesResponse.java"); 
		String getRespnseContract = ioUtil.readFile(templatePath + "/contractEntities/getEntityResponse.java"); 
		getListResponseContract = getListResponseContract.replace("@imports@", imports);
		getRespnseContract = getRespnseContract.replace("@imports@", imports);
		
		for(EntityOperation oneOp:ops)
		{
			if(oneOp.getName().startsWith("manage"))
			{
				String entityName = oneOp.getName().replace("manage", "");
				Entity entityObj = (Entity) StarUmlObjectContainer.getObjectByName(entityName) ;
				if(entityObj != null)
				{					
					String listContractCode = getListResponseContract.replace("@package@", packageName);
					listContractCode = listContractCode.replace("@targetEntity@", entityName);
		    		ioUtil.writeFile(path + "/List" + entityName + "Response.java",listContractCode);

		    		String getContractCode = getRespnseContract.replace("@package@", packageName);
		    		getContractCode = getContractCode.replace("@targetEntity@", entityName);
		    		ioUtil.writeFile(path + "/Get" + entityName + "Response.java",getContractCode);
				}				
			}
			else
			{
				OperationParameter ret =  oneOp.getReturn();
				
				String opName = oneOp.getName().substring(0,1).toUpperCase()
						+ oneOp.getName().substring(1);

	    		String customContractCode = null;
	    		if(ret == null){
	    			/*
					String voidRespnseContract = ioUtil.readFile(templatePath + "/contractEntities/voidResponse.java"); 
					voidRespnseContract = voidRespnseContract.replace("@imports@", imports);
					customContractCode = voidRespnseContract.replace("@package@", packageName);
		    		customContractCode = customContractCode.replace("@operation@", opName);*/
	    		}
	    		else
	    		{
					String customRespnseContract = ioUtil.readFile(templatePath + "/contractEntities/dataResponse.java"); 
					customRespnseContract = customRespnseContract.replace("@imports@", imports);
					customContractCode = customRespnseContract.replace("@package@", packageName);
		    		customContractCode = customContractCode.replace("@operation@", opName);
		    		customContractCode = customContractCode.replace("@datatype@", ret.getTypeStr());
		    		ioUtil.writeFile(path + "/" + opName + "Response.java",customContractCode);
	    		}
				
			
			}
		}
		

		for(Entity one:generatedConnectorEntities)
		{
			PersistEntity per = new PersistEntity(one);

    		String getContractCode = getListResponseContract.replace("@package@", packageName);
    		getContractCode = getContractCode.replace("@targetEntity@",  per.getBaseEntity().getName());
    		ioUtil.writeFile(path + "/List" + per.getBaseEntity().getName() + "Response.java",getContractCode);
		}
		
	}

	private String genTableDivAction(Entity entity,String divActionCode)
	{
		String tableHead = "";
		String tableData = "";
		boolean hasDoc = false;
		
		for(EntityAttribute val : entity.getAttributes())
		{ 	
			String attName = val.getName();
    		if(val.getDocumentation() != null)
    		{
    			hasDoc = true;
    			tableHead += "<th>" + val.getDocumentation() + "</th>";
    			tableData += "\t\t\t\tresDiv += \"<td>\" + one." + 
    					"get" + attName.toUpperCase().substring(0, 1) + attName.substring(1,attName.length()) + "()"
    					+ " + \"</td>\";\r\n";
    		}
		}
		
		if(hasDoc == false)
			return "";

		divActionCode = divActionCode.replace("@tableHead@", tableHead);
		divActionCode = divActionCode.replace("@tableData@", tableData);
		
		return divActionCode;
	}
	

	@SuppressWarnings("rawtypes")
	private String genEditDivAction(Entity entity,String divActionCode)
	{
		String dataHead = "";
		String divData = "";
		String dataButtom = "";
		

		
		for(EntityAttribute val : entity.getAttributes())
		{ 	
			String attName = val.getName();
    		if(val.getDocumentation() != null)
    		{
    			String dataType = "shortStrDataEdit";
    			if(val.getStereotype() != null && !val.getStereotype().equals(""))
    				dataType = val.getStereotype();
    			
    			String edit = ioUtil.readFile(templatePath + "/htmlElements/hplus/" + dataType + ".html"); 

    			edit = edit.replace("\"", "\\\"");
    			edit = edit.replace("\r\n", "\" + \r\n \t\t\t\t\"");
    			edit = edit.replace("@attName@", attName);
    			edit = edit.replace("@attData@", "\" + selectResult.get" +
    						attName.toUpperCase().substring(0, 1) + attName.substring(1,attName.length()) + "()" +
    						" + \"");
    			
    			edit = "\t\t\tresDiv += \"" + edit + "\";\r\n";	
    			
    			divData += edit;
    		}
    		
		}
		

		divActionCode = divActionCode.replace("@dataHead@", dataHead);
		divActionCode = divActionCode.replace("@divData@", divData);
		divActionCode = divActionCode.replace("@dataButton@", dataButtom);
		
		return divActionCode;
	}
	
	
	public String genMultipleAttEntitieSql()
	{
		String res = "";

		for(Entity one:generatedConnectorEntities)
		{
			PersistEntity per = new PersistEntity(one);
			res += per.genIbaitsMapper();
		}
		
		
		return res;
	}

	
	public void genMultipleAttEntitieCode(String path)
	{
		for(Entity one:generatedConnectorEntities)
		{
			PersistEntity per = new PersistEntity(one);
			per.genCode(path);

			//zhang.yf	暂时不需要 多选entity的生成
			/*
			PersistEntityDotNet perDN = new PersistEntityDotNet(one);
			perDN.genEntity(path);
			perDN.genControl(path);
			perDN.genMapper(path);
			perDN.genResponse(path);
			*/
		}
		
	}

	
	private String createMultipleAttributeEntities(PersistEntity targetPersistEntity)
	{
		String res = "";
		Entity target = targetPersistEntity.getBaseEntity();
		for(EntityAttribute val : target.getAttributes())
		{ 	
    		String attName = val.getName();
    		String attType = val.getTypeStr();

    	

    		if(val.getMultiplicity() == null || val.getMultiplicity().equals(""))
    		{
    			if(!"数据表".equals(val.getExtraAttribute("多对多实现")))
    				continue;
    		}
    		
    		Entity typeEntity = target.getDepends(attType);
			Entity connectBaseEntity = new Entity();   
			connectBaseEntity.setDevPackage(this.devPackage);
			connectBaseEntity.setTemplatePath(templatePath);
			connectBaseEntity.setName(target.getName() + NameConverter.toUpperStartName(attName) + "Item");
    		

    		String line = "";
    		if(typeEntity == null)
    		{        
    		}
    		else
    		{ 	
        		//create the connector entity
    			EntityAttribute<String> valueAtt = new EntityAttribute<String>(typeEntity);
    			valueAtt.setName(NameConverter.toUpperStartName(typeEntity.getName()));
    			valueAtt.setType(typeEntity.getName());
    			connectBaseEntity.addAttribute(valueAtt);
    			
    			EntityAttribute<String> ownerAtt = new EntityAttribute<String>(target);
    			ownerAtt.setName(NameConverter.toUpperStartName(target.getName()));
    			ownerAtt.setType("int");
    			connectBaseEntity.addAttribute(ownerAtt);

    			connectBaseEntity.addDepend(typeEntity.getName(), typeEntity); 
    			connectBaseEntity.addDepend(target.getName(), target); 
    			
    			connectBaseEntity.setStereotype("Entity");
    			
    			String actionCode = ioUtil.readFile(templatePath + "/multipleAttAction.java"); 
    			PersistEntity persist = new PersistEntity(connectBaseEntity);
    			
    			actionCode = actionCode.replace("@connectEntity@", connectBaseEntity.getName());
    			actionCode = actionCode.replace("@ownerEntity@", NameConverter.toUpperStartName(target.getName()));
    			actionCode = actionCode.replace("@targetEntity@", NameConverter.toUpperStartName(typeEntity.getName()));
    			res += actionCode;
        		
    		}
    		

    		generatedConnectorEntities.add(connectBaseEntity);
    		
    		
		}
		
		
		return res;
	}
	

	

	private String genFilterParasCodes(Entity target)
	{
		String res = "";
		
		
		for(EntityAttribute val : target.getAttributes())
		{ 	
    		String attName = val.getName();
    		String attType = val.getTypeStr();
    		    		
    		if(attName.contains("."))
    			continue;
    		
    		if(val.getExtraAttribute("搜索") == null)
    			continue;
    		
    		Entity typeEntity = target.getDepends(attType);
    		
    			
    		if(typeEntity == null)
    		{      
        		res += attName;
    		}
    		else
    		{
        		res += attName + "ID";
    		}
    		
			res += ",";
		}
		
		return res;		
	}
	

	private String genFilterParasFromRequestCodes(Entity target)
	{
		String res = "";
		
		
		for(EntityAttribute val : target.getAttributes())
		{ 	
    		String attName = val.getName();
    		String attType = val.getTypeStr();
    		    		
    		if(attName.contains("."))
    			continue;
    		
    		if(val.getExtraAttribute("搜索") == null)
    			continue;
    		
    		Entity typeEntity = target.getDepends(attType);
    		
    			
    		if(typeEntity == null)
    		{      
        		res += "\r\n\t\tString " + attName + " = request.getParameter(\"" + attName + "\");";
    		}
    		else
    		{
    			res += "\r\n\t\tString " + attName + "ID = request.getParameter(\"" + attName + "\");";
    		}
    	
		}
		
		return res;		
	}
	
	private String genGetSaveEntityCodes(Entity target)
	{
		String res = "";
		for(EntityAttribute val : target.getAttributes())
		{ 	
    		String attName = val.getName();
    		String attType = val.getTypeStr();
    		
    		if("id".equals(attName.toLowerCase()))
    		{
    			continue;
    		}
    		
    		if(attName.contains("."))
    			continue;

    		String upperAttName = attName.toUpperCase().substring(0, 1) 
    					+  attName.substring(1,attName.length());
    		
    		Entity typeEntity = target.getDepends(attType);
    		String line = "";
    		if(typeEntity == null)
    		{                		
    			
    			if(attType.equals("int"))
    				line = "\r\n\t\t\tselectResult.set" + upperAttName + "(Common.getIntFromString(request.getParameter(\"" + attName + "\")));";
    			else if(attType.equals("double"))	
    				line = "\r\n\t\t\tselectResult.set" + upperAttName + "(Double.valueOf(request.getParameter(\"" + attName + "\")));";
    			else if(attType.equals("Date"))
    				line = "\r\n\t\t\tselectResult.set" + upperAttName + "(bartDateFormat.parse(request.getParameter(\"" + attName + "\")));";
    			else
        			line = "\r\n\t\t\tselectResult.set" + upperAttName + "(request.getParameter(\"" + attName + "\"));";	
    			
        		res += line;
    		}
    		else
    		{

        		if(val.getMultiplicity() != null && !val.getMultiplicity().equals(""))
        			line = "\r\n\t\t\tselectResult.set" + upperAttName + "(request.getParameter(\"" + attName + "\"));";
        		else
        			line = "\r\n\t\t\tselectResult.set" + upperAttName + "ID(Common.getIntFromString(request.getParameter(\"" + attName + "ID\")));";	
        		res += line;
    		}
    		
    		
		}
		
		return res;
	}

	
	
	
	
	
	private String genOperations()
	{
		String code = "";	
		
	
		List<EntityOperation> ops = m_entity.getOperations();
		String divActionCode = "";// ioUtil.readFile(templatePath + "/manageEntityDiv.java"); 
		String normalCode = ioUtil.readFile(templatePath + "/customApiOperation.java"); 
		for(EntityOperation oneOp:ops)
		{

			String commentCode = "";
			if(oneOp.getDocumentation() != null)
			{
				String lines[] = oneOp.getDocumentation().split("\n");
				for(String line:lines)
				{
					commentCode += "\t\t\t//" + line + "\n";
				}			
			}
			
			if(oneOp.getName().startsWith("manage"))
			{
				String entityName = oneOp.getName().replace("manage", "");
				String divActions = divActionCode;
				Entity entityObj = (Entity) StarUmlObjectContainer.getObjectByName(entityName) ;
				if(entityObj != null)
				{
					String actionCode = ioUtil.readFile(templatePath + "/targetEntityAction.java"); 
					PersistEntity persist = new PersistEntity(entityObj);
					
					code +=  createMultipleAttributeEntities(persist);
					
					divActions = genTableDivAction(entityObj,divActions);
					divActions = genEditDivAction(entityObj,divActions);
					divActions = divActions.replace("@targetEntity@", entityName);
					code += divActions;
					

					String filterParas = genFilterParasCodes(entityObj);				
					actionCode = actionCode.replace("@filterParas@", filterParas);
					

					String filterParasFromResuest = genFilterParasFromRequestCodes(entityObj);				
					actionCode = actionCode.replace("@filterParasFromRequest@", filterParasFromResuest);
					
					
					
					String attAssignment = genGetSaveEntityCodes(entityObj);
				
					actionCode = actionCode.replace("@entityAssignment@", attAssignment);
					code += actionCode.replace("@targetEntity@", entityName);
				}
			}
			else
			{
				String paraCode = "";
				if(oneOp.getParameters() != null)
				{
					for(OperationParameter onePara:oneOp.getParameters())
					{
						if(!"return".equals(onePara.getDirection())){
							String type = onePara.getTypeStr();
							String name = onePara.getName();	
							
							if("int".equals(type))
								paraCode += "\r\n\t\tint " + name + " = Integer.valueOf(request.getParameter(\"" + name + "\"));";
							else if("double".equals(type))
								paraCode += "\r\n\t\tdouble " + name + " = Double.valueOf(request.getParameter(\"" + name + "\"));\r\n";							
							else
								paraCode += "\r\n\t\tString " + name + " = request.getParameter(\"" + name + "\");"; 
						}					
					}	
				}
				String opName = oneOp.getName().substring(0,1).toUpperCase()
						+ oneOp.getName().substring(1);
				
				if(oneOp.getReturn() == null){
					code += normalCode.replace("@operation@", oneOp.getName())
							  .replace("@document@",commentCode)
							  .replace("@paras@", paraCode)
							  .replace("@OpResponse@","AjaxBase");
				}
				else{
					code += normalCode.replace("@operation@", oneOp.getName())
							  .replace("@document@",commentCode)
							  .replace("@paras@", paraCode)
							  .replace("@OpResponse@",opName);					
				}
			
				
			}
		}
			
		return code;
		 
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public List<UMLDependency> getOwnedElements() {
		return ownedElements;
	}

	public void setOwnedElements(List<UMLDependency> ownedElements) {
		this.ownedElements = ownedElements;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getDevPackage() {
		return devPackage;
	}

	public void setDevPackage(String devPackage) {
		this.devPackage = devPackage;
	}
	
}

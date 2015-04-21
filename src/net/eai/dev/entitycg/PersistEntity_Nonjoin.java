package net.eai.dev.entitycg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import net.eai.dev.Entity;
import net.eai.dev.EntityAttribute;
import net.eai.dev.EntityOperation;
import net.eai.dev.OperationParameter;
import net.eai.dev.StarUmlObjectContainer;
import net.eai.dev.UMLDependency;
import net.eai.dev.ioUtil;

public class PersistEntity_Nonjoin {


	private String templatePath;
	private Entity m_entity;
	private List<UMLDependency> ownedElements;
	private String documentation;
	private String devPackage;
	
	public PersistEntity_Nonjoin(Entity entity)
	{
		m_entity = entity;
		templatePath = m_entity.getTemplatePath();
	}
	
	public Entity getBaseEntity()
	{
		return m_entity;
	}
	
	private String genPersistentEntityOperations(EntityOperation op)
	{
		String opDefines = "";
		String paraDef = "";
		String paraVal = "";
		
		List<OperationParameter> paras =  op.getParameters();				
		List<OperationParameter> packParas = new ArrayList<OperationParameter>();
		
		
		
		//normal operations
		String condition = "\"where ";
		
		if(op.getName().equals("select"))
		{
			// case of this operation is "getInstances"
			
			if(paras != null)
			{
				for(OperationParameter onePara : paras){			
	    			if(!"return".equals(onePara.getDirection())){
	    				String typeName = onePara.getTypeStr();   
	    				if(typeName.equals("String") || typeName.equals("int") || typeName.equals("double"))
	    				{
	    					if(!condition.equals("\"where "))
	    						condition += " and ";
	    					if(!typeName.equals("String"))
	    						condition += onePara.getName() + " = \" + " + onePara.getName() + " + \"";
	    					else
	    						condition += onePara.getName() + " = '\" + " + onePara.getName() + " + \"'";
	    						
	        				if(!"".equals(paraDef))
	        					paraDef += ",";
	        				paraDef += typeName + " " + onePara.getName();        				

	        				if(!"".equals(paraVal))
	        					paraVal += ",";
	        				paraVal += onePara.getName();
	    				}
	    			}
	    		}
				
				condition += "\"";
			}
			
			
			String getInstancesFunc = ioUtil.readFile(templatePath + "/getInstances.java"); 
			getInstancesFunc = getInstancesFunc.replace("@parasDef@", paraDef);
			getInstancesFunc = getInstancesFunc.replace("@entity@", m_entity.getName());
			getInstancesFunc = getInstancesFunc.replace("@condition@", condition);
			opDefines += getInstancesFunc;
		}
		
		return opDefines;
	}
	
	
	
	
	public void genCode(String path)
	{
		
		String iBatisAttDefines = "";
		String iBatisAttGetSet = "";		

		String contractAttDefines = "";
		String contractAttGetSet = "";
		
		String opDefines = "";
		String imports = "";
		
	//	Entity persistEntity = new Entity();
		
		imports = m_entity.getImports();
		
		boolean hasID = false;		
		// attributes defines & setters getters
		for(EntityAttribute val : m_entity.getAttributes())
		{ 	
    		String attName = val.getName();
    		String attType = val.getTypeStr();
    		String visibility = "public";
    		
    		if(attName != null && attName.contains("."))
    		{
    			continue;
    		}

    		if(val.getVisibility() != null)
    			visibility = val.getVisibility();
    		
    		if("id".equals(attName.toLowerCase())){
    			attName = "id";
    			hasID = true;
    		}
    		
    		if("".equals(attType) || attType == null)
    			attType = "String";

    		if( val.getMultiplicity() !=  null && !val.getMultiplicity().equals(""))
    		{
    			if("数据表".equals(val.getExtraAttribute("多对多实现")))
    				continue;
    			else 
    				attType = "String";
    		}
    		
    		String normalGetSet = 
					"\r\n\tpublic " + attType + " get" + attName.toUpperCase().substring(0, 1) + attName.substring(1,attName.length()) + "()"
    				+"\r\n\t{\r\n\t\treturn " + attName + ";\r\n\t}\r\n"
					+ "\r\n\tpublic void set" + attName.toUpperCase().substring(0, 1) + attName.substring(1,attName.length())
    				+ "(" + attType + " " + attName + ")"
    				+"\r\n\t{\r\n\t\tthis." + attName + " = " + attName + ";\r\n\t}\r\n";
    		

    		//contract act normal
    		contractAttDefines += "\tprivate " + attType + " " + attName + ";\r\n";
			contractAttGetSet += normalGetSet;
			
			//complex type
    		Entity typeEntity = m_entity.getDepends(attType);
    		if(typeEntity == null)
    		{
    			iBatisAttDefines += "\tprivate " + attType + " " + attName + ";\r\n";    			
    			iBatisAttGetSet += normalGetSet;
    		}
    		else
    		{
    			iBatisAttDefines += "\tprivate int " + attName + "ID;\r\n";		
    			iBatisAttDefines += "\tprivate String " + attName + "Name;\r\n";		
    			iBatisAttGetSet += 
    							//getID
    							"\r\n\tpublic int" + " get" + attName.toUpperCase().substring(0, 1) + attName.substring(1,attName.length()) + "ID()"
    		    				+"\r\n\t{\r\n\t\treturn " + attName + "ID;\r\n\t}\r\n"
    		    				//setID
    							+ "\r\n\tpublic void set" + attName.toUpperCase().substring(0, 1) + attName.substring(1,attName.length())
    		    				+ "ID(int " + attName + "ID)"
    		    				+"\r\n\t{\r\n\t\tthis." + attName + "ID = " + attName + "ID;\r\n\t}\r\n" + 
    		    				//getName
								"\r\n\tpublic String" + " get" + attName.toUpperCase().substring(0, 1) + attName.substring(1,attName.length()) + "Name()"
								+"\r\n\t{\r\n\t\treturn " + attName + "Name;\r\n\t}\r\n"
								//setName
								+ "\r\n\tpublic void set" + attName.toUpperCase().substring(0, 1) + attName.substring(1,attName.length())
								+ "Name(String " + attName + "Name)"
								+"\r\n\t{\r\n\t\tthis." + attName + "Name = " + attName + "Name;\r\n\t}\r\n";
    		}
    			
		}
		

		if(hasID == false )
		{
			iBatisAttDefines +=  "\tprivate int id;\r\n";
			iBatisAttGetSet += "\r\n\tpublic int getId()\r\n\t{\r\n\t\treturn id;\r\n\t}\r\n";    		
			iBatisAttGetSet += "\r\n\tpublic void setId(int id)\r\n\t{\r\n\t\tthis.id = id;\r\n\t}\r\n";
		}
		
		// operations
		for(EntityOperation val : m_entity.getOperations())
		{ 	
    		// generator operation content according to known operation name
    		String persistentEntityOperationCode = genPersistentEntityOperations(val);    	
    		opDefines += persistentEntityOperationCode;
		}
		
		String persistClassCode = "";

		String createTableFunc = ioUtil.readFile(templatePath + "/entityDataAccess.java"); 
		createTableFunc = createTableFunc.replace("@entity@", m_entity.getName());		
		opDefines += createTableFunc;
		
		String getInstances = genConditionalSelectCode();
		opDefines += getInstances;

		String code = "";
		String content = iBatisAttDefines + "\r\n" + iBatisAttGetSet  + "\r\n" +  opDefines + "\r\n";
		code = ioUtil.readFile(templatePath + "/persistentEntity.java"); 
		code = code.replace("@imports@", imports);
		code = code.replace("@entity@", m_entity.getName());
		code = code.replace("@content@", content);
		
		
		code = "package " + m_entity.getDevPackage() + ";\r\n\r\n" + code;
	
		String devPackPath = m_entity.getDevPackage().replace(".", "/");

		ioUtil.writeFileWithExistCode(path + "/" + devPackPath + "/" + m_entity.getName() + ".java",code);	
	
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templaftePath) {
		this.templatePath = templaftePath;
	}
	
	

	private String genConditionalSelectCode()
	{
		String condition = "";
		String parasDef = "";
		boolean isComplexTable = false;
		for(EntityAttribute val : m_entity.getAttributes())
		{ 	
    		String attType = val.getTypeStr();
    		
    		if(val.getMultiplicity() == null && !"".equals(val))
    		{
    			Entity atttypeEntity = m_entity.getDepends(attType);

    			if(atttypeEntity != null )
    			{
    				isComplexTable = true;
    				break;
    			}
    		}			
		}
		
		boolean isFirst = true;

		String entityFilterCode = ioUtil.readFile(templatePath + "/EntityFilter.java"); 
		
		for(EntityAttribute val : m_entity.getAttributes())
		{ 	
    		String attName = val.getName();
    		String attType = val.getTypeStr();
    		String columnName = getColumnName(attName);
    		    		
    		if(attName.contains("."))
    			continue;
    		
    		if(val.getExtraAttribute("搜索") == null)
    			continue;

    		String upperAttName = attName.toUpperCase().substring(0, 1) 
    					+  attName.substring(1,attName.length());
    		
    		Entity typeEntity = m_entity.getDepends(attType);

			String paraFilter = entityFilterCode;
    		
    			
    		if(typeEntity == null)
    		{                	
    			if(attType.equals("String")){
            		parasDef += "String " + attName;	
        			paraFilter = paraFilter.replace("@paraValue@", "\"'\" + " + attName + "+ \"'\"");
    			}
    			else{
            		parasDef += "String " + attName;
        			paraFilter = paraFilter.replace("@paraValue@", attName);
    			}

    			paraFilter = paraFilter.replace("@para@", attName);
    			paraFilter = paraFilter.replace("@paraColumn@", attName);
    		}
    		else
    		{
        		if(val.getMultiplicity() != null && !val.getMultiplicity().equals("")){
        			paraFilter = paraFilter.replace("@paraValue@", "\"'\" + " + attName + "ID + \"'\"");
        		}
        		else{
        			paraFilter = paraFilter.replace("@paraValue@", attName + "ID");
        		}

    			paraFilter = paraFilter.replace("@para@", attName+ "ID");
    			paraFilter = paraFilter.replace("@paraColumn@", attName+ "ID");
        		
        		parasDef += "String " + attName+ "ID";
    		}

    		condition += paraFilter;
			parasDef += ",";

			isFirst = false;    		
		}
		
		
		if(!"".equals(condition))
		{
			if(isComplexTable)
				condition += "\r\n\t\t\tif(!\"\".equals(filtercondition)) filtercondition = \" and \" + filtercondition;";
			else
				condition += "\r\n\t\t\tif(!\"\".equals(filtercondition)) filtercondition = \" where \" + filtercondition;";
			
		}
		
		
		String code = "";
		code = ioUtil.readFile(templatePath + "/getInstances.java"); 
		code = code.replace("@entity@", m_entity.getName());
		code = code.replace("@filtercondition@", condition);
		code = code.replace("@filterPparas@", parasDef);
		
		
		
		
		return code;
	}
	

	public String genIbaitsMapper()
	{
		String daoCode;
		
		//generate insert sql
		String selectTarget = "";
		String paras = "";
		String values = "";
		String sets = "";
		String joinCode = "";
		String columnDef = "`ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键'";

		String tableName = getTableName(m_entity);
		String complexTableNames = getTableName(m_entity) + " as " + m_entity.getName();;
		
		boolean hasComplexData = false;
		
		for(EntityAttribute val : m_entity.getAttributes())
		{ 	
    		String attName = val.getName(); 
    		String attType = val.getTypeStr();
    		
    		String columnName = attName;//getColumnName(attName);
    		
    		if("id".equals(attName.toLowerCase()))
    			continue;

    		String dictionary = val.getExtraAttribute("字典");
    		
    		if(val.getMultiplicity() !=  null && !val.getMultiplicity().equals(""))
    		{
    			if("数据表".equals(val.getExtraAttribute("多对多实现")))
    				continue;
    			else 
    				attType = "String";
    		}
    		
    		if(attName.contains("."))
    			continue;
    		
    		String oneTarget = m_entity.getName() + "." + columnName + " as '" + attName + "' ";
    		    		
    	
			if(attType.equals("String"))
    			columnDef += ",`" + columnName + "` varchar(100)";
    		else if(attType.equals("int"))
    			columnDef += ",`" + columnName + "` bigint(20) ";
    		else if(attType.equals("Date"))
    			columnDef += ",`" + columnName + "` datetime ";
    		else if(attType.equals("double"))
    			columnDef += ",`" + columnName + "` DOUBLE ";
    		else
    		{
    			Entity typeEntity = m_entity.getDepends(attType);
        		if(typeEntity != null)
        		{

        			if(hasComplexData == false)
        			{
        				hasComplexData = true;
        			}
        			
        			complexTableNames +=   "," + getTableName(typeEntity) + " as " + attName + "_" + typeEntity.getName();
            			
        			
        			if(!joinCode.equals(""))
        				joinCode += " and ";
        			joinCode += m_entity.getName() + "." + columnName + "ID = " + attName + "_" + typeEntity.getName() + ".ID" ; 

        			oneTarget = m_entity.getName() + "." + columnName + "ID as " + attName + "ID,";   
        			oneTarget += attName + "_" + typeEntity.getName() + "." + "name as " + attName + "Name";   
        			
        			columnDef += ",`" + columnName + "ID` bigint(20)";
        		}
    		}
    		

    		if(!selectTarget.isEmpty()){
    			selectTarget += ",";    			
    		}
    		else
    		{
    			selectTarget += m_entity.getName() + ".id as id,";
    		}
    		
    		selectTarget += oneTarget;

    		if(!paras.equals(""))
    		{
    			paras += ",";
    			values += ",";
    			sets += ",";
    		}
    		
    		Entity typeEntity = m_entity.getDepends(attType);
    		if(typeEntity != null)
    		{
        		paras += columnName + "ID";
        		values += "#{" + attName + "ID" + "}";
        		sets += columnName + "ID" + "=#{" + attName + "ID" + "}";    	

    		}
    		else
    		{
        		paras += columnName;
        		values += "#{" + attName + "}";
        		sets += columnName + "=#{" + attName + "}";    			
    		}
		}
		
		
		

		daoCode = "\r\n\r\n\t@Insert(\"insert " + tableName + "(" + paras + ") values(" + values + ")\")\r\n";
		daoCode += "\t void insert" + m_entity.getName() + "(\r\n\t\t" + m_entity.getName() + " _" + m_entity.getName() + "\r\n\t\t);";   

		//generate update sql
		daoCode += "\r\n\r\n\t@Update(\"update " + tableName + " set " +  sets + " where id = #{id}\")" ;
		daoCode += "\r\n\t void update" + m_entity.getName() + "(\r\n\t\t" + m_entity.getName() + " _" + m_entity.getName() + "\r\n\t\t);";   
		
		//generate delete sql
		daoCode += "\r\n\r\n\t@Delete(\"delete from " + tableName + " where id = #{id}\")" ;
		daoCode += "\r\n\t void delete" + m_entity.getName() + "(\r\n\t\t" + m_entity.getName() + " _" + m_entity.getName() + "\r\n\t\t);";   
						
		//generate get sql
		if(hasComplexData == false)		
			daoCode += "\r\n\r\n\t@Select(\"select * from " + tableName + " where id = #{id}\")" ;
		else
			daoCode += "\r\n\r\n\t@Select(\"select " + selectTarget + " from " + complexTableNames
			+ " where " +  m_entity.getName()  + ".id = #{id} and "  +  joinCode  + "\")" ;
			
		daoCode += "\r\n\t " + m_entity.getName() + " get" + m_entity.getName() + "(\r\n\t\t@Param(\"id\") int id\r\n\t\t);";   

		//generate custom select sql
		if(hasComplexData == false)		
			daoCode += "\r\n\r\n\t@Select(\"select * from " + tableName + " ${condition}\")" ;
		else
			daoCode += "\r\n\r\n\t@Select(\"select " + selectTarget + " from " + complexTableNames + " where " +  joinCode + " ${condition}\")" ;
		daoCode += "\r\n\t List<" + m_entity.getName() + "> select" + m_entity.getName() + "(\r\n\t\t@Param(\"condition\") String condition\r\n\t\t);";   

		//generate select count sql
		daoCode += "\r\n\r\n\t@Select(\"select count(*) from " + tableName + " ${condition}\")" ;
		daoCode += "\r\n\t int count" + m_entity.getName() + "(\r\n\t\t@Param(\"condition\") String condition\r\n\t\t);";   
		
		//generate createTable sql
		daoCode += "\r\n\r\n\t@Select(\"create table  IF NOT EXISTS `" + tableName + "`(" + columnDef 
				+ " ,PRIMARY KEY (`ID`)"
				+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin\")";
		daoCode += "\r\n\t " + m_entity.getName() + " createTable" + m_entity.getName() + "(\r\n\r\n\t\t);";   
				
	
		
		//other select operations
		// operations
		for(EntityOperation val : m_entity.getOperations())
		{ 	
    		String ret = "void";
    		List<OperationParameter> opParas = val.getParameters();
    		String paraDef = "";
    		String paraVal = "";
    		String selectCondition = "";
    		
    		if("select".equals(val.getName()))
    		{
    			//normal operations
	    		if(opParas != null)
	    		{
	    			for(OperationParameter onePara : opParas)
	        		{
	    				String typeName = (String) onePara.getType();
	    				String paraName = onePara.getName();
	        			if("return".equals(onePara.getDirection()))
	        				continue;
        				if(!"".equals(selectCondition))
        					selectCondition += " and ";
        				selectCondition += paraName + " = #{" + paraName + "}";        				

        				if(!"".equals(paraVal))
        					paraVal += ",";
        				paraVal += paraName;
        				

        				if(!"".equals(paraDef))
        					paraDef += ",\r\n\t\t";
        				paraDef += "@Param(\"" + paraName + 
        						"\"" +
        						") " + typeName +
        						" " + paraName
        						;
	        		}
	    		}
	    		
	    		daoCode += "\r\n\r\n\t@Select(\"select * from D" + tableName ;
	    		
	    		if(!"".equals(selectCondition))
	    			daoCode += " where " + selectCondition;	    		

    			daoCode += "\")" ;
    			
	    		daoCode += "\r\n\t List<" + m_entity.getName() + "> get" + m_entity.getName() + "Instances(\r\n\t\t" + paraDef +
	    				"\r\n\t\t);";   
    		}    		
    		
		}
		
		return daoCode;
		
	}
	
	private String getTableName(Entity entity)
	{
		String tableName = "";
		for(int i = 0; i< entity.getName().length();i++)
		{
			char ch = entity.getName().charAt(i);
			if(ch <= 'Z' && ch >= 'A')
				tableName += "_" + ch;
			else 
				tableName += ch;
		}
		tableName = tableName.toUpperCase();
		
		return "D" + tableName;
	}
	

	private String getColumnName(String name)
	{
		String columnName = "";
		for(int i = 0; i< name.length();i++)
		{
			char ch = name.charAt(i);
			if(ch <= 'Z' && ch >= 'A')
				columnName += "_" + ch;
			else 
				columnName += ch;
		}
		columnName = columnName.toUpperCase();
		
		return columnName;
	}
}

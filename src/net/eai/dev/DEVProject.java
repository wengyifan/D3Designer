package net.eai.dev;

import com.google.gson.Gson;
import net.eai.dev.entitycg.PersistEntityDotNet;

import java.util.ArrayList;
import java.util.List;

public class DEVProject {
	
	private ArrayList<DEVPackage> packages  = new ArrayList<DEVPackage>();
	private String projectPath;
	
	private String _type;
	private String _id;
	private String name;
	private List<DEVPackage> ownedElements;
	
	
	public String getProjectPath() {
		return projectPath;
	}
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
	

	@SuppressWarnings("rawtypes")
	public static DEVProject importFromJson(String jsonfile) {

		String json = ioUtil.readFile(jsonfile); 
		Gson gson = new Gson();
		DEVProject devProject= gson.fromJson(json, DEVProject.class);	
		if(devProject != null && devProject.getOwnedElements() != null)
		{
			List<DEVPackage> ownedElements = (List<DEVPackage>) devProject.getOwnedElements();
			for(int i = 0;i<ownedElements.size();i++)
			{
				DEVPackage devPackage = ownedElements.get(i);
				devPackage.setTemplatePath("codeTemplate");
				devPackage.setPackageName(devProject.getName() + "." + devPackage.getName());
				devPackage.setProjectName(devProject.getName());
				devProject.addPackage(devPackage);
				DEVPackage.initDevPackage(devPackage);
			}
			
			for(int i = 0;i<ownedElements.size();i++)
			{
				DEVPackage devPackage = ownedElements.get(i);
				devPackage.normalizeTypes();
			}
			
		}
		
		return devProject;
		 
	}
	
	
	public void addPackage(DEVPackage pack)
	{
		packages.add(pack);
	}
	
	
/*
	public void createPrototype()
	{
		for(DEVPackage val:packages){

		
		if(val.getDbConfigXml() != null)
			dbXml += val.getDbConfigXml();
		
			
		}
	}*/
	
	
	@SuppressWarnings("unchecked")
	public void exportCode()
	{

		String dbXml = "";
		String strutsCode = "";
		String createTables = "";
		for(DEVPackage val:packages){

    		strutsCode += val.exportCode(projectPath);
    		
    		if(val.getDbConfigXml() != null)
    			dbXml += val.getDbConfigXml();
    		
    		createTables += val.getCreateTables();
    			
		}
		
		@SuppressWarnings("rawtypes")
		ArrayList<DEVPackage> set = StarUmlObjectContainer.getPagePackageSet();
		for(DEVPackage pack:set)
		{
		//	pack.exportUMLFragment(projectPath + "/../pageUml/");
		}
		
		ioUtil.writeSectionCode(projectPath + "/net/eai/app/system/MainInit.java",
				"/* createTable starts",
				"createTable ends */",createTables + "\r\n");
		
		ioUtil.writeSectionCode(projectPath + "/net/eai/app/system/Configuration.xml",
				"<!-- Add mapper here -->\r\n",
				"<!-- mapper ends -->",dbXml);
		
		
		ioUtil.writeSectionCode(projectPath + "/struts.xml",
				"<!-- Add packages here -->\r\n",
				"\r\n<!-- packages ends -->",strutsCode);
		
		// generate ibatis configure				
	}
	
	
	@SuppressWarnings("unchecked")
	public void exportDotNetCode()
	{

		String dbXml = "";
		String strutsCode = "";
		String createTables = "";

		String sqlMap = "";

		String csprojectContent = "";

		for(DEVPackage val:packages){

    		String[] s = val.exportDotNetCode(projectPath);
			sqlMap+=s[0];
			csprojectContent+=s[1];

    		if(val.getDbConfigXml() != null)
    			dbXml += val.getDbConfigXml();

    		createTables += val.getCreateTables();

		}
		String templatePath = "codeTemplate";
		String path = projectPath + "/" + getName() + "/";
		PersistEntityDotNet.genCommon(path, templatePath, sqlMap);
		PersistEntityDotNet.genSlnProject(path, templatePath,csprojectContent);

		
	}
	
	
	public String get_type() {
		return _type;
	}
	public void set_type(String _type) {
		this._type = _type;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<DEVPackage> getOwnedElements() {
		return ownedElements;
	}
	public void setOwnedElements(List<DEVPackage> ownedElements) {
		this.ownedElements = ownedElements;
	}
}

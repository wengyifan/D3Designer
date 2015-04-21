package net.eai.test;

import net.eai.dev.DEVProject;

public class test {
	public static void main(String[] args) {


		
		String url = "http://api.map.baidu.com/place/v2/search?query=%E9%93%B6%E8%A1%8C&bounds=39.915,116.404,39.975,116.414&output=json&page_num=0&page_size=20&ak=39edd98fced1808ee32c639f62c0a936";
		/*
	//	ioUtil.writeFile("api.txt" ,mapreturn);
	
		JsonClassGen json = new JsonClassGen(
				"net.eai.crawler.baidu",
				"codeTemplate",
				"BaiduRectApi",
				"../Parking/src/");
		json.addApi("getBaiduRectParkingLot", url);
		json.genEntity();
	*/
	//	String ret = HttpGetter.httpGet("http://gss3.map.baidu.com/?newmap=1&reqflag=pcmap&biz=1&from=webmap&qt=bkg_data&c=289&ie=utf-8&wd=停车&l=13&xy=1651_444&b=(13509846.26,3637884.36;13545078.26,3647900.36)", "GET", "");
		//String ret = HttpGetter.httpGet("http://map.baidu.com/?newmap=1&reqflag=pcmap&biz=1&from=webmap&qt=s&c=289&wd=%E5	%81%9C%E8%BD%A6&da_src=pcmappg.map&on_gel=1&l=11&gr=1&b=(13467510.26,3622908.36;13608438.26,3662972.36)&tn=B_NORMAL_MAP&nn=0&ie=utf-8", "GET", "");



		///DEVProject project = DEVProject.importFromJson("../baseSettings/base.js");
		DEVProject project = DEVProject.importFromJson("../YouthPms/Fragment.js");
		project.setProjectPath("../YouthPms/src");
		project.exportCode();
		//project.exportDotNetCode();

		//DEVProject project = DEVProject.importFromJson("../sfereLogistics/Fragment.js");
		///project.setProjectPath("../sfereLogistics/src");
		//project.exportCode();
		//project.exportDotNetCode();

		/*
		DEVPackage pack = DEVPackage.importFromJson(jsonStr);
		pack.setTemplatePath("codeTemplate");
		pack.setPackageName("net.eai.parking");
		
		project.addPackage(pack);
		project.exportCode();*/
		
		//ret =  ret + ";";
		//Company.select("order by id desc");
/*
		DEVProject project = new DEVProject();
		project.setProjectPath("D:/workspace/EAIDev/src");
		//project.setProjectPath("/Users/apple/Documents/workspace/EAIDev/src");
		
		
		DEVPackage pack = DEVPackage.importFromJson(jsonStr);
		pack.setTemplatePath("D:/workspace/EAIDev/codeTemplate");
		//pack.setTemplatePath("/Users/apple/Documents/workspace/EAIDev/codeTemplate");
		pack.setPackageName("net.eai.parking");
		
		project.addPackage(pack);
		project.exportCode();*/
	 //   StarUmlFragment obj = gson.fromJson(jsonStr, StarUmlFragment.class);
	  //  obj.getName();
		
	//	testfun(12148313.20075,3122166.7140485);
		System.out.print("Done");
	}
	
	
}

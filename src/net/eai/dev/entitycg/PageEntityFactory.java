package net.eai.dev.entitycg;
import com.google.gson.Gson;
import net.eai.dev.*;

import java.util.List;

public class PageEntityFactory {

	private String entitShowName;
	private DEVPackage pagePackage;
	
	PageEntityFactory(Entity entity,DEVPackage targetPack)
	{
		m_entity = entity;
		
		entitShowName = m_entity.getName() ;
		
		if(m_entity.getExtraAttribute("显示名") != null)
			entitShowName = m_entity.getExtraAttribute("显示名");
		
		if(targetPack == null)
		{
			pagePackage = new DEVPackage();
			pagePackage.setName(m_entity.getName() + "Pages");
			StarUmlObjectContainer.addPagePackage(pagePackage.get_id(), pagePackage);
		}
		else
			pagePackage = targetPack;

		
	}
	
	public void outputPageUmlJs(String path)
	{

		Gson gson = new Gson();
		String outputPack = gson.toJson(pagePackage);
		ioUtil.writeFile(path + "/outputPackage/" + pagePackage.getName() + ".js",outputPack);
		
		
	}
	
	
	public PageEntity genEditPageEntity (String owner)
	{		
		// the entity detail page
		Entity dataPage = new Entity(pagePackage);
		dataPage.setTemplatePath(m_entity.getTemplatePath());
		dataPage.setStereotype("HtmlFormPage");
		dataPage.setName(m_entity.getName() + "EditPage");
		
		String ownerAtt = "";
		// the attributes pages of the detail page
		List<EntityAttribute> atts = m_entity.getAttributes();
		for(EntityAttribute oneAtt:atts)
		{
			String attName = oneAtt.getName();
			String attType = oneAtt.getTypeStr();
			
			
			if(owner != null &&  owner.equals(oneAtt.getExtraAttribute("聚集类")))
			{
				ownerAtt = attName;
				continue;
			}
			
			attName = attName.replace(".", "-");
			
			if(oneAtt.getMultiplicity() != null && !oneAtt.getMultiplicity().equals("")){
				attType = "MultipleString";							
			}
			
			
    		if(oneAtt.getExtraAttribute("显示名") != null/*!"n/a".equals(oneAtt.getExtraAttribute("显示名"))*/)
    		{    

    			String showName = attName;
    			
    			if(oneAtt.getExtraAttribute("显示名") != null)
    				showName = oneAtt.getExtraAttribute("显示名");
    		
    			EntityAttribute<String> oneEle = new EntityAttribute<String>(dataPage);
    			
    			
    			Entity atttypeEntity = m_entity.getDepends(attType);
    			String dictionary = oneAtt.getExtraAttribute("字典");

    			if(atttypeEntity != null || dictionary !=null)
    			{
    				oneEle.setType("Choose");	
    			}
    			else if (attType.equals("Date"))
    			{
    				oneEle.setType("Date");	
    			}
    			else
    			{
	    			if("MultipleString".equals(attType))
    				{
	    				oneEle.setType("MultipleChoose");	
    				}
    				else if(attName.contains("-"))
    					oneEle.setType("readonlyEdit");
	    			else
	    				oneEle.setType("LineEdit");		
    			}

    			oneEle.setExtraAttribute("@nameDesc@", showName);
    			oneEle.setName(attName);
    			dataPage.addAttribute(oneEle);	  			
    		}
		}
		//create the detail page div reder function
		EntityOperation rederDiv = new EntityOperation(dataPage);
		rederDiv.setName("initEntityForm");
		OperationParameter<String> rederDivOp = new OperationParameter<String>(dataPage);
		rederDivOp.setName(m_entity.getName());
		rederDiv.getParameters().add(rederDivOp);
		
		if(owner != null)
		{
			OperationParameter<String> ownerEntity = new OperationParameter<String>(dataPage);
			ownerEntity.setName(owner);
			rederDiv.getParameters().add(ownerEntity);
			
			OperationParameter<String> ownerPara = new OperationParameter<String>(dataPage);
			ownerPara.setName(ownerAtt);
			rederDiv.getParameters().add(ownerPara);
		}
		
		
		dataPage.addOperation(rederDiv);
		dataPage.addDepend(m_entity.getName(), m_entity);
		
		//call page generating
		PageEntity page = new PageEntity(dataPage);
		page.setTemplatePath(m_entity.getTemplatePath());
		

		return page;
	}
	
	
	public PageEntity genOwnedTablePageEntity (Entity targetEntity,String owner)
	{

		//entity list needed elements
		//create table page					
		Entity tablePage = new Entity(pagePackage);
		tablePage.setTemplatePath(targetEntity.getTemplatePath());
		tablePage.setStereotype("HtmlDataListPage");
		tablePage.setName(owner + "Owned" + targetEntity.getName() + "Page");
		//create table card's attributes	

		createOwnerCard(owner,tablePage);		

		createSearchCard(tablePage);		
		createTableCard(tablePage,owner);
		
		//call page generating
		PageEntity page = new PageEntity(tablePage);
		page.setTemplatePath(m_entity.getTemplatePath());
		
		return page;
	}
	
	private void createSearchCard(Entity page)
	{		
		//create search card
		
		EntityAttribute<String> searchCardAtt = new EntityAttribute<String>(page);
		searchCardAtt.setName("searchCard");
		searchCardAtt.setType(m_entity.getName() + "Search");
		page.addAttribute(searchCardAtt);
			
		Entity searchCard = new Entity(pagePackage);
		searchCard.setStereotype("Ele_SearchCard");
		searchCard.setName(m_entity.getName() + "Search");
		
		
		
		//create table card's attributes and operations
		EntityAttribute<String> title = new EntityAttribute<String>(searchCard);
		title.setName("title");
		title.setDefaultValue(entitShowName + "数据");
		searchCard.addAttribute(title);
		
		
		//create search conditions
		// the attributes pages of the detail page
		List<EntityAttribute> atts = m_entity.getAttributes();
		boolean hasSearchable = false;
		for(EntityAttribute oneAtt:atts)
		{
			String attName = oneAtt.getName();
			String attType = oneAtt.getTypeStr();
			String searchLabel = oneAtt.getExtraAttribute("搜索");
			if(searchLabel == null)
				continue;
			
			if(attName.contains("."))
				continue;
						
			if(oneAtt.getExtraAttribute("显示名") == null)
				continue;
			
		
			String showName = oneAtt.getExtraAttribute("显示名");
			hasSearchable = true;
			
			String steroType;
			
			Entity atttypeEntity = m_entity.getDepends(attType);
			String dictionary = oneAtt.getExtraAttribute("字典");

			if(atttypeEntity != null || dictionary !=null)
			{
				steroType = "Choose";	
			}
			else if (attType.equals("Date"))
			{
				steroType = "Date";	
			}
			else
			{
				steroType = "LineEdit";		
			}
			

			createElement(searchCard,attName,showName,steroType);
			
			//pagePackage.getOwnedElements().add(dataEdit);  
		
		}


		if(hasSearchable)
		{
			EntityAttribute att = createElement(searchCard,"searchButton","搜索","Button");
			att.addExtraAttribute("@onClick@", "onSearch()");
		}
		
		
		// create search card's inital js functions
		EntityOperation renderSearch = new EntityOperation(searchCard);
		renderSearch.setName("initSearch");
		OperationParameter<String> searchInitOp = new OperationParameter<String>(searchCard);
		searchInitOp.setName(m_entity.getName());
		renderSearch.getParameters().add(searchInitOp);
		
		//set dependency
		searchCard.addOperation(renderSearch);
		searchCard.addDepend(m_entity.getName(), m_entity);
		page.addDepend(m_entity.getName() + "Search", searchCard);
	}
	
	
	private void createTableCard(Entity page,String owner)
	{
		//create table card	
		EntityAttribute<String> tableCardAtt = new EntityAttribute<String>(page);
		tableCardAtt.setName("tableCard");
		tableCardAtt.setType(m_entity.getName() + "Table");
		page.addAttribute(tableCardAtt);
		
		
		Entity tableCard = new Entity(pagePackage);
		tableCard.setStereotype("Ele_CardContent");
		tableCard.setName(m_entity.getName() + "Table");

		
		//create table card's attributes and operations
		EntityAttribute<String> table = new EntityAttribute<String>(tableCard);
		table.setName("table");
		table.setType("DataTable");
		table.setExtraAttribute("@Entity@", m_entity.getName());
		tableCard.addAttribute(table);
		
		
		EntityOperation renderTable = new EntityOperation(tableCard);
		renderTable.setName("initEntityList");
		
		OperationParameter<String> p1 = new OperationParameter<String>(tableCard);
		p1.setName(m_entity.getName());
		renderTable.getParameters().add(p1);
		
		if(owner != null){
			OperationParameter<String> p2 = new OperationParameter<String>(tableCard);
			p2.setName(owner);
			renderTable.getParameters().add(p2);

			OperationParameter<String> p3 = new OperationParameter<String>(tableCard);
			p3.setName(m_entity.getOwnerAtt(owner));
			renderTable.getParameters().add(p3);
			
		};
		

		tableCard.addOperation(renderTable);
		tableCard.addDepend(m_entity.getName(), m_entity);

		
		//add depend
		page.addDepend(m_entity.getName() + "Table", tableCard);
	}
	
	
	private EntityAttribute createElement(Entity parent,String name,String showName,String steroType)
	{
		//	dataEdit.set_parent(new StarUmlReference(pagePackage.get_id()));
			
		EntityAttribute<String> oneEle = new EntityAttribute<String>(parent);
		oneEle.setType(steroType);
		oneEle.setName(name);
		oneEle.setExtraAttribute("@nameDesc@", showName);
		parent.addAttribute(oneEle);
		
		return oneEle;
	}
	
	private void createOwnerCard(String owner,Entity page)
	{
		
		EntityAttribute<String> ownerCardAtt = new EntityAttribute<String>(page);
		ownerCardAtt.setName("ownerCard");
		ownerCardAtt.setType(m_entity.getName() + "Owner");
		page.addAttribute(ownerCardAtt);
			
		Entity ownerCard = new Entity(pagePackage);
		ownerCard.setStereotype("Ele_Card");
		ownerCard.setName(m_entity.getName() + "Owner");
		//create table card's attributes and operations
		EntityAttribute<String> title = new EntityAttribute<String>(ownerCard);
		title.setName("title");
		Entity ownerEntity = m_entity.getDepends(owner);
		if(ownerEntity.getExtraAttribute("显示名") != null)
			title.setDefaultValue("所属" + ownerEntity.getExtraAttribute("显示名"));
		else
			title.setDefaultValue("所属" + owner);
		
		ownerCard.addAttribute(title);

		page.addDepend(m_entity.getName() + "Owner", ownerCard);
		
		
	}
	
	
	
	
	public PageEntity genTablePageEntity ()
	{
		

		//entity list needed elements
		String entitShowName = m_entity.getName() ;
		
		if(m_entity.getExtraAttribute("显示名") != null)
			entitShowName = m_entity.getExtraAttribute("显示名");

		//create table page					
		Entity tablePage = new Entity(pagePackage);
		tablePage.setTemplatePath(m_entity.getTemplatePath());
		tablePage.setStereotype("HtmlDataListPage");
		tablePage.setName(m_entity.getName() + "TablePage");
		
		//create table card's attributes	
		

		createSearchCard(tablePage);
		
		createTableCard(tablePage,null);
		
		
		
		
		
		//call page generating
		PageEntity page = new PageEntity(tablePage);
		page.setTemplatePath(m_entity.getTemplatePath());

		return page;
		
	}
	
	
	public DEVPackage getPagePackage() {
		return pagePackage;
	}

	public void setPagePackafge(DEVPackage pagePackage) {
		this.pagePackage = pagePackage;
	}


	private Entity m_entity;
}

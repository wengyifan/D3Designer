package net.eai.dev;


import com.google.gson.Gson;
import net.eai.dev.entitycg.*;

import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;

public class DEVPackage<T> {
    private HashMap<String, Entity> entities = new HashMap<String, Entity>();
    private HashMap<String, Entity> filters = new HashMap<String, Entity>();

    public DEVPackage() {
        _type = "UMLPackage";
        String str = this.toString();
        _id = str.substring(str.indexOf("@"));
        name = "untitled";
    }

    private List<T> ownedElements = new ArrayList();
    private String packageName = "domain";
    private String projectName = "domain";
    private String framework;
    private String templatePath;
    private String name;
    private String _type;
    private String _id;


    private String dbConfigXml = null;
    private String createTables = " ";

    public void addEntity(String id, Entity entity) {
        entities.put(id, entity);
        //	ownedElements.add((T) entity);
    }

    public void exportUMLFragment(String path) {
        Gson gson = new Gson();
        String code = gson.toJson(this);
        ioUtil.writeFile(path + "/" + this.getName() + ".mfj", code);
    }


    @SuppressWarnings("rawtypes")
    public static DEVPackage importFromJson(String jsonfile) {

        String json = ioUtil.readFile(jsonfile);
        Gson gson = new Gson();
        DEVPackage devPackage = gson.fromJson(json, DEVPackage.class);
        initDevPackage(devPackage);
        return devPackage;

    }


    @SuppressWarnings("rawtypes")
    public static void initDevPackage(DEVPackage devPackage) {

        Gson gson = new Gson();
        if (devPackage != null && devPackage.getOwnedElements() != null) {
            List ownedElements = (List) devPackage.getOwnedElements();


            Entity entityManageApi = new Entity();
            entityManageApi.setName(devPackage.getName() + "EntityManageApi");
            entityManageApi.set_parent(new StarUmlReference(devPackage.get_id()));
            entityManageApi.setTemplatePath(devPackage.getTemplatePath());
            entityManageApi.setStereotype("Api");
            devPackage.getOwnedElements().add(entityManageApi);

            for (int i = 0; i < ownedElements.size(); i++) {
                Entity entity = null;
                Object aa = ownedElements.get(i);
                if (aa.getClass().toString().contains("Entity")) {
                    entity = (Entity) aa;
                } else {
                    LinkedHashMap data = (LinkedHashMap) ownedElements.get(i);
                    String type = (String) data.get("_type");
                    String id = (String) data.get("_id");
                    String stereotype = (String) data.get("stereotype");
                    if (type.equals("UMLClass")) {
                        String eleJson = gson.toJson(data);
                        entity = gson.fromJson(eleJson, Entity.class);
                    }
                }

                if (entity != null) {
                    entity.collectExtraAttributes();
                    entity.setDevPackage(devPackage.getPackageName());
                    entity.setTemplatePath(devPackage.getTemplatePath());
                    StarUmlObjectContainer.addObject(entity.get_id(), entity);
                    StarUmlObjectContainer.addObjectByName(entity.getName(), entity);
                    devPackage.addEntity(entity.get_id(), entity);
                    StarUmlReference ref = new StarUmlReference(devPackage.get_id());
                    entity.set_parent(ref);
                    ownedElements.set(i, entity);


                    //auto generate ApiManage Entity
                    if (entity.getStereotype() != null && entity.getStereotype().equals("Entity")) {
                        EntityOperation entityManageOp = new EntityOperation(entityManageApi);
                        entityManageOp.setName("manage" + entity.getName());
                        entityManageApi.addOperation(entityManageOp);
                        entityManageApi.addDepend(entity.getName(), entity);
                    }
                }


            }
            //	devPackage.normalizeTypes();
        }
    }


    public void normalizeTypes() {

        Iterator<Entry<String, Entity>> entityIter = entities.entrySet().iterator();

        while (entityIter.hasNext()) {
            Entry<String, Entity> entry = entityIter.next();
            Entity entity = entry.getValue();
            entity.normalizeTypes();
        }

        Iterator<Entry<String, Entity>> depends = entities.entrySet().iterator();

        //create a dictionary entity for all entities to depend
        Entity dictionaryEntity = new Entity();
        dictionaryEntity.setName("MasterDictionary");

        while (depends.hasNext()) {
            Entry<String, Entity> entry = depends.next();
            Entity entity = entry.getValue();
            entity.scanDepends();
            entity.addDepend("MasterDictionary", dictionaryEntity);
        }
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String exportCode(String path) {
        //1. output entities
        String packagePath = packageName;
        packagePath = packagePath.replace(".", "/");
        Iterator<Entry<String, Entity>> entityIter = entities.entrySet().iterator();
        String mapperFile = ioUtil.readFile(templatePath + "/DataMapper.java");

        String strutsCode = "";
        String ibatisMapperCode = "";


        while (entityIter.hasNext()) {
            Entry<String, Entity> entry = entityIter.next();

            String key = entry.getKey();
            Entity val = entry.getValue();

            val.setTemplatePath(templatePath);
            val.setDevPackage(packageName);

            if ("Api".equals(val.getStereotype())) {
                ApiEntity api = new ApiEntity(val);
                api.setDocumentation(val.getDocumentation());
                api.setOwnedElements(val.getOwnedElements());
                api.setTemplatePath(templatePath);
                api.setDevPackage(val.getDevPackage());

                String code = api.genCode(packageName);
                code = "package " + packageName + ";\r\n\r\n" + code;
                ioUtil.writeFileWithExistCode(path + "/" + packagePath + "/" + val.getName() + ".java", code);

                String packageName = packagePath.replace("/", ".");
                api.genContracts(path + "/" + packagePath + "/apiContract", packageName);

                api.genMultipleAttEntitieCode(path);
                strutsCode += api.genStruts(packagePath, packageName);
                ibatisMapperCode += api.genMultipleAttEntitieSql();

                api.createEntityPages(path);

                AjaxFactory ajaxFactory = new AjaxFactory(val);
                ajaxFactory.genApiAjaxs(path);

            } else if ("Entity".equals(val.getStereotype())) {
                PersistEntity persist = new PersistEntity(val);
                persist.setTemplatePath(templatePath);
                String jsCode = "";
                persist.genCode(path);
                ibatisMapperCode += persist.genIbaitsMapper();
                createTables += "\r\n\t\t" + this.packageName + "." + val.getName() + ".createTable();";

            } else if (val.getStereotype() != null && val.getStereotype().startsWith("Html")) {
                PageEntity page = new PageEntity(val);
                page.setTemplatePath(templatePath);
                String jsCode = "";
                String code = page.genCode("page", page);
                ioUtil.writeFileWithExistCode(path + "/../WebContent/" + packageName + "/" + val.getName() + ".html", code);
            } else if (val.getStereotype() == null || !val.getStereotype().startsWith("Ele")) {
                val.setTemplatePath(templatePath);
                String code = val.genCode(path);
                code = "package " + packageName + ";\r\n\r\n" + code;
                ioUtil.writeFileWithExistCode(path + "/" + packagePath + "/" + val.getName() + ".java", code);

            }

        }

        if (!ibatisMapperCode.isEmpty()) {
            mapperFile = mapperFile.replace("@content@", ibatisMapperCode);
            mapperFile = mapperFile.replace("@packageName@", packageName);

            String mapperXmlFile = ioUtil.readFile(templatePath + "/DataMapper.xml");
            mapperXmlFile = mapperXmlFile.replace("@packageName@", packageName);
            ioUtil.writeFile(path + "/" + packagePath + "/DataMapper.xml", mapperXmlFile);
            ioUtil.writeFile(path + "/" + packagePath + "/DataMapper.java", mapperFile);
            dbConfigXml = "\t\t<mapper resource=\"" + packagePath + "/DataMapper.xml\" />\r\n";
        }

        return strutsCode;

    }


    public String[] exportDotNetCode(String path) {
        path = path + "/" + projectName + "/";
        Iterator<Entry<String, Entity>> entityIter = entities.entrySet().iterator();

        String sqlMap = "";

        String csprojectContent = "";

        while (entityIter.hasNext()) {
            Entry<String, Entity> entry = entityIter.next();
            Entity val = entry.getValue();

            val.setTemplatePath(templatePath);
            val.setDevPackage(packageName);

            PersistEntityDotNet persistDN = new PersistEntityDotNet(val);
            persistDN.setTemplatePath(templatePath);
            PersistEntityDotNet.projectName = projectName;
            if ("Api".equals(val.getStereotype())) {
                csprojectContent += persistDN.genOperations(path);
            } else if ("Entity".equals(val.getStereotype())) {
                csprojectContent += persistDN.genEntity(path);
                csprojectContent += persistDN.genMapper(path);
                csprojectContent += persistDN.genControl(path);
                csprojectContent += persistDN.genResponse(path);

                sqlMap += MessageFormat.format("\t<sqlMap resource=\"Mapper/{0}.xml\"/>\r\n", persistDN.getBaseEntity().getName());
            } else if (val.getStereotype() != null && val.getStereotype().startsWith("Html")) {
            } else if (val.getStereotype() == null || !val.getStereotype().startsWith("Ele")) {
                csprojectContent += persistDN.genItem(path);
                csprojectContent += persistDN.genResponse(path);
                sqlMap += MessageFormat.format("\t<sqlMap resource=\"Mapper/{0}.xml\"/>\r\n", persistDN.getBaseEntity().getName());
            }
        }

        String[] s = new String[2];
        s[0] = sqlMap;
        s[1] = csprojectContent;
        return s;


    }


    public List<T> getOwnedElements() {
        return ownedElements;
    }

    public void setOwnedElements(List<T> ownedElements) {
        this.ownedElements = ownedElements;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDbConfigXml() {
        return dbConfigXml;
    }


    public void setDbConfigXml(String dbConfigXml) {
        this.dbConfigXml = dbConfigXml;
    }


    public String getCreateTables() {
        return createTables;
    }


    public void setCreateTables(String createTables) {
        this.createTables = createTables;
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
}

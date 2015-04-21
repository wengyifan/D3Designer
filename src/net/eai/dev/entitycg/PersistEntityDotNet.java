package net.eai.dev.entitycg;

import net.eai.dev.*;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.List;

public class PersistEntityDotNet {
    private String templatePath;
    private Entity m_entity;
    public static String projectName;

    public PersistEntityDotNet(Entity entity) {
        m_entity = entity;
        templatePath = m_entity.getTemplatePath();
    }

    public Entity getBaseEntity() {
        return m_entity;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templaftePath) {
        this.templatePath = templaftePath;
    }

    //Entity
    public String genEntity(String path) {
        String imports = m_entity.getImports();

        String content = "";

        Boolean bFindID = false;
        for (EntityAttribute val : m_entity.getAttributes()) {
            String attName = val.getName();
            String attType = val.getTypeStr();
            String strType = "";

            if (attName.toLowerCase().equals("id")) {
                attName = "id";
                bFindID = true;
            }

            if (attType.equals("String")) {
                strType = "string";
            } else if (attType.equals("int")) {
                strType = "int";
            } else if (attType.equals("Date")) {
                strType = "DateTime";
            } else if (attType.equals("double")) {
                strType = "double";
            } else {
                strType = "string";
            }
            Entity typeEntity = m_entity.getDepends(attType);
            if (typeEntity == null) {
                content += MessageFormat.format("\t\tpublic {0} {1} ", strType, attName) + "{get; set;}\n";
            } else {
                if (val.getMultiplicity() != null) {
                    content += MessageFormat.format("\t\tpublic String {0} ", attName) + "{get; set;}\n";
                } else {
                    content += MessageFormat.format("\t\tpublic int {0}id ", attName) + "{get; set;}\n";
                    content += MessageFormat.format("\t\tpublic String {0}Name ", attName) + "{get; set;}\n";
                }
            }
        }

        if (!bFindID) {
            content = "\t\tpublic int id {get; set;}\n" + content;
        }
        String createTableFunc = ioUtil.readFile(templatePath + "/entityDataAccess.cs");
        createTableFunc = createTableFunc.replace("@entity@", m_entity.getName());
        content += createTableFunc;

        String code = "";
        code = ioUtil.readFile(templatePath + "/persistentEntity.cs");
        code = code.replace("@imports@", imports);
        code = code.replace("@entity@", m_entity.getName());
        code = code.replace("@content@", content);
        code = code.replace("@namespace@", m_entity.getDevPackage());
        code = code.replace("@project@", projectName);


        String devPackPath = m_entity.getDevPackage().replace(".", "/");

        ioUtil.writeFileWithExistCode(path + projectName + "/Entity/" + m_entity.getName() + ".cs", code);
        return MessageFormat.format("\t\t<Compile Include=\"Entity\\{0}.cs\" />\n", m_entity.getName());
    }

    //Mapper.xml
    public String genMapper(String path) {
        String FullResultMap = "";
        String strLeftJoinName = "";
        String strLeftJoin = "";
        String strInsertValues = "";
        String strInsert = "";
        String strUpdate = "";
        String tableName = getTableName(m_entity);

        Boolean bFindID = false;
        for (EntityAttribute val : m_entity.getAttributes()) {
            String attName = val.getName();
            String attType = val.getTypeStr();
            //String columnName = getColumnName(attName);
            if (attName.toLowerCase().equals("id")) {
                attName = "id";
                bFindID = true;
            }
            String strType = "";

            if (attType.equals("String")) {
                strType = "VARCHAR(100)";
            } else if (attType.equals("int")) {
                strType = "BIGINT(20)";
            } else if (attType.equals("Date")) {
                strType = "DATETIME";
            } else if (attType.equals("double")) {
                strType = "DOUBLE";
            } else {
                if (val.getMultiplicity() == null) {
                    Entity typeEntity = m_entity.getDepends(attType);
                    if (typeEntity != null) {
                        String strTB = getTableName(typeEntity);
                        strLeftJoin += MessageFormat.format(" LEFT JOIN {0} AS {0}{2} ON {1}.{2}id = {0}{2}.id ",
                                strTB, m_entity.getName(), attName);
                        strLeftJoinName += MessageFormat.format(",{0}{1}.Name AS {1}Name ", strTB, attName);
                    }

                    strInsert += MessageFormat.format(" {0}id, ", attName);
                    strInsertValues += MessageFormat.format(" #{0}id#,", attName);
                    strUpdate += MessageFormat.format(" {0}id=#{1}id#,", attName, attName);

                    FullResultMap += MessageFormat.format("<result property=\"{0}id\" column=\"{0}id\" dbType=\"BIGINT\"/>\r\n", attName);
                    FullResultMap += MessageFormat.format("<result property=\"{0}Name\" column=\"{0}Name\" dbType=\"VARCHAR\"/>\r\n", attName);
                    continue;
                } else {
                    strType = "VARCHAR";
                }
            }
            strInsert += MessageFormat.format("{0},", attName);
            strInsertValues += MessageFormat.format("#{0}#,", attName);
            strUpdate += MessageFormat.format("{0}=#{1}#,", attName, attName);

            FullResultMap += MessageFormat.format("<result property=\"{0}\" column=\"{0}\" dbType=\"{1}\"/>\r\n", attName, strType);
        }
        if (!bFindID) {
            FullResultMap = "<result property=\"id\" column=\"id\" dbType=\"BIGINT\"/>\n" + FullResultMap;
        }
        String Find = MessageFormat.format("SELECT {3}.* {0} FROM {1} AS {3} {2} WHERE {3}.id = #id#",
                strLeftJoinName, tableName, strLeftJoin, m_entity.getName());
        String Insert = MessageFormat.format("INSERT INTO {0} ({1}) VALUES ({2})",
                tableName, strInsert.substring(0, strInsert.length() - 1), strInsertValues.substring(0, strInsertValues.length() - 1));
        String Update = MessageFormat.format("UPDATE {0} SET {1} WHERE {0}.id = #id#",
                tableName, strUpdate.substring(0, strUpdate.length() - 1));
        String Delete = MessageFormat.format("DELETE FROM {0} WHERE {0}.id = #id#",
                tableName);
        String FindList = MessageFormat.format("SELECT {3}.* {0} FROM {1} AS {3} {2} $value$",
                strLeftJoinName, tableName, strLeftJoin, m_entity.getName());

        String Count = MessageFormat.format("SELECT COUNT(*) AS NUM FROM {1} AS {3} {2} $value$",
                strLeftJoinName, tableName, strLeftJoin, m_entity.getName());

        String LastID = MessageFormat.format("SELECT MAX(id) AS NUM FROM {0} ", tableName);


        String code = ioUtil.readFile(templatePath + "/Mapper.xml");
        code = code.replace("@Find@", Find);
        code = code.replace("@Insert@", Insert);
        code = code.replace("@Update@", Update);
        code = code.replace("@Delete@", Delete);
        code = code.replace("@FindList@", FindList);
        code = code.replace("@Count@", Count);
        code = code.replace("@LastID@", LastID);
        code = code.replace("@FullResultMap@", FullResultMap);
        code = code.replace("@entity@", m_entity.getName());
        code = code.replace("@namespace@", m_entity.getDevPackage());
        code = code.replace("@project@", projectName);
        String devPackPath = m_entity.getDevPackage().replace(".", "/");

        ioUtil.writeFileWithExistCode(path + projectName + "/Mapper/" + m_entity.getName() + ".xml", code);

        return MessageFormat.format("\t\t<Content Include=\"Mapper\\{0}.xml\" />\n", m_entity.getName());
    }

    //ashx ashx.cs
    public String genControl(String path) {
        String setValue = "";
        String Search = "";
        for (EntityAttribute val : m_entity.getAttributes()) {
            String attName = val.getName();
            String attType = val.getTypeStr();
            String TypeValueof = "";

            if (val.getExtraAttribute("搜索") != null) {
                Entity typeEntity = m_entity.getDepends(attType);
                if (typeEntity == null) {
                    Search += MessageFormat.format("\"{0}\",", attName);
                } else {
                    Search += MessageFormat.format("\"{0}id\",", attName);
                }
            }
            if (attType.equals("String")) {
                TypeValueof = "ValueOfString";
            } else if (attType.equals("int")) {
                TypeValueof = "ValueOfInt32";
            } else if (attType.equals("Date")) {
                TypeValueof = "ValueOfDateTime";
            } else if (attType.equals("double")) {
                TypeValueof = "ValueOfDouble";
            } else {
                TypeValueof = "ValueOfString";
            }

            Entity typeEntity = m_entity.getDepends(attType);
            String stSet = "";
            if (typeEntity == null) {
                stSet = attName;
            } else {
                if (val.getMultiplicity() != null) {
                    stSet = attName;
                    TypeValueof = "ValueOfString";
                } else {
                    stSet = attName + "id";
                    TypeValueof = "ValueOfInt32";
                }
            }
            if (!attName.toLowerCase().equals("id")) {
                setValue += MessageFormat.format("\t\t\t\tentity.{0} = TypeConvert.{1}(RequestResolve.GetValue(para, \"{0}\"));\r\n", stSet, TypeValueof);
            }
        }

        String code = ioUtil.readFile(templatePath + "/EntityManageAction.cs");
        code = code.replace("@entity@", m_entity.getName());
        code = code.replace("@namespace@", m_entity.getDevPackage());
        code = code.replace("@project@", projectName);
        code = code.replace("@setValue@", setValue);
        if (!StringUtils.isEmpty(Search)) {
            code = code.replace("@Search@", Search.substring(0, Search.length() - 1));
        } else {
            code = code.replace("@Search@", "");
        }
        String devPackPath = m_entity.getDevPackage().replace(".", "/");

        ioUtil.writeFileWithExistCode(path + projectName + "/Control/" + m_entity.getName() + "Control.ashx.cs", code);

        String codeWebHandler = MessageFormat.format("<%@ WebHandler Language=\"C#\" CodeBehind=\"{0}Control.ashx.cs\" Class=\"{1}.Control.{0}Control\" %>", m_entity.getName(), m_entity.getDevPackage());
        ioUtil.writeFileWithExistCode(path + projectName + "/Control/" + m_entity.getName() + "Control.ashx", codeWebHandler);


        return MessageFormat.format("\t<Content Include=\"Control\\{0}Control.ashx\" />\n\t<Compile Include=\"Control\\{0}Control.ashx.cs\">\n\t<DependentUpon>{0}Control.ashx</DependentUpon>\n\t</Compile>\n", m_entity.getName());

    }

    //
    public String genResponse(String path) {

        String devPackPath = m_entity.getDevPackage().replace(".", "/");

        String getListResponseContract = ioUtil.readFile(templatePath + "/contractEntities/listEntitiesResponse.cs");
        getListResponseContract = getListResponseContract.replace("@entity@", m_entity.getName());
        getListResponseContract = getListResponseContract.replace("@namespace@", m_entity.getDevPackage());

        ioUtil.writeFileWithExistCode(path + projectName + "/Response/List" + m_entity.getName() + "Response.cs", getListResponseContract);

        String getRespnseContract = ioUtil.readFile(templatePath + "/contractEntities/getEntityResponse.cs");
        getRespnseContract = getRespnseContract.replace("@entity@", m_entity.getName());
        getRespnseContract = getRespnseContract.replace("@namespace@", m_entity.getDevPackage());

        ioUtil.writeFileWithExistCode(path + projectName + "/Response/" + m_entity.getName() + "Response.cs", getRespnseContract);
        return MessageFormat.format("\t\t<Compile Include=\"Response\\{0}Response.cs\" />\n\t\t<Compile Include=\"Response\\List{0}Response.cs\" />\n", m_entity.getName());
    }

    public String genOperations(String path) {

        String code = "";
        List<EntityOperation> ops = m_entity.getOperations();
        for (EntityOperation oneOp : ops) {

            String commentCode = "";
            if (oneOp.getDocumentation() != null) {
                String lines[] = oneOp.getDocumentation().split("\n");
                for (String line : lines) {
                    commentCode += "\t\t\t//" + line + "\n";
                }
            }

            if (!oneOp.getName().startsWith("manage")) {
                if (oneOp.getParameters() != null) {
                    String setValue = "";
                    String targetCode = ioUtil.readFile(templatePath + "/customApiOperation.cs");
                    String opName = "";
                    for (OperationParameter onePara : oneOp.getParameters()) {

                        String attName = onePara.getName();
                        String attType = onePara.getTypeStr();
                        String TypeValueof = "";
                        String strType = "";

                        if (!"return".equals(onePara.getDirection())) {
                            if (attType.equals("String")) {
                                TypeValueof = "ValueOfString";
                                strType = "string";
                            } else if (attType.equals("int")) {
                                TypeValueof = "ValueOfInt32";
                                strType = "int";
                            } else if (attType.equals("Date")) {
                                TypeValueof = "ValueOfDateTime";
                                strType = "DateTime";
                            } else if (attType.equals("double")) {
                                TypeValueof = "ValueOfDouble";
                                strType = "double";
                            } else {
                                continue;
                            }

                            setValue += MessageFormat.format("\t\t\t\t{0} {1} = TypeConvert.{2}(RequestResolve.GetValue(para, \"{1}\"));\r\n", strType, attName, TypeValueof);
                        } else {
                            opName = MessageFormat.format("{0}", attType);
                        }
                    }
                    code += targetCode.replace("@operation@", oneOp.getName())
                            .replace("@document@", commentCode)
                            .replace("@setValue@", setValue)
                            .replace("@OpResponse@", opName);

                }
            }
        }

        String ApiCode = ioUtil.readFile(templatePath + "/EntityManageActionCustomApi.cs");

        ApiCode = ApiCode.replace("@entity@", m_entity.getName());
        ApiCode = ApiCode.replace("@namespace@", m_entity.getDevPackage());
        ApiCode = ApiCode.replace("@project@", projectName);
        ApiCode = ApiCode.replace("@targetEntity@", code);
        String devPackPath = m_entity.getDevPackage().replace(".", "/");

        ioUtil.writeFileWithExistCode(path + projectName + "/Control/" + m_entity.getName() + "Control.ashx.cs", ApiCode);

        String codeWebHandler = MessageFormat.format("<%@ WebHandler Language=\"C#\" CodeBehind=\"{0}Control.ashx.cs\" Class=\"{1}.Control.{0}Control\" %>", m_entity.getName(), m_entity.getDevPackage());
        ioUtil.writeFileWithExistCode(path + projectName + "/Control/" + m_entity.getName() + "Control.ashx", codeWebHandler);

        return MessageFormat.format("\t<Content Include=\"Control\\{0}Control.ashx\" />\n\t<Compile Include=\"Control\\{0}Control.ashx.cs\">\n\t<DependentUpon>{0}Control.ashx</DependentUpon>\n\t</Compile>\n", m_entity.getName());

    }

    public static void genCommon(String path, String templatePath, String sqlMap) {
        String code = ioUtil.readFile(templatePath + "/MapperInstance.cs");
        code = code.replace("@namespace@", projectName);
        ioUtil.writeFileWithExistCode(path + projectName + "/Common/MapperInstance.cs", code);

        code = ioUtil.readFile(templatePath + "/RequestResolve.cs");
        code = code.replace("@namespace@", projectName);
        ioUtil.writeFileWithExistCode(path + projectName + "/Common/RequestResolve.cs", code);

        code = ioUtil.readFile(templatePath + "/TypeConvert.cs");
        code = code.replace("@namespace@", projectName);
        ioUtil.writeFileWithExistCode(path + projectName + "/Common/TypeConvert.cs", code);

        code = ioUtil.readFile(templatePath + "/SqlMap.config");
        code = code.replace("@sqlMap@", sqlMap);
        ioUtil.writeFileWithExistCode(path + projectName + "/SqlMap.config", code);

        code = ioUtil.readFile(templatePath + "/providers.config");
        ioUtil.writeFileWithExistCode(path + projectName + "/providers.config", code);

        code = ioUtil.readFile(templatePath + "/Web.config");
        ioUtil.writeFileWithExistCode(path + projectName + "/Web.config", code);
    }

    public static void genSlnProject(String path, String templatePath, String csprojectContent) {

        String code = ioUtil.readFile(templatePath + "/vssln.sln");
        code = code.replace("@project@", projectName);
        code = code.replace("@sln@", projectName);
        ioUtil.writeFileWithExistCode(path + projectName + ".sln", code);

        code = ioUtil.readFile(templatePath + "/vsproject.csproj");
        code = code.replace("@project@", projectName);
        code = code.replace("@ItemGroup@", csprojectContent);
        ioUtil.writeFileWithExistCode(path + projectName + "/" + projectName + ".csproj", code);

    }

    //Entity
    public String genItem(String path) {
        String imports = m_entity.getImports();

        String content = "";

        for (EntityAttribute val : m_entity.getAttributes()) {
            String attName = val.getName();
            String attType = val.getTypeStr();
            String strType = "";


            if (attType.equals("String")) {
                strType = "string";
            } else if (attType.equals("int")) {
                strType = "int";
            } else if (attType.equals("Date")) {
                strType = "DateTime";
            } else if (attType.equals("double")) {
                strType = "double";
            } else {
                strType = attType;
            }
            if (val.getMultiplicity() != null) {
                content += MessageFormat.format("\t\tpublic List<{0}> {1} ", strType, attName) + "{get; set;}\n";
            } else {
                content += MessageFormat.format("\t\tpublic {0} {1} ", strType, attName) + "{get; set;}\n";
            }
        }


        String code = "";
        code = ioUtil.readFile(templatePath + "/persistentEntity.cs");
        code = code.replace("@imports@", imports);
        code = code.replace("@entity@", m_entity.getName());
        code = code.replace("@content@", content);
        code = code.replace("@namespace@", m_entity.getDevPackage());
        code = code.replace("@project@", projectName);


        String devPackPath = m_entity.getDevPackage().replace(".", "/");

        ioUtil.writeFileWithExistCode(path + projectName + "/Entity/" + m_entity.getName() + ".cs", code);
        return MessageFormat.format("\t\t<Compile Include=\"Entity\\{0}.cs\" />\n", m_entity.getName());
    }

    private String getTableName(Entity entity) {
        String tableName = "";
        for (int i = 0; i < entity.getName().length(); i++) {
            char ch = entity.getName().charAt(i);
            if (ch <= 'Z' && ch >= 'A')
                tableName += "_" + ch;
            else
                tableName += ch;
        }
        tableName = tableName.toUpperCase();

        return "D" + tableName;
    }

    private String getColumnName(String name) {
        String columnName = "";
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (ch <= 'Z' && ch >= 'A')
                columnName += "_" + ch;
            else
                columnName += ch;
        }
        columnName = columnName.toUpperCase();

        return columnName;
    }
}

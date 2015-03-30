package com.zpc.timesheet.utility;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.math.BigDecimal;
import java.util.Properties;

/**
 * Created by Administrator on 10/20/2014.
 */
public class EntityEngineXMLParser {
    private static String uri;
    private static String username;
    private static String password;
    private static String jdbcUri;
    private static String jdbcUser;
    private static String jdbcPassword;
    private static String hibernateSqlShow;
    private static String hibernateSqlGenerateddl;
    private static String hibernateSqlDialect;

    public static void main(String[] args) {
        String pathOfEntityEngineXML = args[0];
        String pathOfMySQLDbProperties = args[1];
        String comment = "\n\nCopyright (c) 2010. Axon Framework\n" +
                "\n" +
                " Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                " you may not use this file except in compliance with the License.\n" +
                " You may obtain a copy of the License at\n" +
                "\n" +
                "     http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                " Unless required by applicable law or agreed to in writing, software\n" +
                " distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                " WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                " See the License for the specific language governing permissions and\n" +
                " limitations under the License." +
                "\n\n\n";
        try {
            parseEntityEngineXML(pathOfEntityEngineXML, pathOfMySQLDbProperties);
            changePropertiesInMySqlDbProperties(pathOfMySQLDbProperties);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            new File(pathOfEntityEngineXML).delete();
        }
    }

    private static void changePropertiesInMySqlDbProperties(String pathOfMySQLDbProperties) {
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(new File(pathOfMySQLDbProperties)));
            out.println(jdbcUri);
            out.println(jdbcUser);
            out.println(jdbcPassword);
            out.println(hibernateSqlShow);
            out.println(hibernateSqlDialect);
            out.println(hibernateSqlGenerateddl);
			out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            out.close();
        }
    }

    private static void parseEntityEngineXML(String pathOfEntityEngineXML, String pathOfMySQLDbProperties) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(pathOfEntityEngineXML);
            NodeList nList = document.getElementsByTagName("datasource");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                Element eElement = (Element) nNode;
                if (eElement.getAttribute("name").equals("localmysql")) {
                    NamedNodeMap attributes = eElement.getElementsByTagName("inline-jdbc").item(0).getAttributes();
                    uri = attributes.getNamedItem("jdbc-uri").getNodeValue().replaceAll("\\&","&amp;");
                    username = attributes.getNamedItem("jdbc-username").getNodeValue();
                    password = attributes.getNamedItem("jdbc-password").getNodeValue();
                    String connectionUrl = getUri(uri);
                    jdbcUri = "jdbc.url="+connectionUrl;
                    jdbcPassword = "jdbc.password="+password;
                    jdbcUser = "jdbc.username="+username;
                    hibernateSqlGenerateddl = "hibernate.sql.generateddl=true";
                    hibernateSqlShow = "hibernate.sql.show=false";
                    hibernateSqlDialect = "hibernate.sql.dialect=org.hibernate.dialect.MySQL5InnoDBDialect";
                }
            }
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        } catch (SAXException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static String getUri(String uri) {
        StringBuilder br = new StringBuilder();
        br.append(uri.substring(0, uri.indexOf("?")+1));
        br.append("rewriteBatchedStatements=true&amp;");
        br.append(uri.substring(uri.indexOf("?")+1, uri.length()));
        return br.toString();
    }
}

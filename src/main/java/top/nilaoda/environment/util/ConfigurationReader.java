package top.nilaoda.environment.util;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
* @author nilaoda
* @version 1.0
* @description 读取XML配置文件，返回指定对象
* @date 2019/11/4
* @time 9:06
*/

public class ConfigurationReader {
    private String configFile = "src/main/resources/config.xml";

    public Map<String, Object> readObjects() {
        Map<String, Object> map = new LinkedHashMap<>();

        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new File(configFile));
            //获取根元素
            Element rootElement = document.getRootElement();
            //获取所有子元素
            List<Element> allChildElement = rootElement.elements();
            for (Element element : allChildElement) {
                String className = element.attributeValue("class");
                Object o = Class.forName(className).newInstance();
                map.put(element.getName(), o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public Map<String, String> readConfig() {
        Map<String, String> map = new LinkedHashMap<>();

        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new File(configFile));
            //获取根元素
            Element rootElement = document.getRootElement();
            //获取所有子元素
            List<Element> allChildElement = rootElement.elements();
            for (Element element : allChildElement) {
                String elementName = element.getName();
                //获取所有子元素
                List<Element> childElement = element.elements();
                for (Element e_e : childElement) {
                    //("server_port", "8888")
                    map.put(elementName + "_" + e_e.getName(), e_e.getStringValue().trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public Properties readConfigAsProperties() {
        Properties p = new Properties();
        Map<String, String> map = readConfig();
        for (Map.Entry<String, String> e : map.entrySet()) {
            p.put(e.getKey(), e.getValue());
        }
        return p;
    }
}

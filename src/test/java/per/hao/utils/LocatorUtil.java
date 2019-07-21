package per.hao.utils;

import org.apache.commons.collections.map.HashedMap;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import per.hao.beans.LocatorInfo;
import per.hao.beans.PageInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocatorUtil {

    private static final Logger log = LoggerFactory.getLogger(LocatorUtil.class);
    private static String filePath;
    // Map<filePath, Map<pageName, PageInfo>>
    private static Map<String, Map<String, PageInfo>> pageInfoMaps = new HashMap<>();

    /**
     * 重写构造函数
     *
     * @param filePath locator文件在resources下的路径
     *                 相对于配置文件目录，如：/locator/locator.xml
     *                 即为resources下的locator目录中的locator.xml
     * */
    public LocatorUtil(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 确定只解析一次
     * */
    private static void sureInit() {
        if (pageInfoMaps.get(filePath) == null) {
            loadXML();
        }
    }

    /**
     * 解析locator定位xml文件
     * */
    private static void loadXML() {
        SAXReader saxReader = new SAXReader();
        Document document = null;

        try {
            document = saxReader.read(LocatorUtil.class.getResourceAsStream(filePath));
        } catch (DocumentException e) {
            log.error("open locator file failed:", e);
        }

        if (document != null) {
            log.info("open locator file succeed:" + filePath);
        }

        // 页面page遍历
        Element root = document.getRootElement();
        List<Element> pageElements = root.elements("page");
        Map<String, PageInfo> pageInfoMap = new HashMap<>();

        for (Element pageElement : pageElements) {
            String pageName = pageElement.attributeValue("pageName");
            String url = pageElement.attributeValue("url");
            String defaultTimeOut = pageElement.attributeValue("defaultTimeOut");

            // 定位器locator遍历
            List<Element> locatorElements = pageElement.elements("locator");
            Map<String, LocatorInfo> locatorInfoMap = new HashedMap();
            for (Element locatorElement : locatorElements) {
                String name = locatorElement.attributeValue("name");
                String by = locatorElement.attributeValue("by");
                String value = locatorElement.attributeValue("value");
                String timeOut = locatorElement.attributeValue("timeOut");
                if (timeOut == null || "".equals(timeOut)) {
                    timeOut = defaultTimeOut;
                }

                locatorInfoMap.put(name,
                        new LocatorInfo(name, by, value, Integer.parseInt(timeOut)));
            }

            pageInfoMap.put(pageName, new PageInfo(pageName, url, locatorInfoMap));
        }
        pageInfoMaps.put(filePath, pageInfoMap);
        log.info("parse locator file succeed:" + filePath);
    }

    /**
     * 获取测试界面url
     *
     * @param filePath locator文件在resources下的路径
     *                 相对于配置文件目录，如：/locator/locator.xml
     *                 即为resources下的locator目录中的locator.xml
     *
     * @param pageName 页面名称，locator.xml中page节点pageName元素属性
     *
     * @return 页面url
     * */
    public static String getURL(String filePath, String pageName) {
        new LocatorUtil(filePath);
        sureInit();
        String url = LocatorUtil.pageInfoMaps.get(filePath).get(pageName).getUrl();
        log.debug("get url:" + url);
        return url;
    }

    /**
     * 获取定位器
     *
     * @param filePath locator文件在resources下的路径
     *                 相对于配置文件目录，如：/locator/locator.xml
     *                 即为resources下的locator目录中的locator.xml
     * @param pageName 页面名称，locator.xml中page节点元素属性
     * @param name     定位器名称 locator.xml中locator节点name元素属性
     *
     * @return 定位器
     * */

    public static LocatorInfo getLocator(String filePath, String pageName, String name) {
        new LocatorUtil(filePath);
        sureInit();
        LocatorInfo locatorInfo = LocatorUtil.pageInfoMaps
                .get(filePath)
                .get(pageName)
                .getLocatorInfoMap().get(name);
        log.debug("get locator:" + locatorInfo);
        return locatorInfo;
    }
}

package per.hao.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WebDriverUtil {

    private static final Logger log = LoggerFactory.getLogger(WebDriverUtil.class);
    /* WebDriver class */
    private static Class clazz;
    /* WebDriver Object */
    private static Object obj ;

    /**
     * 根据driver.xml文件初始化获取浏览器WebDriver对象
     *
     * @return WebDriver 浏览器WebDriver对象
     * */
    public static WebDriver getDriver() {
        Document document = null;
        Element driverElement= null;
        String driverClassName =null;

        SAXReader reader = new SAXReader();
        try {
            document = reader.read(WebDriverUtil.class.getResourceAsStream("/driver.xml"));
        } catch (DocumentException e) {
            log.error("read driver.xml failed", e);
        }

        /** 获取驱动类名 */
        Element rootElement = document.getRootElement();
        // driver index
        int index = Integer.parseInt(rootElement.attributeValue("driverIndex"));
        /** 遍历name节点 */
        List<Element> driverNameElements = rootElement.elements("name");
        for (Element driverNameElement : driverNameElements) {
            if (index == Integer.parseInt(driverNameElement.attributeValue("index"))) {
                driverClassName = driverNameElement.attributeValue("value");
                driverElement = driverNameElement;
            }
        }

        /** 获取驱动class */
        try {
            clazz = Class.forName(driverClassName);
            log.info("get class:" + driverClassName);
        } catch (ClassNotFoundException e) {
            log.error("get class error", e);
        }

        /**
         * 下面是解析XML系统参数并设置
         */
        Element propertiesElement = driverElement.element("properties");
        List<Element> propertyElements = propertiesElement.elements("property");
        //设置系统参数(driver路径等)
        for (Element property : propertyElements) {
            System.setProperty(property.attributeValue("name"), formatPath(property.attributeValue("value")));
            log.info("set property:" + property.attributeValue("name") + "=" +formatPath(property.attributeValue("value")));
        }

        //设置能力（ie的话，需要设置忽略域设置等级 以及忽略页面百分比的能力）
        Element capabilitiesElement = driverElement.element("capabilities");
        if (capabilitiesElement != null) {
            DesiredCapabilities realCapabilities = new DesiredCapabilities();
            List<Element> capabilitiesElements = capabilitiesElement.elements("capability");
            for (Element capability : capabilitiesElements) {
                //遍历能力列表，并给能力赋值
                if ("boolean".equals(capability.attributeValue("type"))) {
                    realCapabilities.setCapability(capability.attributeValue("name"),
                            Boolean.parseBoolean(capability.attributeValue("value")));
                } else if ("string".equals(capability.attributeValue("type"))) {
                    realCapabilities.setCapability(capability.attributeValue("name"),
                            capability.attributeValue("value"));
                }
                log.info("set capability:" + capability.attributeValue("name") + "=" + capability.attributeValue("value"));
            }
        }

        /** 如果是chrome设置chrome的一些属性 */
        Element optionsElement = driverElement.element("options");
        if (optionsElement != null) {
            ChromeOptions chromeOptions = new ChromeOptions();
            for (Element optionElement : (List<Element>) optionsElement.elements()) {
                if ("binary".equals(optionElement.attributeValue("type"))) {
                    String binary = formatPath(optionElement.getStringValue());
                    chromeOptions.setBinary(binary);
                    log.info("set chrome Binary:" + binary);
                }
            }

            WebDriver driver = new ChromeDriver(chromeOptions);
            log.info("get driver succeed:" + driverClassName);
            return driver;
        }


        /*
         * 通过反射，创建驱动对象
         */
        try {
            obj = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        log.info("get driver succeed:" + driverClassName);
        return (WebDriver) obj;
    }

    /**
     * 替换xml中出现的需要替换的变量
     *
     * @param path String路径
     *
     * @return String 替换之后的路径
     * */
    private static String formatPath(String path) {
        // chrome不一定安装在用户目录下，去掉
//        if (path.contains("{user.home}")) {
//            path = path.replace("{user.home}", System.getProperty("user.home"));
//        }
        if (path.contains("{default.driver.path}")) {
            path = path.replace("{default.driver.path}", "src/test/resources");
        }

        return path;
    }

}

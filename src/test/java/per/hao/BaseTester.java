package per.hao;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestNGMethod;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import per.hao.annotations.DataSource;
import per.hao.annotations.LocatorSource;
import per.hao.beans.LocatorInfo;
import per.hao.utils.DataSourceType;
import per.hao.utils.ExcelReader;
import per.hao.utils.LocatorUtil;
import per.hao.utils.WebDriverUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.regex.Matcher;


public class BaseTester<T> {

    public static final Logger log =
            LoggerFactory.getLogger(BaseTester.class);
    /** driver */
    public static WebDriver driver;

    /** 当前定位器指针, 用来指向PageInfo的pageName */
    private static String pageName;

    /** 浏览器导航栏对象，封装导航栏方法用 */
    private static WebDriver.Navigation navigation;

    /**
     * 测试集启动前初始化
     * */
    @BeforeSuite
    protected void beforeSuit() {
        driver = WebDriverUtil.getDriver();
        // 窗口最大化
        driver.manage().window().maximize();
        navigation = driver.navigate();
    }

    /**
     * 测试集后置操作
     * */
    @AfterSuite
    protected void afterSuit() {
        driver.quit();
    }
    //-----------------------------浏览器管理-----------------------------------
    /**
     * 清空cookies
     * */
    protected void deleteAllCookies() {
        driver.manage().deleteAllCookies();
    }

    // ------------------------------断言-----------------------------------
    /**
     * 判断expected是否包含actual
     *
     * @param expected 文本
     * @param actural 文本
     * */
    protected void assertTextContain(String expected, String actural) {
        Assert.assertTrue(expected.contains(actural),
                expected + " [not contain] " + actural);
    }

    /**
     * 判断expected是否等于actual
     *
     * @param expected T expected
     * @param actural T actural
     * */
    public void assertTextEqual(T expected, T actural) {
        Assert.assertEquals(expected, actural);
    }

    /**
     * 判断是否为true
     *
     * @param b 参数b
     * */
    public void assertTrue(boolean b) {
        Assert.assertTrue(b);
    }

    // ------------------------------获取指定属性-----------------------------------
    /**
     * 获取页面title标题
     *
     * @return String
     * */
    protected String getTitle() {
        return driver.getTitle();
    }

    /**
     * 获取定位器定位到元素的文本内容
     *
     * @param locatorName 定位器名称 locator.xml中locator节点name元素属性
     *
     * @return String
     * */
    protected String getText(String locatorName) {
        return getWebElement(locatorName).getText();
    }

    /**
     * 获取定位器定位到元素是否被勾选
     *
     * @param locatorName 定位器名称 locator.xml中locator节点name元素属性
     *
     * @return boolean
     * */
    protected boolean isSelected(String locatorName) {
        return getWebElement(locatorName).isSelected();
    }

    // ------------------------------元素基本操作-----------------------------------
    /**
     * WebElement   根据定位器定位并点击
     *
     * @param locatorName 定位器名称 locator.xml中locator节点name元素属性
     * */
    protected void click(String locatorName) {
        getWebElement(locatorName).click();
    }

    /**
     * WebElement   根据定位器定位并输入指定内容
     *
     * @param locatorName 定位器名称 locator.xml中locator节点name元素属性
     * @param input 输入内容
     * */
    protected void input(String locatorName, String input) {
        getWebElement(locatorName).sendKeys(input);
    }

    /**
     * WebElement   根据定位器定位并清空
     *
     * @param locatorName 定位器名称 locator.xml中locator节点name元素属性
     * */
    protected void clear(String locatorName) {
        getWebElement(locatorName).clear();
    }

    // ------------------------------导航栏基本操作-----------------------------------
    /**
     * navigation   浏览器后退操作
     * */
    protected void back() {
        navigation.back();
    }

    /**
     * navigation   浏览器前进操作
     * */
    protected void forward() {
        navigation.forward();
    }

    /**
     * navigation   浏览器刷新操作
     * */
    protected void refresh() {
        navigation.refresh();
    }

    /**
     * navigation   打开指定页面
     *
     * @param pageName 页面名称，locator.xml中page节点pageName元素属性
     * */
    protected void open(String pageName) {
        String url =
                LocatorUtil.getURL(new GetLocatorFilePath().invok().getFilePath(), pageName);
        go(url);
    }

    /**
     * navigation   打开指定url
     *
     * @param url 连接串
     * */
    protected void go(String url) {
        navigation.to(url);
    }

    /**
     * 睡眠指定时间
     *
     * @param millis 毫秒
     * */
    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("sleep error", e);
        }
    }

    /**
     * 将定位器指针移动到指定界面集合下
     * */
    protected void locate(String pageName) {
        this.pageName = pageName;
    }

    /**
     * 根据定位器名称获取界面元素
     *
     * @param locatorName 定位器名称 locator.xml locator节点name属性
     *
     * @return WebElement
     * */
    protected WebElement getWebElement(String locatorName) {
        String filePath = new GetLocatorFilePath().invok().getFilePath();
        return getWebElement(filePath, locatorName);
    }

    /**
     * 根据locater文件的路径、页面名称，定位器名称获取界面元素
     *
     * @param filePath locator文件在resources下的路径
     *                 相对于配置文件目录，如：/locator/locator.xml
     *                 即为resources下的locator目录中的locator.xml
     *
     * @param locatorName 定位器名称 locator.xml locator节点name属性
     *
     * @return WebElement
     * */
    protected WebElement getWebElement(String filePath, String locatorName) {
        LocatorInfo locator = LocatorUtil.getLocator(filePath, pageName, locatorName);
        String byMethodName = locator.getBy();
        String value = locator.getValue();
        int timeOut = locator.getTimeOut();

        Class<By> byClass = By.class;
        Method declaredMethod;

        try {
            declaredMethod = byClass.getDeclaredMethod(byMethodName, String.class);
            By by = (By) declaredMethod.invoke(null, value);

            WebDriverWait wait = new WebDriverWait(driver, timeOut);
            WebElement until = wait.until(new ExpectedCondition<WebElement>() {
                public WebElement apply(WebDriver input) {
                    return input.findElement(by);
                }
            });
            return until;
        } catch (NoSuchMethodException e) {
            log.error("locator by may be error in " + locatorName, e);
        } catch (IllegalAccessException e) {
            log.error("", e);
        } catch (InvocationTargetException e) {
            log.error("", e);
        }
        return null;
    }

    /**
     * 内部工具类，用来获取LocatorSource注解中的locator filePath
     * */
    private class GetLocatorFilePath {
        private String filePath;

        ITestNGMethod iTestNGMethod = Reporter.getCurrentTestResult().getMethod();
        Method method = iTestNGMethod.getMethod();
        LocatorSource locatorSource = method.getAnnotation(LocatorSource.class);

        /**
         * 初始化
         * */
        public GetLocatorFilePath invok() {
            filePath = locatorSource.filePath();
            return this;
        }

        /**
         * 获取LocatorSource注解中的locator filePath
         * */
        public String getFilePath() {
            return filePath;
        }
    }

    /**
     * 数据提供公共接口
     * */
    @DataProvider(name = "getData")
    public static Iterator<Object[]> getData(Method method) {

        DataSource dataSource = null;

        /** 数据源注解存在判断 */
        if (method.isAnnotationPresent(DataSource.class)) {
            dataSource = method.getAnnotation(DataSource.class);
        } else {
            log.error("未指定@DataSource注解却初始化了dataProvider");
        }

        /** 根据数据源类型返回对应数据迭代器 */
        if (DataSourceType.CSV
                .equals(dataSource.dataSourceType())) {

            // CSVReader

        } else if (DataSourceType.POSTGRESQL
                .equals(dataSource.dataSourceType())) {

            // PostgresqlReader

        }

        /* 默认读取excel */
        // 根据名称
        if (!"".equals(dataSource.name())) {

            return ExcelReader.getDataByName(
                    dealFilePath(dataSource.filePath()), dataSource.name());
            // 根据锚点
        } else if (!"".equals(dataSource.locate())) {

            return ExcelReader.getDataByLocate(
                    dealFilePath(dataSource.filePath()), dataSource.sheetName(), dataSource.locate());
           // 读取整个sheet页
        } else {

            return ExcelReader.getDataBySheetName(
                    dealFilePath(dataSource.filePath()), dataSource.sheetName());

        }
    }

    /**
     * 如果只存在文件名，则拼接默认读取目录，否则使用指定的路径
     *
     * @param filePath 文件路径
     * */
    private static  String dealFilePath(String filePath) {
        if (!filePath.matches(".*[/\\\\].*")) {
            filePath = "src/test/resources/data/" + filePath;
        }

        return new File(filePath.replaceAll("[/\\\\]+",
                Matcher.quoteReplacement(File.separator))).getAbsolutePath();
    }
}

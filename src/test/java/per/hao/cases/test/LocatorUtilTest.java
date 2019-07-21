package per.hao.cases.test;

import org.testng.annotations.Test;
import per.hao.beans.LocatorInfo;
import per.hao.utils.LocatorUtil;

import static org.testng.Assert.*;

public class LocatorUtilTest {

    @Test(description = "测试获取URL")
    public void testGetURL() {
        String url = LocatorUtil
                .getURL("/locator/zendao.xml", "百度首页");
        System.out.println(url);
    }

    @Test(description = "测试获取locator")
    public void testGetLocator() {
        LocatorInfo locatorInfo = LocatorUtil
                .getLocator("/locator/zendao.xml", "百度首页"
                        , "查询框");
        System.out.println(locatorInfo);
    }
}
package per.hao.cases;

import io.qameta.allure.*;
import org.testng.annotations.Test;
import per.hao.BaseTester;
import per.hao.annotations.DataSource;
import per.hao.annotations.LocatorSource;

import java.util.Map;

import static io.qameta.allure.Allure.step;

public class BaiDuExampleTest extends BaseTester {

    @Test(description = "百度查询")
    @DataSource(filePath = "param.xlsx", name = "百度测试")
    @Description("输入关键字进行百度查询")
    @LocatorSource(filePath = "/locator/zendao.xml")
    public void simpleTest1(Map<String, String> row) {
        deleteAllCookies();

        // 定位到百度界面
        locate("百度首页");
        open("百度首页");

        step("输入查询关键字");
        sleep(2000);
        input("查询框", row.get("关键字"));

        step("点击查询");
        click("百度一下");
        sleep(3000);
    }
}

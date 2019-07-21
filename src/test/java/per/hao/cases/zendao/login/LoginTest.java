package per.hao.cases.zendao.login;

import io.qameta.allure.*;
import org.testng.annotations.Test;
import per.hao.BaseTester;
import per.hao.annotations.DataSource;
import per.hao.annotations.LocatorSource;

import java.util.Map;

import static io.qameta.allure.Allure.step;

public class LoginTest extends BaseTester {
    @Test(description = "用户登录正确数据测试")
    @Severity(SeverityLevel.CRITICAL)
    @DataSource(filePath = "zendao.xlsx", name = "用户登录正确数据")
    @Description("登录禅道系统，验证界面显示用户姓名是否为当前用户")
    @Step("登录测试")
    @LocatorSource(filePath = "/locator/zendao.xml")
    public void simpleTest1(Map<String, String> row) {
        deleteAllCookies();

        locate("LoginPage");// 切换定位器到xml的LoginPage
        open("LoginPage");
        sleep(1500);

        step("输入用户名密码并登录");
        input("用户名", row.get("用户名"));
        input("密码", row.get("密码"));
        sleep(500);
        click("登录");
        sleep(2000);

        locate("HomePage");
        step("校验用户姓名");
        assertTextEqual(getText("用户姓名"), row.get("预期结果"));

        step("退出当前用户");
        click("系统下拉框");
        sleep(500);
        click("退出");
    }
}

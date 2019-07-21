package per.hao.cases.test;

import io.qameta.allure.*;
import org.testng.annotations.Test;
import per.hao.annotations.DataSource;
import java.util.Map;
import static io.qameta.allure.Allure.*;


public class ExcelReaderTest {

    @Test(description = "测试默认读取Sheet1")// 这个desciption是标题
    @Severity(SeverityLevel.CRITICAL)// 测试优先级
    @DataSource(filePath = "param.xlsx")
    @Description("在不指定Sheet名称的情况下，测试能否读取默认数据。")// 这个才是描述
    @Step("第一步先测试一下能否读取Sheet1中的数据")// 父步骤
    @Link(name = "默认读取Sheet1需求", url = "https://www.cnblogs.com/h-zhang/")// 需求
    @Issue("https://www.cnblogs.com/h-zhang/")// BUG
    public void simpleTest1(Map<String, String> row) {

        step("step 1: 获取用例数据");
        String caseName = row.get("用例名称");

        step("step 2: 返回实际结果" + caseName);
    }

    @Test(description = "测试指定Sheet名称读取")
    @DataSource(filePath = "param.xlsx", sheetName = "Sheet读取")
    public void simpleTest2(Map<String, String> row) {
        step("step 1: 获取用例数据");
        String caseName = row.get("用例名称");

        step("step 2: 返回实际结果" + caseName);
    }

    @Test(description = "测试指定名称读取")
    @DataSource(filePath = "param.xlsx", name = "测试name读取")
    public void simpleTest3(Map<String, String> row) {
        step("step 1: 获取用例数据");
        String caseName = row.get("用例名称");

        step("step 2: 返回实际结果" + caseName);
    }

    @Test(description = "测试指定locate读取")
    @DataSource(filePath = "param.xlsx", sheetName = "locate读取", locate = "I'm locate")
    public void simpleTest4(Map<String, String> row) {
        step("step 1: 获取用例数据");
        String caseName = row.get("用例名称");

        step("step 2: 返回实际结果" + caseName);
    }
}


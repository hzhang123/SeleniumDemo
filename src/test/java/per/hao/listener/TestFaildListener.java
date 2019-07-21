package per.hao.listener;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import per.hao.BaseTester;

public class TestFaildListener extends TestListenerAdapter {
    @Override
    public void onTestFailure(ITestResult iTestResult) {
        screenShot();
    }

    /**
     * 截图并添加为附件
     *
     * @return byte[]附件
     * */
    @Attachment(value = "Page screenshot",type = "image/png")
    public byte[] screenShot() {
        return ((TakesScreenshot) BaseTester.driver).getScreenshotAs(OutputType.BYTES);
    }
}

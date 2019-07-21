package per.hao;

import org.testng.Assert;
import per.hao.beans.LocatorInfo;
import per.hao.utils.LocatorUtil;
import per.hao.utils.WebDriverUtil;

public class TestClass {
    public static void main(String[] args) {
        BaseTester baseTester = new BaseTester();
        baseTester.assertTextEqual(true, false);
    }
}

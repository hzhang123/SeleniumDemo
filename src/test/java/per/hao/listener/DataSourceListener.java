package per.hao.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;
import per.hao.BaseTester;
import per.hao.annotations.DataSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 监听测试是否存在{@code DataSource.CLASS}注解:
 * 1. 如果存在@DataSource注解且@Test注解中未对dataProvider赋值
 *    则指定{@code BaseTester.CLASS}中提供的getData数据提供者方法
 * 2. 如果不存在@DataSource注解或@Test注解中已经对dataProvider赋值
 *    则不修改dataProvider
 *
 * 测试方法@Test注解dataProviderClass如果值为Object.class则修改
 * 为BaseTest.class
 * */
public class DataSourceListener implements IAnnotationTransformer {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceListener.class);

    /**
     * 本监听的入口，监听每次测试方法的调用，并设置相应参数
     *
     * @param iTestAnnotation 提供对@Test注解操作的对象
     * @param aClass
     * @param constructor
     * @param method 本次触发监听的测试方法
     * */
    @Override
    public void transform(ITestAnnotation iTestAnnotation, Class aClass, Constructor constructor, Method method) {
        // 测试为null
        if (iTestAnnotation == null || method == null) {
            return;
        }

        /** 判断并修改@Test注解 dataProvider 值 */
        modifyDataProvider(iTestAnnotation, method);

        /** 判断并修改@Test注解 dataProviderClass 值 */
        if (iTestAnnotation.getDataProviderClass() == null) {
            iTestAnnotation.setDataProviderClass(BaseTester.class);
            logger.debug("dataProviderClass设置为: {}", BaseTester.class);
        } else {
            logger.debug("dataProviderClass已经指定: {}", iTestAnnotation.getDataProviderClass());
        }
    }

    /**
     * 对@Test注解dataProvider数据提者判断修改的方法
     *
     * @param iTestAnnotation 提供对@Test注解操作的对象
     * @param method 本次触发监听的测试方法
     * */
    private void modifyDataProvider(ITestAnnotation iTestAnnotation, Method method) {

        /** 如果存在@DataSource注解 */
        if (method.isAnnotationPresent(DataSource.class)) {
            if ("".equals(iTestAnnotation.getDataProvider())) {
                iTestAnnotation.setDataProvider("getData");
                logger.debug("dataProvider设置为: getData");
            } else {
                logger.debug("dataProvider已指定: {}", iTestAnnotation.getDataProvider());
            }

        /* 未指定@DataSource注解却指定了dataProvider */
        } else if ((! method.isAnnotationPresent(DataSource.class)) &&
                ! "".equals(iTestAnnotation.getDataProvider())) {
            logger.error("未指定@DataSource注解却初始化了dataProvider");
        }
    }
}

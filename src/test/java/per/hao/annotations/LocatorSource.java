package per.hao.annotations;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LocatorSource {
    /**
     * locator文件在resources下的路径
     *         相对于配置文件目录，如：/locator/locator.xml
     *         即为resources下的locator目录中的locator.xml
     * */
    String filePath();
}

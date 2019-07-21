package per.hao.annotations;

import per.hao.utils.DataSourceType;

import java.lang.annotation.*;

/**
 * 标注参数化数据源, 默认从提供Excel读取
 * */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataSource {

    DataSourceType dataSourceType() default DataSourceType.EXCEL;

    String filePath() default "";

    String sheetName() default "Sheet1";// 除根据name外需要指定, 默认读取Sheet1

    String locate() default "";// 定位器, 左上角第一个cell(ReadExcel用)

    String name() default ""; // 可指定Excel名称读取

    String sql() default "";

}


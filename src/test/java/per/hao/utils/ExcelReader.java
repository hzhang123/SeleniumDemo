package per.hao.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelReader implements Iterator<Object[]> {

    private static final Logger log = LoggerFactory.getLogger(ExcelReader.class);

    private static final char[] LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private InputStream in = null;
    private Workbook workbook = null;
    private Sheet sheet = null;

    /* 当前行id 列id */
    private int curRowNum = 1;
    private int curColNum = 0;

    /* 当前工作表中可读最大行 */
    private int maxRowNum = 0;
    private int maxColNum = 0;

    /* 读取行列数量记录 */
    private int readRowNum = 0;
    private int readColNum = 0;

    /* 列名记录 */
    List<String> colNames = new ArrayList<>();


    /**
     * 根据sheetName初始化
     *
     * @param filePath excel文件路径
     * @param sheetName 读取的sheet名称
     * */
    public static ExcelReader getDataBySheetName(String filePath, String sheetName) {
        return new ExcelReader(filePath, sheetName, "", "", 1);
    }

    /**
     * 根据定位器初始化
     *
     * @param filePath excel文件路径
     * @param sheetName 读取的sheet名称
     * @param locate 定位器名称
     * */
    public static ExcelReader getDataByLocate(String filePath, String sheetName, String locate) {
        return new ExcelReader(filePath, sheetName, locate, "", 2);
    }

    /**
     * 根据名称初始化
     *
     * @param filePath excel文件路径
     * @param Name 名称
     * */
    public static ExcelReader getDataByName(String filePath, String Name) {
        return new ExcelReader(filePath, "", "", Name, 3);
    }


    /**
     * 私有构造方法
     *
     * @param filePath excel文件路径
     * @param sheetName 读取的sheet名称
     * @param locate 定位器名称
     * @param name 名称
     * @param type 初始化的类型
     *
     * 根据type的值来确定初始化的方法
     *             1 ： 根据sheetName初始化， filePath、sheetName不为空
     *             2 ： 根据定位器初始化， filePath、sheetName、 locate不为空
     *             3 ： 根据名称初始化， name不为空
     * */
    private ExcelReader(String filePath, String sheetName, String locate, String name, int type) {
        initWorkBook(filePath);
        if (type == 1) {// 根据sheetName初始化
            initSheet(filePath, sheetName);
            initParam();
            ininColumnName();
        } else if (type == 2) {// 根据定位器初始化
            initSheet(filePath, sheetName);
            initParam();
            registerLocate(locate);
            ininColumnName();

        } else if (type == 3) {// 根据名称初始化
            initByName(filePath, name);
        }
    }

    /**
     * 根据名称初始化当前读取区域定位信息
     *
     * @param filePath excel路径
     * @param designation 名称
     *
     * 读取到的名称需要是工作簿作用范围，匹配定位信息初始化curColNum、 curRowNum、maxColNum、maxRowNum
     * */
    private void initByName(String filePath, String designation) {
        Name name = workbook.getName(designation);
        if (name != null) {
            initSheet(filePath, name.getSheetName());

            /* 根据excel定位字符串初始化名称区域 */
            Matcher matcher = Pattern.compile("^.*?\\$(\\w+)\\$(\\d+):\\$(\\w+)\\$(\\d+)$")
                    .matcher(name.getRefersToFormula());
            if (matcher.find()) {
                curColNum = letterToDec(matcher.group(1));
                curRowNum = Integer.parseInt(matcher.group(2));
                maxColNum = letterToDec(matcher.group(3));
                maxRowNum = Integer.parseInt(matcher.group(4));
            } else {
                log.error("cannot find coordinate: {}", name.getRefersToFormula());
            }

            ininColumnName();

        } else {
            log.error("cannot find name: {}", designation);
        }

    }

    /**
     * 将Excel列定位字母转换为数字
     * 从0开始，A -> 0, AA -> 26, BA -> 53以此类推, 也就是特殊的26进制
     *
     * @param letterCoordinate 列
     *
     * @return int
     * */
    private int letterToDec(String letterCoordinate) {
        char[] cs = letterCoordinate.toCharArray();
        int decIndex = 0, i = 0;

        for (; i < cs.length; i++) {
            decIndex += Math.pow(26, (cs.length - i - 1)) * (Arrays.binarySearch(LETTER, cs[i]) + 1);
        }

        return decIndex - 1;// 从0开始 减1
    }

    /**
     * 初始化列名
     * */
    private void ininColumnName() {
        Row colNameRow = sheet.getRow(curRowNum);

        /* 遇到空的cell读取结束 */
        for (int i = 0; i < colNameRow.getLastCellNum(); i++) {
            String cellContents = getCellContents(colNameRow.getCell(i));
            if (!"".equals(cellContents)) {
                colNames.add(cellContents);
            } else {
                log.debug("Read to blank cell default read column end");
                break;
            }
        }
        curRowNum ++;
    }


    /**
     * 根据定位器初始化当前行
     *
     * @param locate 定位器
     * */
    private void registerLocate(String locate) {
        /* 至少存在1列 */
        if (maxColNum >= 1) {
            boolean flag = false;// 默认无定位器

            for (int i = 0; i < sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null) { continue; }

                Cell cell = row.getCell(0);

                /* 找到定位器，初始化当前读取位置 */
                if (locate.equals(getCellContents(cell))) {
                    flag = true;
                    curRowNum = cell.getAddress().getRow() + 1;// 从定位器下一行开始
                }

                if (flag) {
                    log.info("find locate point, read from next line");
                } else {
                    log.info("cannot find locate point, read from default line");
                }
            }
        }

    }

    /**
     * 初始化部分变量
     *
     * */
    private void initParam() {
        maxRowNum = sheet.getLastRowNum() + 1;
        maxColNum = sheet.getLastRowNum();
        readColNum = colNames.size();
    }

    /**
     * 根据Sheet名称获取Sheet
     *
     * @param sheetName sheet名称
     * */
    private void initSheet(String filePath, String sheetName) {
        /* 获取WorkBook 中的sheet */
        if (workbook != null) {
            sheet = workbook.getSheet(sheetName);
        } else {
            log.error("WorkBook Object dose not exist!");
        }

        /* sheet未获取到 */
        if (sheetName == null) {
            log.error("In {} sheet does not exist", filePath);
        } else {
            log.info("read sheet: {}", sheetName);
        }
    }

    /**
     * 根据后缀判断版本获取excel对象
     *
     * @param filePath 文件路径
     * */
    private void initWorkBook(String filePath) {
        try {
            in = new BufferedInputStream(new FileInputStream(filePath));
        /* 2003 Excel */
        if (filePath.endsWith(".xls")) {
            workbook = new HSSFWorkbook(in);
        /* 2007 Excel */
        } else if (filePath.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(in);
        } else {
            log.error("File is not Excel File: {}", filePath);
        }
        } catch (FileNotFoundException e) {
            log.error("File not found: ", e);
        } catch (IOException e) {
            log.error("Create WorkBook failed: ", e);
        }

        // 设置默认返回空串
        workbook.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        log.info("read excel: {}", filePath);
    }

    /**
     * 判断是否为空行
     * */
    private boolean judgeblankRow(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            /* 存在单元格不为空字符串则为非空行 */
            if (!"".equals(getCellContents(cell))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 关闭资源
     * */
    private void close() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                log.error("close file filed", e);
            }
        }
    }

    /**
     * 判断是否存在下一行
     *
     * 如果下一行为空行 @return true
     * 如果下一行为非空行 @return false
     * */
    @Override
    public boolean hasNext() {
        if (curRowNum < maxRowNum) {
            Row curRow = sheet.getRow(curRowNum);
            if (judgeblankRow(curRow)) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 获取下一行数据
     *
     * @return Object[]数组对象
     * */
    @Override
    public Object[] next() {
        Map<String, String> curRowData = new HashMap<>();

        Row curRow = sheet.getRow(curRowNum);
        /* 根据列名取对应列数据 */
        for (int i = curColNum; i < colNames.size(); i++) {
            Cell cell = curRow.getCell(i);
            curRowData.put(colNames.get(i), getCellContents(cell));
        }

        readRowNum ++;
        curRowNum ++;

        return new Object[] { curRowData };
    }

    /**
     * 以String类型返回值
     *
     * */
    private String getCellContents(Cell cell) {
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }

    @Override
    public void remove() {
        log.error("not support remove");
    }
}

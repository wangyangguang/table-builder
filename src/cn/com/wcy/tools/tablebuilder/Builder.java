/**
 * @copyright Copyright 2014-2016 JR.JD.COM All Right Reserved.
 */
package cn.com.wcy.tools.tablebuilder;

import java.io.*;

/**
 * 建表语句生成器
 *
 * @author <a href="mailto:wangchunyang1@jd.com">京东金融-技术研发部-支付业务技术部-支付结算研发部:王小阳</a>
 * @version V1.0
 * @Description 建表语句生成器，最大生成10000个表
 * @since 2016年05月23日 9:46
 */
public class Builder {
    /** 被替换符号 */
    private static final String REPLACED = "xxxx";
    /** 编码格式 */
    private static final String CHARSET_NAME = "UTF-8";

    /**
     * 程序入口
     *
     * @param args 第一个参数是文件路径 第二个参数数是表个数
     */
    public static void main(String[] args) {
        try {
            if (args == null || args.length < 2) {
                throw new RuntimeException("请输入模板文件路径及生成表数量两个参数");
            }

            // 输入参数
            String sqlTemplatePath = args[0];
            String tableNumberStr = args[1];
//            String sqlTemplatePath = "E:\\sql\\pay_orderbank-createtable.sql.txt";
//            String tableNumberStr = "200";

            File sqlTemplateFile = new File(sqlTemplatePath);
            if (!sqlTemplateFile.exists()) {
                throw new RuntimeException("模板文件未找到，请检查模板文件路径是否正确");
            }
            int tableNumber = 0;
            try {
                tableNumber = Integer.parseInt(tableNumberStr);
            } catch (NumberFormatException nfe) {
                throw new RuntimeException("表数量参数错误，请输入大于0并且小于10000的正整数");
            }
            if (tableNumber <= 0 || tableNumber >= 10000) {
                throw new RuntimeException("表数量参数错误，请输入大于0并且小于10000的正整数");
            }

            // 读模板文件
            String sqlTemplate = readFile(sqlTemplateFile);

            File sqlFile = new File(sqlTemplatePath + ".createTable.sql");
            // 循环表个数
            for (int i = 0; i < tableNumber; i++) {
                String tableIndex = getTableIndex(i);
                // 替换表下标
                String createTableSql = sqlTemplate.replaceAll(REPLACED, tableIndex);
                System.out.print("第" + tableIndex + "个建表语句开始:");
                // 写入文件
                writeFile(sqlFile, createTableSql);
            }
        } catch (Exception e) {
            System.out.println("===== " + e.getMessage() + " =====");
        }
    }

    /**
     * 生成表下标
     *
     * @param tableIndex
     * @return
     */
    private static String getTableIndex(int tableIndex) {
        if (tableIndex >= 0 && tableIndex < 10) {
            return "000" + tableIndex;
        } else if (tableIndex >= 10 && tableIndex < 100) {
            return "00" + tableIndex;
        } else if (tableIndex >= 100 && tableIndex < 1000) {
            return "0" + tableIndex;
        } else if (tableIndex >= 1000 && tableIndex < 10000) {
            return "" + tableIndex;
        } else {
            throw new RuntimeException("不支持的表下标");
        }
    }

    /**
     * 读取模板
     *
     * @param file
     * @return
     */
    private static String readFile(File file) {
        BufferedReader reader = null;
        try {
            InputStream is = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(is, CHARSET_NAME));
            StringBuilder sqlTemplate = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sqlTemplate.append(line).append("\r\n");
            }
            return sqlTemplate.toString();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException(ioe);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 写文件
     *
     * @param file
     * @param context
     */
    private static void writeFile(File file, String context) {
        BufferedWriter writer = null;
        try {
            OutputStream is = new FileOutputStream(file, true);
            writer = new BufferedWriter(new OutputStreamWriter(is, CHARSET_NAME));
            writer.write("-- ==================== [begin] ====================");
            writer.newLine();
            writer.write(context);
            writer.newLine();
            writer.write("-- ==================== [end] ====================");
            writer.newLine();
            writer.newLine();

            System.out.println("写入成功！");
        } catch (IOException ioe) {
            System.out.println("写入失败！");
            ioe.printStackTrace();
            throw new RuntimeException(ioe);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

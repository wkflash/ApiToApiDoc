package com.wk.plugin.apitoapidoc;

import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class WordExportFromTemplates {
    public static void exportToWord(ApiInterface apiInterface, String outputFilePath) {
        // 1. 首先尝试从资源文件夹读取
        InputStream inputStream = WordExportFromTemplates.class.getResourceAsStream("/templates/开发设计文档模版.docx");

        // 2. 如果没有找到，尝试从其他位置读取
        if (inputStream == null) {
            inputStream = WordExportFromTemplates.class.getResourceAsStream("/META-INF/templates/开发设计文档模版.docx");
        }
        if (inputStream == null) {
            throw new RuntimeException("无法找到模板文件，请检查文件路径：/templates/开发设计文档模版.docx");
        }

        // 使用 Apache POI 读取 .docx 文件
        XWPFDocument document = null;
        FileOutputStream out = null;
        try {
            document = new XWPFDocument(inputStream);
            // 替换段落中的占位符
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0); // 获取文字内容
                    if (text != null && text.contains("${API_DESCRIPTION}")) {
                        text = text.replace("${API_DESCRIPTION}", apiInterface.getDescription());
                        run.setText(text, 0); // 设置新的文字内容
                    }
                }
            }
            // 获取所有表格
            List<XWPFTable> tables = document.getTables();
            //基本描述
            XWPFTable xwpfTable1 = tables.get(0);
            xwpfTable1.getRow(0).getCell(1).setText(apiInterface.getName());
            xwpfTable1.getRow(1).getCell(1).setText(apiInterface.getDescription());
            xwpfTable1.getRow(2).getCell(1).setText(apiInterface.getHttpMethod());
            xwpfTable1.getRow(3).getCell(1).setText("contentType："+apiInterface.getContentType());
            //入参
            XWPFTable xwpfTableParameter= tables.get(1);
            for (ApiParameter parameter : apiInterface.getParameters()) {
                parameterAddCell(parameter, xwpfTableParameter);
                List<ApiParameter> subParameters = parameter.getSubParameters();
                if (subParameters!=null){
                    for (ApiParameter subParameter : subParameters) {
                        parameterAddCell(subParameter, xwpfTableParameter);
                    }
                }
            }
            //出参
            XWPFTable xwpfTableReturnFields = tables.get(2);
            for (ApiField returnField : apiInterface.getReturnFields()) {
                XWPFTableRow newRow = xwpfTableReturnFields.createRow(); // 创建新行
                returnFieldAddCell(returnField, newRow);
                List<ApiField> subFields = returnField.getSubFields();
                if (subFields!=null){
                    for (ApiField subField : subFields){
                        returnFieldAddCell(subField, newRow);
                    }
                }
            }
            out = new FileOutputStream("开发文档-测试.docx");
            document.write(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                document.close();
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void returnFieldAddCell(ApiField returnField, XWPFTableRow newRow) {
        newRow.getCell(1).setText(returnField.getName());
        newRow.getCell(2).setText(returnField.getDescription());
        newRow.getCell(3).setText(returnField.getType());
        newRow.getCell(4).setText("");
        newRow.getCell(5).setText("");
    }

    private static void parameterAddCell(ApiParameter parameter, XWPFTable xwpfTable) {
        XWPFTableRow newRow = xwpfTable.createRow(); // 创建新行
        newRow.getCell(1).setText(parameter.getName());
        newRow.getCell(2).setText(parameter.getDescription());
        newRow.getCell(3).setText(parameter.getType());
        newRow.getCell(4).setText("0");
        newRow.getCell(5).setText(parameter.isRequired()?"是":"否");
        newRow.getCell(6).setText("");
        newRow.getCell(7).setText(parameter.getDescription());
    }
}

package com.wk.plugin.apitoapidoc;

import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class StyledDocExporter {

    public static void exportToWord(ApiInterface apiInterface, String outputFilePath) throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            // 添加文档标题
            createTitle(document, "服务设计文档");

            // 添加基本描述
            createSectionTitle(document, "【基本描述】");
            createKeyValuePair(document, "接口名称", apiInterface.getName());
            createKeyValuePair(document, "接口描述", apiInterface.getDescription());
            createKeyValuePair(document, "请求方式", apiInterface.getHttpMethod());
            createKeyValuePair(document, "报文格式", "contentType: application/json");

            // 添加接口逻辑
            createSectionTitle(document, "接口逻辑");
            createParagraph(document, "接口具体的逻辑处理规则可以在此处描述。");

            // 添加入参表格
            createSectionTitle(document, "【参数说明】");
            createParagraph(document, "入参：");
            createParametersTable(document, apiInterface.getParameters());

            // 添加出参表格
            createParagraph(document, "出参：");
            createFieldsTable(document, apiInterface.getReturnFields());

            // 保存文档
            try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
                document.write(out);
                System.out.println("文档已成功导出！路径: " + outputFilePath);
            }
        }
    }

    private static void createTitle(XWPFDocument document, String titleText) {
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = title.createRun();
        run.setText(titleText);
        run.setBold(true);
        run.setFontSize(16);
        run.setFontFamily("微软雅黑");
    }

    private static void createSectionTitle(XWPFDocument document, String sectionTitle) {
        XWPFParagraph section = document.createParagraph();
        section.setSpacingBefore(200);
        XWPFRun run = section.createRun();
        run.setText(sectionTitle);
        run.setBold(true);
        run.setFontSize(14);
        run.setFontFamily("微软雅黑");
    }

    private static void createKeyValuePair(XWPFDocument document, String key, String value) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setSpacingBefore(100);
        XWPFRun run = paragraph.createRun();
        run.setText(key + ": " + value);
        run.setFontSize(12);
        run.setFontFamily("微软雅黑");
    }

    private static void createParagraph(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(12);
        run.setFontFamily("微软雅黑");
    }

    private static void createParametersTable(XWPFDocument document, List<ApiParameter> parameters) {
        XWPFTable table = document.createTable();
        XWPFTableRow headerRow = table.getRow(0);
        createTableHeaderCell(headerRow.getCell(0), "序号");
        headerRow.addNewTableCell();
        createTableHeaderCell(headerRow.getCell(1), "参数英文名");
        headerRow.addNewTableCell();
        createTableHeaderCell(headerRow.getCell(2), "参数中文名");
        headerRow.addNewTableCell();
        createTableHeaderCell(headerRow.getCell(3), "参数类型");
        headerRow.addNewTableCell();
        createTableHeaderCell(headerRow.getCell(4), "参数长度");
        headerRow.addNewTableCell();
        createTableHeaderCell(headerRow.getCell(5), "是否必填");
        headerRow.addNewTableCell();
        createTableHeaderCell(headerRow.getCell(6), "所属版本");
        headerRow.addNewTableCell();
        createTableHeaderCell(headerRow.getCell(7), "描述");

        int index = 1;
        for (ApiParameter parameter : parameters) {
            XWPFTableRow row = table.createRow();
            createTableCell(row.getCell(0), String.valueOf(index++));
            createTableCell(row.getCell(1), parameter.getName());
            createTableCell(row.getCell(2), parameter.getDescription());
            createTableCell(row.getCell(3), parameter.getType());
            createTableCell(row.getCell(4), parameter.getLength() != null ? String.valueOf(parameter.getLength()) : "");
            createTableCell(row.getCell(5), parameter.isRequired() ? "是" : "否");
            createTableCell(row.getCell(6), parameter.getVersion());
            createTableCell(row.getCell(7), parameter.getDescription());
        }
    }

    private static void createFieldsTable(XWPFDocument document, List<ApiField> fields) {
        XWPFTable table = document.createTable();
        XWPFTableRow headerRow = table.getRow(0);
        createTableHeaderCell(headerRow.getCell(0), "序号");
        headerRow.addNewTableCell();
        createTableHeaderCell(headerRow.getCell(1), "参数英文名");
        headerRow.addNewTableCell();
        createTableHeaderCell(headerRow.getCell(2), "参数中文名");
        headerRow.addNewTableCell();
        createTableHeaderCell(headerRow.getCell(3), "参数类型");
        headerRow.addNewTableCell();
        createTableHeaderCell(headerRow.getCell(4), "所属版本");
        headerRow.addNewTableCell();
        createTableHeaderCell(headerRow.getCell(5), "描述");

        int index = 1;
        for (ApiField field : fields) {
            XWPFTableRow row = table.createRow();
            createTableCell(row.getCell(0), String.valueOf(index++));
            createTableCell(row.getCell(1), field.getName());
            createTableCell(row.getCell(2), field.getDescription());
            createTableCell(row.getCell(3), field.getType());
            createTableCell(row.getCell(4), field.getVersion());
            createTableCell(row.getCell(5), field.getDescription());
        }
    }

    private static void createTableHeaderCell(XWPFTableCell cell, String text) {
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(12);
        run.setFontFamily("微软雅黑");
        paragraph.setAlignment(ParagraphAlignment.CENTER);
    }

    private static void createTableCell(XWPFTableCell cell, String text) {
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(12);
        run.setFontFamily("微软雅黑");
        paragraph.setAlignment(ParagraphAlignment.CENTER);
    }
}
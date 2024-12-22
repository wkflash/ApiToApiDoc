package com.wk.plugin.apitoapidoc;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class StyledWordExporter {

    public static void exportToStyledWord(ApiInterface apiInterface, String outputFilePath) throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            // 添加标题
            createStyledTitle(document, "接口文档");
            // 添加接口标题
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setStyle("Heading1");
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("1. " + apiInterface.getName() + " (" + apiInterface.getDescription() + ")");
            titleRun.setBold(true);

            // 添加基本描述表格
            XWPFTable descriptionTable = document.createTable(5, 2);
            setTableContent(descriptionTable, new String[][]{
                    {"接口名称", apiInterface.getName()},
                    {"接口描述", apiInterface.getDescription()},
                    {"请求方式", apiInterface.getHttpMethod()},
                    {"报文格式", apiInterface.getContentType() != null ? apiInterface.getContentType() : "application/json"},
                    {"接口逻辑", "（留空）"}
            });

            // 入参表格
            createStyledSectionTitle(document, "入参");
            createStyledTable(document, apiInterface.getParameters());

            // 出参表格
            createStyledSectionTitle(document, "出参");
            createStyledReturnTable(document, apiInterface.getReturnFields());

            // 保存文档
            try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
                document.write(out);
                System.out.println("文档导出成功！路径: " + outputFilePath);
            }
        }
    }
    private static void setTableContent(XWPFTable table, String[][] data) {
        for (int i = 0; i < data.length; i++) {
            XWPFTableRow row = table.getRow(i);
            row.getCell(0).setText(data[i][0]);
            row.getCell(1).setText(data[i][1]);
        }
    }
    // 添加标题样式
    private static void createStyledTitle(XWPFDocument document, String titleText) {
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = title.createRun();
        titleRun.setText(titleText);
        titleRun.setBold(true);
        titleRun.setFontSize(20);
        titleRun.setFontFamily("微软雅黑");
    }

    // 添加小节标题样式
    private static void createStyledSectionTitle(XWPFDocument document, String sectionTitle) {
        XWPFParagraph section = document.createParagraph();
        section.setSpacingBefore(200); // 增加段前距离
        XWPFRun sectionRun = section.createRun();
        sectionRun.setText(sectionTitle);
        sectionRun.setBold(true);
        sectionRun.setFontSize(14);
        sectionRun.setFontFamily("微软雅黑");
    }

    // 添加段落内容样式
    private static void createStyledParagraph(XWPFDocument document, String content) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun paragraphRun = paragraph.createRun();
        paragraphRun.setText(content);
        paragraphRun.setFontSize(12);
        paragraphRun.setFontFamily("微软雅黑");
    }

    // 创建带样式的入参表格
    private static void createStyledTable(XWPFDocument document, List<ApiParameter> parameters) {
        XWPFTable table = document.createTable();

        // 创建表头行
        XWPFTableRow headerRow = table.getRow(0);
       // headerRow.getCTTc().addNewTcPr().addNewShd().setFill(bgColor);
        createStyledCell(headerRow.getCell(0), "参数名", true);
        headerRow.addNewTableCell();
        createStyledCell(headerRow.getCell(1), "类型", true);
        headerRow.addNewTableCell();
        createStyledCell(headerRow.getCell(2), "必填", true);
        headerRow.addNewTableCell();
        createStyledCell(headerRow.getCell(3), "描述", true);

        // 添加参数行
        for (ApiParameter parameter : parameters) {
            addParameterToTable(table, parameter, "");
        }
    }

    // 创建带样式的出参表格
    private static void createStyledReturnTable(XWPFDocument document, List<ApiField> fields) {
        XWPFTable table = document.createTable();

        // 创建表头行
        XWPFTableRow headerRow = table.getRow(0);
        createStyledCell(headerRow.getCell(0), "参数名", true);
        headerRow.addNewTableCell();
        createStyledCell(headerRow.getCell(1), "类型", true);
        headerRow.addNewTableCell();
        createStyledCell(headerRow.getCell(2), "描述", true);

        // 添加出参行
        for (ApiField field : fields) {
            addFieldToTable(table, field, "");
        }
    }

    // 为单元格设置样式
    private static void createStyledCell(XWPFTableCell cell, String text, boolean isHeader) {
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontFamily("微软雅黑");
        run.setFontSize(12);
        if (isHeader) {
            run.setBold(true);
        }
    }

    // 添加参数信息到表格
    private static void addParameterToTable(XWPFTable table, ApiParameter parameter, String indent) {
        XWPFTableRow row = table.createRow();
        createStyledCell(row.getCell(0), indent + parameter.getName(), false);
        createStyledCell(row.getCell(1), parameter.getType(), false);
        createStyledCell(row.getCell(2), parameter.isRequired() ? "是" : "否", false);
        createStyledCell(row.getCell(3), parameter.getDescription(), false);

        if (parameter.getSubParameters() != null) {
            for (ApiParameter subParameter : parameter.getSubParameters()) {
                addParameterToTable(table, subParameter, indent + "    ");
            }
        }
    }

    // 添加出参信息到表格
    private static void addFieldToTable(XWPFTable table, ApiField field, String indent) {
        XWPFTableRow row = table.createRow();
        createStyledCell(row.getCell(0), indent + field.getName(), false);
        createStyledCell(row.getCell(1), field.getType(), false);
        createStyledCell(row.getCell(2), field.getDescription(), false);

        if (field.getSubFields() != null) {
            for (ApiField subField : field.getSubFields()) {
                addFieldToTable(table, subField, indent + "    ");
            }
        }
    }
}
package com.wk.plugin.apitoapidoc;

import org.apache.poi.xwpf.usermodel.*;
import java.io.FileOutputStream;
import java.io.IOException;

public class WordExporter {

    public static void exportToWord(ApiInterface apiInterface, String outputFilePath) throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            // 创建标题
            XWPFParagraph title = document.createParagraph();
            XWPFRun titleRun = title.createRun();
            titleRun.setText("接口文档");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            // 接口名称
            XWPFParagraph nameParagraph = document.createParagraph();
            XWPFRun nameRun = nameParagraph.createRun();
            nameRun.setText("接口名称: " + apiInterface.getName());
            nameRun.setFontSize(12);

            // 接口描述
            XWPFParagraph descriptionParagraph = document.createParagraph();
            XWPFRun descriptionRun = descriptionParagraph.createRun();
            descriptionRun.setText("接口描述: " + apiInterface.getDescription());
            descriptionRun.setFontSize(12);

            // 请求方法
            XWPFParagraph methodParagraph = document.createParagraph();
            XWPFRun methodRun = methodParagraph.createRun();
            methodRun.setText("请求方法: " + apiInterface.getHttpMethod());
            methodRun.setFontSize(12);

            // 入参表格
            XWPFParagraph inputTitle = document.createParagraph();
            XWPFRun inputTitleRun = inputTitle.createRun();
            inputTitleRun.setText("入参:");
            inputTitleRun.setFontSize(14);

            XWPFTable inputTable = document.createTable();
            XWPFTableRow inputHeaderRow = inputTable.getRow(0);
            inputHeaderRow.getCell(0).setText("参数名");
            inputHeaderRow.addNewTableCell().setText("类型");
            inputHeaderRow.addNewTableCell().setText("必填");
            inputHeaderRow.addNewTableCell().setText("描述");

            for (ApiParameter parameter : apiInterface.getParameters()) {
                writeParameterToWord(inputTable, parameter);
            }

            // 出参表格
            XWPFParagraph outputTitle = document.createParagraph();
            XWPFRun outputTitleRun = outputTitle.createRun();
            outputTitleRun.setText("出参:");
            outputTitleRun.setFontSize(14);

            XWPFTable outputTable = document.createTable();
            XWPFTableRow outputHeaderRow = outputTable.getRow(0);
            outputHeaderRow.getCell(0).setText("参数名");
            outputHeaderRow.addNewTableCell().setText("类型");
            outputHeaderRow.addNewTableCell().setText("描述");

            for (ApiField field : apiInterface.getReturnFields()) {
                writeFieldToWord(outputTable, field);
            }

            // 保存文档
            try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
                document.write(out);
            }
        }
    }

    private static void writeParameterToWord(XWPFTable table, ApiParameter parameter) {
        XWPFTableRow row = table.createRow();
        row.getCell(0).setText(parameter.getName());
        row.getCell(1).setText(parameter.getType());
        row.getCell(2).setText(parameter.isRequired() ? "是" : "否");
        row.getCell(3).setText(parameter.getDescription());

        if (parameter.getSubParameters() != null) {
            for (ApiParameter subParameter : parameter.getSubParameters()) {
                writeParameterToWord(table, subParameter);
            }
        }
    }

    private static void writeFieldToWord(XWPFTable table, ApiField field) {
        XWPFTableRow row = table.createRow();
        row.getCell(0).setText(field.getName());
        row.getCell(1).setText(field.getType());
        row.getCell(2).setText(field.getDescription());

        if (field.getSubFields() != null) {
            for (ApiField subField : field.getSubFields()) {
                writeFieldToWord(table, subField);
            }
        }
    }
}
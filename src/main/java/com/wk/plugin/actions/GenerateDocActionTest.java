package com.wk.plugin.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.wk.plugin.apitoapidoc.*;

import java.io.IOException;

public class GenerateDocActionTest extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 获取当前编辑器中的 PsiMethod
        PsiMethod method = getPsiMethodFromContext(e);
        if (method == null) {
            Messages.showMessageDialog("请选择一个接口方法！", "提示", Messages.getInformationIcon());
            return;
        }
        // 获取方法所属类
        PsiClass containingClass = method.getContainingClass();
        ApiInterface apiInterface = InterfaceParser.parseMethod(containingClass,method);

        System.out.println(apiInterface);

        /*try {
            StyledDocExporter.exportToWord(apiInterface, "服务设计文档.docx");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }*/
        WordExportFromTemplates.exportToWord(apiInterface, "服务设计文档.docx");

        //弹窗
        Messages.showMessageDialog("文档生成成功！", "提示", Messages.getInformationIcon());

    }

    @Override
    public void update(AnActionEvent e) {
        // 动作启用条件：只在编辑器中选择方法时启用
        PsiMethod method = getPsiMethodFromContext(e);
        e.getPresentation().setEnabledAndVisible(method != null);
    }

    private PsiMethod getPsiMethodFromContext(AnActionEvent e) {
        PsiElement element = e.getData(CommonDataKeys.PSI_ELEMENT);
        return element instanceof PsiMethod ? (PsiMethod) element : null;
    }
}
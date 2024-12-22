package com.wk.plugin.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;

public class GenerateDocAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 获取当前编辑器中的 PsiMethod
        PsiMethod method = getPsiMethodFromContext(e);
        if (method == null) {
            Messages.showMessageDialog("请选择一个接口方法！", "提示", Messages.getInformationIcon());
            return;
        }

        // 解析接口信息
        String methodName = method.getName();
        String returnType = method.getReturnType() != null ? method.getReturnType().getCanonicalText() : "void";
        String message = "方法名: " + methodName + "\n返回类型: " + returnType;

        // 显示接口信息
        Messages.showMessageDialog(message, "接口信息", Messages.getInformationIcon());
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
package com.wk.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.wk.plugin.apitoapidoc.ApiInterface;
import com.wk.plugin.apitoapidoc.InterfaceParser;
import com.wk.plugin.apitoapidoc.WordExportFromTemplates;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class GenerateDocAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            // 获取当前方法
            PsiMethod method = getPsiMethodFromContext(e);
            if (method == null) {
                Messages.showMessageDialog("请选择一个接口方法！", "提示", Messages.getInformationIcon());
                return;
            }

            // 获取项目
            Project project = e.getProject();
            if (project == null) {
                Messages.showErrorDialog("无法获取项目信息", "错误");
                return;
            }

            // 创建文件选择器
            FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, 
                    false, false, false, false)
                    .withTitle("选择保存位置")
                    .withDescription("请选择接口文档保存位置");

            // 获取默认目录（桌面）
            String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
            File desktopDir = new File(desktopPath);
            VirtualFile defaultDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(desktopDir);
            
            if (defaultDir == null) {
                // 如果找不到桌面目录，就使用项目目录
                defaultDir = project.getBaseDir();
            }
            
            // 显示文件选择对话框
            VirtualFile selectedDir = FileChooser.chooseFile(descriptor, project, defaultDir);
            
            if (selectedDir != null) {
                // 生成文件名
                PsiClass containingClass = method.getContainingClass();
                String className = containingClass != null ? containingClass.getName() : "Unknown";
                String methodName = method.getName();
                String fileName = String.format("%s_%s_接口文档.docx", className, methodName);
                
                // 构建完整的输出路径
                File outputFile = new File(selectedDir.getPath(), fileName);
                String outputPath = outputFile.getAbsolutePath();

                // 解析接口信息
                ApiInterface apiInterface = InterfaceParser.parseMethod(containingClass, method);

                // 导出文档
                WordExportFromTemplates.exportToWord(apiInterface, outputPath);

                // 刷新目录，确保文件在IDE中可见
                selectedDir.refresh(false, false);

                // 验证文件是否生成
                File generatedFile = new File(outputPath);
                if (!generatedFile.exists()) {
                    throw new RuntimeException("文件未能成功创建：" + outputPath);
                }

                // 显示成功消息并询问是否打开文件夹
                int response = Messages.showYesNoDialog(
                    String.format("文档生成成功！\n\n文件位置：\n%s\n\n文件大小：%d bytes\n\n是否打开所在文件夹？", 
                        outputPath, generatedFile.length()),
                    "提示",
                    Messages.getQuestionIcon()
                );
                
                // 如果用户选择打开文件夹
                if (response == Messages.YES) {
                    openFileInSystemExplorer(outputPath);
                }
                
                // 打印日志
                System.out.println("文档已生成到：" + outputPath);
            }
        } catch (Exception ex) {
            // 显示详细错误信息
            String errorMessage = String.format("生成文档失败：\n%s", ex.getMessage());
            Messages.showErrorDialog(errorMessage, "错误");
            ex.printStackTrace();
        }
    }

    /**
     * 在系统文件浏览器中打开文件
     */
    private void openFileInSystemExplorer(String filePath) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            File file = new File(filePath);
            
            if (os.contains("win")) {
                // Windows
                Runtime.getRuntime().exec(String.format("explorer.exe /select,\"%s\"", filePath));
            } else if (os.contains("mac")) {
                // macOS
                Runtime.getRuntime().exec(new String[]{"open", "-R", filePath});
            } else if (os.contains("nix") || os.contains("nux")) {
                // Linux
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file.getParentFile());
                } else {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", file.getParent()});
                }
            }
        } catch (IOException ex) {
            Messages.showErrorDialog("无法打开文件夹：" + ex.getMessage(), "错误");
        }
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
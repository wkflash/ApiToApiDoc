package com.wk.plugin.apitoapidoc;


import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.util.PsiUtil;

import java.util.ArrayList;
import java.util.List;


public class InterfaceParser {


    public static  ApiInterface parseMethod(PsiClass containingClass, PsiMethod method) {

        // 获取类上的路径
        String classPath = getRequestMappingPath(containingClass.getModifierList());
        // 获取方法上的路径
        String methodPath = getRequestMappingPath(method.getModifierList());

        // 拼接完整路径
        String fullPath = combinePaths(classPath, methodPath);
        ApiInterface apiInterface = new ApiInterface();
        apiInterface.setName(fullPath);
        apiInterface.setDescription(getMethodDescription(method));
        apiInterface.setHttpMethod(getHttpMethod(method));
        apiInterface.setParameters(parseParameters(method));
       apiInterface.setReturnFields(parseReturnType(method.getReturnType()));

        return apiInterface;
    }
    private static String getRequestMappingPath(PsiModifierList modifierList) {
        if (modifierList == null) {
            return "";
        }

        // 支持 @RequestMapping 和子注解
        String[] annotations = {
                "org.springframework.web.bind.annotation.RequestMapping",
                "org.springframework.web.bind.annotation.GetMapping",
                "org.springframework.web.bind.annotation.PostMapping",
                "org.springframework.web.bind.annotation.PutMapping",
                "org.springframework.web.bind.annotation.DeleteMapping"
        };

        for (String annotationName : annotations) {
            PsiAnnotation annotation = modifierList.findAnnotation(annotationName);
            if (annotation != null) {
                return getAnnotationValue(annotation, "value");
            }
        }

        return "";
    }
    private static String getHttpMethod(PsiMethod method) {
        PsiModifierList modifierList = method.getModifierList();
        if (modifierList == null) {
            return "UNKNOWN";
        }

        // 支持的注解列表
        String[][] annotations = {
                {"org.springframework.web.bind.annotation.GetMapping", "GET"},
                {"org.springframework.web.bind.annotation.PostMapping", "POST"},
                {"org.springframework.web.bind.annotation.PutMapping", "PUT"},
                {"org.springframework.web.bind.annotation.DeleteMapping", "DELETE"},
                {"org.springframework.web.bind.annotation.PatchMapping", "PATCH"}
        };

        // 直接匹配注解类型
        for (String[] annotation : annotations) {
            if (modifierList.findAnnotation(annotation[0]) != null) {
                return annotation[1];
            }
        }

        // 如果是 @RequestMapping，需要进一步解析 method 属性
        PsiAnnotation requestMapping = modifierList.findAnnotation("org.springframework.web.bind.annotation.RequestMapping");
        if (requestMapping != null) {
            PsiAnnotationMemberValue methodValue = requestMapping.findAttributeValue("method");
            if (methodValue != null) {
                String methodText = methodValue.getText().replace("\"", "");
                if (methodText.contains("RequestMethod.GET")) {
                    return "GET";
                } else if (methodText.contains("RequestMethod.POST")) {
                    return "POST";
                } else if (methodText.contains("RequestMethod.PUT")) {
                    return "PUT";
                } else if (methodText.contains("RequestMethod.DELETE")) {
                    return "DELETE";
                } else if (methodText.contains("RequestMethod.PATCH")) {

                    return "PATCH";
                }
            }
        }

        // 默认返回 UNKNOWN
        return "UNKNOWN";
    }
    private static String combinePaths(String classPath, String methodPath) {
        // 确保路径的格式正确
        if (classPath.endsWith("/")) {
            classPath = classPath.substring(0, classPath.length() - 1);
        }
        if (!methodPath.startsWith("/")) {
            methodPath = "/" + methodPath;
        }
        return classPath + methodPath;
    }
    public static  String getAnnotationValue(PsiAnnotation annotation, String attribute) {
        PsiAnnotationMemberValue value = annotation.findAttributeValue(attribute);
        return value != null ? value.getText().replace("\"", "") : "";
    }

    public static  String getHttpMethod(PsiAnnotation annotation) {
        // 获取注解的全限定名
        String qualifiedName = annotation.getQualifiedName();
        if (qualifiedName == null) {
            return "UNKNOWN";
        }

        // 根据注解类型判断 HTTP 方法
        switch (qualifiedName) {
            case "org.springframework.web.bind.annotation.GetMapping":
                return "GET";
            case "org.springframework.web.bind.annotation.PostMapping":
                return "POST";
            case "org.springframework.web.bind.annotation.PutMapping":
                return "PUT";
            case "org.springframework.web.bind.annotation.DeleteMapping":
                return "DELETE";
            case "org.springframework.web.bind.annotation.RequestMapping":
                // 如果是 @RequestMapping，需要进一步解析 method 属性
                PsiAnnotationMemberValue methodValue = annotation.findAttributeValue("method");
                if (methodValue != null) {
                    String methodText = methodValue.getText().replace("\"", "");
                    // 处理多种请求方式的情况
                    if (methodText.contains("RequestMethod.GET")) {
                        return "GET";
                    } else if (methodText.contains("RequestMethod.POST")) {
                        return "POST";
                    } else if (methodText.contains("RequestMethod.PUT")) {
                        return "PUT";
                    } else if (methodText.contains("RequestMethod.DELETE")) {
                        return "DELETE";
                    }
                }
                return "UNKNOWN";
            default:
                return "UNKNOWN";
        }
    }

    private static List<ApiField> parseReturnType(PsiType returnType) {
        List<ApiField> fields = new ArrayList<>();
        if (returnType == null) {
            return fields; // 如果方法返回 void，则返回空列表
        }

        // 判断返回类型
        if (returnType instanceof PsiPrimitiveType) {
            // 基本类型
            fields.add(createField("value",returnType.getCanonicalText(), "基本类型", ""));
        } else if (returnType instanceof PsiArrayType) {
            // 数组类型
            PsiArrayType arrayType = (PsiArrayType) returnType;
            fields.add(createField("array", arrayType.getComponentType().getCanonicalText() + "[]", "数组类型", ""));
        } else if (returnType instanceof PsiClassType) {
            // 类类型或泛型类型
            PsiClassType classType = (PsiClassType) returnType;
            PsiClass psiClass = classType.resolve();
            if (psiClass != null) {
                // 解析类的字段
                fields.addAll(parseFieldsFromClass(psiClass));
            }

            // 解析泛型参数
            PsiType[] typeParameters = classType.getParameters();
            if (typeParameters.length > 0) {
                for (PsiType parameter : typeParameters) {
                    fields.addAll(parseReturnType(parameter));
                }
            }
        }

        return fields;
    }

    private static List<ApiField> parseFieldsFromClass(PsiClass psiClass) {
        List<ApiField> fields = new ArrayList<>();
        for (PsiField field : psiClass.getFields()) {
            // 跳过常量字段
            if (field.hasModifierProperty(PsiModifier.STATIC) && field.hasModifierProperty(PsiModifier.FINAL)) {
                continue;
            }
            fields.add(createField(
                    field.getName(),
                    field.getType().getCanonicalText(),
                    getFieldDescription(field), // 假设从注释或注解中提取字段描述
                    ""
            ));
        }
        return fields;
    }
    private static String getMethodDescription(PsiMethod method) {

        // 2. 尝试从 JavaDoc 中获取方法描述
        String javadocDescription = getMethodJavaDocComment(method);
        if (javadocDescription != null && !javadocDescription.isEmpty()) {
            return javadocDescription;
        }

        // 3. 如果没有找到描述，则返回默认值
        return "无描述";
    }
    private static String getFieldDescription(PsiField field) {
        // 尝试从 JavaDoc 中获取字段描述
        String javadocDescription = getJavaDocComment(field);
        if (javadocDescription != null && !javadocDescription.isEmpty()) {
            return javadocDescription;
        }

        return "";
    }
    private static String getMethodJavaDocComment(PsiMethod method) {
        PsiDocComment docComment = method.getDocComment();
        if (docComment != null) {
            StringBuilder commentText = new StringBuilder();
            for (PsiElement descriptionElement : docComment.getDescriptionElements()) {
                String text = descriptionElement.getText();
                commentText.append(text);
            }
            return commentText.toString().trim();
        }
        return "";
    }
    private static String getJavaDocComment(PsiField field) {
        PsiDocComment docComment = field.getDocComment();
        if (docComment != null) {
            StringBuilder commentText = new StringBuilder();
            for (PsiElement descriptionElement : docComment.getDescriptionElements()) {
                String text = descriptionElement.getText();
                commentText.append(text);
            }
            return commentText.toString().trim();
        }
        return "";
    }

    private static ApiField createField(String name, String type, String description, String version) {
        ApiField field = new ApiField();
        field.setName(name);
        field.setType(getSimpleTypeName(type));
        field.setDescription(description);
        field.setVersion(version);
        return field;
    }
    private static String getSimpleTypeName(String fullTypeName) {
        // 如果是基本类型或没有包名，直接返回
        if (!fullTypeName.contains(".")) {
            return fullTypeName;
        }
        // 提取简单类名
        return fullTypeName.substring(fullTypeName.lastIndexOf('.') + 1);
    }
    private static List<ApiParameter> parseParameters(PsiMethod method) {
        List<ApiParameter> parameters = new ArrayList<>();
        PsiParameterList parameterList = method.getParameterList();

        for (PsiParameter parameter : parameterList.getParameters()) {
            ApiParameter apiParameter = new ApiParameter();

            // 设置参数基本信息
            apiParameter.setName(parameter.getName());
            apiParameter.setType(getSimpleTypeName(parameter.getType().getCanonicalText()));
            apiParameter.setRequired(isParameterRequired(parameter)); // 是否必填
            apiParameter.setDescription(getParameterDescription(method, parameter)); // 参数描述

            // 如果是 @RequestBody 参数，解析实体类字段
            if (isRequestBodyParameter(parameter)) {
                apiParameter.setSubParameters(parseEntityFields(parameter.getType()));
            }

            parameters.add(apiParameter);
        }
        return parameters;
    }
    private static boolean isParameterRequired(PsiParameter parameter) {
        // 检查是否有 @RequestParam 注解
        PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
        if (requestParam != null) {
            PsiAnnotationMemberValue requiredValue = requestParam.findAttributeValue("required");
            if (requiredValue != null) {
                // 转换为布尔值
                return Boolean.parseBoolean(requiredValue.getText());
            }
            // 如果未指定 required 属性，默认为 true
            return true;
        }

        // 检查其他注解逻辑（如果有自定义必填注解，可以在这里扩展）

        // 如果没有注解，默认为非必填
        return false;
    }
    private static String getParameterDescription(PsiMethod method, PsiParameter parameter) {
        // 1. 从注解中获取描述
        String description = getAnnotationValue(parameter, "org.springframework.web.bind.annotation.RequestParam", "value");
        if (description != null && !description.isEmpty()) {
            return description;
        }

        // 2. 从 JavaDoc 中获取描述
        String javadocDescription = getParameterJavaDoc(method, parameter.getName());
        if (javadocDescription != null && !javadocDescription.isEmpty()) {
            return javadocDescription;
        }

        // 3. 返回默认描述
        return "无描述";
    }

    private static String getAnnotationValue(PsiParameter parameter, String annotationQualifiedName, String attributeName) {
        PsiAnnotation annotation = parameter.getAnnotation(annotationQualifiedName);
        if (annotation != null) {
            PsiAnnotationMemberValue value = annotation.findAttributeValue(attributeName);
            if (value != null) {
                return value.getText().replace("\"", "").trim();
            }
        }
        return "";
    }

    private static String getParameterJavaDoc(PsiMethod method, String parameterName) {
        PsiDocComment docComment = method.getDocComment();
        if (docComment != null) {
            for (PsiDocTag tag : docComment.findTagsByName("param")) {
                PsiElement[] dataElements = tag.getDataElements();
                if (dataElements.length > 0 && dataElements[0].getText().equals(parameterName)) {
                    StringBuilder commentText = new StringBuilder();
                    for (int i = 1; i < dataElements.length; i++) {
                        String text = dataElements[i].getText();
                        commentText.append(text);
                    }
                    return commentText.toString().trim();
                }
            }
        }
        return "";
    }
    private static boolean isRequestBodyParameter(PsiParameter parameter) {
        return parameter.getAnnotation("org.springframework.web.bind.annotation.RequestBody") != null;
    }
    private static List<ApiParameter> parseEntityFields(PsiType type) {
        List<ApiParameter> subParameters = new ArrayList<>();
        PsiClass psiClass = PsiUtil.resolveClassInType(type);

        if (psiClass != null) {
            for (PsiField field : psiClass.getFields()) {
                ApiParameter subParameter = new ApiParameter();
                subParameter.setName(field.getName());
                subParameter.setType(getSimpleTypeName(field.getType().getCanonicalText()));
                subParameter.setDescription(getFieldDescription(field));

                // 如果字段是复杂类型，则递归解析
                if (isComplexType(field.getType())) {
                    subParameter.setSubParameters(parseEntityFields(field.getType()));
                }

                subParameters.add(subParameter);
            }
        }

        return subParameters;
    }
    private static boolean isComplexType(PsiType type) {
        return !(type instanceof PsiPrimitiveType || type.getCanonicalText().startsWith("java.lang"));
    }

}
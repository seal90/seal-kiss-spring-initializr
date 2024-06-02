package io.github.seal90.kiss.mybatis.generator.plugin.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.VisitableElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * 将 Mybatis Generator 查询类 中的 Example 更换为指定字符串
 */
public class RenameExampleToParamPlugin extends PluginAdapter {

    public static String lowerFirstCapse(String str){
        char[]chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public static void methodParameterFromExampleToTypeName(Method method){
        List<Parameter> parameters = method.getParameters();
        for(Parameter parameter : parameters){
            if("example".equals(parameter.getName())){
                parameter.getType().getShortName();
                parameters.remove(parameter);

                String newName = lowerFirstCapse(parameter.getType().getShortName());
                Parameter newParameter = new Parameter(parameter.getType(), newName, parameter.isVarargs());
                List<String> annotations = parameter.getAnnotations();
                for(String annotation : annotations) {
                    String newAnnotation = annotation.replace("example", newName);
                    newParameter.addAnnotation(newAnnotation);
                }
                parameters.add(newParameter);

            }
        }
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        introspectedTable.setUpdateByExampleStatementId("updateByParam");
        introspectedTable.setUpdateByExampleWithBLOBsStatementId("updateByParamWithBLOBs");
        introspectedTable.setCountByExampleStatementId("countByParam");
        introspectedTable.setSelectByExampleStatementId("selectByParam");
        introspectedTable.setExampleWhereClauseId("Param_Where_Clause");
        introspectedTable.setMyBatis3UpdateByExampleWhereClauseId("Update_By_Param_Where_Clause");
        introspectedTable.setSelectByExampleWithBLOBsStatementId("selectByParamWithBLOBs");
        introspectedTable.setUpdateByExampleSelectiveStatementId("updateByParamSelective");
        introspectedTable.setDeleteByExampleStatementId("deleteByParam");
    }


    @Override
    public boolean clientCountByExampleMethodGenerated(Method method,
                                                       Interface interfaze, IntrospectedTable introspectedTable) {
        methodParameterFromExampleToTypeName(method);
        return true;
    }
    @Override
    public boolean clientDeleteByExampleMethodGenerated(Method method,
                                                        Interface interfaze, IntrospectedTable introspectedTable) {
        methodParameterFromExampleToTypeName(method);
        return true;
    }

    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method,
                                                                 Interface interfaze, IntrospectedTable introspectedTable) {
        methodParameterFromExampleToTypeName(method);
        return true;
    }

    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method,
                                                                    Interface interfaze, IntrospectedTable introspectedTable) {
        methodParameterFromExampleToTypeName(method);
        return true;
    }

    @Override
    public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method,
                                                                 Interface interfaze, IntrospectedTable introspectedTable) {
        methodParameterFromExampleToTypeName(method);
        return true;
    }

    @Override
    public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method,
                                                                 Interface interfaze, IntrospectedTable introspectedTable) {
        methodParameterFromExampleToTypeName(method);
        return true;
    }

    @Override
    public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method,
                                                                    Interface interfaze, IntrospectedTable introspectedTable) {
        methodParameterFromExampleToTypeName(method);
        return true;
    }

    @Override
    public boolean providerCountByExampleMethodGenerated(Method method,
                                                         TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        methodParameterFromExampleToTypeName(method);
        return true;
    }

    @Override
    public boolean providerDeleteByExampleMethodGenerated(Method method,
                                                          TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        methodParameterFromExampleToTypeName(method);
        return true;
    }


    @Override
    public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element,
                                                            IntrospectedTable introspectedTable) {
        String domainName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        String paraName = domainName.substring(0, domainName.length()-2)+"Param";
        String oredCriteria = lowerFirstCapse(paraName) + ".oredCriteria";
        List<VisitableElement> elements = element.getElements();
        for(VisitableElement whereElement : elements){
            if(whereElement instanceof XmlElement){
                XmlElement whereXmlElement = (XmlElement)whereElement;
                List<VisitableElement> outerForEachElements = whereXmlElement.getElements();
                for(VisitableElement outerForEachElement : outerForEachElements){
                    if(outerForEachElement instanceof XmlElement){
                        XmlElement outerForEachXmlElement = (XmlElement)outerForEachElement;
                        List<Attribute> attributes = outerForEachXmlElement.getAttributes();
                        for(Attribute attribute : attributes){
                            if("example.oredCriteria".equals(attribute.getValue())){
                                attributes.remove(attribute);
                                attributes.add(new Attribute("collection", oredCriteria));
                                break;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

}
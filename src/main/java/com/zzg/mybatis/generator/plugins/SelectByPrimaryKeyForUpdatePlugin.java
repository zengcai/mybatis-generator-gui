package com.zzg.mybatis.generator.plugins;

import com.itfsw.mybatis.generator.plugins.ModelColumnPlugin;
import com.itfsw.mybatis.generator.plugins.utils.BasePlugin;
import com.itfsw.mybatis.generator.plugins.utils.FormatTools;
import com.itfsw.mybatis.generator.plugins.utils.JavaElementGeneratorTools;
import com.itfsw.mybatis.generator.plugins.utils.XmlElementGeneratorTools;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

/**
 * 只支持一个主键,text(+)类型字段不会查出来
 * Created by zengcai on 2017/12/27.
 */
public class SelectByPrimaryKeyForUpdatePlugin extends BasePlugin {

    public static final String METHOD_NAME = "selectByPrimaryKeyForUpdate";  // 方法名

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 找出全字段对应的Model
        FullyQualifiedJavaType fullFieldModel = introspectedTable.getRules().calculateAllFieldsClass();

        // 方法生成 selectOneByExample
        Method method = JavaElementGeneratorTools.generateMethod(
                METHOD_NAME,
                JavaVisibility.DEFAULT,
                fullFieldModel
        );
        //这里只支持一个主键!!
        List<IntrospectedColumn> introspectedColumns = introspectedTable.getPrimaryKeyColumns();
        FullyQualifiedJavaType type = introspectedColumns.get(0).getFullyQualifiedJavaType();
        Parameter parameter = new Parameter(type, introspectedColumns.get(0).getJavaProperty());
        method.addParameter(parameter);
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        FormatTools.addMethodWithBestPosition(interfaze, method);
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> introspectedColumns = introspectedTable.getPrimaryKeyColumns();
        FullyQualifiedJavaType type = introspectedColumns.get(0).getFullyQualifiedJavaType();
        // 生成查询语句
        XmlElement selectForUpdateElement = new XmlElement("select");
        // 添加注释(!!!必须添加注释，overwrite覆盖生成时，@see XmlFileMergerJaxp.isGeneratedNode会去判断注释中是否存在OLD_ELEMENT_TAGS中的一点，例子：@mbg.generated)
        commentGenerator.addComment(selectForUpdateElement);

        // 添加ID
        selectForUpdateElement.addAttribute(new Attribute("id", METHOD_NAME));
        // 添加返回类型
        selectForUpdateElement.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));
        // 添加参数类型
        selectForUpdateElement.addAttribute(new Attribute("parameterType", type.getFullyQualifiedName()));
        selectForUpdateElement.addElement(new TextElement("select"));

        StringBuilder sb = new StringBuilder();
        if (stringHasValue(introspectedTable.getSelectByExampleQueryId())) {
            sb.append('\'');
            sb.append(introspectedTable.getSelectByExampleQueryId());
            sb.append("' as QUERYID,");
            selectForUpdateElement.addElement(new TextElement(sb.toString()));
        }
        selectForUpdateElement.addElement(XmlElementGeneratorTools.getBaseColumnListElement(introspectedTable));

        sb.setLength(0);
        sb.append(" from ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        sb.append(" where ");
        sb.append(introspectedColumns.get(0).getActualColumnName()).append(" = #{")
                .append(introspectedColumns.get(0).getJavaProperty()).append("} ");
        sb.append(" for update");
        selectForUpdateElement.addElement(new TextElement(sb.toString()));
        FormatTools.addElementWithBestPosition(document.getRootElement(), selectForUpdateElement);
        logger.debug("查询单条数据插件:" + introspectedTable.getMyBatis3XmlMapperFileName() + "增加selectOneByExample方法。");
        return true;
    }
}

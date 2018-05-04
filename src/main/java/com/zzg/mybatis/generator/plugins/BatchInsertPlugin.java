package com.zzg.mybatis.generator.plugins;

import com.itfsw.mybatis.generator.plugins.ModelColumnPlugin;
import com.itfsw.mybatis.generator.plugins.utils.BasePlugin;
import com.itfsw.mybatis.generator.plugins.utils.JavaElementGeneratorTools;
import com.itfsw.mybatis.generator.plugins.utils.PluginTools;
import com.itfsw.mybatis.generator.plugins.utils.XmlElementGeneratorTools;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.List;

/**
 * Created by zengcai on 2018/01/09.
 */
public class BatchInsertPlugin extends BasePlugin {

    public static final String METHOD_BATCH_INSERT_SELECTIVE = "batchInsertSelective";  // 方法名

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(List<String> warnings) {

        // 插件使用前提是数据库为MySQL或者SQLserver，因为返回主键使用了JDBC的getGenereatedKeys方法获取主键
        if ("com.mysql.jdbc.Driver".equalsIgnoreCase(this.getContext().getJdbcConnectionConfiguration().getDriverClass()) == false
                && "com.microsoft.jdbc.sqlserver.SQLServer".equalsIgnoreCase(this.getContext().getJdbcConnectionConfiguration().getDriverClass()) == false
                && "com.microsoft.sqlserver.jdbc.SQLServerDriver".equalsIgnoreCase(this.getContext().getJdbcConnectionConfiguration().getDriverClass()) == false) {
            warnings.add("itfsw:插件" + this.getClass().getTypeName() + "插件使用前提是数据库为MySQL或者SQLserver，因为返回主键使用了JDBC的getGenereatedKeys方法获取主键！");
            return false;
        }


        // 插件使用前提是使用了ModelColumnPlugin插件
        if (!PluginTools.checkDependencyPlugin(getContext(), ModelColumnPlugin.class)) {
            warnings.add("itfsw:插件" + this.getClass().getTypeName() + "插件需配合com.itfsw.mybatis.generator.plugins.ModelColumnPlugin插件使用！");
            return false;
        }

        return super.validate(warnings);
    }


    /**
     * Java Client Methods 生成
     * 具体执行顺序 http://www.mybatis.org/generator/reference/pluggingIn.html
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
        // 2. batchInsertSelective
        FullyQualifiedJavaType selectiveType = new FullyQualifiedJavaType(introspectedTable.getRules().calculateAllFieldsClass().getShortName() + "." + ModelColumnPlugin.ENUM_NAME);
        Method mBatchInsertSelective = JavaElementGeneratorTools.generateMethod(
                METHOD_BATCH_INSERT_SELECTIVE,
                JavaVisibility.DEFAULT,
                FullyQualifiedJavaType.getIntInstance(),
                new Parameter(listType, "list", "@Param(\"list\")"),
                new Parameter(selectiveType, "selective", "@Param(\"selective\")", true)
        );
        commentGenerator.addGeneralMethodComment(mBatchInsertSelective, introspectedTable);
        // interface 增加方法
        interfaze.addMethod(mBatchInsertSelective);
        logger.debug("itfsw(批量插入插件):" + interfaze.getType().getShortName() + "增加batchInsertSelective方法。");

        return true;
    }

    /**
     * SQL Map Methods 生成
     * 具体执行顺序 http://www.mybatis.org/generator/reference/pluggingIn.html
     * @param document
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        // 2. batchInsertSelective
        XmlElement element = new XmlElement("insert");
        element.addAttribute(new Attribute("id", METHOD_BATCH_INSERT_SELECTIVE));
        // 参数类型
        element.addAttribute(new Attribute("parameterType", "map"));
        // 添加注释(!!!必须添加注释，overwrite覆盖生成时，@see XmlFileMergerJaxp.isGeneratedNode会去判断注释中是否存在OLD_ELEMENT_TAGS中的一点，例子：@mbg.generated)
        commentGenerator.addComment(element);

        // 使用JDBC的getGenereatedKeys方法获取主键并赋值到keyProperty设置的领域模型属性中。所以只支持MYSQL和SQLServer
        XmlElementGeneratorTools.useGeneratedKeys(element, introspectedTable);

        element.addElement(new TextElement("insert into " + introspectedTable.getFullyQualifiedTableNameAtRuntime() + " ("));

        XmlElement foreachInsertColumns = new XmlElement("foreach");
        foreachInsertColumns.addAttribute(new Attribute("collection", "selective"));
        foreachInsertColumns.addAttribute(new Attribute("item", "column"));
        foreachInsertColumns.addAttribute(new Attribute("separator", ","));
        foreachInsertColumns.addElement(new TextElement("${column.value}"));

        element.addElement(foreachInsertColumns);

        element.addElement(new TextElement(")"));

        // values
        element.addElement(new TextElement("values"));

        // foreach values
        XmlElement foreachValues = new XmlElement("foreach");
        foreachValues.addAttribute(new Attribute("collection", "list"));
        foreachValues.addAttribute(new Attribute("item", "item"));
        foreachValues.addAttribute(new Attribute("separator", ","));

        foreachValues.addElement(new TextElement("("));

        // foreach 所有插入的列，比较是否存在
        XmlElement foreachInsertColumnsCheck = new XmlElement("foreach");
        foreachInsertColumnsCheck.addAttribute(new Attribute("collection", "selective"));
        foreachInsertColumnsCheck.addAttribute(new Attribute("item", "column"));
        foreachInsertColumnsCheck.addAttribute(new Attribute("separator", ","));

        // 所有表字段
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
        List<IntrospectedColumn> columns1 = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
        for (int i = 0; i < columns1.size(); i++) {
            IntrospectedColumn introspectedColumn = columns.get(i);
            XmlElement check = new XmlElement("if");
            check.addAttribute(new Attribute("test", "'" + introspectedColumn.getActualColumnName() + "' == column.value"));
            check.addElement(new TextElement(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "item.")));

            foreachInsertColumnsCheck.addElement(check);
        }
        foreachValues.addElement(foreachInsertColumnsCheck);

        foreachValues.addElement(new TextElement(")"));

        element.addElement(foreachValues);

        if (context.getPlugins().sqlMapInsertElementGenerated(element, introspectedTable)) {
            document.getRootElement().addElement(element);
            logger.debug("itfsw(批量插入插件):" + introspectedTable.getMyBatis3XmlMapperFileName() + "增加batchInsertSelective实现方法。");
        }

        return true;
    }

}

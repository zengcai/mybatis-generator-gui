package com.zzg.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.PrimitiveTypeWrapper;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * Created by zengcai on 2017/12/27.
 */
public class UseGeneratedKeyPlugin extends PluginAdapter {

    private static final String USEGENERATEDKEYS = "useGeneratedKeys";
    private static final String KEYCOLUMN = "keyColumn";
    private static final String KEYPROPERTY = "keyProperty";

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        // 要使用usegeneratedkeys只能有一个主键，并且主键的类型必须是数字类型；
        //通过introspectedTable的getPrimaryKeyColumns方法得到解析出来数据库中的主键列；
        //因为主键列可能是多个，所以返回的是List<IntrospectedColumn>
        List<IntrospectedColumn> keyColumns = introspectedTable
                .getPrimaryKeyColumns();
        IntrospectedColumn keyColumn;
        //对于usegeneratedkeys来说，只能有一个主键列；
        if (keyColumns.size() == 1) {
            //得到这个唯一的主键列
            keyColumn = keyColumns.get(0);
            //得到这个列映射成Java模型之后的属性对应的Java类型；
            FullyQualifiedJavaType javaType = keyColumn
                    .getFullyQualifiedJavaType();
            //usegeneratedkeys要求主键只能是递增的，所以我们把这个主键属性的类型分别和Integer，Long，Short做对比；
            if (javaType.equals(PrimitiveTypeWrapper.getIntegerInstance())
                    || javaType.equals(PrimitiveTypeWrapper
                    .getLongInstance())
                    || javaType.equals(PrimitiveTypeWrapper
                    .getShortInstance())) {
                //如果是Integer，Long，Short三个类型中的而一个；则添加属性；
                //因为我们要添加的属性就是insert元素上的，而insert元素就是根节点，所以element就是insert元素；
                element.addAttribute(new Attribute(USEGENERATEDKEYS, "true"));
                //通过IntrospectedColumn的getActualColumnName得到列中的名称，用于生成keyColumn属性；
                element.addAttribute(new Attribute(KEYCOLUMN, keyColumn
                        .getActualColumnName()));
                //通过IntrospectedColumn的getJavaProperty方法得到key在Java对象中的属性名，用于生成keyProperty属性
                element.addAttribute(new Attribute(KEYPROPERTY, keyColumn
                        .getJavaProperty()));
            }
        }
        return true;
    }
}

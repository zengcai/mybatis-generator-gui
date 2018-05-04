package com.zzg.mybatis.generator.plugins;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;

import java.util.List;

/**
 * Created by zengcai on 2017/12/28.
 */
public class ExampleTargetPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String examplePackage = context.getProperty("examplePackage");
        if (StringUtils.isBlank(examplePackage)) {
            return;
        }
        String exampleType = introspectedTable.getExampleType();
        JavaModelGeneratorConfiguration configuration = context.getJavaModelGeneratorConfiguration();
        String targetPackage = configuration.getTargetPackage();
        String newExampleType = exampleType.replace(targetPackage, examplePackage);
        introspectedTable.setExampleType(newExampleType);
    }
}

package io.github.seal90.kiss.mybatis.generator.plugin.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * 将 Mybatis Generator xml 中的 Example 更换为指定字符串
 */
public class RenameXMLMapperPlugin extends PluginAdapter {
    private String replaceString;
    private Pattern pattern;

    @Override
    public boolean validate(List<String> warnings) {

        String searchString = properties.getProperty("searchString");
        replaceString = properties.getProperty("replaceString");

        boolean valid = stringHasValue(searchString)
                && stringHasValue(replaceString);

        if (valid) {
            pattern = Pattern.compile(searchString);
        } else {
            if (!stringHasValue(searchString)) {
                warnings.add(getString("ValidationError.18",
                        "RenameExampleClassPlugin",
                        "searchString"));
            }
            if (!stringHasValue(replaceString)) {
                warnings.add(getString("ValidationError.18",
                        "RenameExampleClassPlugin",
                        "replaceString"));
            }
        }

        return valid;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String oldType = introspectedTable.getMyBatis3XmlMapperFileName();
        Matcher matcher = pattern.matcher(oldType);
        oldType = matcher.replaceAll(replaceString);

        introspectedTable.setMyBatis3XmlMapperFileName(oldType);
    }
}
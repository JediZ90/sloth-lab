package sloth.lab.template.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import freemarker.cache.StringTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;

public class FreemarkerTest {

    public static void main(String[] args) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
        ModuleInstance instance = new ModuleInstance();
        instance.setIp("127.0.0.1");
        instance.setPort(9090);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("loadFactor", "1");
        attributes.put("loadFactorq", "1");
        instance.setAttributes(attributes);
        
        ModuleInstance instance2 = new ModuleInstance();
        instance2.setIp("127.0.0.1");
        instance2.setPort(9090);
        
        Set<ModuleInstance> instanceSet = new HashSet<>();
        instanceSet.add(instance);
        instanceSet.add(instance2);
        
        Map<String, Set<ModuleInstance>> instanceInfo = new HashMap<>();
        instanceInfo.put("TestB_100_1", instanceSet);

        String templateContent = "{\n" + 
                "                \"eventRegions\": [\n" + 
                "                    \"MicroService\"\n" + 
                "                ],\n" + 
                "                \"netChannelDeployments\": [\n" + 
                "                <#list TestB_100_1 as instance>\n" + 
                "                    {\n" + 
                "                        \"ip\": \"${instance.ip}\",\n" + 
                "                        \"port\": ${instance.port?c},\n" + 
                "                        \"attributes\": {\n" + 
                "                        <#if instance.attributes?exists>\n" + 
                "                            <#list instance.attributes?keys as key>\n" + 
                "                                \"${key}\": \"${instance.attributes[key]}\"\n" + 
                "                                <#if key_has_next>,</#if>\n" + 
                "                            </#list>\n" + 
                "                        </#if>\n" + 
                "                        }\n" + 
                "                    }\n" + 
                "                    <#if instance_has_next>,</#if>\n" + 
                "                </#list>\n" + 
                "                ],\n" + 
                "                \"moduleInfo\": {\n" + 
                "                    \"dataCenter\": \"defaultCenter\",\n" + 
                "                    \"region\": \"regionB\",\n" + 
                "                    \"module\": \"TestB\",\n" + 
                "                    \"version\": \"100\",\n" + 
                "                    \"unit\": \"1\"\n" + 
                "                }\n" + 
                "            }";

        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("configDocumentTpl", templateContent);

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_27);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setTemplateLoader(stringLoader);

        Template temp = cfg.getTemplate("configDocumentTpl");
        
        Writer out = new StringWriter(2048);
        temp.process(instanceInfo, out);
        
        System.out.println(out.toString());
    }
}

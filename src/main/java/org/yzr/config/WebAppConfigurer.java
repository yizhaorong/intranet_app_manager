package org.yzr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

@Configuration
public class WebAppConfigurer extends WebMvcConfigurationSupport {

    @Resource
    private Environment environment;
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            String path = ResourceUtils.getURL("").getPath();
            String prefix = ResourceUtils.FILE_URL_PREFIX + path;
            String debug = environment.getProperty("config.debug");
            if (debug !=null && debug.equals("debug")) {
                prefix = "classpath:/";
            }

            registry.addResourceHandler("/android/**").addResourceLocations(prefix+ "static/upload/android/");
            registry.addResourceHandler("/ios/**").addResourceLocations(prefix + "static/upload/ios/");
            registry.addResourceHandler("/crt/**").addResourceLocations(prefix + "static/crt/");
            registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
            registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
            registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
            super.addResourceHandlers(registry);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController( "/" ).setViewName( "forward:/apps" );
        super.addViewControllers(registry);
    }
}

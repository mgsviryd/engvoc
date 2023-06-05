package by.sviryd.engvoc.config;

import by.sviryd.engvoc.interceptor.CustomLocaleChangeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Autowired
    private ServerPathConfig serverPathConfig;
    @Autowired
    private LocaleConfig localeConfig;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public void addViewControllers(ViewControllerRegistry registry) {

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        CustomLocaleChangeInterceptor localeChangeInterceptor = new CustomLocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        localeChangeInterceptor.setLocaleConfig(localeConfig);
        registry.addInterceptor(localeChangeInterceptor);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/picture/**")
                .addResourceLocations(
                        "file:///"
                                + serverPathConfig.getAbsolute()
                                + "/"
                                + serverPathConfig.getUploadPicture()
                                + "/");
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

//    @Bean
//    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerCustomizer() {
//        return container -> {
//            container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/notFound"));
//        };
//    }
}
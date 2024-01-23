package by.sviryd.engvoc.config;

import by.sviryd.engvoc.interceptor.CustomLocaleChangeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
        registry.addResourceHandler(serverPathConfig.getUploadPicture() + "/**")
                .addResourceLocations(
                        "file:///"
                                + serverPathConfig.getAbsolute()
                                + "/"
                                + serverPathConfig.getUploadPicture()
                                + "/");
        registry.addResourceHandler(serverPathConfig.getUploadAudio() + "/**")
                .addResourceLocations(
                        "file:///"
                                + serverPathConfig.getAbsolute()
                                + "/"
                                + serverPathConfig.getUploadAudio()
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
package by.sviryd.engvoc.listener;

import com.twelvemonkeys.servlet.image.IIOProviderContextListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextListener;

@Component
public class ServletListener {
    @Bean
    public ServletListenerRegistrationBean<ServletContextListener> twelveMonkeysServletListener() {
        ServletListenerRegistrationBean<ServletContextListener> result =  new ServletListenerRegistrationBean<>();
        result.setListener(new IIOProviderContextListener());
        result.setOrder(1);
        return result;
    }
}

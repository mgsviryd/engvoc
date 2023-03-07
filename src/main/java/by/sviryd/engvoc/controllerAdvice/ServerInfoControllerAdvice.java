package by.sviryd.engvoc.controllerAdvice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ServerInfoControllerAdvice {
    @Value("${spring.profiles.active}")
    private String profile;
    @Value("${webpack.devPort}")
    private String webpackDevPort;

    @ModelAttribute
    public void handleRequest(HttpServletRequest request, Model model) {
        Map<String, Object> serverInfo = new HashMap<>();
        serverInfo.put("isDevMode", "dev".equals(profile));
        serverInfo.put("webpackDevPort", webpackDevPort);
        model.addAttribute("serverInfo", serverInfo);
    }
}

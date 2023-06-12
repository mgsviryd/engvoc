package by.sviryd.engvoc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/error")
public class ErrorCustomController implements ErrorController {
    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping()
    public String error(
            Model model,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        int status = response.getStatus();
        ServletWebRequest servletWebRequest = new ServletWebRequest(request);
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(servletWebRequest, true);
        model.addAllAttributes(errorAttributes);
        model.addAttribute("title", "Error " + status);
        switch (status) {
            case 404:
                return "error-404";
            case 403:
                return "error-403";
            default:
                return "error";
        }
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}

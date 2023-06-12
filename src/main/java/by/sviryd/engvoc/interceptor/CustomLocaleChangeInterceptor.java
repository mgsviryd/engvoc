package by.sviryd.engvoc.interceptor;

import by.sviryd.engvoc.config.LocaleConfig;
import lombok.Setter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Setter
public class CustomLocaleChangeInterceptor extends LocaleChangeInterceptor {
    private LocaleConfig localeConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws ServletException {
        String locale = request.getParameter("lang");
        if (!localeConfig.isPresent(locale)) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            if (localeResolver == null) {
                throw new IllegalStateException(
                        "No LocaleResolver found: not in a DispatcherServlet request?");
            }
            try {
                localeResolver.setLocale(request, response, parseLocaleValue(localeConfig.getDefaultLocale()));
            } catch (IllegalArgumentException ex) {
                if (isIgnoreInvalidLocale()) {
                    logger.debug("Ignoring invalid locale value [" + locale + "]: " + ex.getMessage());
                } else {
                    throw ex;
                }
            }
            return true;
        } else {
            return super.preHandle(request, response, handler);
        }
    }
}

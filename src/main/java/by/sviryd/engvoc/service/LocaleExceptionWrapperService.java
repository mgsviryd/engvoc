package by.sviryd.engvoc.service;

import by.sviryd.engvoc.util.LocaleException;
import by.sviryd.engvoc.util.ThrowingRunnable;
import by.sviryd.engvoc.util.ThrowingSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class LocaleExceptionWrapperService {
    public void runAndWrap(ThrowingRunnable r, List<LocaleException> errors, Object... args) {
        try {
            r.run();
        } catch (Exception e) {
            errors.add(new LocaleException(e, args));
        }
    }

    public Object getAndWrap(ThrowingSupplier s, List<LocaleException> errors, Locale locale, Object... args) {
        Object obj = null;
        try {
            obj = s.get();
        } catch (Exception e) {
            errors.add(new LocaleException(e, args));
        }
        return obj;
    }

    public void runAndWrapIfAttrNoPrevious(ThrowingRunnable r, List<LocaleException> errors, String attrName, Object... args) {
        if (errors.size() > 0 && errors.get(errors.size()-1).getAttrName().equals(attrName)) return;
        try {
            r.run();
        } catch (Exception e) {
            errors.add(new LocaleException(e, null, attrName, args));
        }
    }
    public LocaleException getLocalException(ThrowingRunnable r, String attrName, Object... args) {
        try {
            r.run();
            return null;
        } catch (Exception e) {
            return new LocaleException(e, null, attrName, args);
        }
    }

}

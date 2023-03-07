package by.sviryd.engvoc.service;

import by.sviryd.engvoc.util.LocaleException;
import by.sviryd.engvoc.util.ThrowingRunnable;
import by.sviryd.engvoc.util.ThrowingSupplier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocaleExceptionWrapperService {
    public void runAndWrap(ThrowingRunnable r, List<LocaleException> errors, Object... args) {
        try {
            r.run();
        } catch (Exception e) {
            errors.add(new LocaleException(e, args));
        }
    }

    public Object getAndWrap(ThrowingSupplier s, List<LocaleException> errors, Object... args) {
        Object obj = null;
        try {
            obj = s.get();
        } catch (Exception e) {
            errors.add(new LocaleException(e, args));
        }
        return obj;
    }
}

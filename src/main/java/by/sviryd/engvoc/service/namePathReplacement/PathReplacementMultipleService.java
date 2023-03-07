package by.sviryd.engvoc.service.namePathReplacement;

import by.sviryd.engvoc.domain.IPath;
import by.sviryd.engvoc.service.stringReplacementService.IStringReplacementService;
import org.springframework.stereotype.Service;

@Service
public class PathReplacementMultipleService {
    public void adjustPath(IPath entityWithPath, IStringReplacementService... services) {
        String path = entityWithPath.getPath();
        if (path == null) return;
        path = IStringReplacementService.replace(path, services);
        entityWithPath.setPath(path);
    }
}

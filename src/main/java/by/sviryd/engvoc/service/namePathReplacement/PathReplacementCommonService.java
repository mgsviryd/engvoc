package by.sviryd.engvoc.service.namePathReplacement;

import by.sviryd.engvoc.domain.IPath;
import by.sviryd.engvoc.service.stringReplacementService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathReplacementCommonService<T extends IPath> {
    @Autowired
    private PathReplacementMultipleService pathStringReplacementMultipleService;

    @Autowired
    private UrlEncodingASCIIStringReplacementService urlEncodingASCIIStringReplacementService;
    @Autowired
    private PunctuationHyphenStringReplacementService punctuationHyphenStringReplacementService;
    @Autowired
    private ForwardSlashHyphenStringReplacementService forwardSlashHyphenStringReplacementService;
    @Autowired
    private BackSlashHyphenStringReplacementService backSlashHyphenStringReplacementService;
    @Autowired
    private SpaceHyphenStringReplacementService spaceHyphenStringReplacementService;
    @Autowired
    private UpperCaseLowerCaseStringReplacementService upperCaseLowerCaseStringReplacementService;
    @Autowired
    private UrlReservedWordsCommonStringReplacementService urlReservedWordsCommonStringReplacementService;
    @Autowired
    private UrlReservedWordsProductFieldsStringReplacementService urlReservedWordsProductFieldsStringReplacementService;

    public void adjustPath(T entityWithPath) {
        if (entityWithPath == null) return;
        pathStringReplacementMultipleService.adjustPath(
                entityWithPath,
                urlEncodingASCIIStringReplacementService,
                punctuationHyphenStringReplacementService,
                forwardSlashHyphenStringReplacementService,
                backSlashHyphenStringReplacementService,
                spaceHyphenStringReplacementService,
                upperCaseLowerCaseStringReplacementService,
                urlReservedWordsCommonStringReplacementService,
                urlReservedWordsProductFieldsStringReplacementService
        );
    }

    public void adjustPath(List<T> entitiesWithPath) {
        if (entitiesWithPath == null || entitiesWithPath.isEmpty()) return;
        entitiesWithPath.forEach(this::adjustPath);
    }
}

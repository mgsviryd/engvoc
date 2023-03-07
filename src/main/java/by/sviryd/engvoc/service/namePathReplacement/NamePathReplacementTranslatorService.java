package by.sviryd.engvoc.service.namePathReplacement;

import by.sviryd.engvoc.domain.INamePath;
import by.sviryd.engvoc.service.TranslatorGoogleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NamePathReplacementTranslatorService<T extends INamePath> {
    @Autowired
    private TranslatorGoogleService translatorService;

    public void adjustPaths(List<T> entities, String langFrom, String langTo) throws Exception {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        entities = entities.stream().filter(Objects::nonNull).filter(x -> x.getName() != null).collect(Collectors.toList());
        List<String> names = entities.stream().map(INamePath::getName).collect(Collectors.toList());
        List<String> texts = translatorService.translate(langFrom, langTo, names);
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).setPath(texts.get(i));
        }
    }
}

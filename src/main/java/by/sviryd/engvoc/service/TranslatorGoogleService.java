package by.sviryd.engvoc.service;


import by.sviryd.engvoc.config.GoogleActivatorConfig;
import com.google.cloud.translate.v3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TranslatorGoogleService {
    @Autowired
    private GoogleActivatorConfig googleActivatorConfig;

    public String translate(String langFrom, String langTo, String text) throws IOException {
        if (text == null) return null;
         List<String> texts = translate(langFrom, langTo, Collections.singletonList(text));
         if (texts.isEmpty()){
             return null;
         }else {
             return texts.get(0);
         }
    }
    public List<String> translate(String langFrom, String langTo, List<String> texts) throws IOException {
        if (texts == null || texts.isEmpty()){
            return Collections.emptyList();
        }
        return translateText(googleActivatorConfig.getProjectId(), langFrom, langTo, texts);
    }

    private List<String> translateText(String projectId, String langFrom, String langTo, List<String> texts)
            throws IOException {

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (TranslationServiceClient client = TranslationServiceClient.create()) {
            // Supported Locations: `global`, [glossary location], or [model location]
            // Glossaries must be hosted in `us-central1`
            // Custom Models must use the same location as your model. (us-central1)
            LocationName parent = LocationName.of(projectId, "global");

            // Supported Mime Types: https://cloud.google.com/translate/docs/supported-formats
            TranslateTextRequest request =
                    TranslateTextRequest.newBuilder()
                            .setParent(parent.toString())
                            .setMimeType("text/plain")
                            .setSourceLanguageCode(langFrom)
                            .setTargetLanguageCode(langTo)
                            .addAllContents(texts)
                            .build();
            TranslateTextResponse response = client.translateText(request);
            List<Translation> translationsList = response.getTranslationsList();
            return translationsList.stream().map(Translation::getTranslatedText).collect(Collectors.toList());
        }
    }
}
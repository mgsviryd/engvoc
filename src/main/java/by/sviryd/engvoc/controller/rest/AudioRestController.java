package by.sviryd.engvoc.controller.rest;


import by.sviryd.engvoc.util.LocaleUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import marytts.LocalMaryInterface;
import marytts.modules.synthesis.Voice;
import marytts.util.data.audio.MaryAudioUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Locale;

import static marytts.modules.synthesis.Voice.getDefaultVoice;

@RestController
@RequestMapping("/json/audio")
public class AudioRestController {
    @PostMapping(value = "/generate")
    public ResponseEntity<Resource> generate(
            @RequestBody String json
    ) throws Exception {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        String text = obj.get("text").getAsString();
        Locale locale = LocaleUtils.getLocaleByAbbr(obj.get("locale").getAsString());
        LocalMaryInterface mary = new LocalMaryInterface();
        mary.setLocale(locale);
        Voice defaultVoice = getDefaultVoice(locale);
        mary.setVoice(defaultVoice.getName());
        AudioInputStream audio = mary.generateAudio(text);
        double[] array = MaryAudioUtils.getSamplesAsDoubleArray(audio);
        File file = File.createTempFile("temp", ".wav");
        MaryAudioUtils.writeWavFile(array, file.getAbsolutePath(), audio.getFormat());
        ByteArrayInputStream inp = new ByteArrayInputStream(Files.readAllBytes(file.toPath()));
        InputStreamResource resource = new InputStreamResource(inp);
        file.deleteOnExit();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "temp.wav")
                .header("extension", ".wav")
                .contentType(MediaType.parseMediaType("audio/wav"))
                .body(resource);
    }
}

package by.sviryd.engvoc.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.MalformedURLException;
import java.net.URL;

@Converter(autoApply = true)
public class URLToStringConverter implements AttributeConverter<URL, String> {
    @Override
    public String convertToDatabaseColumn(URL url) {
        return url.toString();
    }

    @Override
    public URL convertToEntityAttribute(String url) {
        if (url != null) {
            try {
                return new URL(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Cannot convert String to URL: " + url);
            }
        } else {
            return null;
        }
    }
}

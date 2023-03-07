package by.sviryd.engvoc.service.jsoup;

import by.sviryd.engvoc.domain.Category;
import by.sviryd.engvoc.domain.Dimension;
import by.sviryd.engvoc.domain.Property;
import by.sviryd.engvoc.type.Unit;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.List;
import java.util.Locale;

public interface IProductURLExtractor {
    String getVendorCode(Document doc);

    String getName(Document doc);

    String getCode(Document doc);

    Unit getUnit(Document doc);

    Integer getCount(Document doc);

    Dimension getDimension(Document doc);

    Double getWeight(Document doc);

    Unit getUnitInPack(Document doc);

    Integer getCountInPack(Document doc);

    Dimension getDimensionPack(Document doc);

    Double getWeightPack(Document doc);

    Unit getUnitCarry(Document doc);

    Integer getCountInPackCarry(Document doc);

    Dimension getDimensionCarry(Document doc);

    Double getWeightCarry(Document doc);

    String getBarcode(Document doc);

    String getDescription(Document doc);

    List<Category> getCategories(Document doc);

    List<Property> getProperties(Document doc);

    List<String> getPropertyValues(Document doc);

    List<URL> getImagesUrl(Document doc);

    List<URL> getDescriptionImagesUrl(Document doc);

    URL getUrl();

    Locale getLocale();

    boolean isHrefExtractsByFileLoading();
}

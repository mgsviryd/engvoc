package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.*;
import by.sviryd.engvoc.service.exception.NoPotentialProductException;
import by.sviryd.engvoc.service.jsoup.IProductURLExtractor;
import by.sviryd.engvoc.service.jsoup.JsoupDocService;
import by.sviryd.engvoc.service.jsoup.extractor.ExtractorErrorService;
import by.sviryd.engvoc.service.jsoup.extractor.ExtractorErrorUrlService;
import by.sviryd.engvoc.service.productPathExtractor.PathProductByNameExtractorService;
import by.sviryd.engvoc.type.Unit;
import by.sviryd.engvoc.util.URLUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ProductURLExtractorService {
    private static final String DESCRIPTION = "описание";
    private static final int BATCH_SIZE = 100;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPropertyService productPropertyService;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private JsoupDocService jsoupDocService;

    @Autowired
    private ExtractorErrorService errorService;

    @Autowired
    private ExtractorErrorUrlService urlService;

    @Autowired
    private PathProductByNameExtractorService productPathExtractor;

    @Autowired
    private PictureMediaService pictureMediaService;


    public Product getProduct(IProductURLExtractor extractor, Document doc) throws NoPotentialProductException {
        String code = extractor.getCode(doc);
        String name = extractor.getName(doc);
        if (!isPotentialProduct(code, name)) {
            throw new NoPotentialProductException("One of necessary product fields evaluate as null.");
        }
        String vendorCode = extractor.getVendorCode(doc);
        Unit unit = extractor.getUnit(doc);
        Integer count = extractor.getCount(doc);
        Dimension dimension = extractor.getDimension(doc);
        Double weight = extractor.getWeight(doc);
        Unit unitPack = extractor.getUnitInPack(doc);
        Integer countInPack = extractor.getCountInPack(doc);
        Dimension dimensionPack = extractor.getDimensionPack(doc);
        Double weightPack = extractor.getWeightPack(doc);
        Unit unitCarry = extractor.getUnitCarry(doc);
        Integer countInPackCarry = extractor.getCountInPackCarry(doc);
        Dimension dimensionCarry = extractor.getDimensionCarry(doc);
        Double weightCarry = extractor.getWeightCarry(doc);
        String barcode = extractor.getBarcode(doc);
        String description = extractor.getDescription(doc);

        Product product = new Product();
        product.setCode(code);
        product.setVendorCode(vendorCode);
        product.setName(name);
        product.setUnit(unit);
        product.setCount(count);
        product.setDimension(dimension);
        product.setWeight(weight);
        product.setUnitPack(unitPack);
        product.setCountPack(countInPack);
        product.setDimensionPack(dimensionPack);
        product.setWeightPack(weightPack);
        product.setUnitCarry(unitCarry);
        product.setCountCarry(countInPackCarry);
        product.setDimensionCarry(dimensionCarry);
        product.setWeightCarry(weightCarry);
        product.setBarcode(barcode);
        product.setDescription(description);
        return product;
    }

    private boolean isPotentialProduct(Object... nullEvaluated) {
        return ObjectUtils.allNotNull(nullEvaluated);
    }

    public ExtractorError saveProductsIfNotExistsByCodeAndSaveErrors(IProductURLExtractor extractor) {
        List<ExtractorErrorUrl> errorUrls = saveProductsIfNotExistsByCode(extractor, extractor.getUrl());
        return saveErrorUrls(errorUrls);
    }

    public ExtractorError saveProductsIfNotExistsByCodeAndSaveErrors(IProductURLExtractor extractor, URL urlMatcher) {
        List<ExtractorErrorUrl> errorUrls = saveProductsIfNotExistsByCode(extractor, urlMatcher);
        return saveErrorUrls(errorUrls);
    }

    public ExtractorError saveProductsIfNotExistsByCodeAndSaveErrors(IProductURLExtractor extractor, Stream<URL> urls) {
        List<ExtractorErrorUrl> errorUrls = saveProductsIfNotExistsByCode(extractor, urls);
        return saveErrorUrls(errorUrls);
    }

    public ExtractorError saveProductIfNotExistsByCodeAndSaveError(IProductURLExtractor extractor, URL url) {
        return saveProductsIfNotExistsByCodeAndSaveErrors(extractor, Stream.of(url));
    }

    public ExtractorError saveProductsIfNotExistsByCodeAndSaveErrorsFromURL(IProductURLExtractor extractor, URL urlWithHrefs) {
        Document doc;
        try {
            doc = jsoupDocService.getJsoupDoc(urlWithHrefs.toString());
        } catch (IOException e) {
            List<ExtractorErrorUrl> errorUrls = Collections.singletonList(new ExtractorErrorUrl(urlWithHrefs, e.getMessage()));
            return saveErrorUrls(errorUrls);
        }
        Stream<URL> allHrefHtml = jsoupDocService.getAllHrefHtml(doc, extractor.isHrefExtractsByFileLoading())
                .map(URLUtil::getURLOrNull)
                .filter(Objects::nonNull);
        return saveProductsIfNotExistsByCodeAndSaveErrors(extractor, allHrefHtml);
    }

    private List<ExtractorErrorUrl> saveProductsIfNotExistsByCode(IProductURLExtractor extractor, URL urlMatcher) {
        Set<String> all = Collections.synchronizedSet(new HashSet<>());
        Set<String> selected = Collections.synchronizedSet(new HashSet<>());
        all.add(urlMatcher.toString());
        List<URL> urls = productService.getUrlsStartWith(extractor.getUrl().toString());
        List<String> urlsString = urls.stream().map(URL::toString).collect(Collectors.toList());
        selected.addAll(urlsString);
        List<ExtractorErrorUrl> errorUrls = new LinkedList<>();
        while (!all.isEmpty()) {
            List<String> batchUrls = all.stream().limit(BATCH_SIZE).collect(Collectors.toList());
            log.info("ALL: " + all.size() + " TRY EXTRACT: " + batchUrls);
            selected.addAll(batchUrls);
            all.removeAll(batchUrls);
            batchUrls.stream().parallel().forEach(url -> tryExtractUrlsAndSaveProduct(extractor, all, selected, errorUrls, url));
        }
        return errorUrls;
    }

    private void tryExtractUrlsAndSaveProduct(IProductURLExtractor extractor, Set<String> all, Set<String> selected, List<ExtractorErrorUrl> errorUrls, String url) {
        Document doc = getDocument(errorUrls, url);
        if (doc != null) {
            extractUrls(doc, all, selected, extractor);
            saveProduct(extractor, errorUrls, url, doc);
        }
    }

    private void saveProduct(IProductURLExtractor extractor, List<ExtractorErrorUrl> errorUrls, URL url, Document doc) {
        try {
            saveProductIfNotExistsByCode(extractor, doc);
        } catch (Exception e) {
            errorUrls.add(new ExtractorErrorUrl(url, e.getMessage()));
            log.info("Cannot save extracted product because cannot fetch: " + url, e);
        }
    }

    private void saveProduct(IProductURLExtractor extractor, List<ExtractorErrorUrl> errorUrls, String urlString, Document doc) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            return;
        }
        saveProduct(extractor, errorUrls, url, doc);
    }

    private Document getDocument(List<ExtractorErrorUrl> errorUrls, String urlString) {
        Document doc = null;
        try {
            doc = jsoupDocService.getJsoupDoc(urlString);
        } catch (Exception e) {
            URL url = URLUtil.getURLOrNull(urlString);
            if (url != null) {
                errorUrls.add(new ExtractorErrorUrl(url, e.getMessage()));
            }
            log.info("Cannot parse jsoup doc: " + urlString, e);
        }
        return doc;
    }

    private void extractUrls(Document doc, Set<String> all, Set<String> selected, IProductURLExtractor extractor) {
        Stream<String> allHrefHtml = jsoupDocService.getAllHrefHtml(doc, extractor.isHrefExtractsByFileLoading());
        List<String> collect = allHrefHtml.filter(e -> {
            String urlMatcher = extractor.getUrl().toString();
            return e.contains(urlMatcher) && !selected.contains(e);
        }).collect(Collectors.toList());
        all.addAll(collect);
    }

    private ExtractorError saveErrorUrls(List<ExtractorErrorUrl> errorUrls) {
        if (errorUrls == null || errorUrls.isEmpty()) {
            return null;
        }
        List<ExtractorErrorUrl> saveErrorUrls = urlService.saveAll(errorUrls);
        ExtractorError extractorError = new ExtractorError();
        extractorError.setUrls(saveErrorUrls);
        return errorService.save(extractorError);
    }

    private List<ExtractorErrorUrl> saveProductsIfNotExistsByCode(IProductURLExtractor extractor, Stream<URL> urls) {
        List<ExtractorErrorUrl> errorUrls = new LinkedList<>();
        urls.filter(Objects::nonNull).distinct().parallel().forEach(url -> {
                    Document doc = getDocument(errorUrls, url.toString());
                    if (doc != null) {
                        saveProduct(extractor, errorUrls, url, doc);
                    }
                }
        );
        return errorUrls;
    }

    private void saveProductIfNotExistsByCode(IProductURLExtractor extractor, Document doc) throws Exception {
        String code = extractor.getCode(doc);
        if (code == null || productService.existsByCode(code)) {
            return;
        }
        saveProductOrRollback(extractor, doc);
    }

    private void saveProductOrRollback(IProductURLExtractor extractor, Document doc) throws NoPotentialProductException {
        Product product = getProduct(extractor, doc);
        List<Category> categories = getCategoriesWithPictures(extractor, doc);
        List<URL> imagesUrl = extractor.getImagesUrl(doc);
        List<URL> descriptionImagesUrl = extractor.getDescriptionImagesUrl(doc);
        List<Property> properties = extractor.getProperties(doc);
        List<String> values = extractor.getPropertyValues(doc);

        List<Picture> pictures = downloadPictures(imagesUrl);
        List<Picture> descriptionPictures = downloadPictures(descriptionImagesUrl);
        Category category = categoryService.saveShaneOfCategory(categories);
        pictures = pictureService.saveAll(pictures);
        descriptionPictures = pictureService.saveAll(descriptionPictures);
        String description = product.getDescription();
        if (description == null) {
            description = getAndRemoveDescription(properties, values);
        }
        properties = propertyService.saveAll(properties, category);
        String path = productPathExtractor.getPath(product);

        product.setCategory(category);
        product.setUrl(URLUtil.getURLOrNull(doc.location()));
        product.setPath(path);
        product.setDescription(description);
        if (!pictures.isEmpty()) {
            product.setPicture(pictures.get(0).getPath());
        }
        product = productService.save(product);

        for (Picture p : pictures) {
            p.setProductPicture(product);
        }
        pictureService.saveAll(pictures);

        for (Picture p : descriptionPictures) {
            p.setProductDescriptionPicture(product);
        }
        pictureService.saveAll(descriptionPictures);

        int size = properties.size();
        for (int i = 0; i < size; i++) {
            ProductProperty productProperty = new ProductProperty();
            productProperty.setProduct(product);
            productProperty.setProperty(properties.get(i));
            productProperty.setPropertyData(new PropertyData(values.get(i),null));
            try {
                productPropertyService.save(productProperty);
            } catch (Exception e) {
                log.error(e.getMessage(), productProperty);
            }
        }
    }

    private List<Category> getCategoriesWithPictures(IProductURLExtractor extractor, Document doc) {
        List<Category> categories = extractor.getCategories(doc);
        categories.forEach(c -> c.setPicture(pictureMediaService.getDefaultPictureFileName()));
        return categories;
    }

    private List<Picture> downloadPictures(List<URL> imagesUrl) {
        if (imagesUrl == null || imagesUrl.isEmpty()) {
            return Collections.emptyList();
        }
        List<Picture> pictures = new ArrayList<>();
        int priority = 0;
        for (URL url : imagesUrl) {
            String name = null;
            if (url != null) {
                name = pictureMediaService.savePictureOrRollback(url);
            }
            if (name == null) {
                name = pictureMediaService.getDefaultPictureFileName();
            }
            pictures.add(new Picture(name, url, priority++));
        }
        return pictures;
    }

    private String getAndRemoveDescription(List<Property> properties, List<String> values) {
        String description = null;
        for (int i = 0; i < properties.size(); i++) {
            Property p = properties.get(i);
            if (p.getName().equalsIgnoreCase(DESCRIPTION)) {
                description = values.get(i);
                properties.remove(i);
                values.remove(i);
            }
        }
        return description;
    }
}

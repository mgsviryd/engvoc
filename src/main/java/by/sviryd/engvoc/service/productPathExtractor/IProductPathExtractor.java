package by.sviryd.engvoc.service.productPathExtractor;

import by.sviryd.engvoc.domain.Product;

public interface IProductPathExtractor {
    String getPath(Product product) throws Exception;
}

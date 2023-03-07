package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.Product;
import by.sviryd.engvoc.domain.Views;
import by.sviryd.engvoc.service.ProductService;
import by.sviryd.engvoc.util.ObjectToViewTransformUtil;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/json/product")
public class ProductRestController {
    @Autowired
    private ProductService productService;

    @PostMapping(value = "/ids")
    @JsonView(Views.ProductRaw.class)
    public List<Product> propertyChoices(
            @RequestBody String json
    ) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        JsonArray idsString = obj.get("ids").getAsJsonArray();
        Type arrayOfLongType = new TypeToken<ArrayList<Long>>() {
        }.getType();
        List<Long> indexes = gson.fromJson(idsString, arrayOfLongType);
        return productService.findAllById(indexes);
    }

    @GetMapping("/{id}")
    public String findById(
            @PathVariable("id") Product product
    ) {
        String description = product.getDescription(); // remove lazy
        product = ObjectToViewTransformUtil.transformToView(product, Views.ProductPage.class);
        Gson gson = new GsonBuilder().create();
        return gson.toJson(product);
    }
}

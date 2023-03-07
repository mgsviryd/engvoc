package by.sviryd.engvoc.util;

public class PriceUtil {
    public static double getRowPriceWithDiscount(double price, double discount) {
        if (price == 0 || discount == 0) return price;
        if (price < 0) throw new IllegalArgumentException("Price is negative: " + price);
        if (discount < 0) throw new IllegalArgumentException("Discount is negative: " + discount);
        return price - (price * discount / 100);
    }

    public static double getRowPriceWithoutVat(double price, double vat) {
        if (price == 0 || vat == 0) return price;
        if (price < 0) throw new IllegalArgumentException("Price is negative: " + price);
        if (vat < 0) throw new IllegalArgumentException("Vat is negative: " + vat);
        return price / (1 + (vat / 100));
    }

}

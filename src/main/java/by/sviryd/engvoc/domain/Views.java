package by.sviryd.engvoc.domain;

public final class Views {
    public interface Message extends Id, Text, Tag, CreationLDT {
    }

    public interface IdText extends Id, Text {
    }

    public interface Id {
    }

    public interface Name {
    }

    public interface Code {
    }

    public interface Icon {
    }

    public interface Picture {
    }

    public interface Priority {
    }

    public interface Path {
    }

    public interface Price {
    }

    public interface Discount {
    }

    public interface PriceWithDiscount {
    }

    public interface PriceWithoutVatWithDiscount {
    }

    public interface Category {
    }

    public interface ProductCount {
    }

    public interface QuantityInStock {
    }

    public interface QuantitySupplier {
    }

    public interface Unit {
    }

    public interface Text {
    }

    public interface Tag {
    }

    public interface CreationLDT {
    }

    public interface Dimension {
    }

    public interface DimensionPack {
    }

    public interface DimensionCarry {
    }

    public interface Url {
    }

    public interface Barcode {
    }

    public interface VendorCode {
    }

    public interface Description {
    }

    public interface QuantityReserved {
    }

    public interface QuantityFuture {
    }

    public interface Vat {
    }

    public interface Weight {
    }

    public interface WeightPack {
    }

    public interface WeightCarry {
    }

    public interface DeliveryCountry {
    }

    public interface MadeCountry {
    }

    public interface UnitPack {
    }

    public interface UnitCarry {
    }

    public interface Count {
    }

    public interface CountPack {
    }

    public interface CountCarry {
    }

    public interface Popular {
    }

    public interface Visitors {
    }

    public interface Fresh {
    }

    public interface Invisible {
    }

    public interface Supplier {
    }

    public interface VendorFull {
    }

    public interface VendorShort {
    }

    public interface VendorPicture {
    }

    public interface ServiceCenter {
    }

    public interface IdNamePathIconProductCount extends Id, Name, Path, Icon, ProductCount, Priority, Picture {
    }

    public interface ProductRaw extends Id, Name, Code, Path, Picture, Price, Discount, PriceWithDiscount, PriceWithoutVatWithDiscount, Category, QuantityInStock, QuantitySupplier, Unit, CreationLDT {
    }

    public interface Parent{}
    public interface DictionaryRaw extends Id, Name, Parent, Picture, Priority, Invisible{}

    public interface Word{}
    public interface Translation{}
    public interface Example{}
    public interface ExampleTranslation {}
    public interface Transcription{}
    public interface Sound {}
    public interface LearnedLDT{}
    public interface ForgotLDT{}
    public interface CountForgot{}
    public interface Dictionary{}
    public interface Learned{}

    public interface CardPage extends
            Id,
            Word,
            Translation,
            Example,
            ExampleTranslation,
            Transcription,
            Sound,
            CreationLDT,
            LearnedLDT,
            ForgotLDT,
            CountForgot,
            Picture,
            Dictionary
    {
    }

    public interface ProductPage extends
            Id,
            Name,
            Code, Path,
            Picture,
            Price,
            Discount,
            PriceWithDiscount,
            PriceWithoutVatWithDiscount,
            Category,
            QuantityInStock,
            QuantitySupplier,
            Unit,
            CreationLDT,
            Dimension,
            DimensionPack,
            DimensionCarry,
            Barcode,
            VendorCode,
            Description,
            Vat,
            Weight,
            WeightPack,
            WeightCarry,
            DeliveryCountry,
            MadeCountry,
            UnitPack,
            UnitCarry,
            Count,
            CountPack,
            CountCarry,
            Popular,
            Visitors,
            Fresh,
            VendorFull,
            VendorShort,
            VendorPicture,
            ServiceCenter {
    }
}

package by.sviryd.engvoc.domain;

public final class Views {

    public interface Active{}
    public interface CountCard{}

    public interface Id {
    }

    public interface Parent {
    }

    public interface Picture {
    }

    public interface Priority {
    }

    public interface Invisible {
    }

    public interface Unique {
    }

    public interface Lang {
    }

    public interface Name {
    }

    public interface CreationLDT {
    }

    public interface Word {
    }

    public interface Translation {
    }

    public interface Example {
    }

    public interface ExampleTranslation {
    }

    public interface Transcription {
    }

    public interface Sound {
    }

    public interface LearnedLDT {
    }

    public interface ForgotLDT {
    }

    public interface CountForgot {
    }

    public interface Learned {
    }

    public interface Dictionary extends
            Id,
            Unique,
            Lang,
            Name,
            CreationLDT,
            Parent,
            Picture,
            Priority,
            Invisible {
    }

    public interface Card extends
            Id,
            Unique,
            Lang,
            Word,
            Translation,
            Example,
            ExampleTranslation,
            Transcription,
            Sound,
            CreationLDT,
            LearnedLDT,
            Learned,
            ForgotLDT,
            CountForgot,
            Picture,
            Dictionary {
    }

    public interface DictionaryCard extends Dictionary, Card {
    }

}

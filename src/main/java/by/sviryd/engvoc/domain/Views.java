package by.sviryd.engvoc.domain;

public final class Views {

    public interface Active {
    }

    public interface CountCard {
    }

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

    public interface Unrepeated {
    }

    public interface LangLocale {
    }

    public interface LangLocalePairs extends LangLocalePair {

    }
    public interface LangLocalePair extends LangLocale, Name, Active {
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

    public interface CountShown {
    }

    public interface CountAnswered {
    }

    public interface Learned {
    }

    public interface Dictionary extends
            Id,
            Unrepeated,
            LangLocalePair,
            Name,
            CreationLDT,
            Parent,
            Picture,
            Priority,
            Invisible {
    }

    public interface Card extends
            Id,
            Unrepeated,
            LangLocalePair,
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
            CountShown,
            CountAnswered,
            Picture,
            Dictionary {
    }

    public interface DictionaryCard extends Dictionary, Card {
    }

    public interface LastModifiedLDT {
    }

    public interface Social {
    }

    public interface Sub {
    }

    public interface Token {

    }

    public interface Email {

    }

    public interface Username {
    }

    public interface User extends Id, Email, LastModifiedLDT, LangLocalePairs, Social, Sub, Token, Username {
    }

}

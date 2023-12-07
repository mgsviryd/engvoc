package by.sviryd.engvoc.domain;

public final class Views {

    public interface UserAndLocaleExceptionMessage extends User, LocaleExceptionMessage {

    }
    public interface LocaleExceptionMessage extends Code, Attribute, Message {

    }

    public interface Code {
    }

    public interface Attribute {
    }

    public interface Message {
    }

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

    public interface DictionaryAndLocaleExceptionMessage extends Dictionary, LocaleExceptionMessage {
    }
    public interface VocabularyAndLocaleExceptionMessage extends Dictionary, LocaleExceptionMessage {
    }

    public interface CardAndLocaleExceptionMessage extends Card, LocaleExceptionMessage {
    }

    public interface Dictionary extends
            Id,
            Unrepeated,
            Vocabulary,
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
            Vocabulary,
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

    public interface VocabularyDictionaryCard extends Vocabulary, Dictionary, Card {
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

    public interface User extends Id, Email, LastModifiedLDT, Vocabulary, Social, Sub, Token, Username {
    }
    public interface UserAndLangLocale extends User, LangLocale{}

    public interface Source extends LangLocale {
    }

    public interface Target extends LangLocale {
    }

    public interface Vocabulary extends Id, Name, Source, Target {
    }

}

export default {
    getLanguageByLangAndCountry(lang) {
        return new Intl.DisplayNames([lang.lang], {type: 'language'}).of(lang.lang + "-" + lang.country)
    },
}
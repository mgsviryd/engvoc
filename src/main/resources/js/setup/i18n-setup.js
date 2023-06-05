import Vue from "vue";
import VueI18n from "vue-i18n";
import commonMessages from "../lang/common.js";

Vue.use(VueI18n)

export const i18n = new VueI18n({
    silentTranslationWarn:true
})

const loadedLanguages = []
function setI18nLanguage (lang) {
    i18n.locale = lang
    Vue.http.interceptors.push((request, next) => {
        request.headers.set('Accept-Language', lang)
        next()
    })
    document.querySelector('html').setAttribute('lang', lang)
    return lang
}
export function loadLanguageAsync(lang) {
    if (i18n.locale === lang) {
        return Promise.resolve(setI18nLanguage(lang))
    }
    if (loadedLanguages.includes(lang)) {
        return Promise.resolve(setI18nLanguage(lang))
    }
    return Promise.resolve().then(
        () => {
            let messages = require('../lang/' + lang + '.js')
            messages = {...messages, ...commonMessages}
            i18n.setLocaleMessage(lang, messages)
            loadedLanguages.push(lang)
            return setI18nLanguage(lang)
        }
    )
}
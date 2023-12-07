import Vue from "vue"

export default {
    getFrontend: (tokens, lang) => Vue.http.post("/json/frontend", {tokens}, {params: {lang}}),
}
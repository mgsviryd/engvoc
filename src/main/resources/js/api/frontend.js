import Vue from "vue"

export default {
    getFrontend: (lang) => Vue.http.get("/json/frontend", {params: {lang}}),
}
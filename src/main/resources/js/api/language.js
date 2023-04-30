import Vue from "vue";

export default {
    getList: () => Vue.http.get("/json/lang/list"),
    getMap: (lang) => Vue.http.get("/json/lang/map", {params: {lang}}),
    changeLang: (lang) => Vue.http.get("json/lang/change", {params: {lang}}),
}

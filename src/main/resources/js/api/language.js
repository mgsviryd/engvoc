import Vue from "vue";

export default {
    getList: () => Vue.http.get("/json/lang/list"),
    changeLang: (lang) => Vue.http.get("/json/lang/change", {params: {lang}}),
}

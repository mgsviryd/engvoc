import Vue from "vue";
const base = "/json/lang"
const list = "/list"
const map = "/map"
export default {
    getList: () => Vue.http.get(base + list),
    getMap: (lang) => Vue.http.get(base + map + "/" + lang),
}

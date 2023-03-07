import Vue from "vue";
const base = "/json/frontend"
export default {
    getFrontend: () => Vue.http.get(base),
}
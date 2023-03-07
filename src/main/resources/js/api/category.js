import Vue from "vue";

export default {
    getCategoryPage: (categoryChainUrl) => Vue.http.get(categoryChainUrl),
}
import Vue from "vue";

export default {
    getHierarchy: ()=> Vue.http.get('/category/hierarchy'),
}
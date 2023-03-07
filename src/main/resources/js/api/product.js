import Vue from "vue";
const productsPage = Vue.resource('/json/product/{id}')
export default {
    findById: (id) => productsPage.get({id}),
    findAllById: (ids) => Vue.http.post('/json/product/ids', {ids}),
}
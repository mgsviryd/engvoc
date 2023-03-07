import Vue from "vue";
const base = "/json/authentication"
const activate = "/activate"

export default {
    getAuthentication: (tokens) => Vue.http.post(base,{tokens}),
    getActivation: (id) => Vue.http.post(base + activate,{id}),
}
import Vue from "vue";

export default {
    getAuthentication: (tokens) => Vue.http.post("/json/authentication", {tokens}),
    getActivation: (id) => Vue.http.post("/json/authentication/activate", {id}),
}
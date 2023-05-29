import Vue from "vue";

const configUrlencoded = {headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"}}
const configJson = {headers: {"Content-Type": "application/json", "Accept": "application/json"}}
export default {
    getAuthentication: (tokens) => Vue.http.post("/json/authentication", {tokens}),
    getActivation: (id) => Vue.http.post("/json/authentication/activate", {id}),
    register: (body) => Vue.http.post("/json/authentication/register", body),
    signin: (body) => Vue.http.post("/sign/in", body, configUrlencoded),
}
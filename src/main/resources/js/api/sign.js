import Vue from "vue";

const configUrlencoded = {headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"}}
export default {
    in: (body) => Vue.http.post("/sign/in", body, configUrlencoded),
    sendVerificationToken: ()=>Vue.http.get("/sign/sendVerificationToken"),
    getUsers: (tokens) => Vue.http.post("/json/sign/users", {tokens}),
    up: (body) => Vue.http.post("/json/sign/up", body),
}
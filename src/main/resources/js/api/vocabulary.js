import Vue from "vue"

const configMultipartJson = {headers: {"Content-Type": "multipart/form-data", "Accept": "application/json"}}
const configUndefinedJson = {headers: {"Content-Type": "undefined", "Accept": "application/json"}}


export default {
    save: (vocabulary) => Vue.http.post("/json/vocabulary/save", vocabulary),
    deleteDanger: (body) => Vue.http.post("/json/vocabulary/delete/danger", body),
    findData: (vocabulary) => Vue.http.post("/json/vocabulary/findData", vocabulary),
}
import Vue from "vue"

const configMultipartJson = { headers: { "Content-Type": "multipart/form-data", "Accept": "application/json" } }
const configUndefinedJson = { headers: { "Content-Type": "undefined", "Accept": "application/json" } }
const dictionaries = Vue.resource('/json/dictionary/{id}')

export default {

    add: dictionary => dictionaries.save({}, dictionary),
    update: dictionary => dictionaries.update({id: dictionary.id}, dictionary),
    remove: id => dictionaries.remove({id}), // actually id: id - but use short form
    get: () => dictionaries.get(),

    findAll: () => Vue.http.get("/json/dictionary/findAll"),
    deleteByIdIn: (body) => Vue.http.delete("/json/dictionary/deleteByIdIn",{body:  body}),
    deleteByUnrepeatedDanger: (body) => Vue.http.post("/json/dictionary/deleteByUnrepeated/danger", body),

    saveWithPicture: (formData) => Vue.http.post("/json/dictionary/saveWithPicture", formData, configUndefinedJson),
    saveWithoutPicture: (body) => Vue.http.post("/json/dictionary/saveWithoutPicture", body),
}
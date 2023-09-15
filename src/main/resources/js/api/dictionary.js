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
    deleteByUnrepeated: (body) => Vue.http.delete("/json/dictionary/deleteByUnrepeated", {body: body}),

    saveUnrepeatedWithPicture: (formData) => Vue.http.post("/json/dictionary/saveUnrepeatedWithPicture", formData, configUndefinedJson),
    saveUnrepeated: (dictionary) => Vue.http.post("/json/dictionary/saveUnrepeated", dictionary),

    findDictionariesAndCards: (vocabulary) => Vue.http.post("/json/dictionary/findDictionariesAndCards", vocabulary),
    saveNewUnrepeated: (vocabulary) => Vue.http.post("/json/dictionary/saveNewUnrepeated", vocabulary),
}
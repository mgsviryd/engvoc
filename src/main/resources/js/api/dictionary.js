import Vue from "vue";
const db = "/json/dictionary"

const configMultipartJson = { headers: { "Content-Type": "multipart/form-data", "Accept": "application/json" } }
const configUndefinedJson = { headers: { "Content-Type": "undefined", "Accept": "application/json" } }
const dictionaries = Vue.resource('/json/dictionary/{id}')

export default {

    add: dictionary => dictionaries.save({}, dictionary),
    update: dictionary => dictionaries.update({id: dictionary.id}, dictionary),
    remove: id => dictionaries.remove({id}), // actually id: id - but use short form
    get: () => dictionaries.get(),

    findAll: () => Vue.http.get(db + "/findAll"),
    deleteByIdIn: (body) => Vue.http.delete(db + "/deleteByIdIn",{body:  body}),
    deleteByUnique: (body) => Vue.http.delete(db + "/deleteByUnique", {body: body}),

    saveUniqueWithPicture: (formData) => Vue.http.post(db+ '/saveUniqueWithPicture', formData, configUndefinedJson),
    saveUnique: (dictionary) => Vue.http.post(db+ '/saveUnique', dictionary),
}
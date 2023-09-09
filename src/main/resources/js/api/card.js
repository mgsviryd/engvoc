import Vue from "vue"

const configMultipartJson = {headers: {"Content-Type": "multipart/form-data", "Accept": "application/json"}}
const configUndefinedJson = {headers: {"Content-Type": "undefined", "Accept": "application/json"}}
const cards = Vue.resource('/json/card/{id}')

export default {
    uploadXmlFile: (formData) => Vue.http.post("/json/card/upload/xml/file", formData, configMultipartJson),
    uploadXmlFiles: (formData) => Vue.http.post("/json/card/upload/xml/files", formData, configMultipartJson),
    uploadExcelFile: (formData) => Vue.http.post("/json/card/upload/excel/file", formData, configMultipartJson),
    uploadExcelFiles: (formData) => Vue.http.post("/json/card/upload/excel/files", formData, configMultipartJson),

    downloadXmlFile: dictionaryId => Vue.http.get("/json/card/download/xml", {params: {dictionaryId}}),
    downloadXmlFiles: (ids) => Vue.http.post("/json/card/download/xmls", ids, {responseType: 'arraybuffer'}),
    downloadExcelFile: dictionaryId => Vue.http.get("/json/card/download/excel", {params: {dictionaryId}, responseType: 'arraybuffer'}),

    add: card => cards.save({}, card),
    update: card => cards.update({id: card.id}, card),
    remove: id => cards.remove({id}), // actually id: id - but use short form
    get: id => cards.get({id}),

    findAll: () => Vue.http.get("/json/card" + "/findAll"),
    deleteByIdIn: (body) => Vue.http.delete("/json/card/deleteByIdIn", {body: body}),
    deleteByDictionary: (body) => Vue.http.delete("/json/card/deleteByDictionary", {body: body}),

    saveWithPicture: (formData) => Vue.http.post("/json/card/saveWithPicture", formData, configUndefinedJson),
    saveWithoutPicture: card => Vue.http.post("/json/card/saveWithoutPicture", card),

    changeDictionary: (card, id) => Vue.http.post("/json/card/changeDictionary", card, {params: {id}}),
    changeDictionaries: (cards, id) => Vue.http.post("/json/card/changeDictionaries", cards, {params: {id}}),
}
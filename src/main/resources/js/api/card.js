import Vue from "vue";

const db = "/json/card"
const upload = "/json/card/upload"
const download = "/json/card/download"
const xml = "/xml"
const excel = "/excel"
const xmlFile = "/xml/file"
const xmlFiles = "/xml/files"
const excelFile = "/excel/file"
const excelFiles = "/excel/files"
const configMultipartJson = {headers: {"Content-Type": "multipart/form-data", "Accept": "application/json"}}
const cards = Vue.resource('/json/card/{id}')

export default {
    uploadXmlFile: (formData) => Vue.http.post(upload + xmlFile, formData, configMultipartJson),
    uploadXmlFiles: (formData) => Vue.http.post(upload + xmlFiles, formData, configMultipartJson),
    uploadExcelFile: (formData) => Vue.http.post(upload + excelFile, formData, configMultipartJson),
    uploadExcelFiles: (formData) => Vue.http.post(upload + excelFiles, formData, configMultipartJson),

    add: card => cards.save({}, card),
    update: card => cards.update({id: card.id}, card),
    remove: id => cards.remove({id}), // actually id: id - but use short form
    get: id => cards.get({id}),

    findAll: () => Vue.http.get(db + "/findAll"),
    deleteByIdIn: (body) => Vue.http.delete(db + "/deleteByIdIn", {body: body}),

    saveUnique: card => Vue.http.post(db + "/saveUnique", card),
    updateUnique: card => Vue.http.post(db + "/updateUnique", card),

    saveAllUnique: (cards) => Vue.http.post(db + "/saveAllUnique", cards),
    updateAllUnique: (cards) => Vue.http.post(db + "/updateAllUnique", cards),

    changeDictionary: (cardId, destId, isNeedCheckUnique) => Vue.http.post(db + "/changeDictionary", {cardId: cardId, destId: destId, isNeedCheckUnique: isNeedCheckUnique}),
    changeDictionaries: (cardIds, destId, isNeedCheckUnique) => Vue.http.post(db + "/changeDictionaries", {cardIds: cardIds, destId: destId, isNeedCheckUnique: isNeedCheckUnique}),
}
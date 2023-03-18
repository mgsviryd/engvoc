import Vue from "vue";
const upload = "/json/card/upload"
const download = "/json/card/download"
const xml = "/xml"
const excel = "/excel"
const xmlFile = "/xml/file"
const xmlFiles = "/xml/files"
const excelFile = "/excel/file"
const excelFiles = "/excel/files"
const configMultipartJson = { headers: { "Content-Type": "multipart/form-data", "Accept": "application/json" } }

export default {
    uploadXmlFile: (formData) => Vue.http.post(upload + xmlFile, formData, configMultipartJson),
    uploadXmlFiles: (formData) => Vue.http.post(upload + xmlFiles, formData, configMultipartJson),
    uploadExcelFile: (formData) => Vue.http.post(upload + excelFile, formData, configMultipartJson),
    uploadExcelFiles: (formData) => Vue.http.post(upload + excelFiles, formData, configMultipartJson),
}
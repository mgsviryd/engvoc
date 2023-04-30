import Vue from "vue";

const pictureMedia = Vue.resource('/json/pictureMedia/{id}')
const configMultipartJson = {headers: {"Content-Type": "multipart/form-data", "Accept": "application/json"}}

export default {
    get: () => pictureMedia.get(),
    savePicture: (formData) => Vue.http.post("/json/pictureMedia/savePicture", formData, configMultipartJson),
}
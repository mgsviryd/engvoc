import Vue from "vue";
const path = '/json/pictureMedia'

const pictureMedia = Vue.resource('/json/pictureMedia/{id}')
const configMultipartJson = { headers: { "Content-Type": "multipart/form-data", "Accept": "application/json" } }


export default {
    get: () => pictureMedia.get(),

    savePicture: (formData) => Vue.http.post(path + '/savePicture', formData, configMultipartJson),
}
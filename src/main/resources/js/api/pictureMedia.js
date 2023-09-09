import Vue from "vue"

const configMultipartJson = {headers: {"Content-Type": "multipart/form-data", "Accept": "application/json"}}

export default {
    get: () => Vue.http.get("/json/pictureMedia"),
    savePicture: (formData) => Vue.http.post("/json/pictureMedia/savePicture", formData, configMultipartJson),
}
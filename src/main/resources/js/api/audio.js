import Vue from "vue"

const configUrlencoded = {headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"}}
export default {
    generateAudio: (text) => Vue.http.post("/json/audio/generate", text, {responseType: 'arraybuffer'}),
}
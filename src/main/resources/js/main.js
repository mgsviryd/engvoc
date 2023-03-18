import Vue from "vue";
import "api/resource";
import router from 'router/router'
import Main from "pages/Main.vue";
import "@babel/polyfill";
import store from "store/store";
import {connect} from "./util/ws";
import "bootstrap";
import vueHeadful from 'vue-headful';
import Multiselect from 'vue-multiselect'
import VueCookies from 'vue-cookies'
import VueWait from 'vue-wait'
import UUID from 'vue-uuid';
var VueScrollTo = require('vue-scrollto');
import VueSlider from 'vue-slider-component'
import 'vue-slider-component/theme/default.css'
import BootstrapVue from 'bootstrap-vue'
import IconsPlugin from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import VueFileAgent from 'vue-file-agent';
import VueFileAgentStyles from 'vue-file-agent/dist/vue-file-agent.css';
import $ from 'jquery';
window.jQuery = $;
window.$ = $;

Vue.component('vue-headful', vueHeadful);
Vue.component('multiselect', Multiselect)
Vue.component('VueSlider', VueSlider)

Vue.use(VueCookies)
Vue.use(VueWait)
Vue.use(UUID)
Vue.use(VueScrollTo, {
    container: "body",
    duration: 500,
    easing: "ease",
    offset: 0,
    force: true,
    cancelable: true,
    onStart: false,
    onDone: false,
    onCancel: false,
    x: false,
    y: true
})
Vue.use(BootstrapVue)
Vue.use(IconsPlugin)
Vue.use(VueFileAgent);
Vue.use(VueFileAgentStyles);

connect()

new Vue({
    el: '#main',
    store,
    router,
    wait: new VueWait(),
    render: a => a(Main),
})
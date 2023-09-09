import Vue from "vue"
import "api/resource"
import router from 'router/router'
import Main from "pages/Main.vue"
import "@babel/polyfill"
import store from "store/store"
import "bootstrap"
import vueHeadful from 'vue-headful'
import Multiselect from 'vue-multiselect'
import VueCookies from 'vue-cookies'
import VueWait from 'vue-wait'
const VueScrollTo = require('vue-scrollto');
import VueSlider from 'vue-slider-component'
import 'vue-slider-component/theme/default.css'
import BootstrapVue from 'bootstrap-vue'
import IconsPlugin from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faTwitter } from '@fortawesome/free-brands-svg-icons'
import { faUserSecret } from '@fortawesome/free-solid-svg-icons'
import VueFileAgent from 'vue-file-agent'
import VueFileAgentStyles from 'vue-file-agent/dist/vue-file-agent.css'
import GlobalEvents from 'vue-global-events'
import VueForceNextTick from 'vue-force-next-tick'
import {i18n} from "./setup/i18n-setup";

import $ from 'jquery';
window.jQuery = $;
window.$ = $;

Vue.component('vue-headful', vueHeadful);
Vue.component('multiselect', Multiselect)
Vue.component('VueSlider', VueSlider)
Vue.component('GlobalEvents', GlobalEvents)
Vue.component('font-awesome-icon', FontAwesomeIcon)
library.add(faTwitter, faUserSecret)

Vue.use(VueCookies)
Vue.use(VueWait)
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
Vue.use(VueFileAgent)
Vue.use(VueFileAgentStyles)
Vue.use(VueForceNextTick)



new Vue({
    el: '#main',
    store,
    router,
    i18n,
    wait: new VueWait(),
    render: a => a(Main),
})
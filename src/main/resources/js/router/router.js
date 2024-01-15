import Vue from 'vue'
import VueRouter from 'vue-router'
import Vocabulary from '../pages/Vocabulary.vue'
import Greeting from '../pages/Greeting.vue'

Vue.use(VueRouter)

const routes = [
    {path: '/', name: 'greeting', component: Greeting},
    {path: '/vocabulary', name: 'vocabulary', component: Vocabulary, meta: {requiresAuth: true}},
    {path: '/sign/:mark', name: 'sign', component: Greeting},
]

export default new VueRouter({
    mode: 'history',
    routes
})
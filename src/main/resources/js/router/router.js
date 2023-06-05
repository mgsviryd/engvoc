import Vue from 'vue'
import VueRouter from 'vue-router'
import Card from '../pages/Card.vue'
import Greeting from '../pages/Greeting.vue'
import Sign from '../pages/Sign.vue'

Vue.use(VueRouter)

const routes = [
    {path: '/', name: 'greeting', component: Greeting},
    {path: '/card', name: 'card', component: Card},
    {path: '/sign/:mark', name: 'sign', component: Sign},
]

export default new VueRouter({
    mode: 'history',
    routes
})
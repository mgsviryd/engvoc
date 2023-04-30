import Vue from 'vue'
import VueRouter from 'vue-router'
import Card from '../pages/Card.vue'
import Activation from '../components/notifications/Activation.vue'

Vue.use(VueRouter)

const routes = [
    { path: '/card', name: 'card', component: Card },
    { path: '/activation/:id', name: 'activation', component: Activation, props: true },
]

export default new VueRouter({
    mode: 'history',
    routes
})
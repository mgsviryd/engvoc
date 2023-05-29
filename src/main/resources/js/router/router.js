import Vue from 'vue'
import VueRouter from 'vue-router'
import Card from '../pages/Card.vue'
import Sign from '../pages/Sign.vue'
import NotFound from '../pages/NotFound.vue'

Vue.use(VueRouter)

const routes = [
    { path: '/card', name: 'card', component: Card },
    { path: '/sign/:mark', name: 'sign', component: Sign },
    { path: '/*', name: 'notFound', component: NotFound },
]

export default new VueRouter({
    mode: 'history',
    routes
})
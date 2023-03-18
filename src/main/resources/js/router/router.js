import Vue from 'vue'
import VueRouter from 'vue-router'
import Card from '../pages/Card.vue'
import Root from 'pages/Root.vue'
import Category from 'pages/Category.vue'
import Basket from 'pages/Basket.vue'
import Favorite from 'pages/Favorite.vue'
import Product from 'pages/Product.vue'
import Recent from 'pages/Recent.vue'
import Activation from '../components/notifications/Activation.vue'

Vue.use(VueRouter)

const routes = [
    { path: '/', name: 'root', component: Root },
    { path: '/card', name: 'card', component: Card },
    { path: '/category/:path/:id', name: 'category', component: Category,},
    { path: '/product/:path/:id', name: 'product', component: Product,},
    { path: '/basket', name: 'basketChange', component: Basket, },
    { path: '/favorite', name: 'favorite', component: Favorite, },
    { path: '/recent', name: 'recent', component: Recent, },
    { path: '/activation/:id', name: 'activation', component: Activation, props: true },
]

export default new VueRouter({
    mode: 'history',
    routes
})
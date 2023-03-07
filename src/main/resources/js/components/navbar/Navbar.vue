<template>
    <div v-if="showComponent" class="navbar-navbar">
        <div class="parts-navbar-navbar">
            <nav class="navbar navbar-expand-lg navbar-dark bg-dark navbar-fixed-top py-2 shadow border-bottom border-secondary"
                 style="position:fixed; z-index: 10000; width:100%;">
                <!-- размер и цветовая схема-->
                <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbar"
                        aria-controls="navbar" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbar">  <!--указываем, что меню сворачивается-->
                    <ul class="navbar-nav mr-auto"> <!-- ul - список; mr-auto - автоматически генерируемое меню-->
                        <li class="nav-item"> <!-- элементы меню -->
                            <div class="btn-group btn-group-md btn-group-justified ml-2 mr-3" role="group"
                                 aria-label="Basic example">
                                <button class="btn btn-secondary"
                                        data-toggle="tooltip"
                                        data-placement="bottom"
                                        :title="[[lang.map.goToCatalogue]]"
                                        @click.prevent.stop="scrollToCategoryHierarchy()">
                                    {{lang.map.catalogue}}
                                </button>
                            </div>
                        </li>
                        <li class="nav-item">
                            <div class="btn-group btn-group-md btn-group-justified">
                                <a
                                        class="btn btn-block btn-light btn-sm rounded-0 border-0 text-left"
                                        href="tel:+375445867823">
                                    <img src="/static/picture/icon/a1.png" width="25" height="25">
                                    <span class="glyphicon glyphicon-earphone"></span>
                                    &nbsp;+375&nbsp;(44)&nbsp;
                                    <b>586-78-23</b>
                                </a>
                                <button type="button"
                                        class="btn btn-light btn-sm border-left dropdown-toggle dropdown-toggle-split"
                                        data-toggle="dropdown"
                                        aria-haspopup="true"
                                        aria-expanded="false"
                                        id="phoneDropdown">
                                    <span class="sr-only">Toggle Dropdown</span>
                                </button>
                                <div class="dropdown-menu shadow-sm" aria-labelledby="phoneDropdown">
                                    <a class="btn btn-block btn-light btn-sm rounded-0 border-0 pr-2 mr-4 text-left"
                                       href="tel:+375292612505">
                                        <span class="glyphicon glyphicon-earphone"></span>
                                        <img src="/static/picture/icon/mts.png" width="25" height="25">
                                        &nbsp;+375&nbsp;(29)&nbsp;
                                        <b>261-25-05</b>
                                    </a>
                                    <a class="btn btn-block btn-light btn-sm rounded-0 border-0 pr-2 mr-4 text-left"
                                       href="tel:+375173884060">
                                        <span class="glyphicon glyphicon-earphone"></span>
                                        <img src="/static/picture/icon/phone.png" width="25" height="25">
                                        &nbsp;+375&nbsp;(17)&nbsp;
                                        <b>388-40-60</b>
                                    </a>
                                </div>
                            </div>
                        </li>
                    </ul>
                </div>

                <div class="btn-group btn-group-md btn-group-justified mx-2">
                    <notifications></notifications>
                    <lang-dropdown></lang-dropdown>
                </div>

                <div class="btn-group btn-group-md btn-group-justified">
                    <button class="btn btn-secondary mr-sm-1"
                            data-toggle="tooltip"
                            data-placement="bottom"
                            :title="[[lang.map.recent]]"
                            @click="href('/recent')">
                        <i class="fas fa-eye" aria-hidden="true"></i>
                    </button>

                    <button class="btn btn-secondary mr-sm-1"
                            data-toggle="tooltip"
                            data-placement="bottom"
                            :title="[[lang.map.ordersHistory]]"
                            @click="href('/history')">
                        <i class="fas fa-history" aria-hidden="true"></i>
                        <span v-if="isAuthenticated" class="badge badge-success">
                        <i class="fas fa-user"></i></span>
                        <span v-else="isAuthenticated" class="badge badge-danger">
                        <i class="fas fa-user-lock"></i>
                        </span>
                    </button>

                    <button v-if="isNotAnimateFavorites" class="btn btn-secondary mr-sm-1"
                            data-toggle="tooltip"
                            data-placement="bottom"
                            :title="[[lang.map.favorites]]"
                            @click="href('/favorite')">
                        <i class="far fa-star" aria-hidden="true"></i>
                    </button>
                    <button v-if="isAnimateFavoritesMark" class="btn btn-light mr-sm-1 text-danger"
                            data-toggle="tooltip"
                            data-placement="bottom"
                            :title="[[lang.map.favorites]]"
                            @click="href('/favorites')">
                        {{favoriteChange.mark}}
                    </button>

                    <button v-if="isAnimateBasketSum" class="btn btn-light bg-light" style="min-width: 100px;"
                            data-toggle="tooltip"
                            data-placement="bottom"
                            :title="[[lang.map.basket]]"
                            @click="href('/basket')">
                        <span class="badge badge-danger">+&nbsp;{{basketChange.sum}} &nbsp;p.</span>
                    </button>
                    <button v-if="isAnimateBasketCount" class="btn btn-light bg-light" style="min-width: 100px;"
                            data-toggle="tooltip"
                            data-placement="bottom"
                            :title="[[lang.map.basket]]"
                            @click="href('/basket')">
                        <span class="badge badge-danger">+&nbsp;{{basketChange.count}}</span>
                    </button>
                    <button v-if="isNotAnimateBasket" class="btn btn-secondary text-left pl-3" style="min-width: 100px;"
                            data-toggle="tooltip"
                            data-placement="bottom"
                            :title="[[lang.map.basket]]"
                            @click="href('/basket')">
                        <i class="fa fa-shopping-cart pr-1" aria-hidden="true"></i>
                        <span class="badge"
                              :class="basketAmount ? 'badge-primary':'badge-white'">{{basketAmount}}</span>
                        <small>&nbsp;{{lang.map.bynShort}}</small>
                    </button>
                </div>
            </nav>
        </div>
    </div>

</template>

<script>
    import {mapState} from 'vuex'
    import LangDropdown from 'components/lang/LangDropdown.vue'
    import Notifications from 'components/notifications/Notifications.vue'
    export default{
        components: {
            LangDropdown,
            Notifications,
        },
        computed: {
            ...mapState([
                'basketChange',
                'favoriteChange',
                'basket',
                'scrollDuration',
                'scrollOptions',
                'lang',
            ]),
            basketAmount(){
                return this.basket.amount
            },
            isAuthenticated(){
                return this.$store.getters.isAuthenticated
            },
        },
        watch: {
            $route: [
                'fetchData',
            ],
        },
        created() {
            this.fetchData()
            this.$store.watch(this.$store.getters.getBasketChangeId, basketChangeId => {
                this.animateBasket()
            })
            this.$store.watch(this.$store.getters.getFavoriteChangeId, favoriteChangeId => {
                this.animateFavorites()
            })
        },
        data(){
            return {
                isNotAnimateBasket: true,
                isAnimateBasketCount: false,
                isAnimateBasketSum: false,
                isNotAnimateFavorites: true,
                isAnimateFavoritesMark: false,
                showComponent: false,
            }
        },
        methods: {
            fetchData(){
                this.showComponent = false
                this.showComponent = true
            },
            animateBasket() {
                setTimeout(() => this.animateBasketCount(), 0);
                setTimeout(() => this.animateBasketSum(), 1000);
                setTimeout(() => this.notAnimateBasket(), 3000);

            },
            animateBasketCount() {
                this.isNotAnimateBasket = false
                this.isAnimateBasketCount = true
                this.isAnimateBasketSum = false
            },
            animateBasketSum() {
                this.isNotAnimateBasket = false
                this.isAnimateBasketCount = false
                this.isAnimateBasketSum = true
            },
            notAnimateBasket() {
                this.isNotAnimateBasket = true
                this.isAnimateBasketCount = false
                this.isAnimateBasketSum = false
            },
            animateFavorites() {
                setTimeout(() => this.animateFavoritesCount(), 0);
                setTimeout(() => this.notAnimateFavorites(), 1000);
            },
            animateFavoritesCount() {
                this.isNotAnimateFavorites = false
                this.isAnimateFavoritesMark = true
            },
            notAnimateFavorites() {
                this.isNotAnimateFavorites = true
                this.isAnimateFavoritesMark = false
            },
            href(path){
                this.$router.push({
                    path: path,
                }).then(() => {
                    if (this.isFavoritePath(path)) {
                        this.scrollToFavorite()
                    }
                    if (this.isBasketPath(path)) {
                        this.scrollToBasket()
                    }
                    if (this.isRecentPath(path)) {
                        this.scrollToRecent()
                    }
                }).catch(err => {
                })
            },
            isFavoritePath(path){
                return path == '/favorite'
            },
            isBasketPath(path){
                return path == '/basket'
            },
            isRecentPath(path){
                return path == '/recent'
            },
            scrollToCategoryHierarchy(){
                this.$scrollTo('#category-hierarchy', this.scrollDuration, this.scrollOptions)
            },
            scrollToFavorite(){
                this.$scrollTo('#favorite', this.scrollDuration, this.scrollOptions)
            },
            scrollToBasket(){
                this.$scrollTo('#basket', this.scrollDuration, this.scrollOptions)
            },
            scrollToRecent(){
                this.$scrollTo('#recent', this.scrollDuration, this.scrollOptions)
            },
        }
    }

</script>

<style>

</style>
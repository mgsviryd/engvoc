<template>
    <div v-if="showComponent">
        <div v-if="!isFavoriteClear" id="favorite">
            <div class="row d-flex no-gutters mx-2">
                <div class="ml-auto">
                    <sortSingleDropdownFavorite></sortSingleDropdownFavorite>
                    <div class="btn-group">
                        <button type="button" class="btn btn-sm btn-outline-danger shadow-sm" data-toggle="modal"
                                data-target="#favorite-idClear">
                            {{lang.map.clear}}&nbsp;
                            <i class="far fa-star" aria-hidden="true"></i>
                        </button>
                    </div>
                    <confirm-action :id="'favorite-idClear'" @confirm="confirmClear" @reject="rejectClear"
                                    :message="lang.map.confirmCleanFavorite"></confirm-action>
                </div>
            </div>
            <v-wait for="loading">
                <template slot="waiting">
                    <div class="d-flex justify-content-center">
                        <google-circle :widthRem="10" :heightRem="10"></google-circle>
                    </div>
                </template>
                <product-row v-if="showComponent" v-for="(product,i) in products"
                             :key="product.id"
                             :product="product"
                             :productIndex="i"
                />
            </v-wait>
        </div>
        <div v-if="isFavoriteClear"
             class="row d-flex no-gutters justify-content-center my-5 text-muted">
            <h6>{{lang.map.favoriteClear}}</h6>
        </div>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    import ProductRow from '../../components/product/ProductRow.vue'
    import GoogleCircle from '../../components/spinner/GoogleCircle.vue'
    import ConfirmAction from '../../components/modal/ConfirmAction.vue'
    import SortSingleDropdownFavorite from '../../components/pager/SortSingleDropdownFavorite.vue'
    export default{
        components: {
            ProductRow,
            GoogleCircle,
            ConfirmAction,
            SortSingleDropdownFavorite,
        },
        data(){
            return {
                products: [],
                isFavoriteClear: false,
                showComponent: false,
                showConfirmClear: false,
            }
        },
        created(){
            this.fetchData()
            this.$store.watch(this.$store.getters.getFavoriteId, favoriteId => {
                this.fetchData()
            })
        },
        watch: {
            $route: 'fetchData',
        },
        computed: {
            ...mapState([
                'favorite',
                'sortOrder',
                'lang',
            ]),
        },
        methods: {
            async fetchData(){
                this.$wait.start('loading');
                this.showComponent = false
                this.products = this.favorite.products
                this.updateFavoriteClear()
                this.showComponent = true
                this.$wait.end('loading');
            },
            updateFavoriteClear(){
                this.isFavoriteClear = this.products == null || this.products.length == 0
            },
            removeFavorite() {
                this.$store.dispatch('removeFavoriteAction')
            },
            confirmClear(){
                this.removeFavorite()
            },
            rejectClear(){

            },
        }
    }
</script>

<style scoped>

</style>
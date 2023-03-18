<template>
    <div v-if="showComponent">
        <div v-if="!isBasketClear" id="basket">
            <div class="row d-flex no-gutters mx-2">
                <div class="ml-auto">
                    <sortSingleDropdownBasket></sortSingleDropdownBasket>
                    <div class="btn-group">
                        <button type="button" class="btn btn-sm btn-outline-danger shadow-sm"
                                data-toggle="modal"
                                data-target="#basket-idClear">
                            {{lang.map.clear}}&nbsp;
                            <i class="fa fa-shopping-cart" aria-hidden="true"></i>
                        </button>
                    </div>
                    <confirm-action :id="'basket-idClear'" @confirm="confirmClear" @reject="rejectClear"
                                    :message="lang.map.confirmCleanBasket"></confirm-action>
                </div>
            </div>
            <div class="row no-gutters border justify-content-between align-items-center mx-2">
                <div class="col-5 px-0 text-center">
                    <small>{{lang.map.productItem}}</small>
                </div>
                <div class="col-1 px-0 text-center">
                    <small>{{lang.map.inSight}}</small>
                </div>
                <div class="col-1 px-0 text-center">
                    <small>{{lang.map.atDepot}}</small>
                </div>
                <div class="col-2 px-0 text-center">
                    <small>{{lang.map.price}}</small>
                </div>
                <div class="col-1 px-0 text-center">
                    <small>{{lang.map.count}}</small>
                </div>
                <div class="col-1 px-0 text-center">
                    <small>{{lang.map.amount}}</small>
                </div>
                <div class="col-1 px-0 text-center">
                </div>
            </div>
            <v-wait for="loading">
                <template slot="waiting">
                    <div class="d-flex justify-content-center">
                        <google-circle :widthRem="10" heightRem="10"></google-circle>
                    </div>
                </template>
                <basket-product-row v-for="(product,i) in products"
                                    :key="product.id"
                                    :product="product"
                                    :productIndex="i"
                />
                <div class="row mx-2 border-top border-muted border-3 bg-white">
                    <div class="col-10 px-0 text-right text-muted">
                        {{lang.map.total}}:
                    </div>
                    <div class="col-1 px-0 text-center text-muted">
                        {{basketAmount}}&nbsp;{{lang.map.bynShort}}
                    </div>
                    <div class="col-1 px-0 text-center"></div>
                </div>
                <div class="row mx-2 border-bottom border-muted border-3 bg-white">
                    <div class="col-10 px-0 text-right">
                        <b>{{lang.map.allForPayment}}:</b>
                    </div>
                    <div class="col-1 px-0 text-center text-danger">
                        <b>
                            {{basketAmount}}&nbsp;{{lang.map.bynShort}}
                        </b>
                    </div>
                </div>
            </v-wait>
        </div>
        <div v-if="isBasketClear"
             class="row d-flex no-gutters justify-content-center my-5 text-muted">
            <h6>{{lang.map.basketClear}}</h6>
        </div>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    import BasketProductRow from '../../components/product/BasketProductRow.vue'
    import ConfirmAction from '../../components/modal/ConfirmAction.vue'
    import GoogleCircle from '../../components/spinner/GoogleCircle.vue'
    import SortSingleDropdownBasket from '../../components/pager/SortSingleDropdownBasket.vue'

    export default{
        components: {
            BasketProductRow,
            ConfirmAction,
            GoogleCircle,
            SortSingleDropdownBasket,
        },
        data(){
            return {
                products: [],
                isBasketClear: false,
                showComponent: false,
            }
        },
        created(){
            this.fetchData()
            this.$store.watch(this.$store.getters.getBasketId, basketId => {
                this.fetchData()
            })
        },
        watch: {
            $route: 'fetchData',
        },
        computed: {
            ...mapState([
                'basket',
                'lang',
            ]),
            basketAmount(){
                return this.basket.amount
            },
        },
        methods: {
            fetchData(){
                this.$wait.start('loading')
                this.showComponent = false
                this.products = this.basket.products
                this.updateBasketClear()
                this.showComponent = true
                this.$wait.end('loading')
            },
            updateBasketClear(){
                this.isBasketClear = this.products === null || this.products.length === 0
            },
            removeBasket() {
                this.$store.dispatch('removeBasketAction')
            },
            confirmClear(){
                this.removeBasket()
            },
            rejectClear(){

            },
        }
    }
</script>

<style scoped>

</style>
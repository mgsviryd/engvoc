<template>
    <div v-if="showComponent">
        <div class="row no-gutters mx-2 py-2 border-top border-muted border-3 bg-white shadow justify-content-between align-items-center">
            <div class="col-5 px-0">
                <button class="btn bg-transparent border-0 rounded-0 btn-block px-0 py-0 mx-0 my-0"
                        style="height: 100%"
                        @click="goTo()">
                    <div class="container-fluid px-0 py-0">
                        <div class="row no-gutters align-items-center">
                            <div class="col-1">
                                {{productIndex+1}}
                            </div>
                            <div class="col-3">
                                <picture-upload :pathPic="picture" :marker="marker" :alt="alt"
                                                :pictureClass="pictureClass" :pictureStyle="pictureStyle"/>
                            </div>
                            <div class="col-8">
                                <div class="container-fluid">
                                    <div class="card mb-3 bg-transparent border-0 text-left"
                                         style="width: 100%">
                                        <div class="card-body border-0 px-0 py-0">
                                            <h6 class="card-title border-0 mb-1">{{product.name}}</h6>
                                        </div>
                                        <div class="card-footer bg-transparent border-0 px-0 py-0">
                                            <div class="text-left">
                                                <small>{{lang.map.code}}:&nbsp;{{product.code}}</small>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </button>
            </div>

            <div class="col-1 px-0 text-center text-muted">
                <div v-if="product.quantityInStock">
                    <i class="fas fa-check-circle text-success"></i>
                    &nbsp;
                    {{product.quantityInStock}}
                    <unit :unit="unit"/>
                </div>
                <div v-else="product.quantityInStock">
                    <i class="fas fa-times-circle text-danger"></i>
                </div>
            </div>

            <div class="col-1 px-0 text-center text-muted">
                <div v-if="product.quantitySupplier">
                    <i class="fas fa-check text-primary"></i>
                    &nbsp;
                    {{product.quantitySupplier}}
                    <unit :unit="unit"/>
                </div>
                <div v-else="product.quantitySupplier">
                    <i class="fas fa-times-circle text-warning"></i>
                </div>
            </div>

            <div class="col-2 px-0 text-center ">
                <div v-if="product.price">
                    <div v-if="product.discount">
                        <a class="btn border-0 disable text-muted">
                            <strike>
                                {{product.price}}&nbsp;{{lang.map.bynShort}}
                            </strike>
                        </a>
                        <span class="badge badge-danger">
                                <i class="fas fa-fire"></i>
                                -{{product.discount}}%
                            </span>
                        <button class="btn border-0 text-danger" @click="goTo()">
                            <b>
                                {{product.priceWithDiscount}}&nbsp;{{lang.map.bynShort}}
                            </b>
                        </button>
                    </div>

                    <div v-else="product.discount">
                        <button class="btn border-0 text-danger" @click="goTo()">
                            <b>
                                {{product.price}}&nbsp;{{lang.map.bynShort}}
                            </b>
                        </button>
                    </div>
                </div>
                <div v-else="product.price">
                    <button class="btn border-0" @click="goTo()">
                        <small>
                            {{lang.map.unpriced}}
                        </small>
                    </button>
                </div>
            </div>

            <div class="col-1 px-0 text-center">
                <div class="btn-group btn-group-sm" role="group">
                    <button type="button" class="btn btn-sm btn-light border shadow-none"
                            @click.stop="decrement()">
                        <i class="fa fa-minus fa-xs" aria-hidden="true"></i>
                    </button>
                    <input v-model.number="count"
                           min="1"
                           class="form-control text-center rounded-0 border border-left-0 border-right-0 px-0"
                           style="width: 32px; height: 32px;"
                           onkeypress="return (event.charCode == 8 || event.charCode == 0 || event.charCode == 13) ? null : event.charCode >= 48 && event.charCode <= 57"
                           @blur="defaultCount"
                    >
                    <button type="button" class="btn btn-sm btn-light border shadow-none"
                            @click.stop="increment()">
                        <i class="fa fa-plus fa-xs" aria-hidden="true"></i>
                    </button>
                </div>
            </div>
            <div class="col-1 px-0 text-center">
                {{sum}}&nbsp;{{lang.map.bynShort}}
            </div>
            <div class="col-1 px-0 text-center">
                <div class="row no-gutters justify-content-center">
                    <button class="btn btn-sm  btn-light shadow-sm my-2"
                            data-toggle="tooltip"
                            data-placement="bottom"
                            :title="[[lang.map.remove]]"
                            @click="removeBasketProduct()">
                        <i class="fas fa-trash-alt fa-lg text-primary"></i>
                    </button>
                </div>
                <div class="row no-gutters justify-content-center">
                    <button v-if="isProductFavorite" class="btn btn-sm  btn-warning shadow-sm my-2"
                            data-toggle="tooltip"
                            data-placement="bottom"
                            :title="[[lang.map.removeFromFavorites]]"
                            @click="removeFavoriteProduct()"
                    >
                        <i class="far fa-star" aria-hidden="true" style="color: yellow;"></i>
                    </button>
                    <button v-else="isProductFavorite" class="btn btn-sm btn-outline-warning shadow-sm my-2"
                            data-toggle="tooltip"
                            data-placement="bottom"
                            :title="[[lang.map.addToFavorites]]"
                            @click="addFavoriteProduct()">
                        <i class="far fa-star " aria-hidden="true" style="color: yellow;"></i>
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import {mapState, mapActions} from 'vuex'
    import Unit from '../../components/types/Unit.vue'
    import PictureUpload from '../../components/picture/PictureUpload.vue'
    import storeMethods from "store/storeMethods";
    export default {
        components: {
            Unit,
            PictureUpload
        },
        props: ['product', 'productIndex'],
        data() {
            return {
                unit: this.product.unit,
                marker: "-icon",
                pictureClass: "img-fluid",
                pictureStyle: "max-width: 100%; height: auto;",
                isProductFavorite: false,
                count: null,
                sum: null,
                showComponent: false,
            }
        },
        created(){
            this.fetchData()
            this.$store.watch(this.$store.getters.getFavoriteId, favoriteId => {
                this.fetchData()
            })
            this.count = this.countState
            this.sum = storeMethods.multiply(this.product.priceWithDiscount, this.count, 2)
        },
        watch: {
            $route: [
                'fetchData',
            ],
            count(newVal, oldVal){
                this.updateCount()
            },
            countState(newVal, oldVal){
                this.count = this.countState
            },
        },
        computed: {
            ...mapState([
                'categoryParentMapIds',
                'categoryMap',
                'basket',
                'favorite',
                'lang',
                'scrollDuration',
                'scrollOptions',
            ]),
            picture(){
                return this.product.picture
            },
            alt(){
                return this.product.name
            },
            store(){
                return !!this.product.quantityInStock;
            },
            countState(){
                let ids = this.basket.products.map((x) => {
                    return x.id
                })
                let index = ids.indexOf(this.product.id)
                if (this.basket.products[index]) {
                    return this.basket.products[index].count
                } else {
                    return 0
                }
            }
        },
        methods: {
            fetchData(){
                this.showComponent = false
                this.isProductFavorite = this.productFavorite()
                this.showComponent = true
            },
            goTo(){
                let path = this.path()
                this.$router.push(path).then(()=>{
                    this.scrollToCatalogueNavigation()
                }).catch(err => {})
            },
            scrollToCatalogueNavigation(){
                this.$scrollTo('#catalogue-navigation', this.scrollDuration, this.scrollOptions)
            },
            path(){
                return '/product' + '/' + this.product.path + '/' + this.product.id
            },
            increment() {
                this.count = this.count + 1
                this.updateCount()
            },
            decrement() {
                if (this.count > 1) {
                    this.count = this.count - 1
                    this.updateCount()
                }
            },
            updateCount(){
                this.sum = storeMethods.multiply(this.product.priceWithDiscount, this.count, 2)
                this.$store.dispatch('updateBasketCountOfProductAction', {product: this.product, count: this.count})
            },
            defaultCount() {
                if (!this.count || this.count == 0) {
                    this.count = 1
                }
            },
            async addFavoriteProduct() {
                await this.$store.dispatch('addFavoriteProductAction', this.product)
            },
            async removeFavoriteProduct() {
                await this.$store.dispatch('removeFavoriteProductAction', this.product)
            },
            async removeBasketProduct(){
                await this.$store.dispatch('removeBasketProductByIdAction', this.product.id)
            },
            productFavorite(){
                return this.$store.getters.isFavoriteProduct(this.product.id)
            },
        }
    }
</script>

<style>
    .border-3 {
        border-width: 3px !important;
    }
</style>
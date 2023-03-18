<template>
    <div v-if="showComponent">
        <div class="row mx-2 py-2 border-top border-muted border-3 bg-white shadow justify-content-center align-items-center">
            <div class="col-8 px-0">
                <button class="btn bg-transparent border-0 rounded-0 btn-block px-0 py-0 mx-0 my-0"
                        style="height: 100%"
                        @click="goTo()">
                    <div class="container-fluid px-0 ">
                        <div class="row no-gutters align-items-center">
                            <div class="col-3">
                                <picture-upload :pathPic="picture" :marker="marker" :alt="alt"
                                                :pictureClass="pictureClass" :pictureStyle="pictureStyle"/>
                                <small>{{lang.map.code}}:&nbsp;{{product.code}}</small>
                            </div>

                            <div class="col-9">
                                <div class="container-fluid px-2">
                                    <div class="card bg-transparent border-0 text-left"
                                         style="width: 100%">
                                        <div class="card-body border-0 px-0 py-0">
                                            <h6 class="card-title border-0 mb-1">{{product.name}}</h6>
                                            <small class="text-muted">
                                                Some quick example text to build on the card title and make up the bulk
                                                of the card's content.
                                            </small>
                                        </div>
                                        <div class="card-footer bg-transparent border-0 my-0">
                                            <div v-if="product.quantityInStock" class="row">
                                                <small>
                                                    {{lang.map.inSight}}:
                                                    &nbsp;
                                                    <i class="fas fa-check-circle text-success"></i>
                                                    {{product.quantityInStock}}
                                                    <unit :unit="unit"/>
                                                </small>
                                            </div>
                                            <div v-if="product.quantitySupplier" class="row">
                                                <small>
                                                    {{lang.map.atDepot}}:
                                                    &nbsp;
                                                    <i class="fas fa-check text-primary"></i>
                                                    {{product.quantitySupplier}}
                                                    <unit :unit="unit"/>
                                                </small>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </button>
            </div>

            <div class="col-2">
                <div class="row justify-content-center align-items-center">
                    <button v-if="isProductFavorite" class="btn btn-sm btn-warning shadow-sm mx-0"
                            @click="removeFavoriteProduct()">
                        <small>
                            {{lang.map.removeFrom}}&nbsp;
                        </small>
                        <i class="far fa-star" aria-hidden="true" style="color: yellow;"></i>
                    </button>
                    <button v-else class="btn btn-sm btn-outline-warning shadow-sm mx-0"
                            @click="addFavoriteProduct()">
                        <i class="far fa-star " aria-hidden="true" style="color: yellow;"></i>
                        <small>
                            &nbsp;{{lang.map.inFavorite}}
                        </small>
                    </button>
                </div>

                <div class="row justify-content-center align-items-center py-2">
                    <div class="btn-group btn-group-sm" role="group">
                        <button type="button" class="btn btn-sm btn-light border shadow-none"
                                @click.stop="decrement()">
                            <i class="fa fa-minus fa-xs" aria-hidden="true"></i>
                        </button>
                        <input v-model.number="count"
                               min="1"
                               class="form-control text-center rounded-0 border border-left-0 border-right-0 px-0"
                               style="width: 53px; height: 32px;"
                               onkeypress="return (event.charCode == 8 || event.charCode == 0 || event.charCode == 13) ? null : event.charCode >= 48 && event.charCode <= 57"
                               @blur="defaultCount"
                        >
                        <button type="button" class="btn btn-sm btn-light border shadow-none"
                                @click.stop="increment()">
                            <i class="fa fa-plus fa-xs" aria-hidden="true"></i>
                        </button>
                    </div>
                </div>

                <div class="row justify-content-center align-items-center">
                    <button class="btn btn-sm btn-primary shadow-sm" @click="addBasketProduct()">
                        <i class="fa fa-shopping-cart" aria-hidden="true"></i>
                        &nbsp;{{lang.map.inBasket}}
                    </button>
                </div>
            </div>

            <div class="col-2">
                <div v-if="product.price">
                    <div v-if="product.discount" class="row d-flex justify-content-center align-items-center">
                        <a class="btn border-0" @click="goTo()">
                            <span class="badge badge-danger">
                                <i class="fas fa-fire"></i>
                                -{{product.discount}}%
                            </span>
                        </a>
                        <a class="btn border-0 text-danger" @click="goTo()">
                            <b>
                                {{product.priceWithDiscount}}&nbsp;{{lang.map.bynShort}}
                            </b>
                        </a>
                    </div>

                    <div v-else="product.discount" class="row d-flex justify-content-center align-items-center">
                        <a class="btn border-0 text-danger" @click="goTo()">
                            <b>
                                {{product.price}}&nbsp;{{lang.map.bynShort}}
                            </b>
                        </a>
                    </div>
                </div>
                <div v-else="product.price" class="row d-flex justify-content-center align-items-center">
                    <a class="btn border-0" @click="goTo()">
                        <small>
                            {{lang.map.unpriced}}
                        </small>
                    </a>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import {mapState, mapActions, mapMutations} from 'vuex'
    import Unit from '../../components/types/Unit.vue'
    import PictureUpload from '../../components/picture/PictureUpload.vue'
    export default {
        components: {
            Unit,
            PictureUpload,
        },
        data() {
            return {
                unit: this.product.unit,
                count: 1,
                marker: "-icon",
                pictureClass: "img-fluid",
                pictureStyle: "max-width: 100%; height: auto;",
                isProductFavorite: false,
                isFavoriteActive: false,
                showComponent: false,
            }
        },
        created(){
            this.fetchData()
            this.$store.watch(this.$store.getters.getFavoriteId, favoriteId => {
                this.fetchData()
            })
        },
        watch: {
            $route: [
                'fetchData',
            ],
        },
        computed: {
            ...mapState([
                'basketChange',
                'favorite',
                'categoryParentMapIds',
                'categoryMap',
                'lang',
                'pictureMedia',
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
        },
        props: ['product', 'productIndex'],
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
            increment: function () {
                this.count = this.count + 1
            },
            decrement: function () {
                if (this.count > 1) {
                    this.count = this.count - 1
                }
            },
            defaultCount: function () {
                if (!this.count || this.count == 0) {
                    this.count = 1
                }
            },
            async addBasketProduct() {
                await this.$store.dispatch('addBasketProductAction', {product: this.product, count: this.count})
                this.count = 1;
            },
            async addFavoriteProduct() {
                await this.$store.dispatch('addFavoriteProductAction', this.product)
            },
            async removeFavoriteProduct() {
                await this.$store.dispatch('removeFavoriteProductAction', this.product)
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
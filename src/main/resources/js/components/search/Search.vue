<template>
    <div v-if="showComponent" class="input-group pr-3">
        <div class="input-group-prepend col-12 px-0 mx-0">
            <div class="input-group-prepend">
            <span class="input-group-text bg-white border-right-0 rounded-0">
                <v-wait for="search">
                <template slot="waiting">
                    <google-circle :widthRem="1.5" heightRem="1.5"></google-circle>
                </template>
                    <i class="fas fa-search text-muted mx-1" aria-hidden="true"></i>
                </v-wait>
            </span>
            </div>
            <input v-model="question" id="question" ref="search" class="form-control rounded-0 border-left-0"
                   type="text"
                   :placeholder="[[lang.map.searchInCatalogue]]" aria-label="Search" data-toggle="dropdown"
                   aria-haspopup="false" aria-expanded="true">
            <div class="dropdown-menu col-12 py-0 shadow-lg dropdown-muted" aria-labelledby="question"
                 id="dropdown-menu">
                <category-row v-for="(category,i) in categories"
                              :key="i"
                              :category="category"
                              @goToCategory="goToCategory"
                />
                <product-row v-for="(product,i) in products"
                             :key="`A-${i}`"
                             :product="product"/>
                <div v-if="isContentPresent" class="row justify-content-center mx-2 my-1">
                    <button type="button" class="btn btn-light btn-block border border-white shadow-lg text-muted"
                            @click.stop="getMoreProducts()">
                        <div class="row justify-content-center">
                            <v-wait for="searchMore">
                                <template slot="waiting">
                                    <google-circle :widthRem="1.5" heightRem="1.5"></google-circle>
                                </template>
                                &#8595;&nbsp;{{lang.map.showMore}} 50
                            </v-wait>
                        </div>
                    </button>
                </div>
                <div v-if="isContentPresent" class="row justify-content-center" id="up">
                    <button type="button" class="btn btn-light btn-block border border-white shadow-lg"
                            @click="newFocusOnQuestion()">
                        <i class="fas fa-chevron-circle-up text-muted"></i>
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    import CategoryRow from 'components/category/CategoryRow.vue'
    import ProductRow from 'components/product/ProductRow.vue'
    import * as _ from 'lodash'
    import searchApi from 'api/search'
    import GoogleCircle from 'components/spinner/GoogleCircle.vue'

    export default {
        components: {
            CategoryRow,
            ProductRow,
            GoogleCircle,
        },
        data() {
            return {
                minSymbols: 3,
                question: '',
                page: 0,
                categories: [],
                products: [],
                showComponent: false,
            }
        },
        watch: {
            $route: [
                'fetchData',
            ],
            question(newQuestion, oldQuestion) {
                if (this.isStringSatisfy(newQuestion)) {
                    $('#dropdown-menu').scrollTop(0)
                    this.debouncedGetAnswer()
                } else {
                    $('#question').dropdown('hide')
                    this.clearAll()
                }
            }
        },
        created() {
            this.fetchData()
            this.debouncedGetAnswer = _.debounce(this.getAnswer, 1000)
        },
        computed: {
            ...mapState([
                'lang',
            ]),
            isContentPresent(){
                return this.categories.length != 0
            },
        },
        methods: {
            fetchData(){
                this.showComponent = false
                this.showComponent = true
            },
            async getAnswer() {
                if (this.isStringSatisfy(this.question)) {
                    this.$wait.start('search')
                    let vm = this
                    await searchApi.getCategoriesAndProducts(this.question, this.page, null)
                        .then(function (response) {
                            vm.categories = response.data.searchCategories
                            vm.products = response.data.searchProducts
                            if (vm.categories.length != 0) {
                                $('#question').dropdown('show')
                            } else {
                                $('#question').dropdown('hide')
                            }
                        })
                        .catch(function (error) {
                            console.info('Ошибка! Не могу связаться с API. ' + error)
                        })
                    this.$wait.end('search')
                }
            },
            clearAll() {
                this.page = 0
                this.categories = []
                this.products = []
            },
            newFocusOnQuestion() {
                this.page = 0
                this.$refs.search.focus();
                $('#dropdown-menu').scrollTop(0)
            },
            async getMoreProducts() {
                this.$wait.start('searchMore')
                let vm = this
                vm.page = vm.page + 1
                await searchApi.getProducts(this.question, this.page, null)
                    .then(function (response) {
                        vm.products = vm.products.concat(response.data)
                    })
                    .catch(function (error) {
                        console.info('Ошибка! Не могу связаться с API. ' + error)
                    })
                this.$wait.end('searchMore')
            },
            isStringSatisfy(string){
                return string != '' && string != null && string.length >= this.minSymbols
            },
            scrollToCatalogueNavigation(){
                this.$scrollTo('#catalogue-navigation', this.scrollDuration, this.scrollOptions)
            },
            goToCategory(category){
                let path = this.makePath(category)
                this.$router.push(path).then(()=>{
                    $('#question').dropdown('hide')
                    this.scrollToCatalogueNavigation()
                }).catch(err => {})
            },
            makePath(category){
                return "/category" + '/' + category.path + '/' + category.id
            },
        },
    }
</script>
<style>
    #dropdown-menu {
        max-height: 600px !important;
        overflow: scroll;
        overflow-x: hidden;
    }

    #up {
        position: -webkit-sticky;
        position: sticky;
        bottom: 0;
        z-index: 10;
    }

    .dropdown-muted {
        background-color: #eaeaea;
    }
</style>
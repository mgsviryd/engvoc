<template>
    <div>
        <vue-headful v-if="showChildComponent"
                     :title="currCategory.name"
                     :description="currCategory.name"
        />
        <div class="container-fluid">
            <div class="row">
                <div class="col px-0">
                    <div class="container-fluid">
                        <div v-if="hasItems()" class="row">
                            <div class="col-4 col-sm-3 border-right border-muted px-0">
                                <property-choices :loading="loading" :page="currPage"
                                                  :propertyChoices="currPropertyChoices"></property-choices>
                            </div>
                            <div class="col-8 col-sm-9 px-0" id="category-product">
                                <div class="row d-flex no-gutters pr-2">
                                    <div class="ml-auto">
                                        <sort-single-dropdown v-if="showChildComponent" :page="currPage"/>
                                        <sizer-dropdown v-if="showChildComponent" :page="currPage"/>
                                    </div>
                                </div>
                                <product-row v-if="showChildComponent" v-for="(product,i) in categoryProducts"
                                             :key="`A-${i}`"
                                             :product="product"/>
                                <div v-if="showChildComponent && !hasPages()"
                                     class="row d-flex no-gutters justify-content-center my-5 text-muted">
                                    <h6>{{lang.map.itemsAbsent}}</h6>
                                </div>
                                <page-line v-if="showChildComponent" :page="currPage"/>
                                <navigation v-if="showChildComponent" :page="currPage"/>
                                <pager v-if="showChildComponent" :page="currPage"/>
                            </div>
                        </div>
                        <div v-if="!hasItems() && !loading"
                             class="row d-flex no-gutters justify-content-center my-5 text-muted">
                            <h6>{{lang.map.categoryAbsent}}</h6>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    import ProductRow from '../../components/product/ProductRow.vue'
    import SizerDropdown from '../../components/pager/SizerDropdown.vue'
    import Navigation from '../../components/pager/Navigation.vue'
    import PageLine from '../../components/pager/PageLine.vue'
    import Pager from '../../components/pager/Pager.vue'
    import SortSingleDropdown from '../../components/pager/SortSingleDropdown.vue'
    import GoogleCircle from '../../components/spinner/GoogleCircle.vue'
    import PropertyChoices from '../../components/category/PropertyChoices.vue'

    export default{
        components: {
            ProductRow,
            SizerDropdown,
            Navigation,
            PageLine,
            Pager,
            SortSingleDropdown,
            GoogleCircle,
            PropertyChoices,
        },
        data() {
            return {
                marker: "-icon",
                pictureClass: "img-fluid",
                pictureStyle: "max-width: 100%; height: auto;",
                currPage: null,
                currCategory: null,
                currPropertyChoices: [],
                categoryProducts: [],
                showChildComponent: false,
                loading: false,
            }
        },
        created(){
            this.fetchData()
        },
        mounted(){
        },
        watch: {
            $route: [
                'fetchData',
            ]
        },
        computed: {
            ...mapState([
                'page',
                'category',
                'propertyChoices',
                'scrollDuration',
                'scrollOptions',
                'lang',
            ]),
            isQueryPresent(){
                let keys = Object.keys(this.$route.query)
                return !(keys == null || keys.length == 0);
            }
        },
        methods: {
            async fetchData() {
                let url = location.origin + '/json' + this.$route.fullPath
                if (!this.isSameCategory()) {
                    if (this.isQueryPresent) {
                        url += '&is-need-property-choices=true'
                    } else {
                        url += '?is-need-property-choices=true'
                    }
                }
                this.loading = true
                await this.$store.dispatch('getCategoryPageAction', url)
                this.showChildComponent = false
                this.currPage = this.page
                this.currCategory = this.category
                this.categoryProducts = this.currPage.content
                this.currPropertyChoices = this.propertyChoices
                this.loading = false
                this.showChildComponent = true
            },
            isSameCategory(){
                let storeCategory = this.category
                if (storeCategory != null) {
                    let category = '/category'
                    let storeCategoryChainPath = this.$store.getters.getCategoryFullPathById(storeCategory.id)
                    let storeCategoryPath = category + storeCategoryChainPath
                    return this.$route.path == storeCategoryPath
                }
                return false
            },
            scrollToCatalogueNavigation(){
                this.$scrollTo('#catalogue-navigation', this.scrollDuration, this.scrollOptions)
            },
            hasPages(){
                return this.page.totalPages > 0
            },
            hasItems(){
                return this.currPropertyChoices.length > 0
            }
        }
    }
</script>

<style scoped>

</style>
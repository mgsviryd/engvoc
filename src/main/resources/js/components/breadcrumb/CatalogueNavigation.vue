<template>
    <div v-if="isNavigableRoute && showComponent"
         class="row no-gutters bg-light border-bottom"
         id="catalogue-navigation">
        <div class="col px-0 my-0 py-2 mx-0">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb my-0  bg-transparent">
                    <li class="breadcrumb-item my-0  bg-transparent">
                        <button class="btn btn-white border border-muted  hover-bg" @click.prevent.stop="clickRoot('/')"
                                href="/">
                            <small>{{lang.map.mainPage}}</small>
                        </button>
                    </li>
                    <li class="breadcrumb-item my-0  bg-transparent">
                        <button class="btn btn-white border border-muted  hover-bg"
                                @click.prevent.stop="scrollToCategoryHierarchy()">
                            <small>{{lang.map.catalogue}}</small>
                        </button>
                    </li>
                    <li v-if="isNameLevel0Present" class="breadcrumb-item my-0  bg-transparent">
                        <button class="btn btn-white border border-muted  hover-bg" @click.prevent.stop="clickLevel(0)">
                            <small>{{nameLevel0}}</small>
                        </button>
                    </li>
                    <li v-if="isNameLevel1Present" class="breadcrumb-item my-0  bg-transparent">
                        <div class="btn-group">
                            <button class="btn btn-white border border-muted  hover-bg"
                                    @click.prevent.stop="clickLevel(1)">
                                <small>{{nameLevel1}}</small>
                            </button>
                            <button v-if="!isNameLevel2Present && isProductRoute"
                                    class="btn btn-white border border-muted hover-bg"
                                    data-toggle="tooltip"
                                    data-placement="bottom"
                                    :title="[[lang.map.backToCategory]]"
                                    @click.prevent.stop="goTo()"
                                    :href="hrefCategory()"
                            >
                                <i class="fas fa-long-arrow-alt-left text-muted"></i>
                            </button>
                        </div>
                    </li>
                    <li v-if="isNameLevel2Present && isCategoryRoute" class="breadcrumb-item my-0  bg-transparent">
                        <button class="btn btn-white border border-muted hover-bg" @click.prevent.stop="focusCategory()"
                                :href="hrefCategory()">
                            <small>{{nameLevel2}}</small>
                        </button>
                    </li>
                    <li v-if="isNameLevel2Present && isProductRoute" class="breadcrumb-item my-0  bg-transparent">
                        <div class="btn-group">
                            <button class="btn btn-white border border-muted hover-bg"
                                    @click.prevent.stop="focusCategory()" :href="hrefCategory()">
                                <small>{{nameLevel2}}</small>
                            </button>
                            <button class="btn btn-white border border-muted hover-bg"
                                    data-toggle="tooltip"
                                    data-placement="bottom"
                                    :title="[[lang.map.backToCategory]]"
                                    @click.prevent.stop="goTo()"
                                    :href="hrefCategory()"
                            >
                                <i class="fas fa-long-arrow-alt-left text-muted"></i>
                            </button>
                        </div>
                    </li>
                    <li v-if="isProductRoute" class="breadcrumb-item my-0  bg-transparent">
                        <button disabled class="btn btn-secondary border border-muted"
                                @click.prevent.stop="clickLevel(1)">
                            <small>{{productName}}</small>
                        </button>
                    </li>
                </ol>
            </nav>
        </div>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    export default{
        data(){
            return {
                showComponent: false,
                indefinite: '...',
                nameLevel0: null,
                nameLevel1: null,
                nameLevel2: null,
                productName: null,

                currentProduct: null,
                currentCategory: null,
            }
        },
        created(){
            if (this.isDataUpdated) {
                this.fetchData()
            }
        },
        watch: {
            $route: [
                'fetchData',
            ],
            isDataUpdated(newVal){
                if (newVal) {
                    this.fetchData()
                }
            },
        },
        computed: {
            ...mapState([
                'categoryChildMapIds',
                'categoryParentMapIds',
                'categoryRootIds',
                'categoryMap',
                'category',
                'product',
                'lang',
            ]),
            isDataPresent(){
                if (this.isProductRoute && !this.isProductPresent()) {
                    return false
                }
                return this.isCategoryHierarchyPresent() && this.isCategoryPresent()
            },
            isDataUpdated(){
                if (this.isProductRoute && this.currentProduct == this.product) {
                    return false
                }
                if (this.isCategoryRoute && this.currentCategory == this.category) {
                    return false
                }
                return this.isDataPresent;
            },
            isNavigableRoute(){
                return this.isCategoryRoute || this.isProductRoute
            },
            isCategoryRoute(){
                return this.$route.name == 'category'
            },
            isProductRoute(){
                return this.$route.name == 'product'
            },
            isNameLevel0Present(){
                return this.nameLevel0 != null
            },
            isNameLevel1Present(){
                return this.nameLevel1 != null
            },
            isNameLevel2Present(){
                return this.nameLevel2 != null
            },
        },
        methods: {
            fetchData(){
                this.showComponent = false
                this.currentProduct = this.product
                this.currentCategory = this.category
                this.nameLevel0 = this.getCategoryName(0)
                this.nameLevel1 = this.getCategoryName(1)
                this.nameLevel2 = this.getCategoryName(2)
                this.productName = this.getProductName()
                this.showComponent = true
            },
            isCategoryHierarchyPresent(){
                return this.categoryChildMapIds != null
                    && this.categoryParentMapIds != null
                    && this.categoryRootIds != null
                    && this.categoryMap != null
            },
            isCategoryPresent() {
                return this.category != null
            },
            isProductPresent(){
                return this.product != null
            },
            clickRoot(){
                this.$router.push('/').catch(err => {
                })
            },
            clickCatalogue(){
                this.$emit('clickCatalogue')
            },
            clickLevel(levelMark){
                this.$emit('clickTab', levelMark, this.getCategoryIds())
            },

            focusCategory(){
                this.$emit('focusCategory', this.getCategoryIds())
            },
            goTo(){
                this.$emit('goTo', this.category)
            },
            scrollToCategoryHierarchy(){
                this.$emit('scrollToCategoryHierarchy')
            },
            hrefCategory(){
                let path = "/category";
                path += this.$store.getters.getCategoryFullPathById(this.currentCategory.id)
                return path
            },
            getCategoryName(levelMark){
                try {
                    return this.$store.getters.getCategoryChildsById(this.currentCategory.id)[levelMark].name
                }
                catch (e) {
                    return null
                }
            },
            getCategoryId(levelMark){
                return this.$store.getters.getCategoryChildsById(this.currentCategory.id)[levelMark].id
            },
            getCategoryIds(){
                return this.$store.getters.getCategoryChildsById(this.currentCategory.id).map((x) => {
                    return x.id
                })
            },
            getProductName(){
                if (this.currentProduct) {
                    return this.currentProduct.name
                } else {
                    return this.indefinite
                }
            },
        }
    }
</script>

<style scoped>
    .hover-bg:hover {
        background: rgb(232, 240, 254);
    }

    .hover-bg {
        background: white;
    }
</style>
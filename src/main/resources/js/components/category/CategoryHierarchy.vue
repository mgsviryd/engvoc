<template>
    <div v-on-clickaway="away" id="category-hierarchy">
        <div class="container-fluid">
            <catalogue v-if="showCatalogue"></catalogue>

            <div class="row">
                <div class="col px-0">
                    <nav>
                        <div class="nav nav-pills d-flex flex-wrap justify-content-center  align-items-stretch py-1 px-0 border-top border-bottom border-muted bg-light"
                             id="nav-tab" role="tablist">
                            <a v-for="(item, index) in categoryRootCategories"
                               class="nav-link btn border border-muted border-1 rounded-0 px-2 py-0 shadow-none"
                               :href="hrefTab(item.id)" data-toggle="tab" role="tab" aria-controls="tabId(item.id)"
                               :id="buttonId(item.id)"
                               aria-selected="false"
                               @click="activateLevel0(item.id)" style="width: 130px; min-height: 40px;">
                                <small>{{item.name}}</small>
                            </a>
                        </div>
                    </nav>
                    <div class="tab-content" id="tab-content-0">
                        <div v-for="(item, index) in categoryRootCategories" class="tab-pane fade"
                             :id="tabId(item.id)"
                             role="tabpanel" aria-labelledby="...">
                            <div class="container-fluid">
                                <div class="row">
                                    <div class="col-4 col-sm-3 border-right border-muted px-0">
                                        <div class="nav nav-pills flex-column " id="v-pills-tab"
                                             role="tablist" aria-orientation="vertical">
                                            <div v-for="(item1, index1) in childMap(item)"
                                                 class="nav-link btn btn-block border-bottom border-muted rounded-0 text-left shadow-none my-0 pl-2 pr-1"
                                                 :href="hrefTab(item1.id)" data-toggle="tab" role="tab"
                                                 :id="buttonId(item1.id)"
                                                 aria-controls="tabId(item1.id)"
                                                 aria-selected="false"
                                                 @click="activateLevel1(item.id)">
                                                <small>{{item1.name}}</small>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-8 col-sm-9 px-0">
                                        <div class="tab-content">
                                            <div v-for="(item1, index1) in childMap(item)" class="tab-pane fade"
                                                 :id="tabId(item1.id)"
                                                 role="tabpanel" aria-labelledby="...">
                                                <div class="container px-0">
                                                    <nav>
                                                        <div class="nav nav-pills d-flex flex-row flex-wrap justify-content-left align-self-stretch">
                                                            <div class="row no-gutters" style="min-width: 100%">
                                                                <a v-for="(item2, index2) in childMap(item1)"
                                                                   class="btn col-6 col-md-2 border-right border-bottom border-1 border-muted btn rounded-0 shadow-none text-left py-1 px-1"
                                                                   :href="makePath(item2)"
                                                                   :id="buttonId(item2.id)"
                                                                   @click.prevent.stop="goTo(item2)">
                                                                    <div class="row no-gutters">
                                                                        <picture-upload :pathPic="picture(item2)"
                                                                                        :marker="marker"
                                                                                        :alt="alt(item2)"
                                                                                        :pictureClass="pictureClass"
                                                                                        :pictureStyle="pictureStyle"/>
                                                                    </div>
                                                                    <div class="row no-gutters my-2 pb-4 px-2">
                                                                        <small>{{item2.name}}</small>
                                                                    </div>
                                                                    <div class="row no-gutters text-muted pb-2 px-2"
                                                                         style="position: absolute; bottom: 0; ">
                                                                        <small>товаров:&nbsp;</small>
                                                                        <span class="badge badge-white">{{item2.productCount}}</span>
                                                                    </div>
                                                                </a>
                                                            </div>
                                                        </div>
                                                    </nav>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div v-if="isOpen" class="row d-flex justify-content-center no-gutters">
            <button type="button"
                    class="btn btn-sm btn-light btn-block border border-muted shadow-sm rounded-0 stickyBottom"
                    @click="fold">
                <i class="fas fa-angle-double-up text-muted"></i>
            </button>
        </div>

        <catalogue-navigation @focusCategory="focusCategory"
                              @clickTab="clickTab"
                              @clickCatalogue="clickCatalogue"
                              @goTo="goTo"
                              @scrollToCategoryHierarchy="scrollToCategoryHierarchy">
        </catalogue-navigation>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    import {mixin as clickaway} from 'vue-clickaway';
    import PictureUpload from 'components/picture/PictureUpload.vue'
    import CatalogueNavigation from 'components/breadcrumb/CatalogueNavigation.vue'
    import Catalogue from 'components/catalogue/Catalogue.vue'

    export default{
        components: {
            PictureUpload,
            CatalogueNavigation,
            Catalogue,
        },
        data() {
            return {
                isOpen: false,
                activeLevel0Id: null,
                activeLevel1Id: null,
                marker: "-icon",
                pictureClass: "img-fluid",
                pictureStyle: "max-width: 100%; height: auto;",
                showCatalogue: false,
                isClickNavigation: false,
            }
        },
        created(){

        },
        mixins: [clickaway],
        computed: {
            ...mapState([
                'categoryMap',
                'categoryRootIds',
                'categoryChildMapIds',
                'category',
                'scrollDuration',
                'scrollOptions',
                'pictureMedia',
            ]),
            categoryRootCategories(){
                return this.categoryRootIds.map((x) => {
                    return this.categoryMap[x]
                }).sort((a, b) => -(a.priority - b.priority))
            },
        },
        methods: {
            hrefTab(index) {
                return "#tab-" + index
            },
            tabId(index) {
                return "tab-" + index
            },
            buttonTab(index) {
                return "#button-" + index
            },
            buttonId(index) {
                return "button-" + index
            },
            childMap(category) {
                let key = category.id
                let ids = this.categoryChildMapIds[key]
                return ids.map((x) => {
                    return this.categoryMap[x]
                }).sort((a, b) => -(a.priority - b.priority))
            },
            activateLevel0(categoryId) {
                this.isOpen = true
                this.activeLevel0Id = categoryId
                if (!this.isClickNavigation) {
                    this.$scrollTo('#tab-content-0', this.scrollDuration, this.scrollOptions)
                }
            },
            activateLevel1(categoryId) {
                this.isOpen = true
                this.activeLevel1Id = categoryId
                this.$scrollTo('#tab-content-0', this.scrollDuration, this.scrollOptions)
            },
            scrollToCategoryHierarchy(){
                this.$scrollTo('#category-hierarchy', this.scrollDuration, this.scrollOptions)
            },
            scrollToCatalogueNavigation(){
                this.$scrollTo('#catalogue-navigation', this.scrollDuration, this.scrollOptions)
            },
            away() {
                this.isOpen = false
                $(this.hrefTab(this.activeLevel1Id)).removeClass('active')
                $(this.buttonTab(this.activeLevel1Id)).removeClass('active')
                $(this.hrefTab(this.activeLevel0Id)).removeClass('active')
                $(this.buttonTab(this.activeLevel0Id)).removeClass('active')
            },
            fold(){
                this.away()
                this.scrollToCatalogueNavigation()
            },
            picture(category) {
                return category.picture
            },
            alt (category) {
                return category.name
            },
            goTo(category) {
                this.away()
                let path = this.makePath(category)
                this.$router.push(path)
                    .then(() => {
                        this.scrollToCatalogueNavigation()
                    })
                    .catch(err => {
                    })
            },
            makePath(category){
                return "/category" + '/' + category.path + '/' + category.id
            },
            clickTab(level, categoryIds){
                this.away()
                if (level !== 0) {
                    this.isClickNavigation = true
                }
                for (let i = 0; i <= level; i++) {
                    document.getElementById(this.buttonId(categoryIds[i].toString())).click()
                }
                this.isClickNavigation = false
            },
            clickCatalogue(){
                this.away()
            },
            focusCategory(categoryIds){
                this.away()
                this.isClickNavigation = true
                for (let i = 0; i < categoryIds.length - 1; i++) {
                    document.getElementById(this.buttonId(categoryIds[i].toString())).click()
                }
                this.isClickNavigation = false
            },
        },
    }

</script>

<style scoped>
    .btn {
        background-color: white;
    }

    .btn:hover, .btn:active:focus {
        color: black;
        background-color: rgb(232, 240, 254);
        cursor: pointer;
    }

    .stickyBottom {
        position: -webkit-sticky;
        position: sticky;
        bottom: 0;
        z-index: 10;
    }
</style>
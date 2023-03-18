<template>
    <div v-if="showComponent">
        <div v-if="!isRecentClear" id="recent">
            <div class="row d-flex no-gutters mx-2">
                <div class="ml-auto">
                    <sortSingleDropdownRecent></sortSingleDropdownRecent>
                    <div class="btn-group">
                        <button type="button" class="btn btn-sm btn-outline-danger shadow-sm" data-toggle="modal"
                                data-target="#recent-idClear">
                            {{lang.map.clear}}&nbsp;
                            <i class="fas fa-eye" aria-hidden="true"></i>
                        </button>
                    </div>
                    <confirm-action :id="'recent-idClear'" @confirm="confirmClear" @reject="rejectClear"
                                    :message="lang.map.confirmCleanRecent"></confirm-action>
                </div>
            </div>
            <v-wait for="loading">
                <template slot="waiting">
                    <div class="d-flex justify-content-center">
                        <google-circle :widthRem="10" :heightRem="10"></google-circle>
                    </div>
                </template>
                <product-row v-for="(product,i) in products"
                             :key="product.id"
                             :product="product"
                             :productIndex="i"
                />
            </v-wait>
        </div>
        <div v-if="isRecentClear"
             class="row d-flex no-gutters justify-content-center my-5 text-muted">
            <h6>{{lang.map.recentClear}}</h6>
        </div>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    import ProductRow from '../../components/product/ProductRow.vue'
    import GoogleCircle from '../../components/spinner/GoogleCircle.vue'
    import ConfirmAction from '../../components/modal/ConfirmAction.vue'
    import SortSingleDropdownRecent from '../../components/pager/SortSingleDropdownRecent.vue'
    export default{
        components: {
            ProductRow,
            GoogleCircle,
            ConfirmAction,
            SortSingleDropdownRecent,
        },
        data(){
            return {
                products: [],
                isRecentClear: false,
                showComponent: false,
                showConfirmClear: false,
            }
        },
        created(){
            this.fetchData()
            this.$store.watch(this.$store.getters.getRecentId, recentId => {
                this.fetchData()
            })
        },
        watch: {
            $route: 'fetchData',
        },
        computed: {
            ...mapState([
                'recent',
                'sortOrder',
                'lang',
            ]),
        },
        methods: {
            async fetchData(){
                this.$wait.start('loading');
                this.showComponent = false
                this.products = this.recent.products
                this.updateRecentClear()
                this.showComponent = true
                this.$wait.end('loading');
            },
            updateRecentClear(){
                this.isRecentClear = this.products == null || this.products.length == 0
            },
            removeRecent() {
                this.$store.dispatch('removeRecentAction')
            },
            confirmClear(){
                this.removeRecent()
            },
            rejectClear(){

            },
        }
    }
</script>

<style scoped>

</style>
<template>
        <div v-if="showChildComponent" class="container">
            <div class="row">
                <div class="col-6 col-lg-3 my-2">
                    <div class="thumbnail hover-zoomin">
                        <a href="#"><img src="/static/picture/category/izmerit.jpg" alt=""></a>

                        <div class="caption">
                            <h5><a href="#">{{product.name}}</a></h5>
                            <p v-if="product.description" v-html="product.description"></p>
                            <a href="#" class="btn btn-success" style="background: green">Купить</a>
                        </div>

                    </div>
                </div>
            </div>
        </div>
</template>

<script>
    import {mapState} from 'vuex'
    export default{
        data() {
            return {
                showChildComponent: false,
            }
        },
        created(){
            this.fetchData()
        },
        mounted(){
            this.scrollToCatalogueNavigation()
        },
        watch:{
            $route: ['fetchData', 'scrollToCatalogueNavigation']
        },
        computed:{
            ...mapState([
                'product',
            ])
        },
        methods:{
            async fetchData() {
                this.showChildComponent = false
                await this.$store.dispatch('getProductPageAction', this.$route.params.id)
                this.$store.dispatch('addRecentProductAction', this.product)
                this.showChildComponent = true
            },
            scrollToCatalogueNavigation(){
                this.$scrollTo('#catalogue-navigation', this.scrollDuration, this.scrollOptions)
            },
        }
    }

</script>

<style>

</style>
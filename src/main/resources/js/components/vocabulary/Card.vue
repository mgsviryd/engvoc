<template>
  <div>

  </div>
</template>

<script>
    import {mapState} from 'vuex'


    export default{
        components: {

        },
        data(){
            return {

            }
        },
        created(){
            this.fetchData()
        },
        watch: {
            $route: 'fetchData',
        },
        computed: {
            ...mapState([
                'lang',
            ]),
        },
        methods: {
            fetchData(){
                // this.$wait.start('loading')
                // this.showComponent = false
                // // this.products = this.basket.products
                // this.updateBasketClear()
                // this.showComponent = true
                // this.$wait.end('loading')
            },
            updateBasketClear(){
                // this.isBasketClear = this.products === null || this.products.length === 0
            },
            removeBasket() {
                // this.$store.dispatch('removeBasketAction')
            },
            confirmClear(){
                // this.removeBasket()
            },
            rejectClear(){

            },
        }
    }
</script>

<style scoped>

</style>
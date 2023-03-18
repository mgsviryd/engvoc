<template>
    <div id="root">
        <vue-headful
                title="Электротехническая продукция, электротовары в Минске по оптовым ценам в магазине Освещение и электрика АТТ - АЛТЕХНОТРЕЙД"
                description="Электротехническая продукция, электротовары в Минске по оптовым ценам в магазине Освещение и электрика АТТ - АЛТЕХНОТРЕЙД"
        />
        <navbar></navbar>
        <navbar1></navbar1>
        <category-hierarchy></category-hierarchy>
        <carousel></carousel>
        <category-main></category-main>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    import Navbar from '../components/navbar/Navbar.vue'
    import Navbar1 from '../components/navbar/Navbar1.vue'
    import CategoryHierarchy from '../components/category/CategoryHierarchy.vue'
    import Carousel from '../components/carousel/Carousel.vue'
    import CategoryMain from '../components/carousel/CategoryMain.vue'

    export default{
        components: {
            Navbar,
            Navbar1,
            CategoryHierarchy,
            Carousel,
            CategoryMain,
        },
        data() {
            return {
                showComponent: false,
            }
        },
        mounted() {
            this.scrollToRoot()
        },
        watch: {
            $route: 'fetchData',
            $route(to, from) {
                const toDepth = to.path.split('/').length
                const fromDepth = from.path.split('/').length
            }
        },
        computed:{
            ...mapState([
                'scrollDuration',
                'scrollOptions',
            ]),
        },
        methods: {
            fetchData() {
                this.scrollToRoot()
            },
            scrollToRoot(){
                this.$scrollTo('#root', this.scrollDuration, this.scrollOptions)
            },
        }
    }

</script>

<style>

</style>
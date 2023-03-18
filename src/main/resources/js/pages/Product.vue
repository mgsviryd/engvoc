<template>
    <div>
        <navbar></navbar>
        <navbar1></navbar1>
        <category-hierarchy></category-hierarchy>
        <product></product>
    </div>
</template>

<script>
    import Navbar from '../components/navbar/Navbar.vue'
    import Navbar1 from '../components/navbar/Navbar1.vue'
    import CategoryHierarchy from '../components/category/CategoryHierarchy.vue'
    import Product from '../components/product/Product.vue'
    export default{
        components:{
            Navbar,
            Navbar1,
            CategoryHierarchy,
            Product,
        },
    }
</script>

<style scoped>

</style>
<template>
    <div>
        <navbar></navbar>
        <navbar1></navbar1>
        <category-hierarchy></category-hierarchy>
        <favorite></favorite>
    </div>
</template>

<script>
    import Navbar from '../components/navbar/Navbar.vue'
    import Navbar1 from '../components/navbar/Navbar1.vue'
    import CategoryHierarchy from '../components/category/CategoryHierarchy.vue'
    import Favorite from '../components/favorite/Favorite.vue'
    export default{
        components: {
            Navbar,
            Navbar1,
            CategoryHierarchy,
            Favorite
        },
    }

</script>

<style scoped>

</style>
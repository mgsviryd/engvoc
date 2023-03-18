<template>
    <div>
        <navbar></navbar>
        <navbar1></navbar1>
        <category-hierarchy></category-hierarchy>
        <recent></recent>
    </div>
</template>

<script>
    import Navbar from '../components/navbar/Navbar.vue'
    import Navbar1 from '../components/navbar/Navbar1.vue'
    import CategoryHierarchy from '../components/category/CategoryHierarchy.vue'
    import Recent from '../components/recent/Recent.vue'
    export default{
        components: {
            Navbar,
            Navbar1,
            CategoryHierarchy,
            Recent
        },
    }

</script>

<style scoped>

</style>
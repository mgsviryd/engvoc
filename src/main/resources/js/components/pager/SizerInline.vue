<template>
    <div v-if="showComponent && hasPages">
        <ul class="pagination">
            <li class="page-item disabled">
                <a class="page-link" href="#" tabindex="-1">по:</a>
            </li>
            <div v-for="(size, index) in sizeList">
                <div v-if="isCurrentSize(size)">
                    <li class="page-item active">
                        <a class="page-link" href="#" tabindex="-1">{{size}}</a>
                    </li>
                </div>
                <div v-else-if="isLastPage()">
                    <li class="page-item">
                        <a class="page-link" @click="hrefLastPage(size)" tabindex="-1">{{size}}</a>
                    </li>
                </div>
                <div v-else>
                    <li class="page-item">
                        <a class="page-link" @click="href(size)" tabindex="-1">{{size}}</a>
                    </li>
                </div>
            </div>
        </ul>
    </div>
</template>

<script>
    export default{
        props: ['page'],
        data(){
            return {
                showComponent: false,
                sort: this.page.size,
                sizeList: [4, 16, 32, 64]
            }
        },
        computed: {
            hasPages(){
                return this.page.totalPages > 0
            }
        },
        created(){
            this.fetchData()
        },
        watch: {
            page: {
                handler: function (newVal, oldVal) {
                    this.fetchData()
                },
                deep: true,
            }
        },
        methods: {
            fetchData(){
                this.showComponent = false
                this.currentLang = this.page.size
                this.showComponent = true
            },
            isCurrentSize(size){
                return this.sort == size
            },
            isLastPage(){
                return this.page.last
            },
            href(size){
                this.$router.push({path: this.$route.path, query: {...this.$route.query, page: 0, size: size} }).catch(err => {})
            },
            hrefLastPage(size){
                let page = Math.floor(this.page.totalElements / size)
                this.$router.push({path: this.$route.path, query: {...this.$route.query, page: 0, size: size} }).catch(err => {})
            },
        }
    }
</script>

<style scoped>

</style>
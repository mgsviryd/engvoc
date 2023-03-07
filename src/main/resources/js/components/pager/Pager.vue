<template>
    <div>
        <ul class="pagination btn-group justify-content-center">
            <div v-for="(p, index) in pages">
                    <li v-if="isCurrentPage(p)" class="page-item active">
                        <a class="btn page-link" tabindex="-1">{{p}}</a>
                    </li>
                    <li v-else-if="isGapPage(p)" class="page-item disabled">
                        <a class="btn page-link" tabindex="-1">...</a>
                    </li>
                    <li v-else class="page-item">
                        <a class="btn page-link" @click="clickPage(p)"
                           tabindex="-1">{{p}}</a>
                    </li>
            </div>
        </ul>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    
    export default{
        props: ['page'],
        computed: {
                ...mapState([
                    'scrollDuration',
                    'scrollOptions',
                ]),
            pages(){
                let array = []
                if (this.page.totalPages > 7) {
                    let totalPages = this.page.totalPages
                    let pageNumber = this.page.number + 1
                    let head = []
                    if (pageNumber > 4) {
                        head = [1, -1]
                    } else {
                        head = [1, 2, 3]
                    }
                    let tail = []
                    if (pageNumber < totalPages - 3) {
                        tail = [-1, totalPages]
                    } else {
                        tail = [totalPages - 2, totalPages - 1, totalPages]
                    }
                    let bodyBefore = [];
                    if (pageNumber > 4 && pageNumber < totalPages - 1) {
                        bodyBefore = [pageNumber - 2, pageNumber - 1]
                    } else {
                        bodyBefore = []
                    }
                    let bodyAfter = []
                    if (pageNumber > 2 && pageNumber < totalPages - 3) {
                        bodyAfter = [pageNumber + 1, pageNumber + 2]
                    } else {
                        bodyAfter = []
                    }
                    let pageBody = [];
                    if (pageNumber > 3 && pageNumber < totalPages - 2) {
                        pageBody = [pageNumber]
                    } else {
                        pageBody = []
                    }
                    array.push(...head)
                    array.push(...bodyBefore)
                    array.push(...pageBody)
                    array.push(...bodyAfter)
                    array.push(...tail)

                } else {
                    for (let i = 1; i <= this.page.totalPages; i++) {
                        array.push(i)
                    }
                }
                return array
            },
        },
        methods: {
            isCurrentPage(page){
                return page - 1 == this.page.number
            },
            isGapPage(page){
                return page == -1
            },
            clickPage(page){
                this.$router.push({
                    path: this.$route.path,
                    query: {...this.$route.query, page: page - 1}
                }).then(()=>{
                    this.scrollToCategoryProduct()
                }).catch(err => {
                })
            },
            scrollToCategoryProduct(){
                this.$scrollTo('#category-product', this.scrollDuration, this.scrollOptions)
            },
        }
    }
</script>

<style scoped>

</style>
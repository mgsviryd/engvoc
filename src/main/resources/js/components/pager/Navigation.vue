<template>
    <div v-if="showComponent && hasPages">
        <ul class="pagination justify-content-center mb-1">
            <li class="page-item" :class="[isPageNotHasPrevious ? 'disabled nohover' : '']">
                <a class="page-link" aria-label="Previous" @click="clickBack">
                    <span aria-hidden="true">&laquo; {{lang.map.back}}</span>
                </a>
            </li>
            <li class="page-item">
                <button id="btnMinusDrop" type="button"
                        class="btn btn-primary rounded-0 dropdown-toggle"
                        :class="[isPageNotHasPrevious ? 'disabled nohover' : '']"
                        data-toggle="dropdown"
                        aria-haspopup="true" aria-expanded="false">
                    <i class="fas fa-minus"></i>
                </button>
                <div class="dropdown-menu" aria-labelledby="btnMinusDrop">
                    <a v-for="(step, index) in steps" class="dropdown-item"
                       @click="clickBackStep(step)">{{step}}</a>
                </div>
            </li>
            <li class="page-item">
                <button id="btnPlusDrop" type="button"
                        class="btn btn-primary rounded-0 dropdown-toggle" :class="[isPageNotHasNext ? 'disabled nohover' : '']"
                        data-toggle="dropdown"
                        aria-haspopup="true" aria-expanded="false">
                    <i class="fas fa-plus"></i>
                </button>
                <div class="dropdown-menu" aria-labelledby="btnPlusDrop">
                    <a v-for="(step, index) in steps" class="dropdown-item"
                       @click="clickNextStep(step)">{{step}}</a>
                </div>
            </li>
            <li class="page-item" :class="[isPageNotHasNext ? 'disabled nohover' : '']">
                <a class="page-link" aria-label="Next" @click="clickNext">
                    <span aria-hidden="true">{{lang.map.next}} &raquo;</span>
                </a>
            </li>
        </ul>
    </div>
</template>

<script>
    import {mapState} from 'vuex'

    export default{
        props: ['page'],
        computed:{
            ...mapState([
                'scrollDuration',
                'scrollOptions',
                'lang',
            ]),
            hasPages(){
                return this.page.totalPages > 0
            },
        },
        watch: {
            $route: [
                'fetchData',
            ],
            page: {
                handler: function (newVal, oldVal) {
                    this.fetchData()
                },
                deep: true,
            }
        },
        created(){
            this.fetchData()
        },
        data(){
            return {
                showComponent: false,
                steps: [5, 10, 25, 50],
                isPageNotHasNext: null,
                isPageNotHasPrevious: null,
            }
        },
        methods: {
            fetchData(){
                this.showComponent = false
                this.isPageNotHasNext = !this.page.hasNext
                this.isPageNotHasPrevious = !this.page.hasPrevious
                this.showComponent = true
            },
            clickBack() {
                let page = this.page.number-1
                this.$router.push({path: this.$route.path, query: {...this.$route.query, page: page} }).catch(err => {})
                this.scrollToCategoryProduct()
            },
            clickBackStep(step) {
                let page = null
                if (this.page.number - 1 - step < 0) {
                    page = 0
                } else {
                    page = this.page.number - step
                }
                this.$router.push({path: this.$route.path, query: {...this.$route.query, page: page} }).catch(err => {})
                this.scrollToCategoryProduct()
            },
            clickNextStep(step) {
                let page = null
                if (this.page.number + step >= this.page.totalPages) {
                    page = this.page.totalPages - 1
                } else {
                    page = this.page.number + step
                }
                this.$router.push({path: this.$route.path, query: {...this.$route.query, page: page} }).catch(err => {})
                this.scrollToCategoryProduct()
            },
            clickNext() {
                let page = this.page.number + 1
                this.$router.push({path: this.$route.path, query: {...this.$route.query, page: page} }).catch(err => {})
                this.scrollToCategoryProduct()
            },
            scrollToCategoryProduct(){
                this.$scrollTo('#category-product', this.scrollDuration, this.scrollOptions)
            },
        }
    }
</script>

<style scoped>
    .nohover:hover {
        cursor:default !important;
    }
</style>
<template>
    <div v-if="showComponent && hasPages" class="btn-group">
        <button class="btn btn-white rounded-0" disabled>
            <small>{{lang.map.showBy}}:</small>
        </button>
        <button type="button" class="btn btn-light dropdown-toggle dropdown-toggle-split rounded-0" data-toggle="dropdown"
                aria-haspopup="true" aria-expanded="false" id="sizerDropdown">
            <small>
                {{currentSize}}
            </small>
        </button>
        <div class="dropdown-menu" aria-labelledby="sizerDropdown">
            <div v-for="(size, index) in sizeList">
                <a v-if="isCurrentSize(size)" class="dropdown-item disable nohover">
                    <small>
                        {{size}}
                    </small>
                    &ensp;<i class="fas fa-check text-success"></i>
                </a>
                <a v-else-if="isLastPage()" class="dropdown-item" @click="hrefLastPage(size)">
                    <small>
                        {{size}}
                    </small>
                </a>
                <a v-else class="dropdown-item" @click="href(size)">
                    <small>
                        {{size}}
                    </small>
                </a>
            </div>
        </div>
    </div>

</template>

<script>
    import {mapState} from 'vuex'

    export default{
        props: ['page'],
        computed: {
            ...mapState([
                'lang',
            ]),
            hasPages(){
                return this.page.totalPages > 0
            },
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
        data(){
            return {
                showComponent: false,
                currentSize: null,
                sizeList: [10, 20, 50, 100],
            }
        },
        methods: {
            fetchData(){
                this.showComponent = false
                this.currentSize = this.page.size
                this.showComponent = true
            },
            isCurrentSize(size){
                return this.currentSize == size
            },
            isLastPage(){
                return this.page.last
            },
            href(size){
                this.$router.push({
                    path: this.$route.path,
                    query: {...this.$route.query, page: 0, size: size}
                }).catch(err => {
                })
            },
            hrefLastPage(size){
                let page = 0
                this.$router.push({
                    path: this.$route.path,
                    query: {...this.$route.query, page: page, size: size}
                }).catch(err => {
                })
            },
        }
    }
</script>

<style scoped>
    .nohover:hover {
        cursor: default !important;
        background-color: white;
    }

    .dropdown-item:hover {
        cursor: pointer;
    }
</style>
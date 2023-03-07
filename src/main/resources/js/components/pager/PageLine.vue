<template>
    <div v-if="showComponent && hasPages" class="row d-flex no-gutters justify-content-center text-muted my-2">
        {{lang.map.showed}} {{start}} - {{end}} {{lang.map.fromAmong}} {{page.totalElements}}
    </div>
</template>

<script>
    import {mapState} from 'vuex'

    export default{
        props: ['page'],
        created(){
            this.fetchData()
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
        data(){
            return {
                showComponent: false,
                start: null,
                end: null,
            }
        },
        computed: {
            ...mapState([
                'lang',
            ]),
            hasPages(){
                return this.page.totalPages > 0
            }
        },
        methods: {
            fetchData(){
                this.showComponent = false
                this.start = this.page.number * this.page.size + 1
                this.end = this.page.number * this.page.size + this.page.numberOfElements
                this.showComponent = true
            },
        }
    }
</script>

<style scoped>

</style>
<template>
    <div v-if="showComponent && hasPages" class="btn-group">
        <button type="button" class="btn btn-light dropdown-toggle dropdown-toggle-split rounded-0"
                data-toggle="dropdown"
                aria-haspopup="true" aria-expanded="false" id="sizerDropdown">
            <small>
                <span v-if="currentSort.htmlBefore" v-html="currentSort.htmlBefore"></span>
                {{lang.map.fromStart}} {{message(currentSort.name)}}
            </small>
        </button>
        <div class="dropdown-menu" aria-labelledby="sizerDropdown">
            <div v-for="(sort, index) in sortOrder">
                <a v-if="isCurrentSort(index)" class="dropdown-item disable nohover">
                    <small>
                        <span v-if="sort.htmlBefore" v-html="currentSort.htmlBefore"></span>
                        {{messageCapital(sort.name)}}
                    </small>
                    &ensp;<i class="fas fa-check text-success"></i>
                </a>
                <a v-else-if="isNotAddUTCMillis(sort)" class="dropdown-item" @click="href(index)">
                    <small>
                        <span v-if="sort.htmlBefore" v-html="sort.htmlBefore"></span>
                        {{messageCapital(sort.name)}}
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
        data(){
            return {
                showComponent: false,
                currentSort: null,
            }
        },
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
        computed: {
            ...mapState([
                'sortOrder',
                'lang',
            ]),
            hasPages(){
                return this.page.totalPages > 0
            }
        },
        methods: {
            fetchData(){
                this.showComponent = false
                if (this.page.sort[0]) {
                    this.currentSort = this.findSortByProperty(this.page.sort[0].property)
                } else {
                    this.currentSort = this.sortOrder[0]
                }
                this.showComponent = true
            },
            isCurrentSort(index){
                return this.currentSort == this.sortOrder[index]
            },
            isNotAddUTCMillis(sort){
                return sort.property != 'UTCMillis'
            },
            href(index){
                let sort = this.sortOrder[index]
                this.$router.push({
                    path: this.$route.path,
                    query: {...this.$route.query, page: 0, property: sort.property, direction: sort.direction}
                }).catch(err => {
                })
            },
            findSortByProperty(property){
                for (let i = 0; i < this.sortOrder.length; i++) {
                    if (this.sortOrder[i].property == property) {
                        return this.sortOrder[i]
                    }
                }
            },
            firstCapital(lower){
                return lower.charAt(0).toUpperCase() + lower.substring(1);
            },
            message(key){
                return this.lang.map[key]
            },
            messageCapital(key){
                return this.firstCapital(this.message(key))
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
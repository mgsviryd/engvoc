<template>
    <div v-if="showComponent" class="btn-group">
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
                <a v-else class="dropdown-item" @click="clickSort(sort)">
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
    import compare from "../../util/compare";
    export default{
        data(){
            return {
                obj: 'basket',
                showComponent: false,
                currentSort: null,
            }
        },
        created(){
            this.fetchData()
            this.$store.watch(this.$store.getters.getBasketId, basketId => {
                this.fetchData()
            })
        },
        watch: {
            $route: [
                'fetchData',
            ],
        },
        computed: {
            ...mapState([
                'basket',
                'sortOrder',
                'lang',
            ]),
        },
        methods: {
            fetchData(){
                this.showComponent = false
                if (this.basket.sort != null) {
                    this.currentSort = this.basket.sort
                } else {
                    this.currentSort = this.sortOrder[0]
                }
                this.showComponent = true
            },
            isCurrentSort(index){
                return this.currentSort.id == this.sortOrder[index].id
            },
            clickSort(sort){
                this.$store.dispatch('basketSetSortAction', sort)
                let products = [...this.basket.products]
                let sorted = this.sortInSortOrder(products, sort.property, sort.direction, sort.typeMark)
                this.$store.dispatch('basketUpdateAction', sorted)
            },
            firstCapital(lower){
                return lower.charAt(0).toUpperCase() + lower.substring(1);
            },
            sortInSortOrder(products, property, direction, typeMark){
                if (property == 'UTCMillis') {
                    property = this.obj + property
                }
                if (typeMark == "number") {
                    if (direction == "asc") {
                        return products.sort(function (a, b) {
                            return compare.compareNumberNaturalByProperty(a, b, property)
                        })
                    } else {
                        return products.sort(function (a, b) {
                            return compare.compareNumberReverseByProperty(a, b, property)
                        })
                    }
                } else if (typeMark === "dateISOString") {
                    if (direction == "asc") {
                        return products.sort(function (a, b) {
                            return compare.compareDateISOStringNaturalByProperty(a, b, property)
                        })
                    } else {
                        return products.sort(function (a, b) {
                            return compare.compareDateISOStringReverseByProperty(a, b, property)
                        })
                    }
                } else if (typeMark == "ldt") {
                    if (direction == "asc") {
                        return products.sort(function (a, b) {
                            return compare.compareLDTNaturalByProperty(a, b, property)
                        })
                    } else {
                        return products.sort(function (a, b) {
                            return compare.compareLDTReverseByProperty(a, b, property)
                        })
                    }
                }
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
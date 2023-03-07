<template>
    <div v-if="showComponent">
        <button type="button" class="btn btn-light dropdown rounded-0 border border-1 border-white"
                data-toggle="dropdown"
                aria-haspopup="true"
                aria-expanded="false"
                data-placement="bottom"
                :title="[[lang.map.changeLang]]"
                id="langDropdown">
            <small>
                <b>{{currentLang.abbr}}</b>
            </small>
        </button>
        <div class="dropdown-menu" aria-labelledby="langDropdown">
            <div v-for="(lang, index) in currentLangList">
                <a v-if="isCurrentLang(lang)"
                   class="dropdown-item disable nohover"
                   data-toggle="tooltip"
                   data-placement="bottom"
                   :title="lang.name"
                >
                    <small>
                        <b>{{lang.abbr}}</b>
                    </small>
                    &ensp;<i class="fas fa-check text-success"></i>
                </a>
                <a v-else class="dropdown-item" @click="href(lang)"
                   data-toggle="tooltip"
                   data-placement="bottom"
                   :title="lang.name"
                >
                    <small>
                        <b>{{lang.abbr}}</b>
                    </small>
                </a>
            </div>
        </div>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    export default{
        data(){
            return {
                showComponent: false,
                currentLangList: [],
                currentLang: {},
            }
        },
        computed: {
            ...mapState([
                'lang',
            ]),
        },
        created(){
            this.fetchData()
        },

        methods: {
            fetchData(){
                this.showComponent = false
                this.currentLangList = this.lang.list
                this.currentLang = this.lang.current
                this.showComponent = true
            },
            isCurrentLang(lang){
                return this.currentLang.abbr == lang.abbr
            },
            href(lang){
                this.currentLang = lang
                this.$store.dispatch('getLanguageMapAction', lang)
            },
        },
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
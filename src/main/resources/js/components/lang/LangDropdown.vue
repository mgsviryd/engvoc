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
        <b>{{ getUpperCase(currentLang.name) }}</b>
      </small>
    </button>
    <div class="dropdown-menu" aria-labelledby="langDropdown">
      <div v-for="(l, i) in currentLangs">
        <a v-if="isCurrentLang(l)"
           class="dropdown-item disable nohover"
           data-toggle="tooltip"
           data-placement="bottom"
           :title="getCapitalizeLang(l.name)"
        >
          <small>
            <b>{{ getUpperCase(l.name) }}</b>
          </small>
          &ensp;<i class="fas fa-check text-success"></i>
        </a>
        <a v-else class="dropdown-item" @click="chooseLang(l)"
           data-toggle="tooltip"
           data-placement="bottom"
           :title="getCapitalizeLang(l.name)"
        >
          <small>
            <b>{{ getUpperCase(l.name) }}</b>
          </small>
        </a>
      </div>
    </div>
  </div>
</template>

<script>
import {mapState} from 'vuex'
import * as _ from "lodash";

export default {
  data() {
    return {
      showComponent: false,
      currentLangs: [],
      currentLang: {},
    }
  },
  computed: {
    ...mapState([
      'lang',
    ]),
  },
  created() {
    this.$store.watch(this.$store.getters.getLangId, langId => {
      this.fetchData()
    })
    this.fetchData()
  },

  methods: {
    fetchData() {
      this.showComponent = false
      this.currentLangs = this.lang.langs
      this.currentLang = this.lang.lang
      this.showComponent = true
    },
    isCurrentLang(lang) {
      return this.currentLang.name === lang.name
    },
    chooseLang(lang) {
      this.$store.dispatch('getLanguageMapAction', lang)
      this.$store.dispatch('changeLangAction', lang)
    },
    getUpperCase(text){
      return _.upperCase(text)
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    getLang(key) {
      return this.lang.map[key]
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
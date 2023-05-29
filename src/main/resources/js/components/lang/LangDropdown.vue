<template>
  <div v-if="show">
    <button type="button" class="btn btn-light dropdown rounded-0 border border-1 border-white"
            data-toggle="dropdown"
            aria-haspopup="true"
            aria-expanded="false"
            data-placement="bottom"
            :title="getLang('changeLang')"
            id="langDropdown">
      <small>
        <b>{{ getUpperCase(currentLang.lang) }}</b>
      </small>
    </button>
    <div class="dropdown-menu" aria-labelledby="langDropdown">
      <div v-for="(l, i) in currentLangs">
        <a v-if="isCurrentLang(l)"
           class="dropdown-item disable nohover"
           data-toggle="tooltip"
           data-placement="bottom"
           :title="getCapitalizeLang(l.lang)"
        >
          <small>
            <b>{{ getUpperCase(l.lang) }}</b>
          </small>
          &ensp;<i class="fas fa-check text-success"></i>
        </a>
        <a v-else class="dropdown-item" @click="chooseLang(l)"
           data-toggle="tooltip"
           data-placement="bottom"
           :title="getCapitalizeLang(l.lang)"
        >
          <small>
            <b>{{ getUpperCase(l.lang) }}</b>
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
      show: true,
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
      this.show = false
      console.info("lang: "+this.lang.lang)
      this.currentLangs = this.lang.langs
      this.currentLang = this.lang.lang
      this.show = true
    },
    isCurrentLang(lang) {
      return this.currentLang.lang === lang.lang
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
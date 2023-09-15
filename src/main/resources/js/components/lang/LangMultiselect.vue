<template>
  <multiselect
      :tabindex="-1"
      v-model="value"
      :options="options"
      track-by="lang"
      :multiple="false"
      :clear-on-select="true"
      :close-on-select="true"
      :show-no-results="true"
      :hide-selected="false"
      :allow-empty="false"
      :option-height="100"
      :showLabels="false"
      :searchable="false"
      @select="onSelect"
  >
    <template slot="singleLabel"
              slot-scope="props">
      <span :class="'fi fi-'+ getLowerCase(props.option.country)"></span>
    </template>

    <template slot="option"
              slot-scope="props">
      <span :class="'fi fi-'+ getLowerCase(props.option.country)"></span>
      <small>{{ getLanguageByLangAndCountry(props.option) }}</small>
    </template>
  </multiselect>
</template>

<script>
import {mapState} from "vuex"
import * as _ from "lodash"
import LocaleJS from "../../util/locale"

export default {
  mounted() {

  },
  created() {
    this.fetchData()
  },
  components: {},
  computed: {
    ...mapState([
      'lang',
    ]),
  },
  watch: {
    $route: [
      'fetchData',
    ],
    lang: {
      handler: function () {
        this.$forceNextTick(() => {
          this.fetchData()
        })
      },
      deep: true
    }
  },
  data() {
    return {
      value: null,
      options: [],
    }
  },
  methods: {
    fetchData() {
      this.value = this.lang.lang
      this.options = this.lang.langs
    },
    onSelect(lang) {
      this.$store.dispatch('changeLangAction', lang)
    },
    getLanguageByLangAndCountry(lang) {
      return LocaleJS.getLanguageByLangAndCountry(lang)
    },
    getCapitalize(text) {
      return _.capitalize(text)
    },
    getLowerCase(text) {
      return _.lowerCase(text)
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    getUpperCaseLang(key) {
      return _.upperCase(this.getLang(key))
    },
    getLang(key) {
      return this.$t(key)
    },
  },
}

</script>

<style>
.multiselect {
  width: fit-content;
}

.multiselect .multiselect__content-wrapper {
  min-width: 100%;
  width: auto;
  border: none;
  box-shadow: 4px 4px 10px 0 rgba(0, 0, 0, .1);
  z-index: 1022;
}

.multiselect--active .multiselect__tags {
  border-bottom: none;
}
</style>
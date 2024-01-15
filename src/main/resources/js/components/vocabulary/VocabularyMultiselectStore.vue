<template>
  <vocabulary-multiselect
      :id="ids.id"
      :ref="ids.id"
      :side="side"
      :data="{watchId: watchIds.vocabularyMultiselect, value: value, options: options}"
      @onSelect="onSelect"
  ></vocabulary-multiselect>
</template>

<script>
import {mapState} from "vuex"
import * as _ from "lodash"
import LocaleJS from "../../util/locale"
import VocabularyMultiselect from "./VocabularyMultiselect.vue"

export default {
  props: [
      'id',
      'side',
  ],
  mounted() {

  },
  created() {
    this.fetchData()
    this.$store.watch(this.$store.getters.getVocabularyId, id => {
      this.fetchData()
      this.watchIds.vocabularyMultiselect = _.now()
    })
  },
  components: {
    VocabularyMultiselect,
  },
  computed: {
    ...mapState([
      'vocabulary',
    ]),
    ids() {
      return {
        id: this.prefixId(),
      }
    },
    watchIds(){
      return {
        vocabularyMultiselect: 0,
      }
    },
  },
  watch: {
    $route: [
      'fetchData',
    ],
  },
  data() {
    return {
      name: 'VocabularyMultiselectStore',
      show: false,
      value: null,
      options: [],
    }
  },
  methods: {
    prefixId() {
      return this.name + '-' + this.id + '-'
    },
    fetchData() {
      this.show = false
        this.value = this.vocabulary.vocabulary
        this.options = this.vocabulary.vocabularies
        this.show = true
    },
    isBlank(value) {
      return _.isNil(value) || _.isEmpty(value)
    },
    onSelect(value) {
      this.$store.commit('setVocabularyMutation', {vocabulary: value})
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
    getUpperCase(text) {
      return _.upperCase(text)
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

<style scoped>


</style>
<template>
  <b-modal id="langPair"
           ref="langPair"
           :header-class="'p-3'"
           :body-class="'py-0'"
           no-fade
           hide-footer
           centered
           :no-close-on-backdrop="!closable"
           :no-close-on-esc="!closable"
  >
    <template #modal-title>
            {{getCapitalizeLang('db')}}
    </template>


  <div class="container-fluid mt-2">
    <b-row class="justify-content-between">
      <b-col sm="4" class="d-flex align-items-center justify-content-start">
        {{ getCapitalizeLang('sourceLang') }}:
      </b-col>
      <b-col sm="2" class="d-flex align-items-center justify-content-end">
        <multiselect
            :tabindex="-1"
            v-model="source"
            :options="options"
            track-by="lang"
            :multiple="false"
            :clear-on-select="true"
            :close-on-select="true"
            :show-no-results="true"
            :hide-selected="false"
            :allow-empty="true"
            :option-height="100"
            :showLabels="false"
            :searchable="false"
            @select="onSelectSource"
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
      </b-col>
      <b-col sm="4" class="d-flex align-items-center justify-content-start">
        {{ getCapitalizeLang('targetLang') }}:
      </b-col>
      <b-col sm="2" class="d-flex align-items-center justify-content-end">
        <multiselect
            :tabindex="-1"
            v-model="target"
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
            @select="onSelectTarget"
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
      </b-col>
    </b-row>

    <b-button class="my-3" variant="outline-success" block @click="createVocabularyDatabase">
      {{getCapitalizeLang('create')}}
    </b-button>
  </div>
  </b-modal>
</template>

<script>
import {mapState} from "vuex"
import LocaleJS from "../../util/locale"
import CloseRow from "../close/CloseRow.vue"

export default {
  props: [
    'closable',
    'show'
  ],

  components: {
    CloseRow,
  },

  mounted() {
    this.switchModal(this.show)
  },

  created() {
    this.fetchData()
  },

  computed: {
    ...mapState([
      'lang',
      'vocabulary',
      'props',
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
    },
    props: {
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
      source: null,
      target: null,
      options: [],
    }
  },
  methods: {
    showModal() {
      this.$refs.langPair.show()
    },
    hideModal() {
      this.$refs.langPair.hide()
    },
    switchModal(show) {
      if (show) {
        this.showModal()
      } else {
        this.hideModal()
      }
    },
    closeModal() {
      this.$refs.langPair.hide()
    },
    fetchData() {
      if(this.vocabulary.lang.source){
        this.source = this.vocabulary.lang.source
      }else{
        this.source = this.lang.lang
      }
      if(this.vocabulary.lang.target){
        this.target = this.vocabulary.lang.target
      }else{
        this.target = this.lang.lang
      }
      this.options = this.lang.langs
    },
    onSelectSource(lang) {
      this.source = lang
    },
    onSelectTarget(lang) {
      this.target = lang
    },
    createVocabularyDatabase(){
      this.$store.dispatch("createVocabularyDatabaseAction", {source: this.source, target: this.target})
      this.closeModal()
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
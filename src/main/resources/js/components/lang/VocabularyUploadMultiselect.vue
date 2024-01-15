<template>
  <div class="container-fluid">
    <b-row class="justify-content-between">
      <b-col sm="7" class="px-0 d-flex align-items-center justify-content-start">
        {{ getCapitalizeLang('sourceLang') }}:
      </b-col>
      <b-col sm="5" class="pl-2 pr-0 d-flex align-items-center justify-content-end">
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
            :allow-empty="false"
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
    </b-row>
    <b-row>
      <b-col sm="7" class="px-0 d-flex align-items-center justify-content-start">
        {{ getCapitalizeLang('targetLang') }}:
      </b-col>
      <b-col sm="5" class="pl-2 pr-0 d-flex align-items-center justify-content-end">
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
            :limit="15"
            @select="onSelectTarget"
        >
          <span slot="noResult">{{ getCapitalizeLang('nothingFound') }}</span>
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
  </div>
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
    fetchData() {
      this.source = this.props.upload.lang.source
      this.target = this.props.upload.lang.target
      this.options = this.lang.langs
    },
    onSelectSource(lang) {
      this.$store.dispatch("changePropsUploadLangAction", {source: lang, target: this.target})
    },
    onSelectTarget(lang) {
      this.$store.dispatch("changePropsUploadLangAction", {source: this.source, target: lang})
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
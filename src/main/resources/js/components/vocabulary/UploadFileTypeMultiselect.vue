<template>
  <multiselect
      v-if="show"
      :id="ids.id"
      :ref="ids.id"
      v-model="value"
      :allow-empty="false"
      :clear-on-select="true"
      :close-on-select="true"
      :hide-selected="false"
      :limit="15"
      :multiple="false"
      :option-height="100"
      :options="options"
      :searchable="false"
      :show-no-results="true"
      :showLabels="false"
      :tabindex="-1"
      :track-by="trackBy"
      @select="onSelect"
  >
    <template slot="singleLabel"
              slot-scope="props">
        <small>
          <span>
              <img alt="..." height="24"
                   :src="props.option.imgSource"
                   width="24">
            {{ getCapitalizeLang(props.option.label) }}
          </span>
        </small>
    </template>

    <template slot="option"
              slot-scope="props">
        <small>
          <span>
              <img alt="..." height="24"
                   :src="props.option.imgSource"
                   width="24">
            {{ getCapitalizeLang(props.option.label) }}
          </span>
        </small>
    </template>
  </multiselect>
</template>

<script>
import {mapState} from "vuex"
import * as _ from "lodash"
import LocaleJS from "../../util/locale"

export default {
  props: [
    'id',
    'data',
  ],
  mounted() {

  },
  created() {
    this.fetchData()
  },
  components: {},
  computed: {
    ...mapState([

    ]),

    ids() {
      return {
        id: this.prefixId(),
        singleLabel: this.prefixId() + 'single-label-id',
        option: this.prefixId() + 'option-id',
      }
    }
  },
  watch: {
    data(){
      this.fetchData()
    },
  },
  data() {
    return {
      name: 'VocabularyMultiselect',
      show: false,
      value: null,
      options: [],
      trackBy: 'label',
    }
  },
  methods: {
    prefixId() {
      return this.name + '-' + this.id + '-'
    },
    fetchData() {
      this.show = false
      this.value = this.data.value
      this.options = this.data.options
      this.show = true
    },
    isBlank(value) {
      return _.isNil(value) || _.isEmpty(value)
    },
    onSelect() {
      this.$emit('onSelect', this.value)
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
.multiselect {
  /*width: fit-content;*/
  width: auto;
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
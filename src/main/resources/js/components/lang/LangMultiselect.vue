<template>
  <multiselect
      v-if="show"
      :id="ids.id"
      :ref="ids.id"
      v-model="value"
      :allow-empty="false"
      :clear-on-select="true"
      :close-on-select="true"
      :deselect-label="getCapitalizeLang('cannotDeselect')"
      :hide-selected="false"
      :internal-search="false"
      :limit="20"
      :multiple="false"
      :option-height="100"
      :options="options"
      :options-limit="1000"
      :placeholder="getCapitalizeLang('enterLang')"
      :preselect-first="true"
      :preselectFirst="false"
      :preserveSearch="false"
      :preventAutofocus="false"
      :searchable="true"
      :show-no-results="true"
      :showLabels="false"
      :track-by="trackBy"
      :value="value"
      @select="onSelect"
      @search-change="asyncFind"

  >
    <span slot="noResult">{{ getCapitalizeLang('nothingFound') }}</span>
    <template slot="singleLabel"
              slot-scope="props">
      <small :id="ids.singleLabel">
        <span :class="'fi fi-'+ getLowerCase(props.option.country)"></span>
        <span>{{ getUpperCase(props.option.lang) }}</span>
      </small>

      <b-popover
          :delay="{ show: 800, hide: 40 }"
          :placement="'bottom'"
          :target="ids.singleLabel"
          no-fade
          triggers="hover focus"
      >
        <b-row no-gutters>
          <small>
            <span>{{ getLanguageByLangAndCountry(props.option) }}</span>
          </small>
        </b-row>
      </b-popover>
    </template>


    <template slot="option"
              slot-scope="props">
      <small>
        <span :class="'fi fi-'+ getLowerCase(props.option.country)"></span>
        <span>{{ getUpperCase(props.option.lang) }}</span>
        <span>{{ ': ' + getLanguageByLangAndCountry(props.option) }}</span>
      </small>
    </template>
  </multiselect>
</template>

<script>
import {mapState} from "vuex"
import * as _ from "lodash"
import LocaleJS from "../../util/locale"
import CompareJS from "../../util/compare";

export default {
  props: [
    'id',
    'data',
  ],
  components: {},
  mounted() {

  },
  created() {
    this.fetchData()
  },
  computed: {
    ...mapState([
      'lang',
    ]),
    ids() {
      return {
        id: this.prefixId(),
        singleLabel: this.prefixId() + 'single-label-id',
      }
    }
  },
  watch: {
    $route: [
      'fetchData',
    ],
    data: {
      handler: function () {
        this.fetchData()
      },
      deep: true
    },
  },
  data() {
    return {
      name: "LangMultiselect",
      show: false,
      trackBy: 'lang',
      value: null,
      allOptions: [],
      options: [],
    }
  },
  methods: {
    fetchData() {
      this.show = false
      this.value = this.data.value
      this.allOptions = _.cloneDeep(this.data.options)
      this.allOptions.sort((x, y) => CompareJS.compareStringNaturalByProperty(x, y, 'locale'))
      this.options = this.allOptions
      this.show = true
    },
    asyncFind(query) {
      if (query === '') {
        this.options = this.allOptions
      } else {
        this.options = this.allOptions.filter(o => _.startsWith(o.locale, query) || _.includes(_.lowerCase(this.getLanguageByLangAndCountry(o)), _.lowerCase(query)))
      }
    },
    prefixId() {
      return this.id + '-' + this.name + '-'
    },
    onSelect(lang) {
      this.$emit('onSelect', lang)
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
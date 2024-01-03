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
      :placeholder="getCapitalizeLang('enterType')"
      :preselect-first="true"
      :preselectFirst="false"
      :preserveSearch="false"
      :preventAutofocus="false"
      :searchable="true"
      :show-no-results="true"
      :showLabels="false"
      :track-by="trackBy"
      @select="onSelect"
      @search-change="asyncFind"
  >
    <template slot="singleLabel"
              slot-scope="props">
      <div
          :id="ids.singleLabel"
      >
        <small>
          <span :class="'fi fi-'+ getLowerCase(props.option.source.country)"></span>
          <span>{{ getUpperCase(props.option.source.lang) }}</span>
          <span :class="'fi fi-'+ getLowerCase(props.option.target.country)"></span>
          <span>{{ getUpperCase(props.option.target.lang) }}</span>
          <span class="text-secondary pl-1">{{ '|' }}</span>
          <span>{{ props.option.name }}</span>
        </small>
      </div>

      <b-popover
          :delay="{ show: 1500, hide: 40 }"
          :placement="'bottom'+side"
          :target="ids.singleLabel"
          no-fade
          triggers="hover focus"
      >
        <template #title>
          <span>{{ props.option.name }}</span>
        </template>
        <b-row no-gutters>
          <span :class="'fi fi-'+ getLowerCase(props.option.source.country)"></span>
          <span class="px-1">
            {{ getUpperCase(props.option.source.lang) + ': ' + getLanguageByLangAndCountry(props.option.source) }}
          </span>
        </b-row>
        <b-row no-gutters>
          <span :class="'fi fi-'+ getLowerCase(props.option.target.country)"></span>
          <span class="px-1">
            {{ getUpperCase(props.option.target.lang) + ': ' + getLanguageByLangAndCountry(props.option.target) }}
          </span>
        </b-row>
      </b-popover>
    </template>

    <template slot="option"
              slot-scope="props">
      <div :id="ids.option+'-'+props.option.source.lang+'-'+props.option.target.lang+'-'+props.option.name"
      >
        <small>
          <span :class="'fi fi-'+ getLowerCase(props.option.source.country)"></span>
          <span>{{ getUpperCase(props.option.source.lang) }}</span>
          <span :class="'fi fi-'+ getLowerCase(props.option.target.country)"></span>
          <span>{{ getUpperCase(props.option.target.lang) }}</span>
          <span class="text-secondary pl-1">{{ '|' }}</span>
          <span>{{ props.option.name }}</span>
        </small>
      </div>
      <b-popover
          :delay="{ show: 1500, hide: 40 }"
          :placement="'top'+side"
          :target="ids.option+'-'+props.option.source.lang+'-'+props.option.target.lang+'-'+props.option.name"
          no-fade
          triggers="hover focus"
      >
        <template #title>
          <span>{{ props.option.name }}</span>
        </template>
        <b-row no-gutters>
          <span :class="'fi fi-'+ getLowerCase(props.option.source.country)"></span>
          <span class="px-1">
            {{ getUpperCase(props.option.source.lang) + ': ' + getLanguageByLangAndCountry(props.option.source) }}
          </span>
        </b-row>
        <b-row no-gutters>
          <span :class="'fi fi-'+ getLowerCase(props.option.target.country)"></span>
          <span class="px-1">
            {{ getUpperCase(props.option.target.lang) + ': ' + getLanguageByLangAndCountry(props.option.target) }}
          </span>
        </b-row>
      </b-popover>
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
    'side',
    'data',
  ],
  components: {},
  mounted() {

  },
  created() {
    this.fetchData()
  },
  computed: {
    ...mapState([]),

    ids() {
      return {
        id: this.prefixId(),
        singleLabel: this.prefixId() + 'single-label-id',
        option: this.prefixId() + 'option-id',
      }
    }
  },
  watch: {
    data: {
      handler: function () {
        this.fetchData()
      },
      deep: true
    },
  },
  data() {
    return {
      name: 'VocabularyMultiselect',
      show: false,
      trackBy: 'source',
      value: null,
      allOptions: [],
      options: [],
    }
  },
  methods: {
    prefixId() {
      return this.name + '-' + this.id + '-'
    },
    fetchData() {
      this.show = false
      this.value = this.data.value
      this.allOptions = _.cloneDeep(this.data.options)
      this.allOptions.sort((x, y) => CompareJS.compareStringNaturalByProperty(x, y, 'name'))
      this.options = this.allOptions
      this.show = true
    },
    asyncFind(query) {
      if (query === '') {
        this.options = this.allOptions
      } else {
        this.options = this.allOptions.filter(o => _.startsWith(o.name, query))
      }
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
  height: 50px;
}

</style>
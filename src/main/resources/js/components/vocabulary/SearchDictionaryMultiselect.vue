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
      :group-label="'group'"
      :group-select="false"
      :group-values="'options'"
      :hide-selected="false"
      :internal-search="false"
      :label="''"
      :limit="10"
      :multiple="false"
      :option-height="50"
      :options="options"
      :options-limit="1000"
      :placeholder="getCapitalizeLang('enterDictionary')"
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
    <span slot="noResult">{{ getLang('nothingFound') }}</span>
    <template slot="placeholder" slot-scope="props">
      <i class="fa-solid fa-magnifying-glass fa text-secondary"></i>
      {{getCapitalizeLang('enterDictionary')}}
    </template>
    <template slot="singleLabel" slot-scope="props">
      <i class="fa-solid fa-magnifying-glass fa-sm text-secondary"></i>
      <small>{{getCapitalizeLang('enterDictionary')}}</small>
    </template>

    <template slot="option"
              slot-scope="props"
    >
      <b-button v-if="props.option.$isLabel"
                :id="ids.option+'-'+props.option.$groupLabel"
                :variant="props.option.$groupLabel === 'unique'?'info':'warning'"
                class="m-0 p-0"
                size="sm"
      >
        <span>{{ getLang(props.option.$groupLabel) }}</span>
      </b-button>
      <small v-else
             :id="ids.option+'-'+props.option.id"
             class="d-flex"
      >
        <span>{{ props.option.name }}</span>
      </small>
    </template>
  </multiselect>
</template>

<script>
import {mapState} from "vuex"
import * as _ from "lodash"
import LocaleJS from "../../util/locale"
import CompareJS from "../../util/compare"

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
      handler: function (newVal, oldVal) {
        if (!oldVal || newVal.watchId !== oldVal.watchId) {
          this.fetchData()
        }
      },
      immediate: true,
      deep: true,
    },
  },
  data() {
    return {
      name: 'VocabularyMultiselect',
      show: false,
      trackBy: 'name',
      value: null,
      allOptions: [],
      groupOptions: [],
      options: [],
    }
  },
  methods: {
    prefixId() {
      return this.name + '-' + this.id + '-'
    },
    fetchData() {
      this.show = false
      if (!this.isBlank(this.data.value)) {
        this.value = this.data.value
      } else {
        this.value = null
      }
      if (!this.isBlank(this.data.options)) {
        this.allOptions = this.data.options
        this.allOptions.sort((x, y) => CompareJS.compareStringNaturalByProperty(x, y, 'name'))
        this.groupOptions = [
          {group: 'unique', options: this.allOptions.filter(o => o.unrepeated)},
          {group: 'notUnique', options: this.allOptions.filter(o => !o.unrepeated)},
        ]
      } else {
        this.allOptions = []
        this.groupOptions = [
          {group: 'unique', options: []},
          {group: 'notUnique', options: []},
        ]
      }
      this.options = this.groupOptions
      this.show = true
    },
    asyncFind(query) {
      if (query === '') {
        this.options = this.groupOptions
      } else {
        this.options = [
          {
            group: this.groupOptions[0].group,
            options: this.groupOptions[0].options.filter(o => _.startsWith(o.name, query))
          },
          {
            group: this.groupOptions[1].group,
            options: this.groupOptions[1].options.filter(o => _.startsWith(o.name, query))
          },
        ]
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
.st-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

</style>
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
      :placeholder="getCapitalizeLang('enterName')"
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
    <span slot="noResult">{{ getCapitalizeLang('nothingFound') }}</span>
    <template slot="singleLabel"
              slot-scope="props">
      <small class="d-flex">
          <span>
            <i class="fa-solid fa-table text-success"></i>
            {{ getCapitalizeLang(props.option.label) }}
          </span>
        <span v-if="props.option.progress === 0" class="ml-auto font-weight-bold text-danger">
            {{ getUpperCaseLang('notLoaded') }}
            <i class="fa-solid fa-circle-info"></i>
          </span>
        <span v-if="props.option.progress === 100" class="ml-auto font-weight-bold text-success">
            {{ getUpperCaseLang('loaded') }}
          </span>
      </small>
    </template>

    <template slot="option"
              slot-scope="props">
      <small class="d-flex">
          <span>
            <i class="fa-solid fa-table text-success"></i>
            {{ getCapitalizeLang(props.option.label) }}
          </span>
        <span v-if="props.option.progress === 0" class="ml-auto font-weight-bold text-danger">
            {{ getUpperCaseLang('notLoaded') }}
            <i class="fa-solid fa-circle-info"></i>
          </span>
        <span v-if="props.option.progress === 100" class="ml-auto font-weight-bold text-success">
            {{ getUpperCaseLang('loaded') }}
          </span>
      </small>
    </template>
  </multiselect>
</template>

<script>
import {mapState} from "vuex"
import * as _ from "lodash"
import CompareJS from "../../util/compare"

export default {
  props: [
    'id',
    'watchId',
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
      name: 'SheetMultiselect',
      show: false,
      trackBy: 'name',
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
      if (!this.isBlank(this.data.value)) {
        this.value = this.data.value
      } else {
        this.value = null
      }
      if (!this.isBlank(this.data.options)) {
        this.allOptions = this.data.options
      } else {
        this.allOptions = []
      }
      this.options = this.allOptions
      this.show = true
    },
    asyncFind(query) {
      if (query === '') {
        this.options = this.allOptions
      } else {
        this.options = this.allOptions.filter(o => _.startsWith(o.label, query))
      }
    },
    isBlank(value) {
      return _.isNil(value) || _.isEmpty(value)
    },
    onSelect() {
      this.$emit('onSelect', this.value)
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
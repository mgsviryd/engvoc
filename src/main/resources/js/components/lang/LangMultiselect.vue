<template>
  <multiselect
      v-if="show"
      :id="ids.id"
      :ref="ids.id"
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
      :limit="15"
      @select="onSelect"

  >
    <template slot="singleLabel"
              slot-scope="props">
      <div
        :id="ids.singleLabel"
      >
      <span :class="'fi fi-'+ getLowerCase(props.option.country)"></span>
      <span>{{ getUpperCase(props.option.lang) }}</span>
      </div>

      <b-popover
          :target="ids.singleLabel"
          :delay="{ show: 800, hide: 40 }"
          :placement="'bottom'"
          triggers="hover focus"
          no-fade
      >
        <b-row no-gutters>
          <small>
            <span>{{getLanguageByLangAndCountry(props.option) }}</span>
          </small>
        </b-row>
      </b-popover>
    </template>


    <template slot="option"
              slot-scope="props">
      <span :class="'fi fi-'+ getLowerCase(props.option.country)"></span>
      <small>
        <span>{{ getUpperCase(props.option.lang) }}</span>
        <span>{{ ': '+getLanguageByLangAndCountry(props.option) }}</span>
      </small>
    </template>
  </multiselect>
</template>

<script>
import {mapState} from "vuex"
import * as _ from "lodash"
import LocaleJS from "../../util/locale"

export default {
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
    ids(){
      return{
        id: this.prefixId(),
        singleLabel: this.prefixId() + 'single-label-id',
      }
    }
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
      name: "LangMultiselect",
      show: false,
      value: null,
      options: [],
    }
  },
  methods: {
    fetchData() {
      this.show = false
      this.value = this.lang.lang
      this.options = this.lang.langs
      this.show = true
    },
    prefixId(){
      return this.name + '-'
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
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
      track-by="source"
      @select="onSelect"
  >
    <template slot="singleLabel"
              slot-scope="props">
      <div
          :id="ids.singleLabel"
          class="text-truncate"
          style="position: absolute; width: 100%;"
      >
        <small>
          <span :class="'fi fi-'+ getLowerCase(props.option.source.country)"></span>
          <span>{{ getUpperCase(props.option.source.lang) }}</span>
          <span :class="'fi fi-'+ getLowerCase(props.option.target.country)"></span>
          <span>{{ getUpperCase(props.option.target.lang) }}</span>
          <span class="text-secondary pl-1">{{'|'}}</span>
          <span>{{ props.option.name }}</span>
        </small>
      </div>

      <b-popover
          :delay="{ show: 1500, hide: 40 }"
          :placement="'bottom'+side"
          :target="ids.singleLabel"
          triggers="hover focus"
          no-fade
      >
        <template #title>
          <span>{{ props.option.name }}</span>
        </template>
        <b-row no-gutters>
          <span :class="'fi fi-'+ getLowerCase(props.option.source.country)"></span>
          <span class="px-1">
            {{getUpperCase(props.option.source.lang) + ': ' + getLanguageByLangAndCountry(props.option.source) }}
          </span>
        </b-row>
        <b-row no-gutters>
          <span :class="'fi fi-'+ getLowerCase(props.option.target.country)"></span>
          <span class="px-1">
            {{getUpperCase(props.option.target.lang) + ': ' + getLanguageByLangAndCountry(props.option.target) }}
          </span>
        </b-row>
      </b-popover>
    </template>

    <template slot="option"
              slot-scope="props">
      <div :id="ids.option+'-'+props.option.source.lang+'-'+props.option.target.lang+'-'+props.option.name"
           class="text-truncate"
           style="position: absolute; width: 80%;"
      >
        <small>
          <span :class="'fi fi-'+ getLowerCase(props.option.source.country)"></span>
          <span>{{ getUpperCase(props.option.source.lang) }}</span>
          <span :class="'fi fi-'+ getLowerCase(props.option.target.country)"></span>
          <span>{{ getUpperCase(props.option.target.lang) }}</span>
          <span class="text-secondary pl-1">{{'|'}}</span>
          <span>{{props.option.name }}</span>
        </small>
      </div>
      <b-popover
          :delay="{ show: 1500, hide: 40 }"
          :placement="'top'+side"
          :target="ids.option+'-'+props.option.source.lang+'-'+props.option.target.lang+'-'+props.option.name"
          triggers="hover focus"
          no-fade
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
            {{getUpperCase(props.option.target.lang) + ': ' + getLanguageByLangAndCountry(props.option.target) }}
          </span>
        </b-row>
      </b-popover>
    </template>
  </multiselect>
  <div v-else>

  </div>
</template>

<script>
import {mapState} from "vuex"
import * as _ from "lodash"
import LocaleJS from "../../util/locale"

export default {
  props: [
    'id',
    'side',
  ],
  mounted() {

  },
  created() {
    this.fetchData()
  },
  components: {},
  computed: {
    ...mapState([
      'vocabulary',
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
    $route: [
      'fetchData',
    ],
    vocabulary: {
      handler: function () {
        this.show = false
        this.$forceNextTick(() => {
          this.fetchData()
        })
      },
      deep: true
    }
  },
  data() {
    return {
      name: 'VocabularyMultiselect',
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
      if (!this.isBlank(this.vocabulary.vocabulary)) {
        this.value = this.vocabulary.vocabulary
        this.options = this.vocabulary.vocabularies
        this.show = true
      }
    },
    isBlank(value) {
      return _.isNil(value) || _.isEmpty(value)
    },
    onSelect() {
      this.$store.commit('setVocabularyMutation', {vocabulary: this.value})
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
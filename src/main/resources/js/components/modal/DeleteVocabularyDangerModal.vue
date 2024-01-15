<template>
  <b-modal
      v-if="show"
      :id="id"
      :ref="id"
      :body-class="'py-1'"
      :footer-class="''"
      :header-class="'p-3'"
      :no-close-on-backdrop="!closable"
      :no-close-on-esc="!closable"
      no-fade
      @shown="focusInput()"
  >
    <template #modal-header="{ close }">
      <b-container class="px-1" fluid>
        <close-row v-if="closable"
                   :title="getCapitalizeLang('deleteVocabulary')"
                   @close="reject()"
        ></close-row>
      </b-container>
    </template>

    <b-row>
      <b-col class="" sm="10">
        <b-form-group
            :label="getCapitalizeLang('vocabulary') + ':'"
            :label-for="ids.vocabulary.name"
            content-cols-lg="7"
            content-cols-sm="7"
            label-class="py-0"
            label-cols-lg="3"
            label-cols-sm="3"
        >
          <div :id="ids.vocabulary.name" class="text-truncate">
            <span :class="'fi fi-'+ getLowerCase(vocabulary.source.country)"></span>
            <span :class="'fi fi-'+ getLowerCase(vocabulary.target.country)"></span>
            <span>{{ vocabulary.name }}</span>
          </div>
        </b-form-group>
      </b-col>
    </b-row>

    <b-row>
      <b-col class="" sm="10">
        <b-form-group
            :label="''"
            :label-for="ids.actual"
            content-cols-lg="7"
            content-cols-sm="7"
            label-class="py-0"
            label-cols-lg="3"
            label-cols-sm="3"
        >
          <b-form-input
              :id="ids.actual"
              :ref="ids.actual"
              v-model="input.actual"
              :class="{'border-success':showBorderProperty('actual')}"
              :placeholder="getCapitalizeLang('enterVocabularyName')"
              :state="stateInput()"
              class="rounded-sm"
              size="sm"
              trim
              @input="inputProperty($event, 'actual')"
              @keyup.enter="confirm()"
              @focusin.prevent.stop="onFocusinProperty($event, ids.actual, 'actual')"
              @focusout.prevent.stop="onFocusoutProperty($event, ids.actual, 'actual')"
          >
          </b-form-input>
          <div v-if="properties.actual.wasOutFocus && actualError()" class="invalid-feedback my-0">
            <small>{{ actualError() }}</small>
          </div>
        </b-form-group>
        <div v-if="properties.actual.showError" class="my-0 text-danger">
          <small v-for="(e,i) in getErrors('actual')">{{ upperFirst(e.message) }}</small>
        </div>
      </b-col>
      <b-col class="px-0" sm="2">
        <b-button-group>
          <b-button class="shadow-none"
                    size="sm"
                    tabindex="-1"
                    variant="outline-secondary"
                    @click.prevent.stop="cancel(ids.actual, 'actual')"
          >
            <i class="fa fa-close"></i>
          </b-button>
          <b-button :id="'copy-'+ids.actual"
                    class="shadow-none"
                    size="sm"
                    tabindex="-1"
                    variant="outline-secondary"
                    @click.prevent.stop="copy(input.actual, ids.actual, 'actual')"
          >
            <i class="fa fa-copy"></i>
          </b-button>
          <b-tooltip :id="'tooltip-'+'copy-'+ids.actual"
                     :ref="'tooltip-'+'copy-'+ids.actual"
                     :target="'copy-'+ids.actual"
                     :title="getCapitalizeLang('copied')"
                     triggers
                     variant="secondary"
          ></b-tooltip>
        </b-button-group>
      </b-col>
    </b-row>

    <template #modal-footer>
      <b-button variant="secondary"
                @click.prevent.stop="reject()">
        {{ getCapitalizeLang('no') }}
      </b-button>
      <b-button
          :class="!stateTrue()?'cursor-not-allowed':null"
          :disabled="!stateTrue()"
          :variant="stateTrue()?'success':'outline-success'"
          @click.prevent.stop="confirm()">
        {{ getCapitalizeLang('yes') }}
      </b-button>
    </template>
  </b-modal>
</template>

<script>
import {mapState} from "vuex"
import * as _ from "lodash"
import LocaleJS from "../../util/locale"
import CloseRow from "../close/CloseRow.vue"

export default {
  props: [
    'id',
    'closable',
    'vocabulary',
  ],
  created() {
    Object.assign(this.properties, this.defaultProperties)
    this.fetchData()
  },
  components: {
    CloseRow,
  },
  computed: {
    ...mapState({
      lang: 'lang',
    }),
    defaultProperties() {
      return {
        actual: {
          hasFocus: false,
          wasOutFocus: false,
          showError: false,
          timeoutCopyTooltip: 300,
        },
      }
    },
    ids() {
      return {
        id: this.prefixId(),
        vocabulary: {
          name: this.prefixId() + 'vocabulary' + 'name-id',
        },
        actual: this.prefixId() + 'actual-id',
      }
    }
  },
  watch: {
    $route: [
      'fetchData',
    ],
    vocabulary: {
      handler: function (newVal, oldVal) {
        this.fetchData()
      },
      immediate: true,
      deep: true,
    },
    props: {
      handler: function () {
        this.show = false
        this.$nextTick(() => {
          this.fetchData()
        })
      },
      deep: true
    },
  },
  data() {
    return {
      show: false,
      name: 'VocabularyModal',
      input: {
        actual: '',
        expected: '',
      },
      properties: {},
      errors: [],
    }
  },
  methods: {
    prefixId() {
      return this.name + '-' + this.id + '-'
    },
    fetchData() {
      this.show = false
      if (!this.isBlank(this.vocabulary)) {
        this.input.expected = this.vocabulary.name
        this.show = true
      }
    },
    onSelectSource() {
    },
    onSelectTarget() {
    },
    focusInput() {
      this.$refs[this.ids.actual].focus()
    },
    showModal() {
      if (this.show) {
        this.$refs[this.id].show()
      }
    },
    hideModal() {
      if (this.show) {
        this.$refs[this.id].hide()
      }
    },
    confirm() {
      if (this.stateTrue()) {
        this.$store.dispatch(
            'deleteVocabularyAction',
            {vocabulary: this.vocabulary, actual: this.input.actual, expected: this.input.expected}
        ).then((errors) => {
          if (errors.length === 0) {
            this.closeModal()
          } else {
            this.errors = errors
            this.showErrors()
          }
        })
      }
    },
    closeModal() {
      this.hideModal()
      this.setDataToDefault()
    },
    reject() {
      this.hideModal()
      this.setDataToDefault()
    },
    setDataToDefault() {
      this.hideErrorsAndFlush()
      this.errors = []
      this.input = {
        actual: '',
        expected: '',
      }
    },
    cancel(ref, property) {
      this.input[property] = ''
      this.$refs[ref].focus();
    },
    getLanguageByLangAndCountry(lang) {
      return LocaleJS.getLanguageByLangAndCountry(lang)
    },
    copy(text, ref, property) {
      this.$refs[ref].focus();
      this.$refs['tooltip-' + 'copy-' + ref].$emit('open')
      _.delay(() => {
            this.$refs['tooltip-' + 'copy-' + ref].$emit('close')
          },
          this.properties[property].timeoutCopyTooltip)
      navigator.clipboard.writeText(text)
    },
    actualError() {
      if (this.isBlank(this.input.actual)) return ''
      if (!this.isInputCoincide()) return this.getLang('incorrectInputError')
      return ''
    },
    isInputCoincide() {
      return this.input.actual === this.input.expected
    },
    stateInput() {
      return !this.isBlank(this.input.actual)
          && this.isInputCoincide()
    },
    inputProperty(event, property) {
      this.properties[property].showError = false
    },
    onFocusinProperty(event, ref, property) {
      this.properties[property].hasFocus = true
    },
    onFocusoutProperty(event, ref, property) {
      this.show = false
      this.properties[property].wasOutFocus = true
      this.properties[property].hasFocus = false
      this.show = true
    },
    flush() {
      this.input.actual = ''
      Object.keys(this.properties).forEach(p => {
        this.properties[p].wasOutFocus = false
      })
    },
    hideErrorsAndFlush() {
      this.hideErrors()
      this.flush()
    },
    showBorderProperty(property) {
      if (!this.properties[property].wasOutFocus) return true
      if (this.properties[property].hasFocus) return true
      return false
    },
    showErrors() {
      this.show = false
      this.errors.forEach(e => {
        try {
          this.properties[e.attribute].showError = true
        } catch (e) {
        }
      })
      this.show = true
    },
    isAnyErrorsShow() {
      let any = false
      Object.keys(this.properties).forEach(p => {
        if (this.properties[p].showError) {
          any = true
        }
      })
      return any
    },
    hideErrors() {
      Object.keys(this.properties).forEach(p => {
        this.properties[p].showError = false
      })
    },
    getErrors(property) {
      return this.errors.filter(e => e.attribute === property)
    },
    stateTrue() {
      return this.stateInput() && !this.isAnyErrorsShow()
    },
    isBlank(value) {
      return _.isNil(value) || _.isEmpty(value)
    },
    getLang(key) {
      return this.$t(key)
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    getLowerCase(text) {
      return _.lowerCase(text)
    },
    upperFirst(text) {
      return _.upperFirst(text)
    },
  }
}
</script>

<style scoped>
.cursor-not-allowed {
  cursor: not-allowed;
}

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
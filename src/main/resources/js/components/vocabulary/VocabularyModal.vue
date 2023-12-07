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
      @shown="focusName()"
  >
    <template #modal-header="{ close }">
      <b-container class="px-1" fluid>
        <close-row v-if="closable"
                   :title="getCapitalizeLang('addVocabulary')"
                   @close="reject()"
        ></close-row>
      </b-container>
    </template>

    <b-row>
      <b-col class="" sm="10">
        <b-form-group
            :label="getCapitalizeLang('name') + ':'"
            :label-for="properties.name.inputId"
            content-cols-lg="7"
            content-cols-sm="7"
            label-class="py-0"
            label-cols-lg="3"
            label-cols-sm="3"
        >
          <b-form-input
              :id="properties.name.inputId"
              :ref="properties.name.inputId"
              v-model="vocabulary.name"
              :class="{'border-success':showBorderProperty('name')}"
              :state="stateName()"
              class="rounded-sm"
              size="sm"
              trim
              @input="inputProperty($event, 'name')"
              @keyup.enter="confirm()"
              @focusin.prevent.stop="onFocusinProperty($event, properties.name.inputId, 'name')"
              @focusout.prevent.stop="onFocusoutProperty($event, properties.name.inputId, 'name')"
          >
          </b-form-input>
          <div v-if="properties.name.wasOutFocus && nameError()" class="invalid-feedback my-0">
            <small>{{ nameError() }}</small>
          </div>
        </b-form-group>
        <div v-if="properties.name.showError" class="my-0 text-danger">
          <small v-for="(e,i) in getErrors('name')">{{ upperFirst(e.message) }}</small>
        </div>
      </b-col>
      <b-col class="px-0" sm="2">
        <b-button-group>
          <b-button class="shadow-none"
                    size="sm"
                    tabindex="-1"
                    variant="outline-secondary"
                    @click.prevent.stop="cancel(properties.name.inputId, 'name')"
          >
            <i class="fa fa-close"></i>
          </b-button>
          <b-button :id="'copy-'+properties.name.inputId"
                    class="shadow-none"
                    size="sm"
                    tabindex="-1"
                    variant="outline-secondary"
                    @click.prevent.stop="copy(vocabulary.name, properties.name.inputId, 'name')"
          >
            <i class="fa fa-copy"></i>
          </b-button>
          <b-tooltip :id="'tooltip-'+'copy-'+properties.name.inputId"
                     :ref="'tooltip-'+'copy-'+properties.name.inputId"
                     :target="'copy-'+properties.name.inputId"
                     :title="getCapitalizeLang('copied')"
                     triggers
                     variant="secondary"
          ></b-tooltip>
        </b-button-group>
      </b-col>
    </b-row>

    <b-row>
      <b-col class="" sm="10">
        <b-form-group
            :label="getCapitalizeLang('sourceLang') + ':'"
            :label-for="ids.multiselectSource"
            content-cols-lg="7"
            content-cols-sm="7"
            label-class="py-0"
            label-cols-lg="3"
            label-cols-sm="3"
        >
          <multiselect
              :id="ids.multiselectSource"
              :ref="ids.multiselectSource"
              v-model="source"
              track-by="lang"
              :allow-empty="false"
              :clear-on-select="true"
              :close-on-select="true"
              :hide-selected="false"
              :multiple="false"
              :option-height="100"
              :options="options"
              :searchable="false"
              :show-no-results="false"
              :showLabels="false"
              :limit="15"
              :tabindex="-1"
              @select="onSelectSource"
          >
            <template slot="singleLabel"
                      slot-scope="props">
              <span :class="'fi fi-'+ getLowerCase(props.option.country)"></span>
              <small>
                <span>{{ getUpperCase(props.option.lang) }}</span>
                <span>{{ ': '+getLanguageByLangAndCountry(props.option) }}</span>
              </small>
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
        </b-form-group>
      </b-col>
    </b-row>
    <b-row>
      <b-col class="" sm="10">
        <b-form-group
            :label="getCapitalizeLang('targetLang') + ':'"
            :label-for="ids.multiselectTarget"
            content-cols-lg="7"
            content-cols-sm="7"
            label-class="py-0"
            label-cols-lg="3"
            label-cols-sm="3"
        >
          <multiselect
              :id="ids.multiselectTarget"
              :ref="ids.multiselectTarget"
              v-model="target"
              track-by="lang"
              :allow-empty="false"
              :clear-on-select="true"
              :close-on-select="true"
              :hide-selected="false"
              :multiple="false"
              :option-height="100"
              :options="options"
              :searchable="false"
              :show-no-results="false"
              :showLabels="false"
              :limit="15"
              :tabindex="-1"
              @select="onSelectTarget"
          >
            <template slot="singleLabel"
                      slot-scope="props">
              <span :class="'fi fi-'+ getLowerCase(props.option.country)"></span>
              <small>
                <span>{{ getUpperCase(props.option.lang) }}</span>
                <span>{{ ': '+getLanguageByLangAndCountry(props.option) }}</span>
              </small>
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
        </b-form-group>
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
  ],
  created() {
    Object.assign(this.properties, this.defaultProperties)
    this.fetchData()
  },
  components: {
    CloseRow,
  },
  computed: {
    ...mapState([
      'lang',
    ]),
    defaultProperties() {
      return {
        name: {
          inputId: this.prefixId() + "name-input-id",
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
        multiselectSource: this.prefixId() + 'multiselect-source-id',
        multiselectTarget: this.prefixId() + 'multiselect-target-id',
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
    },
    props: {
      handler: function () {
        this.$forceNextTick(() => {
          this.fetchData()
        })
      },
      deep: true
    },
  },
  data() {
    return {
      show: true,
      name: 'VocabularyModal',
      source: null,
      target: null,
      options: [],
      vocabulary: {
        name: '',
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
      this.source = this.lang.lang
      this.target = this.lang.lang
      this.options = this.lang.langs
      this.show = true
    },
    onSelectSource() {
    },
    onSelectTarget() {
    },
    focusName() {
      this.$refs[this.properties.name.inputId].focus()
    },
    showModal() {
      this.$refs[this.id].show()
    },
    hideModal() {
      this.$refs[this.id].hide()
    },
    confirm() {
      if (this.stateTrue()) {
        this.$store.dispatch("saveVocabularyAction",
            {name: this.vocabulary.name, source: this.source, target: this.target})
            .then((errors) => {
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
      this.source = this.lang.lang
      this.target = this.lang.lang
      this.vocabulary = {
        name: '',
      }
      this.options = this.lang.langs
    },
    cancel(ref, property) {
      this.vocabulary[property] = ''
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
    nameError() {
      if (this.isBlank(this.vocabulary.name)) return ''
      return ''
    },
    stateName() {
      return !this.isBlank(this.vocabulary.name)
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
      this.vocabulary.name = ''
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
      return this.stateName() && !this.isAnyErrorsShow()
    },
    isBlank(str) {
      return _.isNil(str) || _.isEmpty(str)
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
    getUpperCase(text) {
      return _.upperCase(text)
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
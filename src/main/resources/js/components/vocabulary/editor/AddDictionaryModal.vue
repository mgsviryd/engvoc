<template>
  <b-modal
      v-if="show"
      :id="id"
      :ref="id"
      :body-class="'py-1'"
      :header-class="'p-3'"
      :footer-class="''"
      :no-close-on-backdrop="!closable"
      :no-close-on-esc="!closable"
      no-fade
      @shown="focusName()"
  >
    <template #modal-header="{ close }">
      <b-container class="px-1" fluid>
        <close-row v-if="closable"
                   :title="getCapitalizeLang('addDictionary')"
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
              v-model="dictionary.name"
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
                    @click.prevent.stop="copy(dictionary.name, properties.name.inputId, 'name')"
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

    <span>{{ getCapitalizeLang('picture') + ':' }}</span>
    <single-picture-drop-zone></single-picture-drop-zone>

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
import {mapState} from 'vuex'
import SinglePictureDropZone from './SinglePictureDropZone.vue'
import CloseRow from '../../close/CloseRow.vue'
import * as _ from 'lodash'

export default {
  props: [
    'id',
    'unrepeated',
    'parent',
    'closable',
  ],
  created() {
    Object.assign(this.properties, this.defaultProperties)
    this.$root.$on('getPictureFormData', payload => {
      this.formData = payload.formData
    })
  },
  components: {
    SinglePictureDropZone,
    CloseRow,
  },
  computed: {
    ...mapState([
      'lang',
      'vocabulary',
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
  },
  watch: {
    $route: [
      'fetchData',
    ],
  },
  data() {
    return {
      show: true,
      name: "AddDictionaryModal",
      dictionary: {
        name: '',
        parent: this.parent,
        unrepeated: this.unrepeated,
        picture: null,
      },
      formData: null,
      properties: {},
      errors: [],
    }
  },
  methods: {
    fetchData() {
    },
    prefixId() {
      return this.id + '-'
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
        this.$store.dispatch(
            'addDictionaryWithPictureAction',
            {
              formData: this.formData,
              dictionary: this.dictionary,
              vocabulary: this.vocabulary.vocabulary,
            }
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
      this.dictionary = {
        name: '',
        parent: this.parent,
        unrepeated: this.unrepeated,
        picture: null,
      }
      this.formData = null
      this.$root.$emit('setDefaultDropZone')
    },
    cancel(ref, property) {
      this.dictionary[property] = ''
      this.$refs[ref].focus();
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
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    getLang(key) {
      return this.$t(key)
    },
    nameError() {
      if (this.isBlank(this.dictionary.name)) return ''
      return ''
    },
    stateName() {
      return !this.isBlank(this.dictionary.name)
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
      this.dictionary.name = ''
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
    isBlank(value) {
      return _.isNil(value) || _.isEmpty(value)
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

</style>
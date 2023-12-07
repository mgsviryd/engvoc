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
      @shown="focusWord()"
  >
    <template #modal-header="{ close }">
      <b-container class="px-1" fluid>
        <close-row v-if="closable"
                   :title="getCapitalizeLang('addCard')"
                   @close="reject()"
        ></close-row>
      </b-container>
    </template>

    <span>{{ getCapitalizeLang("dictionary") + ':' }}</span>
    {{ dictionary.name }}

    <b-row>
      <b-col class="pr-1" sm="10">
        <b-form-group
            :label="getCapitalizeLang('word') + ':'"
            :label-for="properties.word.inputId"
            content-cols-lg="7"
            content-cols-sm="7"
            label-class="py-0"
            label-cols-lg="3"
            label-cols-sm="3"
        >
          <b-form-input
              :id="properties.word.inputId"
              :ref="properties.word.inputId"
              v-model="card.word"
              :class="[{'border-success':showBorderProperty('word')}]"
              :maxlength="properties.word.maxlength"
              :state="stateWord()"
              class="rounded-sm"
              debounce="300"
              size="sm"
              trim
              @focus="$event.target.select()"
              @input="inputProperty($event, 'word')"
              @keyup.enter="confirm()"
              @keyup.native="insert($event,'word')"
              @focusin.prevent.stop="onFocusinProperty($event, properties.word.inputId, 'word')"
              @focusout.prevent.stop="onFocusoutProperty($event, properties.word.inputId, 'word')"
          >
          </b-form-input>
          <div v-if="properties.word.wasOutFocus && wordError()" class="invalid-feedback my-0">
            <small>{{ wordError() }}</small>
          </div>
        </b-form-group>
        <div v-if="properties.word.showError" class="my-0 text-danger">
          <small v-for="(e,i) in getErrors('word')">{{ e.message }}</small>
        </div>
      </b-col>
      <b-col class="px-0" sm="2">
        <b-button-group>
          <b-button class="shadow-none"
                    size="sm"
                    tabindex="-1"
                    variant="outline-secondary"
                    @click.prevent.stop="cancel(properties.word.inputId, 'word')"
          >
            <i class="fa fa-close"></i>
          </b-button>
          <b-button :id="'copy-'+properties.word.inputId"
                    class="shadow-none"
                    size="sm"
                    tabindex="-1"
                    variant="outline-secondary"
                    @click.prevent.stop="copy(card.word, properties.word.inputId, 'word')"
          >
            <i class="fa fa-copy"></i>
          </b-button>
          <b-tooltip :id="'tooltip-'+'copy-'+properties.word.inputId"
                     :ref="'tooltip-'+'copy-'+properties.word.inputId"
                     :target="'copy-'+properties.word.inputId"
                     :title="getCapitalizeLang('copied')"
                     triggers
                     variant="secondary"
          ></b-tooltip>
        </b-button-group>
      </b-col>
    </b-row>

    <b-row>
      <b-col class="pr-1" sm="10">
        <b-form-group
            :label="getCapitalizeLang('translation') + ':'"
            :label-for="properties.translation.inputId"
            content-cols-lg="7"
            content-cols-sm="7"
            label-class="py-0"
            label-cols-lg="3"
            label-cols-sm="3"
        >
          <b-form-input
              :id="properties.translation.inputId"
              :ref="properties.translation.inputId"
              v-model="card.translation"
              :class="[{'border-success':showBorderProperty('translation')}]"
              :maxlength="properties.translation.maxlength"
              :state="stateTranslation()"
              class="rounded-sm"
              debounce="300"
              size="sm"
              trim
              @focus="$event.target.select()"
              @input="inputProperty($event, 'translation')"
              @keyup.enter="confirm()"
              @keyup.native="insert($event,'translation')"
              @focusin.prevent.stop="onFocusinProperty($event, properties.translation.inputId, 'translation')"
              @focusout.prevent.stop="onFocusoutProperty($event, properties.translation.inputId, 'translation')"
          >
          </b-form-input>
          <div v-if="properties.translation.wasOutFocus && translationError()" class="invalid-feedback my-0">
            <small>{{ translationError() }}</small>
          </div>
        </b-form-group>
        <div v-if="properties.translation.showError" class="my-0 text-danger">
          <small v-for="(e,i) in getErrors('translation')">{{ e.message }}</small>
        </div>
      </b-col>
      <b-col class="px-0" sm="2">
        <b-button-group>
          <b-button class="shadow-none"
                    size="sm"
                    tabindex="-1"
                    variant="outline-secondary"
                    @click.prevent.stop="cancel(properties.translation.inputId, 'translation')"
          >
            <i class="fa fa-close"></i>
          </b-button>
          <b-button :id="'copy-'+properties.translation.inputId"
                    class="shadow-none"
                    size="sm"
                    tabindex="-1"
                    variant="outline-secondary"
                    @click.prevent.stop="copy(card.translation, properties.translation.inputId, 'translation')"
          >
            <i class="fa fa-copy"></i>
          </b-button>
          <b-tooltip :id="'tooltip-'+'copy-'+properties.translation.inputId"
                     :ref="'tooltip-'+'copy-'+properties.translation.inputId"
                     :target="'copy-'+properties.translation.inputId"
                     :title="getCapitalizeLang('copied')"
                     triggers
                     variant="secondary"
          ></b-tooltip>
        </b-button-group>
      </b-col>
    </b-row>

    <b-row>
      <b-col class="pr-1" sm="10">
        <b-form-group
            :label="getCapitalizeLang('example') + ':'"
            :label-for="properties.example.inputId"
            content-cols-lg="7"
            content-cols-sm="7"
            label-class="py-0"
            label-cols-lg="3"
            label-cols-sm="3"
        >
          <b-form-textarea
              :id="properties.example.inputId"
              :ref="properties.example.inputId"
              v-model="card.example"
              :maxlength="properties.example.maxlength"
              :trim="true"
              debounce="300"
              no-resize
              rows="2"
              size="sm"
              @focus="$event.target.select()"
              @input="inputProperty($event, 'example')"
              @keyup.native="insert($event,'example')"
              @keyup.enter="confirm()"
              @focusin.prevent.stop="onFocusinProperty($event, properties.example.inputId, 'example')"
              @focusout.prevent.stop="onFocusoutProperty($event, properties.example.inputId, 'example')"
          >
          </b-form-textarea>
          <div v-if="properties.example.wasOutFocus && exampleError()" class="invalid-feedback my-0">
            <small>{{ exampleError() }}</small>
          </div>
        </b-form-group>
        <div v-if="properties.example.showError" class="my-0 text-danger">
          <small v-for="(e,i) in getErrors('example')">{{ e.message }}</small>
        </div>
      </b-col>
      <b-col class="px-0" sm="2">
        <b-button-group>
          <b-button class="shadow-none"
                    size="sm"
                    tabindex="-1"
                    variant="outline-secondary"
                    @click.prevent.stop="cancel(properties.example.inputId, 'example')"
          >
            <i class="fa fa-close"></i>
          </b-button>
          <b-button :id="'copy-'+properties.example.inputId"
                    class="shadow-none"
                    size="sm"
                    tabindex="-1"
                    variant="outline-secondary"
                    @click.prevent.stop="copy(card.example, properties.example.inputId, 'example')"
          >
            <i class="fa fa-copy"></i>
          </b-button>
          <b-tooltip :id="'tooltip-'+'copy-'+properties.example.inputId"
                     :ref="'tooltip-'+'copy-'+properties.example.inputId"
                     :target="'copy-'+properties.example.inputId"
                     :title="getCapitalizeLang('copied')"
                     triggers
                     variant="secondary"
          ></b-tooltip>
        </b-button-group>
      </b-col>
    </b-row>

    <b-row>
      <b-col class="pr-1" sm="10">
        <b-form-group
            :label="getCapitalizeLang('exampleTranslation') + ':'"
            :label-for="properties.exampleTranslation.inputId"
            content-cols-lg="7"
            content-cols-sm="7"
            label-class="py-0"
            label-cols-lg="3"
            label-cols-sm="3"
        >
          <b-form-textarea
              :id="properties.exampleTranslation.inputId"
              :ref="properties.exampleTranslation.inputId"
              v-model="card.exampleTranslation"
              :maxlength="properties.exampleTranslation.maxlength"
              :trim="true"
              debounce="300"
              no-resize
              rows="2"
              size="sm"
              @focus="$event.target.select()"
              @input="inputProperty($event, 'exampleTranslation')"
              @keyup.native="insert($event,'exampleTranslation')"
              @keyup.enter="confirm()"
              @focusin.prevent.stop="onFocusinProperty($event, properties.exampleTranslation.inputId, 'exampleTranslation')"
              @focusout.prevent.stop="onFocusoutProperty($event, properties.exampleTranslation.inputId, 'exampleTranslation')"
          >
          </b-form-textarea>
          <div v-if="properties.exampleTranslation.wasOutFocus && exampleTranslationError()"
               class="invalid-feedback my-0">
            <small>{{ exampleTranslationError() }}</small>
          </div>
        </b-form-group>
        <div v-if="properties.exampleTranslation.showError" class="my-0 text-danger">
          <small v-for="(e,i) in getErrors('exampleTranslation')">{{ e.message }}</small>
        </div>
      </b-col>
      <b-col class="px-0" sm="2">
        <b-button-group>
          <b-button class="shadow-none"
                    size="sm"
                    tabindex="-1"
                    variant="outline-secondary"
                    @click.prevent.stop="cancel(properties.exampleTranslation.inputId, 'exampleTranslation')"
          >
            <i class="fa fa-close"></i>
          </b-button>
          <b-button :id="'copy-'+properties.exampleTranslation.inputId"
                    class="shadow-none"
                    size="sm"
                    tabindex="-1"
                    variant="outline-secondary"
                    @click.prevent.stop="copy(card.exampleTranslation, properties.exampleTranslation.inputId, 'exampleTranslation')"
          >
            <i class="fa fa-copy"></i>
          </b-button>
          <b-tooltip :id="'tooltip-'+'copy-'+properties.exampleTranslation.inputId"
                     :ref="'tooltip-'+'copy-'+properties.exampleTranslation.inputId"
                     :target="'copy-'+properties.exampleTranslation.inputId"
                     :title="getCapitalizeLang('copied')"
                     triggers
                     variant="secondary"
          ></b-tooltip>
        </b-button-group>
      </b-col>
    </b-row>

    <span>{{ getCapitalizeLang('picture') + ':' }}</span>
    <single-picture-drop-zone></single-picture-drop-zone>

    <template #modal-footer="{ ok, cancel, hide }">
      <b-button variant="secondary"
                @click.prevent.stop="reject()">
        {{ getCapitalizeLang("no") }}
      </b-button>
      <b-button
          :class="!stateTrue()?'cursor-not-allowed':null"
          :disabled="!stateTrue()"
          :variant="stateTrue()?'success':'outline-success'"
          @click.prevent.stop="confirm()">
        {{ getCapitalizeLang("yes") }}
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
    'closable',
    'unrepeated',
    'dictionary',
  ],
  components: {
    SinglePictureDropZone,
    CloseRow,
  },
  created() {
    Object.assign(this.properties, this.defaultProperties)
    this.$root.$on('getPictureFormData', payload => {
      this.formData = payload.formData
    })
  },
  computed: {
    ...mapState([
      'lang',
    ]),
    defaultProperties() {
      return {
        word: {
          inputId: this.prefixId() + "word-input-id",
          hasFocus: false,
          wasOutFocus: false,
          showError: false,
          timeoutCopyTooltip: 300,
          maxlength: 100,
        },
        translation: {
          inputId: this.prefixId() + "translation-input-id",
          hasFocus: false,
          wasOutFocus: false,
          showError: false,
          timeoutCopyTooltip: 300,
          maxlength: 100,
        },
        example: {
          inputId: this.prefixId() + "example-input-id",
          hasFocus: false,
          wasOutFocus: false,
          showError: false,
          timeoutCopyTooltip: 300,
          maxlength: 500,
        },
        exampleTranslation: {
          inputId: this.prefixId() + "exampleTranslation-input-id",
          hasFocus: false,
          wasOutFocus: false,
          showError: false,
          timeoutCopyTooltip: 300,
          maxlength: 500,
        }
      }
    },
  },
  watch: {
    $route: [
      'fetchData',
    ],
    dictionary() {
      this.fetchData()
    }
  },
  data() {
    return {
      show: true,
      card: {
        word: '',
        translation: '',
        example: '',
        exampleTranslation: '',
        unrepeated: this.unrepeated,
        dictionary: this.dictionary,
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
    showModal() {
      this.$refs[this.id].show()
    },
    hideModal() {
      this.$refs[this.id].hide()
    },
    closeModal() {
      this.hideModal()
      this.setDataToDefault()
    },
    confirm() {
      if (this.stateTrue()) {
        this.$store.dispatch(
            'addCardWithPictureAction',
            {formData: this.formData, card: this.card}
        ).then((errors) => {
          if (errors.length === 0) {
            this.closeModal()
          } else {
            this.errors = errors
            console.info(this.errors[0])
            this.showErrors()
          }
        })
      }
    },
    reject() {
      this.hideModal()
      this.setDataToDefault()
    },
    setDataToDefault() {
      this.hideErrorsAndFlush()
      this.errors = []
      this.card = {
        word: '',
        translation: '',
        example: '',
        exampleTranslation: '',
        dictionary: this.dictionary,
        unrepeated: this.unrepeated,
        picture: null,
      }
      this.formData = null
      this.$root.$emit('setDefaultDropZone')
    },
    hideErrorsAndFlush() {
      this.hideErrors()
      this.flush()
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    getLang(key) {
      return this.$t(key)
    },
    cancel(ref, property) {
      this.card[property] = ''
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
    focusWord() {
      this.$refs[this.properties.word.inputId].focus();
    },
    insert(event, property) {
      if (event.ctrlKey && event.keyCode === 86) {

      }
      if (event.metaKey && event.keyCode === 86) {

      }
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
    stateWord() {
      return !this.isBlank(this.card.word)
    },
    stateTranslation() {
      return !this.isBlank(this.card.translation)
    },
    stateTrue() {
      return this.stateWord() && this.stateTranslation() && !this.isAnyErrorsShow()
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
    flush() {
      this.card.word = ''
      this.card.translation = ''
      this.card.example = ''
      this.card.exampleTranslation = ''
      Object.keys(this.properties).forEach(p => {
        this.properties[p].wasOutFocus = false
      })
    },
    getErrors(property) {
      return this.errors.filter(e => e.attribute === property)
    },
    isBlank(str) {
      return _.isNil(str) || _.isEmpty(str)
    },
    wordError() {
      if (this.isBlank(this.card.word)) return ''
      return ''
    },
    translationError() {
      if (this.isBlank(this.card.translation)) return ''
      return ''
    },
    exampleError() {
      if (this.isBlank(this.card.example)) return ''
      return ''
    },
    exampleTranslationError() {
      if (this.isBlank(this.card.exampleTranslation)) return ''
      return ''
    },
  }
}
</script>

<style scoped>
.cursor-not-allowed {
  cursor: not-allowed;
}

</style>
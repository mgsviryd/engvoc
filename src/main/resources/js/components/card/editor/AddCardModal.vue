<template>
  <b-modal :id="id"
           @shown="focusWord()"
           :header-class="'p-3'"
           no-fade
  >
    <template #modal-header="{ close }">
      <h5>{{ getCapitalizeLang("addCard") }}</h5>
      <button type="button" class="close" aria-label="Close" @click.prevent.stop="reject()">
        <span aria-hidden="true">&times;</span>
      </button>
    </template>

    <span>{{ getCapitalizeLang("dictionary") + ':' }}</span>
    {{ card.dictionary.name }}

    <b-row>
      <b-col sm="10" class="pr-1">
        <b-form-group
            label-class="py-0"
            :label="getCapitalizeLang('word') + ':'"
            label-cols-sm="3"
            label-cols-lg="3"
            content-cols-sm="7"
            content-cols-lg="7"
            label-for="textarea-word"
            :invalid-feedback="actionLocal.errors.cardWordError?actionLocal.errors.cardWordError:''"
        >
          <b-form-input
              id="textarea-word"
              ref="textarea-word"
              size="sm"
              :state="card.word !== null && card.word !== ''"
              trim
              debounce="300"
              :maxlength="properties.word.maxlength"
              v-model="card.word"
              @focus="$event.target.select()"
              @keyup.native="insert($event,'word')"
          >
          </b-form-input>
        </b-form-group>
      </b-col>
      <b-col sm="2" class="px-0">
        <b-button-group>
          <b-button variant="outline-danger"
                    size="sm"
                    tabindex="-1"
                    class="shadow-none"
                    @click.prevent.stop="cancel('textarea-word', 'word')"
          >
            <i class="fa fa-times"></i>
          </b-button>
          <b-button variant="outline-primary"
                    :id="'copy'+'textarea-word'"
                    size="sm"
                    tabindex="-1"
                    class="shadow-none"
                    @click.prevent.stop="copy(card.word, 'textarea-word', 'word')"
          >
            <i class="fa fa-copy"></i>
          </b-button>
          <b-tooltip :show.sync="properties.word.showCopyTooltip" @shown="copyTooltipHideLater('word')"
                     variant="secondary" :target="'copy'+'textarea-word'" triggers
                     :title="getCapitalizeLang('copied')"></b-tooltip>
        </b-button-group>
      </b-col>
    </b-row>

    <b-row>
      <b-col sm="10" class="pr-1">
        <b-form-group
            label-class="py-0"
            :label="getCapitalizeLang('translation') + ':'"
            label-cols-sm="3"
            label-cols-lg="3"
            content-cols-sm="7"
            content-cols-lg="7"
            label-for="textarea-translation"
            :invalid-feedback="actionLocal.errors.cardTranslationError?actionLocal.errors.cardTranslationError:''"
        >
          <b-form-input
              id="textarea-translation"
              ref="textarea-translation"
              size="sm"
              :state="card.translation !== null && card.translation !== ''"
              trim
              debounce="300"
              :maxlength="properties.translation.maxlength"
              v-model="card.translation"
              @focus="$event.target.select()"
              @keyup.native="insert($event,'translation')"
          >
          </b-form-input>
        </b-form-group>
      </b-col>
      <b-col sm="2" class="px-0">
        <b-button-group>
          <b-button variant="outline-danger"
                    size="sm"
                    tabindex="-1"
                    class="shadow-none"
                    @click.prevent.stop="cancel('textarea-translation', 'translation')"
          >
            <i class="fa fa-times"></i>
          </b-button>
          <b-button variant="outline-primary"
                    :id="'copy'+'textarea-translation'"
                    size="sm"
                    tabindex="-1"
                    class="shadow-none"
                    @click.prevent.stop="copy(card.translation, 'textarea-translation', 'translation')"
          >
            <i class="fa fa-copy"></i>
          </b-button>
          <b-tooltip :show.sync="properties.translation.showCopyTooltip" @shown="copyTooltipHideLater('translation')"
                     variant="secondary" :target="'copy'+'textarea-translation'" triggers
                     :title="getCapitalizeLang('copied')"></b-tooltip>
        </b-button-group>
      </b-col>
    </b-row>

    <b-row>
      <b-col sm="10" class="pr-1">
        <b-form-group
            label-class="py-0"
            :label="getCapitalizeLang('example') + ':'"
            label-cols-sm="3"
            label-cols-lg="3"
            content-cols-sm="7"
            content-cols-lg="7"
            label-for="textarea-example"
            :invalid-feedback="actionLocal.errors.cardExampleError?actionLocal.errors.cardExampleError:''"
        >
          <b-form-textarea
              id="textarea-example"
              ref="textarea-example"
              size="sm"
              rows="2"
              :trim="true"
              debounce="300"
              no-resize
              v-model="card.example"
              :maxlength="properties.example.maxlength"
              @focus="$event.target.select()"
              @keyup.native="insert($event,'example')"
          >
          </b-form-textarea>
        </b-form-group>
      </b-col>
      <b-col sm="2" class="px-0">
        <b-button-group>
          <b-button variant="outline-danger"
                    size="sm"
                    tabindex="-1"
                    class="shadow-none"
                    @click.prevent.stop="cancel('textarea-example', 'example')"
          >
            <i class="fa fa-times"></i>
          </b-button>
          <b-button variant="outline-primary"
                    :id="'copy'+'textarea-example'"
                    size="sm"
                    tabindex="-1"
                    class="shadow-none"
                    @click.prevent.stop="copy(card.example, 'textarea-example', 'example')"
          >
            <i class="fa fa-copy"></i>
          </b-button>
          <b-tooltip :show.sync="properties.example.showCopyTooltip" @shown="copyTooltipHideLater('example')"
                     variant="secondary" :target="'copy'+'textarea-example'" triggers
                     :title="getCapitalizeLang('copied')"></b-tooltip>
        </b-button-group>
      </b-col>
    </b-row>

    <b-row>
      <b-col sm="10" class="pr-1">
        <b-form-group
            label-class="py-0"
            :label="getCapitalizeLang('exampleTranslation') + ':'"
            label-cols-sm="3"
            label-cols-lg="3"
            content-cols-sm="7"
            content-cols-lg="7"
            label-for="textarea-exampleTranslation"
            :invalid-feedback="actionLocal.errors.cardExampleTranslationError?actionLocal.errors.cardExampleTranslationError:''"
        >
          <b-form-textarea
              id="textarea-exampleTranslation"
              ref="textarea-exampleTranslation"
              size="sm"
              rows="2"
              :trim="true"
              debounce="300"
              no-resize
              :maxlength="properties.exampleTranslation.maxlength"
              v-model="card.exampleTranslation"
              @focus="$event.target.select()"
              @keyup.native="insert($event,'translation')"
          >
          </b-form-textarea>
        </b-form-group>
      </b-col>
      <b-col sm="2" class="px-0">
        <b-button-group>
          <b-button variant="outline-danger"
                    size="sm"
                    tabindex="-1"
                    class="shadow-none"
                    @click.prevent.stop="cancel('textarea-exampleTranslation', 'exampleTranslation')"
          >
            <i class="fa fa-times"></i>
          </b-button>
          <b-button variant="outline-primary"
                    :id="'copy'+'textarea-exampleTranslation'"
                    size="sm"
                    tabindex="-1"
                    class="shadow-none"
                    @click.prevent.stop="copy(card.exampleTranslation, 'textarea-exampleTranslation', 'exampleTranslation')"
          >
            <i class="fa fa-copy"></i>
          </b-button>
          <b-tooltip :show.sync="properties.exampleTranslation.showCopyTooltip"
                     @shown="copyTooltipHideLater('exampleTranslation')" variant="secondary"
                     :target="'copy'+'textarea-exampleTranslation'" triggers
                     :title="getCapitalizeLang('copied')"></b-tooltip>
        </b-button-group>
      </b-col>
    </b-row>

    <span>{{ getCapitalizeLang("picture") + ':' }}</span>
    <single-picture-drop-zone></single-picture-drop-zone>

    <template #modal-footer="{ ok, cancel, hide }">
      <button type="button" class="btn btn-secondary" @click.prevent.stop="reject()">
        {{ getCapitalizeLang("no") }}
      </button>
      <button type="button" class="btn btn-primary" @click.prevent.stop="confirm()">
        {{ getCapitalizeLang("yes") }}
      </button>
    </template>
  </b-modal>
</template>

<script>
import {mapState} from 'vuex'
import singlePictureDropZone from "./SinglePictureDropZone.vue"
import * as _ from "lodash"
import langDetect from "languagedetect"

export default {
  created() {
    this.$store.watch(this.$store.getters.getActionId, actionId => {
      if (actionId === this.actionLocal.id) {
        this.updateAction()
        this.closeIfNoErrors()
      }
    })
    this.$root.$on('getPictureFormData', payload => {
      this.formData = payload.formData
    })
  },
  components: {
    singlePictureDropZone,
  },
  props: [
    'id',
    'unique',
    'dictionary',
  ],
  computed: {
    ...mapState([
      'action',
      'lang',
    ]),
  },
  watch: {
    $route: [
      'fetchData',
    ],
  },
  data() {
    return {
      card: {
        unique: this.unique,
        word: null,
        translation: null,
        example: null,
        exampleTranslation: null,
        dictionary: this.dictionary,
        picture: null,
      },
      properties: {
        word: {
          showCopyTooltip: false,
          timeoutCopyTooltip: 300,
          maxlength: 100,
        },
        translation: {
          showCopyTooltip: false,
          timeoutCopyTooltip: 300,
          maxlength: 100,
        },
        example: {
          showCopyTooltip: false,
          timeoutCopyTooltip: 300,
          maxlength: 500,
        },
        exampleTranslation: {
          showCopyTooltip: false,
          timeoutCopyTooltip: 300,
          maxlength: 500,
        },
      },
      formData: null,
      actionLocal: {
        id: -1,
        errors: {},
      },
    }
  },
  methods: {
    fetchData() {
    },
    updateAction() {
      this.actionLocal.id = this.action.id
      this.actionLocal.errors = this.action.errors
    },
    confirm() {
      this.actionLocal.id = _.now()
      this.$store.dispatch(
          'addCardWithPictureAction',
          {actionId: this.actionLocal.id, formData: this.formData, card: this.card}
      )
    },
    reject() {
      this.setDataToDefault()
      this.close()
    },
    setDataToDefault() {
      this.actionLocal.errors = {}
      this.card = {
        unique: this.unique,
        word: null,
        translation: null,
        example: null,
        exampleTranslation: null,
        dictionary: this.dictionary,
        picture: null,
      }
      this.formData = null
      this.$root.$emit('setDefaultDropZone')
    },
    close() {
      this.$bvModal.hide(this.id)
    },
    closeIfNoErrors() {
      if (Object.keys(this.actionLocal.errors).length === 0) {
        this.reject()
      }
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    getLang(key) {
      return this.lang.map[key]
    },
    cancel(ref, property) {
      this.card[property] = null
      this.$refs[ref].focus();
    },
    copy(text, ref, property) {
      this.properties[property].showCopyTooltip = true
      this.$refs[ref].focus();
      navigator.clipboard.writeText(text)
    },
    copyTooltipHideLater(property) {
      setTimeout(() => {
        this.properties[property].showCopyTooltip = false
      }, this.properties[property].timeoutCopyTooltip);

    },
    focusWord() {
      this.$refs['textarea-word'].focus();
    },
    insert(event, property){
      if(event.ctrlKey && event.keyCode === 86){
        console.info(property)
      }
      if (event.metaKey && event.keyCode === 86){
        console.info(property)
      }
    },
  }
}
</script>

<style scoped>

</style>
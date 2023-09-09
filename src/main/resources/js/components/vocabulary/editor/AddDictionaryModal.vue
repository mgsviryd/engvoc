<template>
  <b-modal
      v-if="show"
      :id="id"
      :ref="id"
      :header-class="'p-3'"
      :body-class="'py-0'"
      no-fade
      :no-close-on-backdrop="!closable"
      :no-close-on-esc="!closable"
  >
    <template #modal-header="{ close }">
      <b-container fluid class="px-1">
        <close-row v-if="closable"
                   :title="getCapitalizeLang('addDictionary')"
                   @close="reject()"
        ></close-row>
      </b-container>
    </template>

    <b-input v-model="dictionary.name" :placeholder="getCapitalizeLang('enterName')"></b-input>
    <div v-if="actionLocal.errors.notUnrepeatedDictionaryError" class="alert alert-danger">
      {{ getLang('notUnrepetedDictionaryError') }}
    </div>
    <single-picture-drop-zone></single-picture-drop-zone>

    <template #modal-footer>
      <b-button variant="secondary" @click.prevent.stop="reject()">
        {{ getCapitalizeLang('no') }}
      </b-button>
      <b-button variant="primary" @click.prevent.stop="confirm()">
        {{ getCapitalizeLang('yes') }}
      </b-button>
    </template>
  </b-modal>
</template>

<script>
import {mapState} from 'vuex'
import SinglePictureDropZone from "./SinglePictureDropZone.vue"
import CloseRow from "../../close/CloseRow.vue"
import * as _ from "lodash"

export default {
  props: [
    'id',
    'closable',
    'unrepeated',
  ],
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
    SinglePictureDropZone,
    CloseRow,
  },
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
      show: true,
      dictionary: {
        name: "",
        parent: 0,
        picture: null,
        unrepeated: this.unrepeated,
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
    showModal() {
      this.$refs[this.id].show()
    },
    hideModal() {
      this.$refs[this.id].hide()
    },
    updateAction() {
      this.actionLocal.id = this.action.id
      this.actionLocal.errors = this.action.errors
    },
    confirm() {
      this.actionLocal.id = _.now()
      this.$store.dispatch(
          'addDictionaryWithPictureAction',
          {actionId: this.actionLocal.id, formData: this.formData, dictionary: this.dictionary}
      )
    },
    reject() {
      this.setDataToDefault()
      this.close()
    },
    setDataToDefault() {
      this.actionLocal.errors = {}
      this.dictionary = {
        name: null,
        parent: null,
        picture: null,
        unrepeated: this.unrepeated,
      }
      this.formData = null
      this.$root.$emit('setDefaultDropZone')
    },
    close() {
      this.hideModal()
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
      return this.$t(key)
    },
  }
}
</script>

<style scoped>

</style>
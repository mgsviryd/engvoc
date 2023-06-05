<template>
  <div v-if="show" class="modal fade" :id="id">
    <div class="modal-dialog modal-dialog-centered" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">
            {{ getCapitalizeLang('addDictionary') }}
          </h5>
          <button type="button" class="close" aria-label="Close" @click.prevent.stop="reject()">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <b-input v-model="dictionary.name" :placeholder="getCapitalizeLang('enterName')"></b-input>
          <div v-if="actionLocal.errors.notUniqueDictionaryError" class="alert alert-danger">
            {{ lang.map.notUniqueDictionaryError }}
          </div>
          <single-picture-drop-zone></single-picture-drop-zone>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click.prevent.stop="reject()">
            {{ getCapitalizeLang('no') }}
          </button>
          <button type="button" class="btn btn-primary" @click.prevent.stop="confirm()">
            {{ getCapitalizeLang('yes') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {mapState} from 'vuex'
import singlePictureDropZone from "./SinglePictureDropZone.vue"
import * as _ from "lodash"

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
      show: true,
      dictionary: {
        name: "",
        parent: 0,
        picture: null,
        unique: this.unique,
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
        unique: this.unique,
      }
      this.formData = null
      this.$root.$emit('setDefaultDropZone')
    },
    close() {
      $("#" + this.id).modal('hide')
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
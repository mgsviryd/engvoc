<template>
  <div v-if="show" class="modal fade" :id="idModal">
    <div class="modal-dialog modal-dialog-centered" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="exampleModalLabel">{{lang.map.addNewDictionary}}</h5>
          <button type="button" class="close" aria-label="Close" @click.prevent.stop="reject">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <b-input v-model="dictionary.name"></b-input>
          <div v-if="actionLocal.errors.notUniqueDictionaryError" class="alert alert-danger">
            {{ lang.map.notUniqueDictionaryError }}
          </div>
          <single-picture-drop-zone></single-picture-drop-zone>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click.prevent.stop="reject">{{ lang.map.no }}
          </button>
          <button type="button" class="btn btn-primary" @click.prevent.stop="confirm">{{ lang.map.yes }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {mapState} from 'vuex'
import singlePictureDropZone from "./SinglePictureDropZone.vue"
import date from "../../../util/date";

export default {
  created() {
    this.$root.$on('getPictureFormData', payload => {
      this.formData = payload.formData
    })
    this.$store.watch(this.$store.getters.getActionId, actionId => {
      if(actionId === this.actionLocal.id){
        this.updateAction()
        this.closeIfNoErrors()
      }
    })
  },
  components: {
    singlePictureDropZone,
  },
  props: [
    'storeAction',
    'dictionaries',
    'id'
  ],
  computed: {
    ...mapState([
      'action',
      'lang',
    ]),
    idModal() {
      return this.id
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
      dictionary: {
        name: "",
        parent: 0,
        picture: null,
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
    updateAction(){
      this.actionLocal.id = this.action.id
      this.actionLocal.errors = this.action.errors
    },
    confirm() {
      this.actionLocal.id = date.getUTCMilliseconds(new Date())
      this.$store.dispatch(this.storeAction,
          {actionId: this.actionLocal.id, formData: this.formData, dictionary: this.dictionary}
      )
    },
    reject() {
      this.setDataToDefault()
      this.close()
    },
    setDataToDefault() {
      this.dictionary = {
        name: "",
        parent: 0,
        picture: null,
      }
      this.formData = null
      this.actionLocal.errors = {}
      this.$root.$emit('setDefaultDropZone')
    },
    close(){
      $("#" + this.id).modal('hide')
    },
    closeIfNoErrors() {
      if (Object.keys(this.actionLocal.errors).length === 0) {
        this.reject()
      }
    },
  }
}
</script>

<style scoped>

</style>
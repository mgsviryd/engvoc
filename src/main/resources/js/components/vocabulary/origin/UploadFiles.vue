<template>
  <div v-if="show" class="container-fluid">
    <upload-lang-multiselect></upload-lang-multiselect>
    <div class="accordion" role="tablist">
      <b-card class="mb-1" no-body>
        <b-card-header class="p-1" header-tag="header" role="tab">
          <b-button v-b-toggle.accordion-1 block disabled variant="light">{{ getCapitalizeLang('options') }}</b-button>
        </b-card-header>
        <b-collapse id="accordion-1" accordion="my-accordion" role="tabpanel" visible>
          <b-card-body body-class="py-1">
            <b-form-checkbox
                v-model="options.saveAllUploadCards"
                size="sm"
                switch>
              <small>{{ getCapitalizeLang('saveAllUploadCards') }}</small>
            </b-form-checkbox>
            <b-form-checkbox
                v-model="options.saveNewUnrepeatedCards"
                size="sm"
                switch>
              <small>{{ getCapitalizeLang('saveNewUnrepeatedCards') }}</small>
            </b-form-checkbox>
            <b-form-checkbox
                v-model="options.updateLearnedStatusUnrepeatedCards"
                size="sm"
                switch>
              <small>{{ getCapitalizeLang('updateLearnedStatusUnrepeatedCards') }}</small>
            </b-form-checkbox>
            <b-form-checkbox
                v-model="options.updateCardsWithAbsentSound"
                size="sm"
                switch>
              <small>{{ getCapitalizeLang('updateCardsWithAbsentSound') }}</small>
            </b-form-checkbox>
          </b-card-body>
        </b-collapse>
      </b-card>
    </div>
    <drop-zone :props="dropZoneProps" @uploadFile="uploadFile" @uploadFiles="uploadFiles"></drop-zone>
  </div>
</template>

<script>
import {mapState} from 'vuex'
import DropZone from "../../upload/DropZone.vue"
import UploadLangMultiselect from "../../lang/LangPairUploadMultiselect.vue"
import * as _ from "lodash"

export default {
  props: [
    'dropZoneProps'
  ],
  components: {
    DropZone,
    UploadLangMultiselect,
  },
  computed: {
    ...mapState([
      'props',
    ]),
  },
  data() {
    return {
      show: true,
      options: {
        saveAllUploadCards: true,
        saveNewUnrepeatedCards: false,
        updateLearnedStatusUnrepeatedCards: false,
        updateCardsWithAbsentSound: false,
      },
    }
  },
  methods: {
    async uploadFile(payload) {
      payload.pair = this.props.upload.lang
      payload.options = this.options
      await this.$store.dispatch(this.dropZoneProps.fileStoreAction, payload)
    },
    async uploadFiles(payload) {
      payload.pair = this.props.upload.lang
      payload.options = this.options
      await this.$store.dispatch(this.dropZoneProps.filesStoreAction, payload)
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

</style>
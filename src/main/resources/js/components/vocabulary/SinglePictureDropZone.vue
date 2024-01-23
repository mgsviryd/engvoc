<template>
  <b-container fluid class="px-0">
    <VueFileAgent
        ref="vueFileAgent"
        :theme="'list'"
        :multiple="props.multiple"
        :deletable="true"
        :meta="true"
        :accept="props.accept"
        :maxSize="props.size"
        :maxFiles="props.maxFiles"
        :helpText="choose"
        :errorText="{
      type: type,
      size: size,
    }"
        @select="filesSelected($event)"
        @beforedelete="onBeforeDelete($event)"
        @delete="fileDeleted($event)"
        v-model="fileRecords"
    ></VueFileAgent>
  </b-container>
</template>

<script>
import {mapState} from "vuex"
import * as _ from "lodash"

export default {
  components: {},
  props: [],
  created() {
    this.$root.$on('setDefaultDropZone', () => {
      this.setDefaultDropZone()
    })
  },
  watch: {
    fileRecordsForUpload(newVal, oldVal) {
      if (newVal.length === 1) {
        this.$root.$emit('getPictureFormData', {formData: this.getFormData()})
      }
    },
  },
  computed: {
    ...mapState([
      'lang',
    ]),
    type() {
      return this.getCapitalizeLang('invalidTypeFile') + " " + this.props.accept + "."
    },
    size() {
      return this.getCapitalizeLang('sizeExceed') + ": " + this.props.size + "."
    },
    choose() {
      return this.getCapitalizeLang('dragFileWithExtension') + " " + this.props.accept + "."
    }
  },
  data() {
    return {
      props: {
        accept: 'image/*',
        size: '5MB',
        multiple: false,
        maxFiles: 1,
      },
      fileRecords: [],
      fileRecordsForUpload: [], // maintain an upload queue
      uploadUrl: 'https://www.mocky.io/v2/5d4fb20b3000005c111099e3', // is not used, but for VueFileAgent
      uploadHeaders: {'X-Test-Header': 'vue-file-agent'}, // is not used, but for VueFileAgent
    }
  },
  methods: {
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    getLang(key) {
      return this.$t(key)
    },
    setDefaultDropZone() {
      this.fileRecordsForUpload = []
      this.fileRecords = []
    },
    getFormData() {
      const formData = new FormData()
      const file = this.fileRecordsForUpload[0].file
      formData.append('file', file)
      return formData
    },
    getFile(){
      if (this.fileRecordsForUpload.length > 0){
        return this.fileRecordsForUpload[0].file
      }else return null
    },
    deleteUploadedFile(fileRecord) {
      const i = this.fileRecords.indexOf(fileRecord)
      if (i !== -1) {
        this.fileRecords.splice(i, 1)
      }
    },
    filesSelected(fileRecordsNewlySelected) {
      const validFileRecords = fileRecordsNewlySelected.filter((fileRecord) => !fileRecord.error)
      this.fileRecordsForUpload = this.fileRecordsForUpload.concat(validFileRecords)
    },
    onBeforeDelete(fileRecord) {
      const i = this.fileRecordsForUpload.indexOf(fileRecord)
      if (i !== -1) {
        // queued file, not yet uploaded. Just remove from the arrays
        this.fileRecordsForUpload.splice(i, 1)
        const k = this.fileRecords.indexOf(fileRecord)
        if (k !== -1) this.fileRecords.splice(k, 1)
      } else {
        this.$refs.vueFileAgent.deleteFileRecord(fileRecord) // will trigger 'delete' event
      }
    },
    fileDeleted(fileRecord) {
      const i = this.fileRecordsForUpload.indexOf(fileRecord)
      if (i !== -1) {
        this.fileRecordsForUpload.splice(i, 1)
      } else {
        this.deleteUploadedFile(fileRecord)
      }
    },
  },
}
</script>

<style scoped>
</style>
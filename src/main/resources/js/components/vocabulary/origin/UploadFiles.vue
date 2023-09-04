<template>
  <div v-if="show" class="container-fluid">
    <upload-lang-multiselect></upload-lang-multiselect>
    <drop-zone :props="dropZoneProps"></drop-zone>
  </div>
</template>

<script>
import {mapState} from 'vuex'
import DropZone from "../../upload/DropZone.vue"
import UploadLangMultiselect from "./UploadLangMultiselect.vue"
import * as _ from "lodash"

export default {
  props: [
    'dropZoneProps'
  ],
  components: {
    DropZone,
    UploadLangMultiselect,
  },
  data() {
    return {
      show: true,
    }
  },

}
</script>

<style scoped>

</style>
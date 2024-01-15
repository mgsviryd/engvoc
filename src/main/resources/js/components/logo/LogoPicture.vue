<template>
  <picture-static :alt="'...'" :pathPic="'/logo/logo.svg'"
                  :pictureClass="''" :pictureStyle="style"/>
</template>

<script>
import {mapState} from "vuex"
import PictureStatic from "../picture/PictureStatic.vue"

export default {
  components: {
    PictureStatic,
  },
  computed: {
    ...mapState([
      'lang',
      'config',
    ]),
  },
  data() {
    return {
      style: {
        width: `${80}px`,
        height: `${20}px`,
      },
    }
  },
  methods: {}
}
</script>

<style scoped>

</style>
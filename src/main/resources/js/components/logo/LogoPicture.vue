<template>
  <picture-static :pathPic="'/logo/logo.svg'" :alt="'...'"
                  :pictureClass="''" :pictureStyle="style"/>
</template>

<script>
import {mapState} from 'vuex'
import PictureStatic from "../picture/PictureStatic.vue"

export default {
  components: {
    PictureStatic,
  },
  computed: {
    ...mapState([
      'lang',
      'frontend',
    ]),
  },
  data() {
    return {
      style: 'width:80px; height:20px;'
    }
  },
  methods: {

  }
}
</script>

<style scoped>

</style>
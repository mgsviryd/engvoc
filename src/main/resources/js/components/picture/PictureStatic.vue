<template>
  <picture v-if="isDataPresent">
    <source v-for="(item, index) in pictureMedia.pictureMedias"
            :media="item.media"
            :srcset="srcsetItem(item.path)">

    <source :srcset="srcset">
    <img
        :alt="alt"
        :class="pictureClass"
        :srcset="srcset"
        :style="pictureStyle"
    />
  </picture>
</template>

<script>
import {mapState} from 'vuex'

export default {
  props: [
    'pathPic',
    'alt',
    'pictureClass',
    'pictureStyle',
  ],
  computed: {
    ...mapState([
      'pictureMedia',
    ]),
    isDataPresent() {
      return this.pathPic != null
    },
    srcset() {
      return this.root + this.pathPic
    },
  },
  data() {
    return {
      root: '/static/picture',
      slash: '/',
    }
  },
  methods: {
    srcsetItem(path) {
      return this.root + path + this.pathPic
    },
  },
}
</script>

<style>

</style>
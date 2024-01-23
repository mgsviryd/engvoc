<template>
  <picture v-if="isDataPresent">
    <source v-for="(item, index) in pictureMedia.pictureMedias"
            :media="item.media"
            :srcset="srcsetItem(item.path)"
    >

    <source :srcset="srcset">
    <img
        :alt="alt"
        :class="pictureClass"
        :srcset="srcset"
        :style="pictureStyle"
        @click="onClick"
    />
  </picture>
</template>

<script>
import {mapState} from 'vuex'

export default {
  props: [
    'pathPic',
    'marker',
    'alt',
    'pictureClass',
    'pictureStyle',
  ],
  computed: {
    ...mapState([
      'pictureMedia',
      'config',
    ]),

    root() {
      if (this.isDefaultPictureFileName()) {
        return '/static/picture'
      } else {
        return this.config.uploadResource + this.config.uploadPicture
      }
    },

    isDataPresent() {
      return this.pathPic != null && this.marker != null
    },
    srcset() {
      return this.root + this.slash + this.markerPath
    },
    markerPath() {
      let split = this.pathPic.split('.')
      return split[0] + this.marker + '.' + split[1]
    },
  },
  data() {
    return {
      slash: "/",
    }
  },
  methods: {
    srcsetItem(path) {
      return this.root + path + this.slash + this.markerPath
    },
    isDefaultPictureFileName() {
      return this.pictureMedia.defaultPictureFileName === this.pathPic
    },
    onClick(){
      this.$emit('onClick')
    },
  },
}
</script>

<style>

</style>
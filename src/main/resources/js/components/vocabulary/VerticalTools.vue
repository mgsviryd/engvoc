<template>
  <b-button-toolbar
      v-if="show"
      class="vertical-tools"
  >
    <b-button-group size="sm" vertical>
      <b-button class="p-1 rounded-0 border-dark shadow-none"
                size="sm"
                variant="secondary"
                @click.prevent.stop="showHideInstance()"
      >
        <i class="fa-solid fa-clone fa-xs"></i>
      </b-button>
      <b-button class="p-1 rounded-0 border-dark shadow-none"
                size="sm"
                variant="secondary"
      >
        <i class="fa-solid fa-maximize"></i>
      </b-button>
      <b-button class="p-1 rounded-0 border-dark shadow-none"
                size="sm"
                variant="light"
                @click.prevent.stop="showFullNav()"
      >
        <i v-if="instance.instanceMark === 'left'" class="fa-solid fa-angles-right"></i>
        <i v-else class="fa-solid fa-angles-left"></i>
      </b-button>
      <b-button class="p-1 rounded-0 border-dark shadow-none"
                size="sm"
                variant="light"
                @click.prevent.stop="hideFullNav()"
      >
        <i v-if="instance.instanceMark === 'left'" class="fa-solid fa-angles-left"></i>
        <i v-else class="fa-solid fa-angles-right"></i>
      </b-button>
      <b-button class="p-1 rounded-0 border-dark shadow-none"
                size="sm"
                variant="light"
                @click.prevent.stop="stepUpNav()"
      >
        <i v-if="instance.instanceMark === 'left'" class="fa-solid fa-angle-right"></i>
        <i v-else class="fa-solid fa-angle-left"></i>
      </b-button>
      <b-button class="p-1 rounded-0 border-dark shadow-none"
                size="sm"
                variant="light"
                @click.prevent.stop="stepDownNav()"
      >
        <i v-if="instance.instanceMark === 'left'" class="fa-solid fa-angle-left"></i>
        <i v-else class="fa-solid fa-angle-right"></i>
      </b-button>
    </b-button-group>
  </b-button-toolbar>
</template>

<script>
import {mapState} from 'vuex'
import * as _ from "lodash"

export default {
  props: [
    'instance'
  ],
  components: {},
  created() {
    this.fetchData()
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
    instance(newVal) {
      this.fetchData()
    }
  },
  data() {
    return {
      show: false,
    }
  },
  methods: {
    fetchData() {
      this.show = false

      this.show = true
    },
    showHideInstance() {
      this.$emit('showHideInstance', this.instance.instanceMark)
    },
    showFullNav() {
      this.$emit('showFullNav', this.instance.instanceMark)
    },
    hideFullNav() {
      this.$emit('hideFullNav', this.instance.instanceMark)
    },
    stepUpNav() {
      this.$emit('stepUpNav', this.instance.instanceMark)
    },
    stepDownNav() {
      this.$emit('stepDownNav', this.instance.instanceMark)
    },
  }
}
</script>

<style scoped>
.vertical-tools{

}


</style>
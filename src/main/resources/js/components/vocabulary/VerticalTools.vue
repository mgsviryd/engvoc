<template>
  <b-button-toolbar
      v-if="show"
      class="vertical-tools"
  >
    <b-button-group size="sm" vertical>
      <b-button class="p-0 rounded-0 border-dark shadow-none p-0"
                size="sm"
                variant="secondary"
                style="width: 20px;"
                @click.prevent.stop="showHideInstance()"
      >
        <i class="fa-solid fa-clone fa-xs"></i>
      </b-button>
      <b-button class="rounded-0 border-dark shadow-none p-0"
                size="sm"
                variant="secondary"
                style="width: 20px;"
      >
        <i class="fa-solid fa-right-left fa-xs"></i>
      </b-button>
      <b-button class="rounded-0 border-dark shadow-none p-0"
                size="sm"
                variant="secondary"
                style="width: 20px;"
      >
        <i class="fa-solid fa-repeat fa-xs"></i>
      </b-button>
      <b-button class="rounded-0 border-dark shadow-none p-0"
                size="sm"
                variant="secondary"
                style="width: 20px;"
                @click.prevent.stop="showFullNav()"
      >
        <i v-if="instance.instanceMark === 'left'" class="fa-solid fa-angles-right fa-xs"></i>
        <i v-else class="fa-solid fa-angles-left fa-xs"></i>
      </b-button>
      <b-button class="rounded-0 border-dark shadow-none p-0"
                size="sm"
                variant="secondary"
                style="width: 20px;"
                @click.prevent.stop="hideFullNav()"
      >
        <i v-if="instance.instanceMark === 'left'" class="fa-solid fa-angles-left fa-xs"></i>
        <i v-else class="fa-solid fa-angles-right fa-xs"></i>
      </b-button>
      <b-button class="rounded-0 border-dark shadow-none p-0"
                size="sm"
                variant="secondary"
                style="width: 20px;"
                @click.prevent.stop="stepUpNav()"
      >
        <i v-if="instance.instanceMark === 'left'" class="fa-solid fa-angle-right fa-xs"></i>
        <i v-else class="fa-solid fa-angle-left fa-xs"></i>
      </b-button>
      <b-button class="rounded-0 border-dark shadow-none p-0"
                size="sm"
                variant="secondary"
                @click.prevent.stop="stepDownNav()"
                style="width: 20px;"
      >
        <i v-if="instance.instanceMark === 'left'" class="fa-solid fa-angle-left fa-xs"></i>
        <i v-else class="fa-solid fa-angle-right fa-xs"></i>
      </b-button>
    </b-button-group>
  </b-button-toolbar>
</template>

<script>
import {mapState} from "vuex"
import * as _ from "lodash"

export default {
  props: [
    'instance'
  ],
  components: {

  },
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
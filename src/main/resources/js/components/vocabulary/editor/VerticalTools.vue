<template>
  <div v-if="show" class="btn-group-vertical d-inline-block" style="border: 1px solid #6C757D;">
    <b-button size="sm"
              variant="secondary"
              class="p-1 border-right-0 border-left-0 rounded-0 border-dark shadow-none"
              @click.prevent.stop="showHideInstance()"
    >
      <i v-if="instance.show" class="fa-solid fa-clone fa-xs"></i>
      <i v-else class="fa-solid fa-square fa-xs"></i>
    </b-button>
    <b-button size="sm"
              variant="secondary"
              class="p-1 border-right-0 border-left-0 rounded-0 border-dark shadow-none"
    >
      <i class="fa-solid fa-maximize"></i>
    </b-button>
    <b-button size="sm"
              variant="light"
              class="p-1 border-right-0 border-left-0 rounded-0 border-secondary shadow-none"
    >
      <i class="fa-solid fa-angles-right"></i>
    </b-button>
    <b-button size="sm"
              variant="light"
              class="p-1 border-right-0 border-left-0 rounded-0 border-secondary shadow-none"
    >
      <i class="fa-solid fa-angle-right"></i>
    </b-button>
    <b-button size="sm"
              variant="light"
              class="p-1 border-right-0 border-left-0 rounded-0 border-secondary shadow-none"
    >
      <i class="fa-solid fa-angle-left"></i>
    </b-button>
    <b-button size="sm"
              variant="light"
              class="p-1 border-right-0 border-left-0 rounded-0 border-secondary shadow-none"
    >
      <i class="fa-solid fa-angles-left"></i>
    </b-button>
    </div>
</template>

<script>
import {mapState} from 'vuex'
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
    instance(newVal){
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
    showHideInstance(){
      this.$emit('showHideInstance', this.instance.instanceMark)
    }
  }
}
</script>

<style scoped>

</style>
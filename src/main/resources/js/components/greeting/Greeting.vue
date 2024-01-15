<template>
<div
    v-if="show"
    :style="{height: `${colHeight}px`}"
>
  <h6>Greeting</h6>
  <h6 v-if="pageAttributes.user">{{pageAttributes.user.username}}</h6>
</div>
</template>

<script>
import {mapState} from "vuex"

export default {
  mounted() {

  },
  components: {

  },
  created() {
    this.fetchData()
  },
  watch: {
    $route: [
      'fetchData',
    ],
  },
  computed: {
    ...mapState([
      'lang',
        'height',
        'pageAttributes',
    ]),
    colHeight() {
      return window.innerHeight - this.height.header - this.height.footer
    },
  },
  data() {
    return {
      show: false,
    }
  },
  methods:{
    fetchData(){
      this.show=false

      this.show = true
    }
  }
}
</script>

<style scoped>

</style>
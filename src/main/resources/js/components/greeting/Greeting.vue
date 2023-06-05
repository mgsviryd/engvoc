<template>
<div v-if="show">
  <h6>Greeting</h6>
  <h6 v-if="pageAttributes.user">{{pageAttributes.user.username}}</h6>
</div>
</template>

<script>
import {mapState} from "vuex";
import documentJS from "../../util/document"

export default {
  mounted() {

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
    ]),
  },
  data() {
    return {
      show: false,
      pageAttributes: null,
    }
  },
  methods:{
    fetchData(){
      this.show=false
      this.pageAttributes = documentJS.getPageAttributes("main", "data")
      this.show = true
    }
  }
}
</script>

<style scoped>

</style>
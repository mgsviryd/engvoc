<template>
  <b-row class="my-0" no-gutters>
    <b-col class="mr-auto px-0" cols="auto">
      <b-button
          class="shadow-none"
          variant="transparent"
          @click.prevent.stop="routerMainPage()"
      >
        <logo-picture></logo-picture>
      </b-button>
    </b-col>
    <b-col>
      <b-button class="float-right shadow-none"
                variant="transparent"
                @click.prevent.stop="close()"
      >
        <i class="fa-solid fa-xmark fa-lg text-muted"></i>
      </b-button>
    </b-col>
  </b-row>
</template>

<script>
import {mapState} from "vuex"
import LogoPicture from "../logo/LogoPicture.vue"

export default {
  props: [],
  mounted() {
  },
  components: {
    LogoPicture,
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
    return {}
  },
  methods: {
    fetchData() {
      this.show = false

      this.show = true
    },
    routerMainPage() {
      this.close()
      this.$router.push({
        path: "/"
      }).then(() => {
      }).catch(err => {
      })
    },
    close() {
      this.$emit('close')
    },
  }
}
</script>

<style scoped>

</style>
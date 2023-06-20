<template>
  <b-row no-gutters class="my-0">
    <b-col>
      <h5>{{ title }}</h5>
    </b-col>
    <b-col>
      <b-button variant="transparent"
                class="float-right shadow-none"
                @click.prevent.stop="close()"
      >
        <i class="fa-solid fa-xmark fa-lg text-muted"></i>
      </b-button>
    </b-col>
  </b-row>
</template>

<script>
import {mapState} from "vuex"

export default {
  props: ['title'],
  mounted() {

  },
  components: {},
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
      'pageAttributes',
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
    close() {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>

</style>
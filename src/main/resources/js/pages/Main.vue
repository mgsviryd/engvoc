<template>
  <div>
    <b-overlay :show="overlay.show" no-wrap rounded="sm">
      <template #overlay>
        <div class="text-center">
          <div class="d-flex justify-content-center">
            <google-circle :heightRem="6" :widthRem="6"></google-circle>
          </div>
        </div>
      </template>
    </b-overlay>

    <router-view v-if="!overlay.show"></router-view>
  </div>
</template>

<script>

import {mapState} from "vuex"
import * as _ from "lodash"
import GoogleCircle from "../components/spinner/GoogleCircle.vue"
import store from "../store/store"

export default {
  components: {
    GoogleCircle,
  },
  async created() {
    this.overlay.show = true
    await store.restored
    await this.$store.dispatch('updateFrontendAction')
    this.$cookies.config('365d')
    this.sync()
    this.overlay.show = false
  },
  computed: {
    ...mapState([
      'lang',
      'frontend',
      'authentication',
    ]),
    main() {
      return document.getElementById("main")
    }
  },
  watch: {
    main(newVal) {
      if (newVal) {
        this.$store.dispatch('setPageAttributesAction', {id: "main", attr: "data"})
      }
    },
    authentication: {
      handler: function () {
        this.$forceNextTick(() => {
          this.watchAuthentication()
        })
      },
      deep: true
    },
  },
  data() {
    return {
      overlay: {
        show: true,
      }
    }
  },
  methods: {
    async sync() {
      let result = false
      await this.sleep(1)
      await this.syncAll()
      while (true) {
        await this.sleep(1000)
        await this.syncAll()
      }
      return result
    },
    sleep(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },
    async syncAll() {
      await this.syncAuthentication()
    },

    async syncAuthentication() {
      await this.$store.dispatch('syncAuthenticationStateWithLocalAction')
    },

    watchAuthentication() {
      this.$store.commit('watchAuthenticationMutation')
    },

    getCapitalizeLang(key) {
      return _.capitalize(this.$t(key))
    },
  },
}
</script>


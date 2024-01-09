<template>
  <div v-if="show">
    <b-overlay
        :show="overlay.show"
        no-fade
        no-wrap
        opacity="1"
        rounded="sm"
        z-index="1500"
    >
      <template #overlay>
        <div class="text-center">
          <div class="d-flex justify-content-center">
            <google-circle :heightRem="6" :widthRem="6"></google-circle>
          </div>
        </div>
      </template>
    </b-overlay>

    <greeting-nav
        :id="ids.header"
        :ref="ids.header"
    ></greeting-nav>

    <router-view v-if="!overlay.show"></router-view>

    <footer-nav
        :id="ids.footer"
        :ref="ids.footer"
    ></footer-nav>
  </div>
</template>

<script>

import {mapState} from "vuex"
import * as _ from "lodash"
import store from "../store/store"
import GoogleCircle from "../components/spinner/GoogleCircle.vue"
import GreetingNav from "../components/greeting/GreetingNav.vue"
import FooterNav from "../components/footer/FooterNav.vue"

export default {
  components: {
    GoogleCircle,
    GreetingNav,
    FooterNav,
  },
  mounted(){
  },
  async created() {
    this.addListeners()
    this.overlay.show = true
    await store.restored
    await this.$store.dispatch('resetAction')
    await this.$store.dispatch('updateFrontendAction')
    this.$cookies.config('365d')
    this.sync()
    await this.setSizeHeaderFooter()
    this.overlay.show = false
  },
  destroyed() {
    this.removeListeners()
  },
  computed: {
    ...mapState([
      'lang',
      'authentication',
    ]),
    main() {
      return document.getElementById("main")
    },
    ids() {
      return {
        id: this.prefixId(),
        footer: 'footer-id',
        header: 'header-id',
        vocabulary: this.prefixId() + 'vocabulary-id',
        vocabularyModal: this.prefixId() + 'vocabulary-modal-id',
      }
    },
  },
  watch: {
    main(newVal) {
      if (newVal) {
        this.$store.dispatch('setPageAttributesAction', {id: "main", attr: "data"})
      }
    },
    authentication: {
      handler: function () {
        this.$nextTick(() => {
          this.watchAuthentication()
        })
      },
      deep: true
    },
  },
  data() {
    return {
      name: 'Main',
      show: true,
      listeners: [],
      overlay: {
        show: true,
      }
    }
  },
  methods: {
    prefixId() {
      return this.name + '-'
    },
    async fetchData(){

    },
    async setSizeHeaderFooter() {
      const heightHeader = document.getElementById(this.ids.header).offsetHeight
      const heightFooter = document.getElementById(this.ids.footer).offsetHeight
      this.$store.commit('setHeightHeaderFooterMutation', {header: heightHeader, footer: heightFooter})
    },
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

    handleResize: _.debounce( function () {
      this.setSizeHeaderFooter()
    },),

    resizeListener(){
      return () => {
        this.handleResize()
      }
    },

    addListeners() {
      const resizeListener = this.resizeListener()
      this.listeners.push({level: window, type: 'resize', listener: resizeListener})
      this.listeners.forEach(pair => {
        pair.level.addEventListener(pair.type, pair.listener)
      })
    },

    removeListeners() {
      this.listeners.forEach(pair => {
        pair.level.removeEventListener(pair.type, pair.listener)
      })
      this.listeners = []
    },

    getCapitalizeLang(key) {
      return _.capitalize(this.$t(key))
    },
  },
}
</script>

<style scoped>

</style>


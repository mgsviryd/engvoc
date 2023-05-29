<template>
  <div>
    <router-view></router-view>
  </div>
</template>
<script>

import {mapState} from 'vuex'

export default {
  components: {

  },
  async created() {
    let frontend = await this.$store.dispatch('getFrontendAction', this.lang.lang.locale)
    this.refreshVersions(frontend)
    this.$store.dispatch('setFrontendAction', frontend)
    this.$store.dispatch('getAuthenticationAction', this.$store.getters.getUsersTokens)
    this.$cookies.config('365d')
    this.sync()
  },
  computed: {
    ...mapState([
      'lang',
        'frontend',
    ]),
  },
  methods: {
    refreshVersions(frontend){
      let version = frontend.version
      let keys = Object.keys(version)
      for (let k in keys) {
        if (this.frontend && this.frontend.version) {
          if (this.needLoad(keys[k], version)) {
            let action = 'get' + keys[k] + 'Action'
            this.$store.dispatch(action)
          }
        } else {
          this.$store.dispatch('get' + keys[k] + 'Action')
        }
      }
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

    needLoad(key, version) {
      return !this.frontend.version[key]
          || this.frontend.version[key] !== version[key]
    },
  },
}
</script>


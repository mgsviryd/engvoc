<template>
  <div>
    <b-row class="my-1 text-secondary justify-content-center">
      <small>{{ getLang('useServices') }}</small>
    </b-row>
    <b-row class="mb-1 justify-content-center">
      <a href="/login/google"
         tabindex="-1"
         :id="prefixId +'google'"
         class="btn btn-outline-danger mx-2"
         @click="onClick()"
      >
        <i class="fab fa-google fa-1x"></i>
      </a>
      <b-tooltip
          variant="danger"
          :target="prefixId +'google'"
          placement="bottomleft"
          :delay="{ show: 500, hide: 100 }"
          triggers="hover"
      >
        {{ getCapitalizeLang('google') }}
      </b-tooltip>
      <a href="/login/facebook"
         tabindex="-1"
         :id="prefixId +'facebook'"
         class="btn btn-outline-primary mx-2"
         @click="onClick()"
      >
        <i class="fab fa-facebook fa-1x"></i>
      </a>
      <b-tooltip
          variant="primary"
          :target="prefixId +'facebook'"
          placement="bottom"
          :delay="{ show: 500, hide: 100 }"
          triggers="hover"
      >
        {{ getCapitalizeLang('facebook') }}
      </b-tooltip>
      <a href="/login/github"
         tabindex="-1"
         :id="prefixId +'github'"
         class="btn btn-outline-dark mx-2"
         @click="onClick()"
      >
        <i class="fab fa-github  fa-1x"></i>
      </a>
      <b-tooltip
          variant="dark"
          :target="prefixId +'github'"
          placement="bottomright"
          :delay="{ show: 500, hide: 100 }"
          triggers="hover"
      >
        {{ getCapitalizeLang('github') }}
      </b-tooltip>
    </b-row>
    <b-row class="mb-2 text-secondary justify-content-center">
      <small>{{ getLang('or') }}</small>
    </b-row>
  </div>
</template>

<script>
import * as _ from "lodash";
import {mapState} from "vuex";

export default {
  props: ['prefixId'],
  computed: {
    ...mapState([
      'lang',
    ]),
  },
  data() {
    return {
      name: "Services",
    }
  },

  methods: {
    onClick() {
      this.$emit('onClick')
    },
    getCapitalize(text) {
      return _.capitalize(text)
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    getUpperCaseLang(key) {
      return _.upperCase(this.getLang(key))
    },
    getLang(key) {
      return this.$t(key)
    },
  }
}
</script>

<style scoped>

</style>
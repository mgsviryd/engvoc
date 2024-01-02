<template>
  <div
      v-if="show"
      :id="ids.id"
      :ref="ids.id"
  >
    <nav
        class="navbar navbar-expand-lg navbar-dark bg-dark py-0 my-0 border-bottom border-secondary justify-content-between"
        style="position: sticky; bottom: 0; width:100%;"
    >
      <b-button-group class="" size="sm">
        <b-button
            class="text-white shadow-none"
            size="sm"
            variant="transparent"
        >
          <i class="fab fa-cc-visa fa-lg"></i>
        </b-button>
        <b-button
            class="text-white shadow-none"
            size="sm"
            variant="transparent"
        >
          <i class="fab fa-cc-mastercard fa-lg"></i>
        </b-button>
      </b-button-group>

      <b-button-group class="mx-4" size="sm">
        <b-button
            class="text-light shadow-none"
            size="sm"
            variant="transparent"
        >
          {{ getCapitalizeLang('confidence') }}
        </b-button>
        <b-button
            class="text-light shadow-none"
            size="sm"
            variant="transparent"
        >
          {{ getCapitalizeLang('conditions') }}
        </b-button>
      </b-button-group>

      <b-button-group class="mx-4" size="sm">
        <span class="text-light"><small>{{ getCapitalizeLang('media') + ':' }}</small></span>
        <b-button
            class="text-primary shadow-none"
            size="sm"
            variant="transparent"
        >
          <i class="fab fa-facebook fa-lg"></i>
        </b-button>
        <b-button
            class="text-warning shadow-none"
            size="sm"
            variant="transparent"
        >
          <i class="fab fa-instagram fa-lg"></i>
        </b-button>
        <b-button
            class="text-danger shadow-none"
            size="sm"
            variant="transparent"
        >
          <i class="fab fa-youtube fa-lg"></i>
        </b-button>

      </b-button-group>
      <b-button-group class="mx-4" size="sm">
        <span class="text-light"><small>{{ getCapitalizeLang('contactUs') + ':' }}</small></span>
        <b-button
            class="text-primary shadow-none"
            size="sm"
            variant="transparent"
        >
          <i class="fab fa-telegram fa-lg"></i>
        </b-button>

        <b-button
            class="text-white shadow-none"
            size="sm"
            variant="transparent"
        >
          <i class="fa-regular fa-envelope fa-lg"></i>
        </b-button>

        <b-button
            class="text-info shadow-none"
            size="sm"
            variant="transparent"
        >
          <i class="fa-regular fa-comment-dots fa-lg"></i>
        </b-button>

      </b-button-group>

      <b-button-group class="" size="sm">
        <b-button
            class="text-white shadow-none border-0"
            disabled
            size="sm"
            variant="transparent"
        >
          <span><small>©</small></span>
          <span><small>{{ getLang('logoStartDate') }}–{{ new Date().getFullYear() }}</small></span>
          <span><small>{{ getUpperCaseLang('logo') }}</small></span>
        </b-button>
      </b-button-group>

    </nav>
  </div>
</template>

<script>
import {mapState, mapGetters} from "vuex"
import * as _ from "lodash"
import LogoPicture from "../logo/LogoPicture.vue"

export default {
  props: [],
  components: {
    LogoPicture,
  },
  created() {

  },
  computed: {
    ...mapState([
      'lang',
      'authentication',
    ]),
    ...mapGetters([
      'isNoAuthentication',
      'isNoUsers',
    ]),
    ids() {
      return {
        id: this.prefixId(),
      }
    }
  },
  watch: {},
  data() {
    return {
      name: 'FooterNav',
      show: true,
    }
  },
  methods: {
    prefixId() {
      return this.name + '-'
    },
    getLang(key) {
      return this.$t(key)
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    getUpperCaseLang(key) {
      return _.upperCase(this.getLang(key))
    },

    routerMainPage() {
      this.$router.push({
        path: "/"
      }).then(() => {
      }).catch(err => {
      })
    },
  }
}
</script>

<style scoped>

</style>
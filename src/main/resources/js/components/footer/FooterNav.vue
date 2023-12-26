<template>
  <div v-if="show">
    <nav
        class="navbar navbar-expand-lg navbar-dark bg-dark py-0 my-0 border-bottom border-secondary justify-content-between"
        style="position: sticky; bottom: 0; width:100%;"
    >
        <b-button-group size="sm">
          <b-button
              variant="transparent"
              size="sm"
              class="text-white shadow-none"
          >
            <i class="fab fa-cc-visa fa-lg"></i>
          </b-button>
          <b-button
              variant="transparent"
              size="sm"
              class="text-white shadow-none"
          >
            <i class="fab fa-cc-mastercard fa-lg"></i>
          </b-button>
        </b-button-group>

        <b-button-group size="sm" class="mx-4">
          <span class="text-light"><small>{{getCapitalizeLang('media')+':'}}</small></span>
          <b-button
              variant="transparent"
              size="sm"
              class="text-primary shadow-none"
          >
            <i class="fab fa-facebook fa-lg"></i>
          </b-button>
          <b-button
              variant="transparent"
              size="sm"
              class="text-warning shadow-none"
          >
            <i class="fab fa-instagram fa-lg"></i>
          </b-button>
          <b-button
              variant="transparent"
              size="sm"
              class="text-danger shadow-none"
          >
            <i class="fab fa-youtube fa-lg"></i>
          </b-button>

        </b-button-group>
        <b-button-group size="sm" class="mx-4">
          <span class="text-light"><small>{{getCapitalizeLang('contactUs')+':'}}</small></span>
          <b-button
              variant="transparent"
              size="sm"
              class="text-primary shadow-none"
          >
            <i class="fab fa-telegram fa-lg"></i>
          </b-button>

          <b-button
              variant="transparent"
              size="sm"
              class="text-white shadow-none"
          >
            <i class="fa-regular fa-envelope fa-lg"></i>
          </b-button>

          <b-button
              variant="transparent"
              size="sm"
              class="text-info shadow-none"
          >
            <i class="fa-regular fa-comment-dots fa-lg"></i>
          </b-button>

        </b-button-group>
        <b-button-group size="sm" class="mx-4">
          <b-button
              variant="transparent"
              size="sm"
              class="text-light shadow-none"
          >
            {{getCapitalizeLang('confidence')}}
          </b-button>
          <b-button
              variant="transparent"
              size="sm"
              class="text-light shadow-none"
          >
            {{getCapitalizeLang('conditions')}}
          </b-button>
        </b-button-group>

      <span class="text-white ml-4">
          <small>
            © {{ getLang('logoStartDate') }}–{{ new Date().getFullYear() }}
          </small>
          <small>
            {{ getUpperCaseLang('logo') }}
          </small>
      </span>
    </nav>
  </div>
</template>

<script>
import {mapState, mapGetters} from "vuex"
import * as _ from "lodash"
import LogoPicture from "../logo/LogoPicture.vue"

export default {
  created() {

  },
  components: {
    LogoPicture,
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
  },
  data() {
    return {
      show: true,
    }
  },
  methods: {
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
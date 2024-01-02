<template>
  <div
      v-if="show"
  >
    <nav
        class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top border-bottom border-secondary d-flex py-1 mb-0"
        style=" width:100%;"
    >
      <b-button-group class="" size="sm">
        <b-button
            class="shadow-none rounded-0 border border-3 border-danger py-1 mr-3"
            style="width: 130px;"
            variant="light"
            @click.prevent.stop="routerMainPage()"
        >
          <logo-picture></logo-picture>
        </b-button>
      </b-button-group>

      <b-button-group class="" size="sm">
        <b-button
            class="mr-1 border-2"
            style="width: 130px;"
            variant="outline-danger"
            @click="routerVocabulary()"
        >
          <small class="text-white">
            <b>
              {{ getCapitalizeLang('vocabulary') }}
            </b>
          </small>
        </b-button>
      </b-button-group>

      <b-button-group class="" size="sm">
        <b-button
            class="mr-1"
            style="width: 80px;"
            variant="transparent"
            @click="">
          <small class="text-white">
            <b>{{ getCapitalizeLang('aboutUs') }}</b>
          </small>
        </b-button>
      </b-button-group>

      <b-button-group class="" size="sm">
        <b-button
            class="mr-1"
            style="width: 80px;"
            variant="transparent"
            @click="">
          <small class="text-white">
            <b>{{ getCapitalizeLang('contacts') }}</b>
          </small>
        </b-button>
      </b-button-group>

      <div class="ml-auto">
        <b-button-group class="mx-2" size="sm">
          <lang-multiselect></lang-multiselect>
        </b-button-group>

        <b-button-group class="mx-1" size="sm">
          <notification-dropdown></notification-dropdown>
        </b-button-group>

        <b-button-group class="" size="">
          <b-button
              class="shadow-none"
              variant="outline-light"
              @click.prevent.stop="$refs.sign.openSignUp()"
          >
            <small>{{ getCapitalizeLang('signUp') }}</small>
          </b-button>
          <b-button
              v-if="isNoUsersGetter()"
              class="border-0 shadow-none"
              variant="outline-light"
              @click.prevent.stop="$refs.sign.openSignIn()"
          >
            <small>{{ getCapitalizeLang('signIn') }}</small>
          </b-button>
          <account-dropdown v-else></account-dropdown>

        </b-button-group>
      </div>

      <sign
          :ref="'sign'"
          :closable="true"
          :show="false"
      ></sign>
    </nav>
  </div>
</template>

<script>
import {mapState, mapGetters} from "vuex"
import * as _ from "lodash"
import LangMultiselect from "../../components/lang/LangMultiselect.vue"
import LogoPicture from "../logo/LogoPicture.vue"
import Sign from "../sign/SignModal.vue"
import AccountDropdown from "../account/AccountDropdown.vue"
import NotificationDropdown from "../account/NotificationDropdown.vue"

export default {
  props: [],
  components: {
    LangMultiselect,
    LogoPicture,
    Sign,
    AccountDropdown,
    NotificationDropdown,
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
    isNoAuthenticationGetter() {
      return this.isNoAuthentication
    }
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
    routerVocabulary() {
      this.$router.push({
        path: "/vocabulary"
      }).then(() => {
      }).catch(err => {
      })
    },
    isNoUsersGetter() {
      return this.isNoUsers
    },
  }
}
</script>

<style scoped>
.border-3 {
  border-width: 3px !important;
}

.border-2 {
  border-width: 3px !important;
}

</style>
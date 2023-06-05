<template>
  <b-modal id="sign"
           ref="sign"
           :header-class="'p-3'"
           :body-class="'py-0'"
           no-fade
           :no-close-on-backdrop="!closable"
           :no-close-on-esc="!closable"
  >
    <template #modal-header="{ close }">
      <b-container fluid>
        <b-row no-gutters v-if="closable" align-v="end" class="my-2">
          <button type="button" class="close" aria-label="Close" @click.prevent.stop="closeModal()">
            <span aria-hidden="true">&times;</span>
          </button>
        </b-row>
        <b-row no-gutters>
          <b-col cols="auto" class="mr-auto">
            <h5 class="text-danger my-0">
              <a type="button" @click.prevent.stop="routerMainPage()">
                {{ getUpperCaseLang('logo') }}
              </a>
            </h5>
          </b-col>
          <b-col cols="auto" class="ml-auto">
            <lang-multiselect></lang-multiselect>
          </b-col>
          <b-col cols="auto" class="ml-auto">
            <account-dropdown></account-dropdown>
          </b-col>
        </b-row>
      </b-container>
    </template>

    <b-row class="px-3">
      <b-col sm="6" class="px-0">
        <b-button
            tabindex="-1"
            block
            variant="light"
            class="rounded-0 border-0 shadow-none bg-white"
            :class="[showSignIn?'collapsed':null, showSignIn?'text-dark':'text-secondary']"
            :aria-expanded="showSignIn ? 'true' : 'false'"
            aria-controls="collapse-signin"
            @click="openSignIn()"
        >
          {{ getCapitalizeLang('signIn') }}
        </b-button>
        <hr class="my-0"
            :class="[showSignIn?'border-secondary':'border-light']"
            style="border-top: 3px solid;"
        >
      </b-col>
      <b-col sm="6" class="px-0">
        <b-button
            tabindex="-1"
            block
            variant="light"
            class="rounded-0 border-0 shadow-none bg-white"
            :class="[showSignUp?'collapsed':null, showSignUp?'text-dark':'text-secondary']"
            :aria-expanded="showSignUp ? 'true' : 'false'"
            aria-controls="collapse-signup"
            @click="openSignUp()"
        >
          {{ getCapitalizeLang('signUp') }}
        </b-button>
        <hr class="my-0"
            :class="[showSignUp?'border-secondary':'border-light']"
            style="border-top: 3px solid;"
        >
      </b-col>
    </b-row>

    <sign-in
        ref="signin"
        :signinId="'collapse-signin'"
        :signinVmodel="showSignIn"
    ></sign-in>

    <sign-up
        ref="signup"
        :signupId="'collapse-signup'"
        :signupVmodel="showSignUp"
    ></sign-up>

    <template #modal-footer>
          <span>
          <small>
            © {{ getLang('logoStartDate') }}–{{ new Date().getFullYear() }}
          </small>
          <small>
            {{ getUpperCaseLang('logo') }}
          </small>
            </span>
    </template>

  </b-modal>
</template>

<script>
import SignIn from "./SignIn.vue"
import SignUp from "./SignUp.vue"
import {mapState} from "vuex";
import LangMultiselect from "../lang/LangMultiselect.vue";
import AccountDropdown from "../../account/AccountDropdown.vue";
import * as _ from "lodash"

export default {
  mounted() {
    this.$refs.sign.show()
  },
  created() {

  },
  props: ['closable'],
  components: {
    SignIn,
    SignUp,
    LangMultiselect,
    AccountDropdown,
  },
  computed: {
    ...mapState([
      'lang',
    ]),
  },
  watch: {
    '$route.params.mark': {
      handler: function (mark) {
        this.$forceNextTick(() => {
          this.showSign(mark)
        })
      },
      immediate: true
    }
  },
  data() {
    return {
      showSignIn: false,
      showSignUp: false,
    }
  },
  methods: {
    closeModal() {
      this.$refs.sign.hide()
      this.$refs.signin.closeSignIn()
      this.$refs.signup.closeSignUp()
    },
    showSign(mark) {
      if (mark === "in") {
        this.openSignIn()
      }
      if (mark === "up") {
        this.openSignUp()
      }
    },
    openSignIn() {
      this.showSignUp = false
      this.showSignIn = true
      this.$refs.signin.openSignIn()
    },
    openSignUp() {
      this.showSignIn = false
      this.showSignUp = true
      this.$refs.signup.openSignUp()
    },
    routerMainPage() {
      this.$router.push({
        path: "/"
      }).then(() => {
      }).catch(err => {
      })
    },
    routeSignIn() {
      this.$router.push({
        path: "/sign/in"
      }).then(() => {
      }).catch(err => {
      })
    },
    routeSignUp() {
      this.$router.push({
        path: "/sign/up"
      }).then(() => {
      }).catch(err => {
      })
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
  },

}

</script>

<style>

</style>
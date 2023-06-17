<template>
    <b-modal id="sign"
             ref="sign"
             :header-class="'p-3'"
             :body-class="'py-0'"
             no-fade
             :no-close-on-backdrop="!closable"
             :no-close-on-esc="!closable"
    >
      <b-overlay no-wrap :show="overlay.showOverlay" rounded="sm">
        <template #overlay>
          <div v-if="overlay.showSignInSpin" class="text-center">
            <div class="d-flex justify-content-center">
              <google-circle :widthRem="3" :heightRem="3"></google-circle>
            </div>
            <p>
              {{ getCapitalizeLang('pleaseWaitTreeDot') }}
            </p>
          </div>
          <div v-if="overlay.showSignInFailure" class="text-center">
            <div class="d-flex justify-content-center">
              <i class="fa-solid fa-triangle-exclamation fa-2x text-danger"></i>
            </div>
            <p>
              {{ getCapitalizeLang('failureOperation') }}
            </p>
          </div>
          <div v-if="overlay.showSignInSuccess">
            <b-card
                header-tag="header"
                header-class="py-0"
                header-bg-variant="success"
                footer-tag="footer"
                footer-class="py-1"
                border-variant="success"
                align="center"
            >
              <template #header>
                <h6 class="py-1">
                  {{ getUpperCaseLang('successOperation') }}
                </h6>
              </template>

              <small>{{ getCapitalizeLang('pleaseWaitLoading') }}&nbsp;{{ getUpperCaseLang('logo') }}</small>

              <template #footer>
                <google-circle :widthRem="1" :heightRem="1"></google-circle>
              </template>
            </b-card>
          </div>

          <div v-if="overlay.showSignUpSpin" class="text-center">
            <div class="d-flex justify-content-center">
              <google-circle :widthRem="3" :heightRem="3"></google-circle>
            </div>
            <p id="cancel-label">
              {{ getCapitalizeLang('pleaseWaitTreeDot') }}
            </p>
          </div>
          <div v-if="overlay.showSignUpFailure" class="text-center">
            <div class="d-flex justify-content-center">
              <i class="fa-solid fa-triangle-exclamation fa-2x text-danger"></i>
            </div>
            <p id="cancel-label">
              {{ getCapitalizeLang('failureOperation') }}
            </p>
          </div>
          <div v-if="overlay.showSignUpSuccess">
            <b-card
                header-tag="header"
                header-class="py-0"
                header-bg-variant="success"
                footer-tag="footer"
                footer-class="py-1"
                border-variant="success"
                align="center"
            >
              <template #header>
                <i class="fa-badge-check text-success"></i>
                <h6 class="py-1">
                  {{ getUpperCaseLang('successOperation') }}
                </h6>
              </template>
              <b-card-text>
                <b-row>
                  <small>{{ getCapitalizeLang('onlyWithActivatedAccountHaveAccessTo') }}&nbsp;{{
                      getUpperCaseLang('logo')
                    }}</small>
                </b-row>
                <hr class="my-0">
                <b-row>
                  <small>{{ getCapitalizeLang('checkEmailToActivateSignUp') }}</small>
                </b-row>
              </b-card-text>
              <b-button
                  variant="outline-primary"
                  size="sm"
                  @click="routerSignIn()"
              >
                {{ getCapitalizeLang('signIn') }}
              </b-button>
              <template #footer>
                <b-button
                    variant="outline-secondary"
                    size="sm"
                    @click="this.$refs.signup.hideSignUpOverlayAndErrorAndFlush()"
                >
                  <small>{{ getCapitalizeLang('hide') }}</small>
                </b-button>
              </template>
            </b-card>
          </div>
        </template>
      </b-overlay>

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
          @showOverlayMethod="showOverlayMethod"
          @showSignInSpinMethod="showSignInSpinMethod"
          @showSignInSuccessMethod="showSignInSuccessMethod"
          @showSignInFailureMethod="showSignInFailureMethod"
      ></sign-in>

      <sign-up
          ref="signup"
          :signupId="'collapse-signup'"
          :signupVmodel="showSignUp"
          @showOverlayMethod="showOverlayMethod"
          @showSignUpSpinMethod="showSignUpSpinMethod"
          @showSignUpSuccessMethod="showSignUpSuccessMethod"
          @showSignUpFailureMethod="showSignUpFailureMethod"
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
import GoogleCircle from "../spinner/GoogleCircle.vue"
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
    GoogleCircle,
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
      overlay: {
        showOverlay: false,
        showSignInSpin: false,
        showSignInSuccess: false,
        showSignInFailure: false,
        showSignUpSpin: false,
        showSignUpSuccess: false,
        showSignUpFailure: false,
      },
      showSignIn: false,
      showSignUp: false,
    }
  },
  methods: {
    routerSignIn() {
      this.$router.push({
        path: "/sign/in"
      }).then(() => {
      }).catch(err => {
      })
    },
    showOverlayMethod(bool) {
      this.overlay.showOverlay = bool
    },
    showSignInSpinMethod(bool) {
      this.overlay.showSignUpSpin = bool
    },
    showSignInSuccessMethod(bool) {
      this.overlay.showSignInSuccess = bool
    },
    showSignInFailureMethod(bool) {
      this.overlay.showSignUpFailure = bool
    },
    showSignUpSpinMethod(bool) {
      console.info(bool)
      this.overlay.showSignUpSpin = bool
    },
    showSignUpSuccessMethod(bool) {
      this.overlay.showSignUpSuccess = bool
    },
    showSignUpFailureMethod(bool) {
      this.overlay.showSignUpFailure = bool
    },
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
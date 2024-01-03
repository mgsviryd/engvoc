<template>
  <b-modal id="sign"
           ref="sign"
           :body-class="'py-0'"
           :header-class="'p-3'"
           :no-close-on-backdrop="!closable"
           :no-close-on-esc="!closable"
           no-fade
           @hide="shown=false"
           @show="shown=true"
  >
    <b-overlay :show="overlay.showOverlay" no-wrap rounded="sm">
      <template #overlay>
        <div v-if="overlay.showSignInSpin" class="text-center">
          <div class="d-flex justify-content-center">
            <google-circle :heightRem="3" :widthRem="3"></google-circle>
          </div>
          <p>
            {{ getCapitalizeLang('pleaseWaitThreeDot') }}
          </p>
        </div>
        <div v-if="overlay.showSignInFailure" class="text-center">
          <div class="d-flex justify-content-center">
            <i class="fa-solid fa-triangle-exclamation fa-2x text-danger"></i>
          </div>
          <p>
            {{ getCapitalizeLang('failure') }}
          </p>
        </div>
        <div v-if="overlay.showSignInSuccess">
          <div class="text-center">
            <div class="d-flex justify-content-center">
              <b-button size="lg" variant="outline-success">
                {{ getUpperCaseLang('accessAllowed') }}
              </b-button>
            </div>
          </div>
        </div>

        <div v-if="overlay.showSignUpSpin" class="text-center">
          <div class="d-flex justify-content-center">
            <google-circle :heightRem="3" :widthRem="3"></google-circle>
          </div>
          <p id="cancel-label">
            {{ getCapitalizeLang('pleaseWaitThreeDot') }}
          </p>
        </div>
        <div v-if="overlay.showSignUpFailure" class="text-center">
          <div class="text-center">
            <div class="d-flex justify-content-center">
              <b-button size="lg" variant="outline-danger">
                {{ getUpperCaseLang('accessDenied') }}
              </b-button>
            </div>
          </div>
        </div>
        <div v-if="overlay.showSignUpSuccess">
          <b-card
              align="center"
              border-variant="success"
              footer-class="py-1"
              footer-tag="footer"
              header-bg-variant="success"
              header-class="py-0"
              header-tag="header"
          >
            <template #header>
              <i class="fa-badge-check text-success"></i>
              <h6 class="py-1">
                {{ getUpperCaseLang('success') }}
              </h6>
            </template>
            <b-card-text>
              <b-row>
                <small>
                  {{ getCapitalizeLang('onlyWithConfirmedEmailHaveAccess') }}&nbsp;{{ getUpperCaseLang('logo') }}
                </small>
              </b-row>
              <hr class="my-0">
              <b-row>
                <small>{{ getCapitalizeLang('checkAndConfirmEmail') }}</small>
              </b-row>
            </b-card-text>
            <template #footer>
              <b-button
                  size="sm"
                  variant="outline-secondary"
                  @click="$refs.signup.hideSignUpOverlayAndErrorAndFlush()"
              >
                <small>{{ getCapitalizeLang('close') }}</small>
              </b-button>
            </template>
          </b-card>
        </div>
      </template>
    </b-overlay>

    <template #modal-header="{ close }">
      <b-container class="px-1" fluid>
        <close-row v-if="closable"
                   :title="''"
                   @close="closeModal()"
        ></close-row>
        <b-row no-gutters>
          <b-col class="mr-auto" cols="auto">
            <button class="btn btn-transparent shadow-none py-1 px-3 mx-1"
                    @click.prevent.stop="routerMainPage()">
              <logo-picture></logo-picture>
            </button>
          </b-col>
          <b-col class="ml-auto" cols="auto">
            <lang-multiselect
                :id="ids.langMultiselect"
                :ref="ids.langMultiselect"
                :data="{value: lang.lang, options: lang.langs}"
                @onSelect="onSelectLang"
            ></lang-multiselect>
          </b-col>
        </b-row>
      </b-container>
    </template>

    <b-row class="px-3">
      <b-col class="px-0" sm="6">
        <b-button
            :aria-expanded="showSignIn ? 'true' : 'false'"
            :class="[showSignIn?'collapsed':null, showSignIn?'text-dark':'text-secondary']"
            aria-controls="collapse-signin"
            block
            class="rounded-0 border-0 shadow-none bg-white"
            tabindex="-1"
            variant="light"
            @click="switchSignIn()"
        >
          {{ getCapitalizeLang('signIn') }}
        </b-button>
        <hr :class="[showSignIn?'border-secondary':'border-light']"
            class="my-0"
            style="border-top: 3px solid;"
        >
      </b-col>
      <b-col class="px-0" sm="6">
        <b-button
            :aria-expanded="showSignUp ? 'true' : 'false'"
            :class="[showSignUp?'collapsed':null, showSignUp?'text-dark':'text-secondary']"
            aria-controls="collapse-signup"
            block
            class="rounded-0 border-0 shadow-none bg-white"
            tabindex="-1"
            variant="light"
            @click="switchSignUp()"
        >
          {{ getCapitalizeLang('signUp') }}
        </b-button>
        <hr :class="[showSignUp?'border-secondary':'border-light']"
            class="my-0"
            style="border-top: 3px solid;"
        >
      </b-col>
    </b-row>

    <sign-in
        ref="signin"
        :signinId="'collapse-signin'"
        :signinVmodel="showSignIn"
        @showOverlayMethod="showOverlayMethod"
        @showSignInFailureMethod="showSignInFailureMethod"
        @showSignInSpinMethod="showSignInSpinMethod"
        @showSignInSuccessMethod="showSignInSuccessMethod"
    ></sign-in>

    <sign-up
        ref="signup"
        :signupId="'collapse-signup'"
        :signupVmodel="showSignUp"
        @showOverlayMethod="showOverlayMethod"
        @showSignUpFailureMethod="showSignUpFailureMethod"
        @showSignUpSpinMethod="showSignUpSpinMethod"
        @showSignUpSuccessMethod="showSignUpSuccessMethod"
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
import GoogleCircle from "../spinner/GoogleCircle.vue"
import LogoPicture from "../logo/LogoPicture.vue"
import CloseRow from "../close/CloseRow.vue"
import * as _ from "lodash"

export default {
  props: [
    'closable',
    'show',
  ],
  components: {
    SignIn,
    SignUp,
    LangMultiselect,
    GoogleCircle,
    LogoPicture,
    CloseRow,
  },
  mounted() {
    this.switchModal(this.show)
  },
  created() {

  },
  computed: {
    ...mapState([
      'lang',
      'authentication',
    ]),
    ids(){
      return{
        id: this.prefixId(),
        langMultiselect: this.prefixId() + 'lang-multiselect-id',
      }
    },
  },
  watch: {
    '$route.params.mark': {
      handler: function (mark) {
        this.$forceNextTick(() => {
          this.showSign(mark)
        })
      },
      immediate: true
    },
    show(newVal) {
      this.switchModal(newVal)
    }
  },
  data() {
    return {
      name: 'SignModal',
      shown: false,
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
    prefixId(){
      return this.name + '-'
    },
    showModal() {
      this.$refs.sign.show()
    },
    hideModal() {
      this.$refs.sign.hide()
    },
    switchModal(show) {
      if (show) {
        this.showModal()
      } else {
        this.hideModal()
      }
    },
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
      if (bool) {
        this.$store.commit('authenticationIsNewMutation', {isNew: true})
        _.delay(() => {
          this.overlay.showSignInSuccess = !bool
          if (this.shown) {
            this.closeModal()
          }
        }, 1500)
      }
    },
    showSignInFailureMethod(bool) {
      this.overlay.showSignUpFailure = bool
    },
    showSignUpSpinMethod(bool) {
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
        this.switchSignIn()
      }
      if (mark === "up") {
        this.switchSignUp()
      }
    },
    switchSignIn() {
      this.openSignIn()
      this.$refs.signin.openSignIn()
    },
    switchSignUp() {
      this.openSignUp()
      this.$refs.signup.openSignUp()
    },
    openSignIn() {
      this.showSignUp = false
      this.showSignIn = true
      this.showModal()
    },
    openSignUp() {
      this.showSignIn = false
      this.showSignUp = true
      this.showModal()
    },
    openSignInWithEmail(email) {
      this.showSignUp = false
      this.showSignIn = true
      this.showModal()
      this.$forceNextTick(() => {
        this.$refs.signin.changeEmail(email)
        this.$refs.signin.focusPassword()
      })
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
    onSelectLang(lang){
      this.$store.dispatch('changeLangAction', lang)
    },
  },

}

</script>

<style>

</style>
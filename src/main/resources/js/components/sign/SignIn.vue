<template>
  <b-collapse v-if="show" @shown="focusEmail()" :id="signinId" v-model="signinVmodel" class="mt-2">
    <b-overlay :show="overlay.showSignInOverlay" rounded="sm">
      <b-card
          body-class="pt-1"
          border-variant="light"
      >

        <services :prefix-id="prefixId()"></services>

        <b-row class="mb-1">
          <b-col sm="12" class="">
            <b-input-group
                label-class="py-0"
                label-cols-sm="3"
                label-cols-lg="3"
                content-cols-sm="7"
                content-cols-lg="7"
                :label-for="properties.email.inputId"
            >
              <b-form-input
                  class="shadow-none rounded-sm"
                  :class="{'border-success':showBorderProperty('email')}"
                  :id="properties.email.inputId"
                  :ref="properties.email.inputId"
                  size="sm"
                  :state="stateEmail()"
                  trim
                  v-model="email"
                  @input="inputProperty($event, 'email')"
                  @focusin.prevent.stop="onFocusinProperty($event, properties.email.inputId, 'email')"
                  @focusout.prevent.stop="onFocusoutProperty($event, properties.email.inputId, 'email')"
                  :placeholder="getCapitalizeLang('email')"
              >
              </b-form-input>
              <div class="invalid-feedback my-0" v-if="properties.email.wasOutFocus">
                <small>{{ emailError() }}</small>
              </div>
            </b-input-group>
            <div class="my-0 text-danger" v-if="properties.email.showError">
              <small v-for="(e,i) in getErrors('email')">{{ getCapitalize(e.message) }}</small>
            </div>
          </b-col>
        </b-row>

        <b-row class="mb-1">
          <b-col sm="12" class="">
            <b-input-group
                label-class="py-0"
                label-cols-sm="3"
                label-cols-lg="3"
                content-cols-sm="7"
                content-cols-lg="7"
                :label-for="properties.password.inputId"
            >
              <b-form-input
                  class="shadow-none rounded-sm"
                  :class="{'border-success':showBorderProperty('password')}"
                  type="password"
                  :id="properties.password.inputId"
                  :ref="properties.password.inputId"
                  size="sm"
                  :state="statePassword()"
                  trim
                  v-model="password"
                  @input="inputProperty($event,'password')"
                  @focusin.prevent.stop="onFocusinProperty($event, properties.password.inputId, 'password')"
                  @focusout.prevent.stop="onFocusoutProperty($event, properties.password.inputId, 'password')"
                  :placeholder="getCapitalizeLang('password')"
              >
              </b-form-input>

              <div class="invalid-feedback my-0" v-if="properties.password.wasOutFocus">
                <small>{{ passwordError() }}</small>
              </div>
            </b-input-group>
            <div class="my-0 text-danger" v-if="properties.password.showError">
              <small v-for="(e,i) in getErrors('password')">{{ getCapitalize(e.message) }}</small>
            </div>
          </b-col>
        </b-row>

        <b-button
            block
            variant="success"
            size="sm"
            :disabled="!stateTrue()"
            :class="!stateTrue()?'no-stateTrue':null"
            @click.prevent.stop="enter()"
        >
          {{ getUpperCaseLang("enter") }}
        </b-button>
      </b-card>

      <b-alert
          class="mx-3 mb-1"
          variant="danger"
          dismissible
          fade
          :show="alert.showInternetConnectionError"
          @dismissed="alert.showInternetConnectionError=false"
      >
        <b-row>
          <small>{{ getCapitalizeLang('noInternetConnection') }}</small>
        </b-row>
      </b-alert>

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
      </template>
    </b-overlay>
  </b-collapse>
</template>

<script>
import {mapState} from "vuex";
import * as _ from "lodash"
import RegexJS from "../../util/regex"
import StringJS from "../../util/string"
import RequestJS from "../../util/request"
import GoogleCircle from "../spinner/GoogleCircle.vue"
import Services from "./Services.vue"

export default {
  mounted() {

  },
  created() {
    Object.assign(this.properties, this.defaultProperties)
  },
  components: {
    GoogleCircle,
    Services,
  },
  props: [
    'signinId',
    'signinVmodel',
  ],
  computed: {
    ...mapState([
      'lang',
    ]),
    defaultProperties() {
      return {
        email: {
          inputId: this.prefixId() + "-" + "emailInputId",
          hasFocus: false,
          wasOutFocus: false,
          showError: false,
          capsLockOn: false,
          noLatinDetect: false,
          showTooltip: false,
          timeoutTooltip: 2000,
        },
        password: {
          inputId: this.prefixId() + "-" + "passwordInputId",
          hasFocus: false,
          wasOutFocus: false,
          showError: false,
          capsLockOn: false,
          noLatinDetect: false,
          showTooltip: false,
          timeoutTooltip: 2000,
        },
      }
    },
  },
  watch: {},
  data() {
    return {
      name: "SignIn",
      show: true,
      errors: [],
      alert: {
        showInternetConnectionError: false,
      },
      overlay: {
        showSignInOverlay: false,
        showSignInSpin: false,
        showSignInSuccess: false,
        showSignInFailure: false,
      },
      email: '',
      password: '',
      properties: {},
    }
  },
  methods: {
    prefixId() {
      return this.name
    },
    inputProperty(event, property) {
      this.properties[property].showError = false
    },
    checkCapsLock(event, property) {
      if (event.getModifierState("CapsLock")) {
        this.properties[property].capsLockOn = true
      } else {
        this.properties[property].capsLockOn = false
      }
    },
    checkWrongChar(event, property) {
      if (this.isSignCharWrong(event.key)) {
        event.preventDefault()
        navigator.vibrate(200);
        this.properties[property].noLatinDetect = true
      } else {
        this.properties[property].noLatinDetect = false
      }
    },

    isSignCharWrong(char) {
      if (RegexJS.hasLatinLetter(char)) return false
      if (RegexJS.hasNumber(char)) return false
      if (RegexJS.hasSpecialCharacter(char)) return false
      return true
    },

    getErrors(property) {
      return this.errors.filter(e => e.attrName === property)
    },

    closeAllAlert() {
      this.alert.showInternetConnectionError = false
    },
    enter() {
      this.closeAllAlert()
      this.overlay.showSignInOverlay = true
      this.overlay.showSignInSpin = true
      if (!window.navigator.onLine) {
        this.alert.showInternetConnectionError = true
        this.overlay.showSignInSpin = false
        this.overlay.showSignInOverlay = false
        return
      }
      this.enterUser()
    },
    enterUser() {
      this.$store.dispatch('enterUserAction',
          RequestJS.getBodyContentTypeUrlencoded({username: this.email, password: this.password})
      ).then((errors) => {
        this.overlay.showSignInSpin = false
        if (errors.length === 0) {
          this.overlay.showSignInSuccess = true
        } else {
          this.errors = errors
          this.showErrors()
          this.overlay.showSignInFailure = true
          _.delay(() => {
            this.overlay.showSignInFailure = false
            this.hideRegisterOverlay()
          }, 1000)
        }
      })
    },
    showErrors() {
      this.errors.forEach(e => {
        try {
          this.properties[e.attrName].showError = true
        } catch (e) {
        }
      })
    },
    hideError() {
      this.properties.email.showError = false
      this.properties.password.showError = false
    },
    hideRegisterOverlay() {
      this.overlay.showSignInSpin = false
      this.overlay.showSignInSuccess = false
      this.overlay.showSignInOverlay = false
    },
    goToSignIn() {
      this.hideRegisterOverlay()
      this.openSignIn()
    },
    hideRegisterOverlayAndErrorAndFlush() {
      this.hideRegisterOverlay()
      this.hideError()
      this.flushSignIn()
    },
    flushSignIn() {
      this.email = ''
      this.password = ''
      this.properties.email.wasOutFocus = false
      this.properties.password.wasOutFocus = false
    },
    openSignIn() {
      this.hideRegisterOverlayAndErrorAndFlush()
      this.focusEmail()
    },
    focusEmail() {
      this.$refs[this.properties.email.inputId].focus();
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
      return this.lang.map[key]
    },
    isBlank(str) {
      return str === null || str === ''
    },
    stateEmail() {
      return !this.isBlank(this.email)
          && RegexJS.isEmail(this.email)
    },
    statePassword() {
      return !this.isBlank(this.password)
          && this.password.length >= 8
          && this.password.length <= 20
          && RegexJS.isSignUpPassword(this.password)
    },

    stateTrue() {
      return this.stateEmail() && this.statePassword()
    },
    emailError() {
      if (this.isBlank(this.email)) {
        return this.getCapitalizeLang("enterEmail")
      }
      if (!RegexJS.isEmail(this.email)) {
        return this.getCapitalizeLang("signUpEmailError")
      }
      return ''
    },
    passwordError() {
      if (this.isBlank(this.password)) {
        return this.getCapitalizeLang("enterPassword")
      }
      if (this.password.length < 8) {
        return this.getCapitalizeLang("signUpPasswordShortLengthError")
      }
      if (this.password.length > 20) {
        return this.getCapitalizeLang("signUpPasswordLongLengthError")
      }
      if (!RegexJS.isSignUpPassword(this.password)) {
        return this.getLang("signUpPasswordSyntaxError")
      }
      return ''
    },

    onFocusinProperty(event, ref, property) {
      this.properties[property].hasFocus = true
    },
    onFocusoutProperty(event, ref, property) {
      this.show = false
      this.properties[property].wasOutFocus = true
      this.properties[property].hasFocus = false
      this.show = true
    },
    breakNewLinesAndGetAsHtml(str) {
      return StringJS.breakNewLinesAndGetAsHtml(str)
    },
    showBorderProperty(property) {
      if (!this.properties[property].wasOutFocus) return true
      if (this.properties[property].hasFocus) return true
      return false
    },
    isSignInPasswordMinCharacters() {
      return this.password.length >= 8
    },
    isSignInPasswordMaxCharacters() {
      return this.password.length <= 20
    },
    isSignInPasswordUppercaseLatinLetter() {
      return RegexJS.hasUppercaseLatinLetter(this.password)
    },
    isSignInPasswordLowercaseLatinLetter() {
      return RegexJS.hasLowercaseLatinLetter(this.password)
    },
    isSignInPasswordNumber() {
      return RegexJS.hasNumber(this.password)
    },
    isSignInPasswordSpecialCharacter() {
      return RegexJS.hasSpecialCharacter(this.password)
    },
  },
}
</script>

<style scoped>
.no-stateTrue {
  cursor: not-allowed;
}
</style>
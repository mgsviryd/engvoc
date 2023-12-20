<template>
  <b-collapse
      v-if="show"
      :id="signinId"
      v-model="signinVmodel"
      class="mt-2"
      @shown="focus()"
  >
    <b-card
        body-class="pt-1"
        border-variant="light"
    >
      <services
          :prefix-id="prefixId()"
          @onClick="showSpinOverlay()"
      ></services>

      <b-row class="mb-1">
        <b-col class="" sm="12">
          <b-input-group
              :label-for="properties.email.inputId"
              content-cols-lg="7"
              content-cols-sm="7"
              label-class="py-0"
              label-cols-lg="3"
              label-cols-sm="3"
          >
            <b-form-input
                :id="properties.email.inputId"
                :ref="properties.email.inputId"
                v-model="email"
                :class="{'border-success':showBorderProperty('email')}"
                :placeholder="getCapitalizeLang('email')"
                :state="stateEmail()"
                class="shadow-none rounded-sm"
                size="sm"
                trim
                @input="inputProperty($event, 'email')"
                @keyup.enter="signIn()"
                @focusin.prevent.stop="onFocusinProperty($event, properties.email.inputId, 'email')"
                @focusout.prevent.stop="onFocusoutProperty($event, properties.email.inputId, 'email')"
            >
            </b-form-input>
            <div v-if="properties.email.wasOutFocus && emailError()" class="invalid-feedback my-0">
              <small>{{ emailError() }}</small>
            </div>
          </b-input-group>
          <div v-if="properties.email.showError" class="my-0 text-danger">
            <small v-for="(e,i) in getErrors('email')">{{ getCapitalize(e.message) }}</small>
          </div>
        </b-col>
      </b-row>

      <b-row class="mb-1">
        <b-col class="" sm="12">
          <b-input-group
              :label-for="properties.password.inputId"
              content-cols-lg="7"
              content-cols-sm="7"
              label-class="py-0"
              label-cols-lg="3"
              label-cols-sm="3"
          >
            <b-form-input
                :id="properties.password.inputId"
                :ref="properties.password.inputId"
                v-model="password"
                :class="{'border-success':showBorderProperty('password')}"
                :placeholder="getCapitalizeLang('password')"
                :state="statePassword()"
                class="shadow-none rounded-sm"
                size="sm"
                trim
                type="password"
                @input="inputProperty($event,'password')"
                @keyup.enter="signIn()"
                @focusin.prevent.stop="onFocusinProperty($event, properties.password.inputId, 'password')"
                @focusout.prevent.stop="onFocusoutProperty($event, properties.password.inputId, 'password')"
            >
            </b-form-input>

            <div v-if="properties.password.wasOutFocus" class="invalid-feedback my-0">
              <small>{{ passwordError() }}</small>
            </div>
          </b-input-group>
          <div v-if="properties.password.showError && passwordError()" class="my-0 text-danger">
            <small v-for="(e,i) in getErrors('password')">{{ getCapitalize(e.message) }}</small>
          </div>
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          <b-link class="float-right mt-0 mb-1"
                  tabindex="-1"
                  @click="routerForgotPassword()"
                  @mousedown="$event.preventDefault()"
          >
            <small>{{ getCapitalizeLang('forgotPassword') }}?</small>
          </b-link>
        </b-col>
      </b-row>

      <b-button
          :class="!stateTrue()?'cursor-not-allowed':null"
          :disabled="!stateTrue()"
          block
          size="sm"
          variant="success"
          @click.prevent.stop="signIn()"
      >
        {{ getUpperCaseLang('signIn') }}
      </b-button>
    </b-card>

    <b-alert
        :show="alert.showInternetConnectionError"
        class="mx-3 mb-1"
        dismissible
        fade
        variant="danger"
        @dismissed="alert.showInternetConnectionError=false"
    >
      <b-row>
        <small>{{ getCapitalizeLang('noInternetConnection') }}</small>
      </b-row>
    </b-alert>
  </b-collapse>
</template>

<script>
import {mapState} from "vuex";
import * as _ from "lodash"
import RegexJS from "../../util/regex"
import RequestJS from "../../util/request"
import Services from "./Services.vue"

export default {
  mounted() {

  },
  created() {
    Object.assign(this.properties, this.defaultProperties)
  },
  components: {
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
          inputId: this.prefixId() + "-" + "email-input-id",
          hasFocus: false,
          wasOutFocus: false,
          showError: false,
          capsLockOn: false,
          noLatinDetect: false,
          showTooltip: false,
          timeoutTooltip: 2000,
        },
        password: {
          inputId: this.prefixId() + "-" + "password-input-id",
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
      updated: false,
      alert: {
        showInternetConnectionError: false,
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

    routerForgotPassword() {
    },

    inputProperty(event, property) {
      this.updated = true
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
      return this.errors.filter(e => e.attribute === property)
    },

    closeAllAlert() {
      this.alert.showInternetConnectionError = false
    },
    signIn() {
      if (this.stateTrue()) {
        this.closeAllAlert()
        this.$emit('showOverlayMethod', true)
        this.$emit('showSignInSpinMethod', true)
        if (!window.navigator.onLine) {
          this.alert.showInternetConnectionError = true
          this.$emit('showSignInSpinMethod', false)
          this.$emit('showOverlayMethod', false)
          return
        }
        this.enterUser()
      }
    },
    enterUser() {
      this.$store.dispatch('signInAction',
          RequestJS.getBodyContentTypeUrlencoded({username: this.email, password: this.password})
      ).then((errors) => {
        this.$emit('showSignInSpinMethod', false)
        if (errors.length === 0) {
          this.$emit('showSignInSuccessMethod', true)
        } else {
          this.errors = errors
          this.showErrors()
          this.$emit('showSignInFailureMethod', true)
          _.delay(() => {
            this.$emit('showSignInFailureMethod', false)
            this.hideSignInOverlay()
          }, 1000)
        }
      })
    },
    showSpinOverlay() {
      this.$emit('showOverlayMethod', true)
      this.$emit('showSignInSpinMethod', true)
    },
    showErrors() {
      this.updated = false
      this.errors.forEach(e => {
        try {
          this.properties[e.attribute].showError = true
        } catch (e) {
        }
      })
    },
    hideErrors() {
      this.properties.email.showError = false
      this.properties.password.showError = false
    },
    hideSignInOverlay() {
      this.$emit('showSignInSpinMethod', false)
      this.$emit('showSignInSuccessMethod', false)
      this.$emit('showOverlayMethod', false)
    },
    goToSignIn() {
      this.hideSignInOverlay()
      this.openSignIn()
    },
    hideSignInOverlayAndErrorAndFlush() {
      this.hideSignInOverlay()
      this.hideErrors()
      this.flushSignIn()
    },
    flushSignIn() {
      this.email = ''
      this.password = ''
      this.properties.email.wasOutFocus = false
      this.properties.password.wasOutFocus = false
    },
    openSignIn() {
      this.hideSignInOverlayAndErrorAndFlush()
    },
    closeSignIn() {
      this.hideSignInOverlayAndErrorAndFlush()
    },
    focus() {
      if (this.isBlank(this.email)) {
        this.focusEmail()
      } else {
        this.focusPassword()
      }
    },
    focusEmail() {
      this.$refs[this.properties.email.inputId].focus();
    },
    focusPassword() {
      this.$refs[this.properties.password.inputId].focus();
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
    isBlank(value) {
      return _.isNil(value) || _.isEmpty(value)
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
      return this.stateEmail() && this.statePassword() && !this.isAnyErrorsShow() && this.updated
    },
    isAnyErrorsShow() {
      let any = false
      Object.keys(this.properties).forEach(p => {
        if (this.properties[p].showError) {
          any = true
        }
      })
      return any
    },
    emailError() {
      if (this.isBlank(this.email)) return ''
      if (!RegexJS.isEmail(this.email)) {
        return this.getCapitalizeLang('signUpEmailError')
      }
      return ''
    },
    passwordError() {
      if (this.isBlank(this.password)) return ''
      if (this.password.length < 8) {
        return this.getCapitalizeLang('signUpPasswordShortLengthError')
      }
      if (this.password.length > 20) {
        return this.getCapitalizeLang('signUpPasswordLongLengthError')
      }
      if (!RegexJS.isSignUpPassword(this.password)) {
        return this.getLang('signUpPasswordSyntaxError')
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
    showBorderProperty(property) {
      if (!this.properties[property].wasOutFocus) return true
      if (this.properties[property].hasFocus) return true
      return false
    },
    changeEmail(email) {
      this.email = email
    },
  },
}
</script>

<style scoped>
.cursor-not-allowed {
  cursor: not-allowed;
}
</style>
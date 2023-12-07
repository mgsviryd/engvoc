<template>
  <b-collapse v-if="show" @shown="focusEmail()" :id="signupId" v-model="signupVmodel" class="mt-2">
      <b-card
          body-class="pt-1"
          border-variant="light"
      >

        <services
            :prefix-id="prefixId()"
            @onClick="showSpinOverlay()"
        ></services>

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
                  @keyup.enter="signUp()"
                  @focusin="onFocusinProperty('email')"
                  @focusout="onFocusoutProperty( 'email')"
                  @input="inputProperty('email')"
                  :placeholder="getCapitalizeLang('email')"
              >
              </b-form-input>
              <div class="invalid-feedback my-0" v-if="properties.email.wasOutFocus && emailError()">
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
                  @keyup.enter="signUp()"
                  @focusin="onFocusinProperty('password')"
                  @focusout="onFocusoutProperty( 'password')"
                  @input="inputProperty('password')"
                  :placeholder="getCapitalizeLang('password')"
              >
              </b-form-input>
              <div class="invalid-feedback my-0" v-if="properties.password.wasOutFocus && passwordError()">
                <small>{{ passwordError() }}</small>
              </div>
            </b-input-group>
            <div class="my-0 text-danger" v-if="properties.password.showError">
              <small v-for="(e,i) in getErrors('password')">{{ getCapitalize(e.message) }}</small>
            </div>
          </b-col>
        </b-row>

        <b-row class="">
          <b-col sm="12" class="">
            <small
                :class="isSignUpPasswordMinCharacters()?'text-success':'text-muted'">{{
                getLang('signUpPasswordMinCharacters')
              }}</small>
          </b-col>
        </b-row>
        <b-row class="">
          <b-col sm="12" class="">
            <small
                :class="isSignUpPasswordMaxCharacters()?'text-success':'text-muted'">{{
                getLang('signUpPasswordMaxCharacters')
              }}</small>
          </b-col>
        </b-row>
        <b-row class="">
          <b-col sm="12" class="">
            <small
                :class="isSignUpPasswordUppercaseLatinLetter()?'text-success':'text-muted'">{{
                getLang('signUpPasswordUppercaseLetter')
              }}</small>
          </b-col>
        </b-row>
        <b-row class="">
          <b-col sm="12" class="">
            <small
                :class="isSignUpPasswordLowercaseLatinLetter()?'text-success':'text-muted'">{{
                getLang('signUpPasswordLowercaseLetter')
              }}</small>
          </b-col>
        </b-row>
        <b-row class="">
          <b-col sm="12" class="">
            <small
                :class="isSignUpPasswordNumber()?'text-success':'text-muted'">{{
                getLang('signUpPasswordNumber')
              }}</small>
          </b-col>
        </b-row>
        <b-row class="mb-1">
          <b-col sm="12" class="">
            <small
                :class="isSignUpPasswordSpecialCharacter()?'text-success':'text-muted'">{{
                getLang('signUpPasswordSpecialCharacter')
              }}</small>
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
                :label-for="properties.passwordRepeat.inputId"
            >
              <b-form-input
                  class="shadow-none rounded-sm"
                  :class="{'border-success':showBorderProperty('passwordRepeat')}"
                  type="password"
                  :id="properties.passwordRepeat.inputId"
                  :ref="properties.passwordRepeat.inputId"
                  size="sm"
                  :state="statePasswordRepeat()"
                  trim
                  v-model="passwordRepeat"
                  @keyup.enter="signUp()"
                  @focusin="onFocusinProperty('passwordRepeat')"
                  @focusout="onFocusoutProperty('passwordRepeat')"
                  @input="inputProperty('passwordRepeat')"
                  :placeholder="getCapitalizeLang('passwordRepeat')"
              >
              </b-form-input>
              <div class="invalid-feedback my-0" v-if="properties.passwordRepeat.wasOutFocus && passwordRepeatError()">
                <small>{{ passwordRepeatError() }}</small>
              </div>
            </b-input-group>
            <div class="my-0 text-danger" v-if="properties.passwordRepeat.showError">
              <small v-for="(e,i) in getErrors('passwordRepeat')">{{ getCapitalize(e.message) }}</small>
            </div>
          </b-col>
        </b-row>

        <b-button
            :id="properties.recaptcha.buttonId"
            block
            variant="success"
            size="sm"
            :disabled="!stateTrue()"
            :class="!stateTrue()?'no-stateTrue':null"
            @click.prevent.stop="signUp()"
        >
          {{ getUpperCaseLang('signUpDo') }}
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

      <b-alert
          class="mx-3 mb-1"
          variant="danger"
          dismissible
          fade
          :show="alert.showRecaptchaResponseError"
          @dismissed="alert.showRecaptchaResponseError=false"
      >
        <b-row>
          <small>{{ getUpperCaseLang('externalServerError') }}</small>
        </b-row>
        <b-row>
          <small>{{ getCapitalizeLang('cannotGetAnswerFrom') }}&nbsp;{{ getLang('googleRecaptcha') }} </small>
        </b-row>
        <hr class="my-0">
        <b-row>
          <small>{{ getCapitalizeLang('tryToReloadPage') }}</small>
        </b-row>
        <b-row>
          <small>{{ getCapitalizeLang('checkInternetConnection') }}</small>
        </b-row>
      </b-alert>

      <b-alert
          class="mx-3 mb-1"
          variant="danger"
          dismissible
          fade
          :show="properties.recaptcha.showError"
          @dismissed="properties.recaptcha.showError=false"
      >
        <template v-for="(e,i) in getErrors('recaptcha')">
          <b-row>
            {{ getCapitalize(e.message) }}
          </b-row>
          <hr class="my-0">
        </template>
      </b-alert>
  </b-collapse>

</template>

<script>
import {mapState} from "vuex";
import * as _ from "lodash"
import RegexJS from "../../util/regex"
import GoogleCircle from "../spinner/GoogleCircle.vue"
import Services from "./Services.vue"

export default {
  mounted() {

  },
  created() {
    Object.assign(this.properties, this.defaultProperties)
    this.$forceNextTick(() => {
      this.focusEmail()
    })
  },
  components: {
    GoogleCircle,
    Services,
  },
  props: [
    'signupId',
    'signupVmodel',
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
        },
        password: {
          inputId: this.prefixId() + "-" + "passwordInputId",
          hasFocus: false,
          wasOutFocus: false,
          showError: false,
        },
        passwordRepeat: {
          inputId: this.prefixId() + "-" + "passwordRepeatInputId",
          hasFocus: false,
          wasOutFocus: false,
          showError: false,
        },
        recaptcha: {
          buttonId: this.prefixId() + "-" + "recaptchaButtonId",
          showError: false,
        },
      }
    },
  },
  watch: {
    recaptchaResponse(newVal) {
      this.signUpUser()
    },
  },
  data() {
    return {
      name: "SignUp",
      show: true,
      errors: [],
      recaptchaSitekey: '6Lf-SzQmAAAAAIaMwoFGYAJFmZVG0n7TH8zK_Cq4',
      alert: {
        showRecaptchaResponseError: false,
        showInternetConnectionError: false,
      },
      recaptchaWait: 3000,
      recaptchaWidgetId: 0,
      email: '',
      password: '',
      passwordRepeat: '',
      recaptchaResponse: '',
      properties: {},
    }
  },
  methods: {
    prefixId() {
      return this.name
    },

    inputProperty(property) {
      this.properties[property].showError = false
    },

    getErrors(property) {
      return this.errors.filter(e => e.attribute === property)
    },

    recaptchaReset() {
      grecaptcha.reset(this.recaptchaWidgetId)
    },
    recaptchaExecute() {
      grecaptcha.execute(this.recaptchaWidgetId)
    },
    processRecaptchaResponse(response) {
      this.recaptchaResponse = response
    },
    recaptchaValidate() {
      if ($('#' + this.properties.recaptcha.buttonId).length) {
        try {
          this.recaptchaWidgetId = grecaptcha.render(this.properties.recaptcha.buttonId, {
            'sitekey': this.recaptchaSitekey,
            callback: (response) => {
              this.processRecaptchaResponse(response)
              this.recaptchaReset()
            }
          });
        } catch (error) {
        }
      }
    },
    closeAllAlert() {
      this.alert.showInternetConnectionError = false
      this.alert.showRecaptchaResponseError = false
    },
    showSpinOverlay() {
      this.$emit('showOverlayMethod', true)
      this.$emit('showSignUpSpinMethod', true)
    },
    signUp() {
      if(this.stateTrue()) {
        this.closeAllAlert()
        this.$emit('showOverlayMethod', true)
        this.$emit('showSignUpSpinMethod', true)
        if (!window.navigator.onLine) {
          this.alert.showInternetConnectionError = true
          this.$emit('showSignUpSpinMethod', false)
          this.$emit('showOverlayMethod', false)
          return
        }
        this.recaptchaValidate()
        this.recaptchaExecute()
      }
    },
    signUpUser() {
      // console.info("recaptcha: " + this.recaptchaResponse)
      if (this.isBlank(this.recaptchaResponse)) {
        this.alert.showRecaptchaResponseError = true
        this.$emit('showSignUpSpinMethod', false)
        this.$emit('showOverlayMethod', false)
        return
      }
      this.alert.showRecaptchaResponseError = false
      this.$store.dispatch('signUpUserAction',
          {
            email: this.email,
            password: this.password,
            passwordRepeat: this.passwordRepeat,
            recaptchaResponse: this.recaptchaResponse,
          }).then((errors) => {
        this.$emit('showSignUpSpinMethod', false)
        if (errors.length === 0) {
          this.$emit('showSignUpSuccessMethod', true)
        } else {
          this.errors = errors
          this.showErrors()
          this.$emit('showSignUpFailureMethod', true)
          _.delay(() => {
            this.$emit('showSignUpFailureMethod', false)
            this.hideSignUpOverlay()
          }, 1000)
        }
      })
    },
    showErrors() {
      this.errors.forEach(e => {
        try {
          this.properties[e.attribute].showError = true
        } catch (e) {
        }
      })
    },
    hideError() {
      this.properties.email.showError = false
      this.properties.password.showError = false
      this.properties.passwordRepeat.showError = false
      this.properties.recaptcha.showError = false
    },
    hideSignUpOverlay() {
      this.$emit('showSignUpSpinMethod', false)
      this.$emit('showSignUpSuccessMethod', false)
      this.$emit('showOverlayMethod', false)
    },
    routerSignIn() {
      this.$router.push({
        path: "/sign/in"
      }).then(() => {
      }).catch(err => {
      })
    },
    hideSignUpOverlayAndErrorAndFlush() {
      this.hideSignUpOverlay()
      this.hideError()
      this.flushSignUp()
    },
    flushSignUp() {
      this.email = ''
      this.password = ''
      this.passwordRepeat = ''
      this.properties.email.wasOutFocus = false
      this.properties.password.wasOutFocus = false
      this.properties.passwordRepeat.wasOutFocus = false
    },
    openSignUp() {
      this.hideSignUpOverlayAndErrorAndFlush()
      this.focusEmail()
    },
    closeSignUp() {
      this.hideSignUpOverlayAndErrorAndFlush()
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
    statePasswordRepeat() {
      return !this.isBlank(this.passwordRepeat)
          && this.password === this.passwordRepeat
    },
    stateTrue() {
      return this.stateEmail() && this.statePassword() && this.statePasswordRepeat() && !this.isAnyErrorsShow()
    },
    isAnyErrorsShow(){
      let any = false
      Object.keys(this.properties).forEach(p => {
        if (this.properties[p].showError){
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
    passwordRepeatError() {
      if (this.isBlank(this.passwordRepeat)) return ''
      if (this.password !== this.passwordRepeat) {
        return this.getLang('signUpPasswordRepeatError')
      }
      return ''
    },
    onFocusinProperty(property) {
      this.properties[property].hasFocus = true
    },
    onFocusoutProperty(property) {
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
    isSignUpPasswordMinCharacters() {
      return this.password.length >= 8
    },
    isSignUpPasswordMaxCharacters() {
      return this.password.length <= 20
    },
    isSignUpPasswordUppercaseLatinLetter() {
      return RegexJS.hasUppercaseLatinLetter(this.password)
    },
    isSignUpPasswordLowercaseLatinLetter() {
      return RegexJS.hasLowercaseLatinLetter(this.password)
    },
    isSignUpPasswordNumber() {
      return RegexJS.hasNumber(this.password)
    },
    isSignUpPasswordSpecialCharacter() {
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
<template>
  <b-collapse v-if="show" :id="signupId" v-model="signupVmodel" class="mt-2" @shown="focusEmail()">
    <b-card
        body-class="pt-1"
        border-variant="light"
    >

      <services
          :prefix-id="prefixId()"
          @onClick="showSpinOverlay()"
      ></services>
      <b-form @submit.prevent="signUp">
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
                  required
                  size="sm"
                  trim
                  @focusin="onFocusinProperty('email')"
                  @focusout="onFocusoutProperty( 'email')"
                  @input="inputProperty('email')"
                  @keyup.enter="signUp()"
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
                  :formatter="formatPassword"
                  :placeholder="getCapitalizeLang('password')"
                  :state="statePassword()"
                  :type="showPassword?'text':'password'"
                  autocomplete="off"
                  class="shadow-none rounded-sm"
                  required
                  size="sm"
                  trim
                  @focusin="onFocusinProperty('password')"
                  @focusout="onFocusoutProperty( 'password')"
                  @input="inputProperty('password')"
                  @keyup.enter="signUp()"
              >
              </b-form-input>
              <div class="d-flex justify-content-center align-self-center" style="width: 30px">
                <i v-if="showPassword"
                   class="fa-regular fa-eye text-primary"
                   @click.prevent.stop="showPassword=!showPassword"></i>
                <i v-else
                   class="fa-regular fa-eye-slash text-muted"
                   @click.prevent.stop="showPassword=!showPassword"></i>
              </div>
              <div v-if="properties.password.wasOutFocus && passwordError()" class="invalid-feedback my-0">
                <small>{{ passwordError() }}</small>
              </div>
            </b-input-group>
            <div v-if="properties.password.showError" class="my-0 text-danger">
              <small v-for="(e,i) in getErrors('password')">{{ getCapitalize(e.message) }}</small>
            </div>
          </b-col>
        </b-row>

        <b-row class="">
          <b-col class="" sm="12">
            <small v-if="isSignUpPasswordMaxCharacters()">
              <i class="fa-regular fa-circle-check text-success mr-2"></i>
              <span class="text-muted">{{ getLang('signUpPasswordMaxCharacters') }}</span>
            </small>
            <small v-else>
              <i class="fa-solid fa-circle-minus fa-xs text-danger mr-2"></i>
              <span class="text-muted">{{ getLang('signUpPasswordMaxCharacters') }}</span>
            </small>
          </b-col>
        </b-row>
        <b-row class="">
          <b-col class="" sm="12">
            <small v-if="isSignUpPasswordMinCharacters()">
              <i class="fa-regular fa-circle-check text-success mr-2"></i>
              <span class="text-muted">{{ getLang('signUpPasswordMinCharacters') }}</span>
            </small>
            <small v-else>
              <i class="fa-solid fa-circle-minus fa-xs text-danger mr-2"></i>
              <span class="text-muted">{{ getLang('signUpPasswordMinCharacters') }}</span>
            </small>
          </b-col>
        </b-row>
        <b-row class="">
          <b-col class="" sm="12">
            <small v-if="isSignUpPasswordUppercaseLatinLetter()">
              <i class="fa-regular fa-circle-check text-success mr-2"></i>
              <span class="text-muted">{{ getLang('signUpPasswordUppercaseLetter') }}</span>
            </small>
            <small v-else>
              <i class="fa-solid fa-circle-minus fa-xs text-danger mr-2"></i>
              <span class="text-muted">{{ getLang('signUpPasswordUppercaseLetter') }}</span>
            </small>
          </b-col>
        </b-row>
        <b-row class="">
          <b-col class="" sm="12">
            <small v-if="isSignUpPasswordLowercaseLatinLetter()">
              <i class="fa-regular fa-circle-check text-success mr-2"></i>
              <span class="text-muted">{{ getLang('signUpPasswordLowercaseLetter') }}</span>
            </small>
            <small v-else>
              <i class="fa-solid fa-circle-minus fa-xs text-danger mr-2"></i>
              <span class="text-muted">{{ getLang('signUpPasswordLowercaseLetter') }}</span>
            </small>
          </b-col>
        </b-row>
        <b-row class="">
          <b-col class="" sm="12">
            <small v-if="isSignUpPasswordNumber()">
              <i class="fa-regular fa-circle-check text-success mr-2"></i>
              <span class="text-muted">{{ getLang('signUpPasswordNumber') }}</span>
            </small>
            <small v-else>
              <i class="fa-solid fa-circle-minus fa-xs text-danger mr-2"></i>
              <span class="text-muted">{{ getLang('signUpPasswordNumber') }}</span>
            </small>
          </b-col>
        </b-row>
        <b-row class="mb-1">
          <b-col class="" sm="12">
            <small v-if="isSignUpPasswordSpecialCharacter()">
              <i class="fa-regular fa-circle-check text-success mr-2"></i>
              <span class="text-muted"> {{ getLang('signUpPasswordSpecialCharacter') }}</span>
            </small>
            <small v-else>
              <i class="fa-solid fa-circle-minus fa-xs text-danger mr-2"></i>
              <span class="text-muted"> {{ getLang('signUpPasswordSpecialCharacter') }}</span>
            </small>
          </b-col>
        </b-row>


        <b-row class="mb-1">
          <b-col class="" sm="12">
            <b-input-group
                :label-for="properties.passwordRepeat.inputId"
                content-cols-lg="7"
                content-cols-sm="7"
                label-class="py-0"
                label-cols-lg="3"
                label-cols-sm="3"
            >
              <b-form-input
                  :id="properties.passwordRepeat.inputId"
                  :ref="properties.passwordRepeat.inputId"
                  v-model="passwordRepeat"
                  :class="{'border-success':showBorderProperty('passwordRepeat')}"
                  :formatter="formatPassword"
                  :placeholder="getCapitalizeLang('passwordRepeat')"
                  :state="statePasswordRepeat()"
                  :type="showPassword?'text':'password'"
                  autocomplete="off"
                  class="shadow-none rounded-sm"
                  required
                  size="sm"
                  trim
                  @focusin="onFocusinProperty('passwordRepeat')"
                  @focusout="onFocusoutProperty('passwordRepeat')"
                  @input="inputProperty('passwordRepeat')"
                  @keyup.enter="signUp()"
              >
              </b-form-input>
              <div v-if="properties.passwordRepeat.wasOutFocus && passwordRepeatError()" class="invalid-feedback my-0">
                <small>{{ passwordRepeatError() }}</small>
              </div>
            </b-input-group>
            <div v-if="properties.passwordRepeat.showError" class="my-0 text-danger">
              <small v-for="(e,i) in getErrors('passwordRepeat')">{{ getCapitalize(e.message) }}</small>
            </div>
          </b-col>
        </b-row>

        <b-button
            :id="properties.recaptcha.buttonId"
            :class="!stateTrue()?'no-cursor':null"
            :disabled="!stateTrue()"
            block
            size="sm"
            type="submit"
            variant="success"
        >
          {{ getUpperCaseLang('signUpDo') }}
        </b-button>
      </b-form>
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

    <b-alert
        :show="alert.showRecaptchaResponseError"
        class="mx-3 mb-1"
        dismissible
        fade
        variant="danger"
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
        :show="properties.recaptcha.showError"
        class="mx-3 mb-1"
        dismissible
        fade
        variant="danger"
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
    this.fetchData()
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
      show: false,
      showPassword: false,
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
    fetchData() {
      this.show = false

      this.show = true
    },
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
      if (this.stateTrue()) {
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
    formatPassword(input) {
      return String(input).substring(0, 20)
    },
  },
}
</script>

<style scoped>
.no-cursor {
  cursor: not-allowed;
}

</style>
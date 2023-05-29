<template>
  <b-modal id="sign"
           ref="sign"
           :header-class="'p-3'"
           :body-class="'py-0'"
           no-fade
           no-close-on-backdrop
           no-close-on-esc
  >
    <template #modal-header="{ close }">
      <b-container fluid>
      <b-row no-gutters align-v="start">
        <b-col cols="10">
          <h5 class="text-danger my-0">
            <a type="button" @click.prevent.stop="routerMainPage()">
            {{ getUpperCaseLang("logo") }}
            </a>
          </h5>
        </b-col>
        <b-col cols="2">
         <lang-multiselect></lang-multiselect>
        </b-col>
      </b-row>
      </b-container>
    </template>

    <b-row class="px-3">
      <b-col sm="6" class="px-0">
        <b-button
            block
            variant="light"
            class="rounded-0 border-0 shadow-none bg-white"
            :class="[showSignIn?'collapsed':null, showSignIn?'text-dark':'text-secondary']"
            :aria-expanded="showSignIn ? 'true' : 'false'"
            aria-controls="collapse-signin"
            @click="routeSignIn()"
        >
          {{ getCapitalizeLang("signIn") }}
        </b-button>
        <hr class="my-0"
            :class="[showSignIn?'border-secondary':'border-light']"
            style="border-top: 3px solid;"
        >
      </b-col>
      <b-col sm="6" class="px-0">
        <b-button
            block
            variant="light"
            class="rounded-0 border-0 shadow-none bg-white"
            :class="[showSignUp?'collapsed':null, showSignUp?'text-dark':'text-secondary']"
            :aria-expanded="showSignUp ? 'true' : 'false'"
            aria-controls="collapse-signup"
            @click="routeSignUp()"
        >
          {{ getCapitalizeLang("signUp") }}
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
            © {{ getLang("logoStartDate") }}–{{ new Date().getFullYear() }}
          </small>
          <small>
            {{ getUpperCaseLang("logo") }}
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

export default {
  mounted() {
    this.$refs.sign.show()
  },
  created(){

  },
  components: {
    SignIn,
    SignUp,
    LangMultiselect,
  },
  computed: {
    ...mapState([
      'lang',
    ]),
  },
  watch: {
    '$route.params.mark': {
      handler: function(mark) {
        this.$forceNextTick(() => {
          this.showSign(mark)
        })
      },
      immediate: true
    }
    // $route: [
    //   'fetchData',
    // ],
  },
  data(){
    return{
      showSignIn: false,
      showSignUp: false,
    }
  },
  methods:{
    showSign(mark){
      if (mark === "in"){
        this.openSignIn()
      }
      if (mark === "up"){
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
    routerMainPage(){
      this.$router.push({
        path: "/"
      }).then(() => {
      }).catch(err => {
      })
    },
    routeSignIn(){
      this.$router.push({
        path: "/sign/in"
      }).then(() => {
      }).catch(err => {
      })
    },
    routeSignUp(){
      this.$router.push({
        path: "/sign/up"
      }).then(() => {
      }).catch(err => {
      })
    },
    getCapitalize(text){
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
  },

}

</script>

<style>

</style>
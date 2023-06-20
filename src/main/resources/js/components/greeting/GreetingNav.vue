<template>
  <div v-if="show">
    <nav
        class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top py-1 mb-0 shadow border-bottom border-secondary"
        style=" width:100%;"
    >
      <b-button-group class="btn-group-justified">
        <b-button variant="light"
                  class="shadow-none pb-1 pt-0 mx-1"
                  @click.prevent.stop="routerMainPage()"
                  style="width: 130px;"
        >
          <logo-picture></logo-picture>
        </b-button>

        <b-button variant="light"
                  class="mr-1" v-bind:class="{ active: isButtonActive1}"
                  href="#tab1" data-toggle="tab" role="tab" aria-controls="tab1"
                  id="button1"
                  aria-selected="false"
                  @click="activateLevel1()"
                  style="width: 130px;">
          <small>
            <b>
              {{ getCapitalizeLang('contacts') }}
            </b>
          </small>
        </b-button>
      </b-button-group>
      <b-button-group>
        <b-button variant="danger"
                  class="mr-1" v-bind:class="{ active: isButtonActive2}"
                  @click="routerVocabulary()"
                  style="width: 130px;">
          <small>
            <b>
              {{ getCapitalizeLang('vocabulary') }}
            </b>
          </small>
        </b-button>
      </b-button-group>

      <b-button-group class="mx-2">
        <lang-multiselect></lang-multiselect>
      </b-button-group>

      <div class="float-right">
        <b-button variant="outline-light" class="shadow-none"
                  @click.prevent.stop="$refs.sign.openSignUp()"
        >
          <small>{{ getCapitalizeLang('signUp') }}</small>
        </b-button>
        <b-button v-if="isNoUsersGetter()" variant="outline-light" class="border-0 shadow-none"
                  @click.prevent.stop="$refs.sign.openSignIn()"
        >
          <small>{{ getCapitalizeLang('signIn') }}</small>
        </b-button>
        <account-dropdown v-else></account-dropdown>
      </div>
      <sign
          ref="sign"
          :closable="true"
          :show="false"
      ></sign>
    </nav>
    <div class="tab-content" id="tab-content-0 d-inline-block" style="width: 100%">
      <div class="tab-pane fade bg-light border-1 border-secondary" style="width: 100%"
           v-bind:class="{ active: isButtonActive1, show: isButtonActive1}"
           id="tab1"
           role="tabpanel" aria-labelledby="...">
      </div>
    </div>
  </div>
</template>

<script>
import {mapState, mapGetters} from "vuex"
import LangMultiselect from '../../components/lang/LangMultiselect.vue'
import LogoPicture from "../logo/LogoPicture.vue"
import * as _ from "lodash"
import Sign from '../sign/SignModal.vue'
import AccountDropdown from "../account/AccountDropdown.vue"

export default {
  created() {

  },
  components: {
    LangMultiselect,
    LogoPicture,
    Sign,
    AccountDropdown,
  },
  computed: {
    ...mapState([
      'lang',
      'frontend',
      'authentication',
    ]),
    ...mapGetters([
      'isNoAuthentication',
        'isNoUsers',
    ]),
    isNoAuthenticationGetter(){
      return this.isNoAuthentication
    }
  },
  data() {
    return {
      show: true,
      isButtonActive1: false,
      isButtonActive2: false,
      isButtonActive3: false,
      isButtonActive4: false,
      isButtonActive5: false,
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
    activateLevel1() {
      this.disactiveAll()
      this.isButtonActive1 = true
    },
    activateLevel2() {
      this.disactiveAll()
      this.isButtonActive2 = true
    },
    activateLevel3() {
      this.disactiveAll()
      this.isButtonActive3 = true
    },
    activateLevel4() {
      this.disactiveAll()
      this.isButtonActive4 = true
    },
    activateLevel5() {
      this.disactiveAll()
      this.isButtonActive5 = true
    },
    disactiveAll() {
      this.isButtonActive1 = false
      this.isButtonActive2 = false
      this.isButtonActive3 = false
      this.isButtonActive4 = false
      this.isButtonActive5 = false
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
    }
  }
}
</script>

<style scoped>

</style>
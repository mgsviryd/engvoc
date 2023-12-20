<template>
  <b-dropdown
      v-if="show"
      :id="prefixId()"
      :ref="prefixId()"
      class="border-0 shadow-none"
      :class="isNoUserGetter?'bg-transparent':'bg-white'"
      toggle-class="shadow-none rounded-sm px-1"
      variant="transparent"
      no-caret
      dropleft
      @hide="shown=false"
      @show="shown=true"
  >
    <b-popover :target="prefixId()"
               :title="username"
               :show.sync="isPopover"
               placement="bottomleft"
               variant="success"
    >
      {{getCapitalizeLang('welcomeTo')+ ' '}}{{getUpperCaseLang('logo')}}
    </b-popover>
    <template #button-content>
      <i class="fa-solid fa-user"
         :class="isNoUserGetter?'text-white':'text-success'"></i>
      <i v-if="shown" class="fa-sharp fa-solid fa-xs fa-caret-up"
         :class="isNoUserGetter?'text-white':'text-success'"></i>
      <i v-else class="fa-sharp fa-solid fa-xs fa-caret-down"
         :class="isNoUserGetter?'text-white':'text-success'"></i>
    </template>

    <b-dropdown-group v-if="!isNoUserGetter" :header="getCapitalizeLang('account')">
      <b-dropdown-text>
        {{ username }}
        <b-button
            variant="outline-secondary"
            href="/sign/logout"
        >
          <i class="fa-solid fa-power-off text-danger"></i>
          {{ getCapitalizeLang('logout') }}
        </b-button>
      </b-dropdown-text>
    </b-dropdown-group>
    <b-dropdown-divider v-if="!isNoUserGetter"></b-dropdown-divider>
    <b-dropdown-group v-if="!isNoUserGetter" :header="getCapitalizeLang('settings')">
      <b-dropdown-item-button>First Grouped item</b-dropdown-item-button>
      <b-dropdown-item-button>Second Grouped Item</b-dropdown-item-button>
    </b-dropdown-group>
    <b-dropdown-divider v-if="!isNoUserGetter"></b-dropdown-divider>
    <b-dropdown-group v-if="!isNoUsersGetter" :header="getCapitalizeLang('signIn')">
      <div v-for="(u,i) in users">
        <b-dropdown-item-button
            v-if="!u.social"
            :disabled="isCurrent(u)"
            @click.prevent.stop="signIn(u.email)">
          {{ u.username }}
        </b-dropdown-item-button>
        <b-dropdown-item
            v-else
            :disabled="isCurrent(u)"
            :href="'/login/'+ u.social">
          <span v-if="u.social === 'google'"><i class="fab fa-google fa-sm text-danger"></i></span>
          <span v-else-if="u.social === 'facebook'"><i class="fab fa-facebook fa-sm text-primary"></i></span>
          <span v-else-if="u.social === 'github'"><i class="fab fa-github  fa-sm text-dark"></i></span>
          {{ u.username }}
        </b-dropdown-item>
      </div>
    </b-dropdown-group>
    <b-dropdown-divider></b-dropdown-divider>
    <b-dropdown-text>
      {{ getCapitalizeLang('otherAccount') }}?
      <b-button variant="outline-secondary" @click.prevent.stop="$refs.sign.openSignIn()">
        {{ getCapitalizeLang('signIn') }}
      </b-button>
    </b-dropdown-text>
    <sign
        ref="sign"
        :closable="true"
        :show="false"
    ></sign>
  </b-dropdown>
</template>

<script>
import {mapState, mapGetters} from "vuex"
import * as _ from "lodash"
import Sign from "../sign/SignModal.vue"
import PictureStatic from "../picture/PictureStatic.vue"

export default {
  components: {
    Sign,
    PictureStatic,
  },
  created() {
    this.fetchData()
  },
  computed: {
    ...mapState([
      'lang',
      'authentication',
    ]),
    ...mapGetters([
      'isNoUser',
      'isNoUsers'
    ]),
  },
  watch: {
    $route: [
      'fetchData',
    ],
    authentication: {
      handler: function () {
        this.$forceNextTick(() => {
          this.fetchData()
        })
      },
      deep: true
    }
  },
  data() {
    return {
      name: 'AccountDropdown',
      shown: false,
      show: true,
      isNoUserGetter: true,
      isNoUsersGetter: true,
      isPopover: false,
      user: null,
      isNew: false,
      username: "",
      users: [],
    }
  },
  methods: {
    fetchData() {
      this.show = false
      this.isNoUserGetter = this.isNoUser
      this.isNoUsersGetter = this.isNoUsers
      this.user = this.authentication.user
      if (this.user) {
        this.username = this.user.username
      } else {
        this.username = ""
      }
      if(this.authentication.isNew){
        this.launchPopover()
      }
      this.users = this.authentication.users
      this.show = true
    },
    launchPopover(){
      this.isPopover = true
      _.delay(()=>{
        this.isPopover = false
      }, 4000)
    },
    getCapitalize(text) {
      return _.capitalize(text)
    },
    getLowerCase(text) {
      return _.lowerCase(text)
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
    signIn(email) {
      this.$refs[this.prefixId()].hide()
      this.$refs.sign.openSignInWithEmail(email)
    },
    prefixId() {
      return this.name + "-"
    },
    isCurrent(user) {
      if (this.authentication.user && user) {
        return this.authentication.user.id === user.id
      } else {
        return false
      }
    }
  }
}
</script>

<style scoped>

</style>
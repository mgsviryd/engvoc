<template>
  <b-dropdown
      v-if="show"
      :id="prefixId()"
      :ref="prefixId()"
      class="border-0 shadow-none"
      :class="''"
      toggle-class="shadow-none rounded-sm px-1"
      variant="transparent"
      no-caret
      dropleft
      disabled
      @hide="shown=false"
      @show="shown=true"
  >
    <b-popover :target="prefixId()"
               :title="''"
               :show.sync="isPopover"
               placement="bottomleft"
               variant="warning"
    >

    </b-popover>
    <template #button-content>
      <span><i class="fa-regular fa-bell text-white"></i></span>
    </template>

    <b-dropdown-group :header="''">
      <b-dropdown-text>
      </b-dropdown-text>
    </b-dropdown-group>

    <b-dropdown-divider></b-dropdown-divider>

    <b-dropdown-group :header="''">
    </b-dropdown-group>
  </b-dropdown>
</template>

<script>
import {mapState, mapGetters} from "vuex"
import * as _ from "lodash"

export default {
  components: {
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
    ids(){
      return {
        id: this.prefixId(),
      }
    }
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
      name: 'NotificationDropdown',
      shown: false,
      show: true,
      isPopover: false,
    }
  },
  methods: {
    fetchData() {
      this.show = false

      if(false){
        this.launchPopover()
      }
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
    prefixId() {
      return this.name + "-"
    },
  }
}
</script>

<style scoped>
.st-float-right {
  float: right
}
</style>
<template>
    <b-dropdown
        variant="transparent"
        toggle-class="shadow-none rounded-sm px-1"
        :text="getCapitalizeLang('account')"

    >
      <b-dropdown-item-button>
        Non-grouped Item
      </b-dropdown-item-button>
      <b-dropdown-divider></b-dropdown-divider>
      <b-dropdown-group id="dropdown-group-1" header="Group 1">
        <b-dropdown-item-button>First Grouped item</b-dropdown-item-button>
        <b-dropdown-item-button>Second Grouped Item</b-dropdown-item-button>
      </b-dropdown-group>
      <b-dropdown-group id="dropdown-group-2" header="Group 2">
        <b-dropdown-item-button>First Grouped item</b-dropdown-item-button>
        <b-dropdown-item-button>Second Grouped Item</b-dropdown-item-button>
      </b-dropdown-group>
      <b-dropdown-divider></b-dropdown-divider>
      <b-dropdown-item-button>
        Another Non-grouped Item
      </b-dropdown-item-button>
    </b-dropdown>
</template>

<script>
import {mapState} from "vuex";

export default {
  computed: {
    ...mapState([
      'lang',
    ]),
  },
  watch: {
    $route: [
      'fetchData',
    ],
  },
  data() {
    return {

    }
  },
  methods:{
    fetchData(){},
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
  }
}
</script>

<style scoped>

</style>
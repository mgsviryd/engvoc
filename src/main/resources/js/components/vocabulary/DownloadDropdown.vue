<template>
  <b-dropdown
      v-if="show"
      :id="ids.id"
      v-b-tooltip="{trigger: 'hover', delay: { 'show': 800, 'hide': 40 }, placement: 'bottomright'}"
      :title="getCapitalizeLang('download')"
      menu-class="w-100"
      role="button"
      size="sm"
      toggle-class="py-0 px-1 shadow-none border border-secondary"
      variant="light"
  >
    <template slot="button-content"
    >
      <i class="fa-solid fa-download text-success"></i>
    </template>

    <b-dropdown-item
        @click.prevent.stop="downloadXmlFile()"
    >
      <b-row no-gutters>
        <b-col class="col-3 d-flex align-items-center justify-content-left text-left">
          <img alt="..." height="24"
               src="/static/picture/icon/xml-extension.png"
               width="24"></b-col>
        <b-col class="col-9"><small>{{ getCapitalizeLang('xml') }}</small></b-col>
      </b-row>
    </b-dropdown-item>

    <b-dropdown-item
        @click.prevent.stop="downloadExcelFile()"
    >
      <b-row no-gutters>
        <b-col class="col-3 d-flex align-items-center justify-content-left text-left">
          <img alt="..." height="24"
               src="/static/picture/icon/excel.png"
               width="24">
        </b-col>
        <b-col class="col-9"><small>{{ getCapitalizeLang('excel') }}</small></b-col>
      </b-row>
    </b-dropdown-item>
  </b-dropdown>
</template>

<script>
import {mapState} from "vuex"
import * as _ from "lodash"

export default {
  props: [
    'id',
    'dictionary',
    'side',
  ],
  mounted() {

  },
  created() {
    this.fetchData()
  },
  components: {},
  computed: {
    ...mapState({
      vocabularyStore: 'vocabulary',
    }),
    ids() {
      return {
        id: this.prefixId(),
      }
    }
  },
  watch: {
    $route: [
      'fetchData',
    ],
    vocabularyStore: {
      handler: function () {
        this.show = false
        this.fetchData()
      },
      deep: true
    },

  },
  data() {
    return {
      name: 'DownloadDropdown',
      show: false,
      vocabulary: null,
      oppositeSide: '',
    }
  },
  methods: {
    prefixId() {
      return this.name + '-' + this.id + '-'
    },
    fetchData() {
      this.show = false
      if (!this.isBlank(this.vocabularyStore.vocabulary)) {
        this.vocabulary = this.vocabularyStore.vocabulary
        this.oppositeSide = this.side === 'left' ? 'right' : 'left'
        this.show = true
      }
    },
    downloadExcelFile(d) {
      this.$store.dispatch("downloadExcelFileAction",
          {vocabulary: this.vocabulary, dictionary: this.dictionary}
      )
    },
    downloadXmlFile(d) {
      this.$store.dispatch("downloadXmlFileAction",
          {vocabulary: this.vocabulary, dictionary: this.dictionary}
      )
    },

    isBlank(value) {
      return _.isNil(value) || _.isEmpty(value)
    },
    getCapitalize(text) {
      return _.capitalize(text)
    },
    getLowerCase(text) {
      return _.lowerCase(text)
    },
    getUpperCase(text) {
      return _.upperCase(text)
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
  },
}

</script>

<style scoped>

</style>
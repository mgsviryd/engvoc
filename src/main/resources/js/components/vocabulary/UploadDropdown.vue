<template>
  <b-dropdown
      v-if="show"
      :id="ids.id"
      v-b-tooltip="{trigger: 'hover', delay: { 'show': 800, 'hide': 40 }, placement: 'bottomright'}"
      :title="getCapitalizeLang('upload')"
      menu-class="w-100"
      role="button"
      size="sm"
      toggle-class="py-0 px-1 shadow-none border border-secondary"
      variant="light"
  >
    <template slot="button-content"
    >
      <i class="fa-solid fa-upload text-primary"></i>
    </template>

    <b-dropdown-item
        v-for="(item, i) in fileTypes"
        @click.prevent.stop="onSelect(item)"
    >
      <b-row no-gutters>
        <b-col class="col-3 d-flex align-items-center justify-content-left text-left">
          <img alt="..." height="24"
               :src="item.imgSource"
               width="24"></b-col>
        <b-col class="col-9"><small>{{ getCapitalizeLang(item.label) }}</small></b-col>
      </b-row>
    </b-dropdown-item>

    <upload-cards-modal
        :id="ids.uploadCardsModal"
        :ref="ids.uploadCardsModal"
        :closable="true"
    ></upload-cards-modal>
  </b-dropdown>
</template>

<script>
import {mapState} from "vuex"
import * as _ from "lodash"
import UploadCardsModal from "../upload/UploadCardsReusableModal.vue"

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
  components: {
    UploadCardsModal,
  },
  computed: {
    ...mapState({
      vocabularyStore: 'vocabulary',
    }),
    ids() {
      return {
        id: this.prefixId(),
        uploadCardsModal: this.prefixId() + 'upload-cards-modal-id',
      }
    }
  },
  watch: {
    $route: [
      'fetchData',
    ],
    dictionary: {
      handler: function () {
        this.fetchData()
      },
      deep: true
    },

  },
  data() {
    return {
      name: 'UploadDropdown',
      show: false,
      vocabulary: null,
      oppositeSide: null,
      fileTypes:[
        {
          label: 'xml',
          imgSource: '/static/picture/icon/xml-extension.png',
        } ,
        {
          label: 'excel',
          imgSource: '/static/picture/icon/excel.png',
        },
      ],
  }
  },
  methods: {
    prefixId() {
      return this.name + '-' + this.id + '-'
    },
    onSelect(fileType){
      const data = {
        vocabulary: this.vocabulary,
        dictionary: this.dictionary,
        unrepeated: this.dictionary.unrepeated,
        fileType: fileType,
      }
      this.$refs[this.ids.uploadCardsModal].setDataAndShowModal(data)
    },
    fetchData() {
      this.show = false
      if (!this.isBlank(this.vocabularyStore.vocabulary)) {
        this.vocabulary = this.vocabularyStore.vocabulary
        this.oppositeSide = this.side === 'left' ? 'right' : 'left'
        this.show = true
      }
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
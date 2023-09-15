<template>
  <div v-if="show">
    <greeting-nav></greeting-nav>
    <vocabulary-nav></vocabulary-nav>
    <lang-pair-modal v-if="showVocabularyModal" :closable="true" :show="true"></lang-pair-modal>
  </div>
</template>

<script>

import {mapState, mapGetters} from "vuex"
import GreetingNav from "../components/greeting/GreetingNav.vue"
import VocabularyNav from "../components/vocabulary/VocabularyNav.vue"
import LangPairModal from "../components/lang/LangPairModal.vue"


export default {
  components: {
    GreetingNav,
    VocabularyNav,
    LangPairModal,
  },
  computed: {
    ...mapState([
      'authentication',
    ]),
    ...mapGetters([
      'isVocabularyPresent',
      'isNoUser',
    ])
  },
  watch: {
    authentication: {
      handler: function () {
        this.$forceNextTick(() => {
          this.fetchData()
        })
      },
      deep: true
    },
  },
  created() {
    this.fetchData()
  },
  data() {
    return {
      show: true,
      showVocabularyModal: false,
    }
  },
  methods: {
    fetchData() {
      this.show = false
      if (!this.isNoUser) {
        if (!this.isVocabularyPresent) {
          this.showVocabularyModal = true
        } else {
          this.showVocabularyModal = false
          this.$store.dispatch('findDictionariesAndCardsAction', this.authentication.user.vocabulary)
        }
      }
      this.show = true
    },
  },
}
</script>

<style>
</style>
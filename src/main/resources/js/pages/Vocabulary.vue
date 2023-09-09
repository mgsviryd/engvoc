<template>
  <div>
    <greeting-nav></greeting-nav>
    <vocabulary-nav></vocabulary-nav>
    <lang-pair-modal v-if="!isVocabularyLangPresentGetter" :closable="true" :show="true"></lang-pair-modal>
  </div>
</template>

<script>
import GreetingNav from '../components/greeting/GreetingNav.vue'
import VocabularyNav from '../components/vocabulary/VocabularyNav.vue'
import LangPairModal from "../components/lang/LangPairModal.vue"
import {mapState, mapGetters} from "vuex"


export default {
  components: {
    GreetingNav,
    VocabularyNav,
    LangPairModal,
  },
  computed: {
    ...mapState([
      'vocabulary',
    ]),
    ...mapGetters([
      'isVocabularyLangPresent',
    ])
  },
  watch:{
    vocabulary: {
      handler: function () {
        this.$forceNextTick(() => {
          this.fetchData()
        })
      },
      deep: true
    },
  },
  created(){
    this.$store.dispatch('findDictionariesAndCardsAction', this.vocabulary.lang)
  },
  data() {
    return {
      isVocabularyLangPresentGetter: true,
    }
  },
  methods: {
    fetchData(){
      this.isVocabularyLangPresentGetter = this.isVocabularyLangPresent
    },
  },
}
</script>

<style>
</style>
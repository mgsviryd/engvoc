<template>
  <div v-if="show">
    <greeting-nav></greeting-nav>
    <vocabulary-nav></vocabulary-nav>
    <vocabulary-modal
        :id="ids.vocabularyModal"
        :ref="ids.vocabularyModal"
        :closable="true"
    >
    </vocabulary-modal>
  </div>
</template>

<script>

import {mapState, mapGetters} from "vuex"
import GreetingNav from "../components/greeting/GreetingNav.vue"
import VocabularyNav from "../components/vocabulary/VocabularyNav.vue"
import VocabularyModal from "../components/vocabulary/VocabularyModal.vue"
import * as _ from "lodash"

export default {
  components: {
    GreetingNav,
    VocabularyNav,
    VocabularyModal,
  },
  created() {
  },
  computed: {
    ...mapState({
      authentication: 'authentication',
      vocabularyStore: 'vocabulary',
    }),
    ...mapGetters([
      'isVocabulariesPresent',
      'isUserPresent',
    ]),
    ids() {
      return {
        id: this.prefixId(),
        vocabularyModal: this.prefixId() + 'vocabulary-modal',
      }
    }
  },
  watch: {
    vocabularyStore: {
      handler: function () {
        this.$forceNextTick(() => {
          this.showVocabularyModal()
          if (this.vocabularyStore.vocabulary !== this.vocabulary){
            this.fetchData()
          }
        })
      },
      deep: true
    },
  },
  data() {
    return {
      name: 'Vocabulary',
      show: true,
      vocabulary: null,
    }
  },
  methods: {
    prefixId() {
      return this.name + '-'
    },
    showVocabularyModal(){
      if(this.isUserPresent && !this.isVocabulariesPresent){
        this.$refs[this.ids.vocabularyModal].showModal()
      }
    },
    fetchData() {
      this.show = false
      this.vocabulary = this.vocabularyStore.vocabulary
      if (this.isBlank(this.vocabulary)) {
        this.$store.commit('resetVocabularyDatabaseMutation')
      } else {
        this.$store.dispatch('findVocabularyDataAction', this.vocabulary)
      }
      this.show = true
    },
    isBlank(value) {
      return _.isNil(value) || _.isEmpty(value)
    },
  },
}
</script>

<style>
</style>
<template>
  <div v-if="show">
    <editor
        :id="ids.vocabulary"
        :ref="ids.vocabulary"
    ></editor>
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
import * as _ from "lodash"
import GreetingNav from "../components/greeting/GreetingNav.vue"
import FooterNav from "../components/footer/FooterNav.vue"
import Editor from "../components/vocabulary/Vocabulary.vue"
import VocabularyModal from "../components/vocabulary/VocabularyModal.vue"

export default {
  components: {
    GreetingNav,
    FooterNav,
    Editor,
    VocabularyModal,
  },
  mounted() {
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
        vocabulary: this.prefixId() + 'vocabulary-id',
        vocabularyModal: this.prefixId() + 'vocabulary-modal-id',
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

<style scoped>

</style>
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
import VocabularyModal from "../components/vocabulary/AddVocabularyModal.vue"

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
    this.fetchData()
  },
  computed: {
    ...mapState([
      'authentication',
      'vocabulary',
    ]),
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
    vocabulary: {
      handler: function () {
        if (this._vocabulary !== this.vocabulary.vocabulary) {
          this.fetchData()
        }
        this.$forceNextTick(() => {
          if (this.isUserPresent && !this.isVocabulariesPresent) {
            this.showVocabularyModal()
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
      _vocabulary: null,
    }
  },
  methods: {
    prefixId() {
      return this.name + '-'
    },
    showVocabularyModal() {
      this.$refs[this.ids.vocabularyModal].showModal()
    },
    fetchData() {
      this.show = false
      this._vocabulary = this.vocabulary.vocabulary
      if (!this.isBlank(this._vocabulary)) {
        this.$store.dispatch('findVocabularyDataAction', {vocabulary: this._vocabulary})
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
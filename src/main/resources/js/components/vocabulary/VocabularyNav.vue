<template>
  <div v-if="show">
    <nav
        class="navbar navbar-expand-lg navbar-dark bg-dark py-1 mb-0 shadow border-bottom border-secondary"
        style="width:100%;"
    >
      <b-button-group size="sm" class="btn-group-justified">
        <b-button variant="light"
                  class="mr-1"
                  :class="{ active: card.activated}"
                  href="#tab1"
                  data-toggle="tab"
                  role="tab"
                  aria-controls="tab1"
                  aria-selected="false"
                  @click="activate('card')"
                  style="width: 130px;">
          <small>
            <b>
              {{ getCapitalizeLang('card') }}
            </b>
          </small>
        </b-button>

        <b-button variant="light"
                  class="mr-1"
                  :class="{ active: dictionary.activated}"
                  href="#tab2"
                  data-toggle="tab"
                  role="tab"
                  aria-controls="tab2"
                  aria-selected="false"
                  @click="activate('dictionary')"
                  style="width: 130px;">
          <small>
            <b>
              {{ getCapitalizeLang('dictionary') }}
            </b>
          </small>
        </b-button>

        <b-button variant="light"
                  class="mr-sm-1"
                  :class="{ active: editor.activated}"
                  href="#tab3"
                  data-toggle="tab"
                  role="tab"
                  aria-controls="tab3"
                  aria-selected="false"
                  @click="activate('editor')"
                  style="width: 130px;">
          <small>
            <b>
              {{ getCapitalizeLang('editor') }}
            </b>
          </small>
        </b-button>

        <b-button variant="light"
                  class="mr-sm-1"
                  :class="{ active: origin.activated}"
                  href="#tab4"
                  data-toggle="tab"
                  role="tab"
                  aria-controls="tab4"
                  aria-selected="false"
                  @click="activate('origin')"
                  style="width: 130px;">
          <small>
            <b>
              {{ getCapitalizeLang('origin') }}
            </b>
          </small>
        </b-button>

        <b-button variant="light"
                  class="mr-1"
                  :class="{ active: settings.activated}"
                  href="#tab5"
                  data-toggle="tab"
                  role="tab"
                  aria-controls="tab5"
                  aria-selected="false"
                  @click="activate('settings')"
                  style="width: 130px;">
          <small>
            <b>
              {{ getCapitalizeLang('settings') }}
            </b>
          </small>
        </b-button>
      </b-button-group>
    </nav>

    <div class="tab-content" id="tab-content-0 d-inline-block" style="width: 100%">
      <div class="tab-pane fade bg-light border-1 border-secondary"
           :class="{ active: card.activated, show: card.activated}"
           id="tab1"
           role="tabpanel"
           aria-labelledby="..."
           style="width: 100%"
      >
        <card></card>
      </div>

      <div class="tab-pane fade bg-light"
           :class="{ active: dictionary.activated, show: dictionary.activated}"
           id="tab2"
           role="tabpanel" aria-labelledby="..."
           style="width: 100%"
      >
        <dictionary></dictionary>
      </div>

      <div class="tab-pane fade bg-light"
           :class="{ active: editor.activated, show: editor.activated}"
           id="tab3"
           role="tabpanel"
           aria-labelledby="..."
           style="width: 100%"
      >
        <editor
            ref="editor"
        ></editor>
      </div>

      <div class="tab-pane fade bg-light"
           id="tab4"
           :class="{ active: origin.activated, show: origin.activated}"
           role="tabpanel"
           aria-labelledby="..."
           style="width: 100%"
      >
        <origin></origin>
      </div>
      <div class="tab-pane fade bg-light"
           :class="{ active: settings.activated, show: settings.activated}"
           id="tab5"
           role="tabpanel"
           aria-labelledby="..."
           style="width: 100%"
      >
        <settings></settings>
      </div>
    </div>
  </div>
</template>

<script>
import {mapState, mapGetters} from 'vuex'
import Card from './Card.vue'
import Dictionary from './Dictionary.vue'
import Editor from './editor/Editor.vue'
import Origin from './Origin.vue'
import Settings from './Settings.vue'
import * as _ from 'lodash'

export default {
  components: {
    Card,
    Dictionary,
    Editor,
    Origin,
    Settings,
  },
  created() {
    this.$store.watch(this.$store.getters.getVocabularyId, vocabularyId => {
      this.activateDictionaries(this.id1, this.id2)
    })
    this.$watch(
        () => this.$route.query,
        (toQueries, previousQueries) => {
          this.$forceNextTick(() => {
            this.activateDictionaries(toQueries.id1, toQueries.id2)
          })
        }
    )
  },
  watch: {
    '$route.params.mark': {
      handler: function (mark) {
        this.$forceNextTick(() => {
          this.activate(mark)
            const id1 = this.$route.query.id1
            const id2 = this.$route.query.id2
            this.activateDictionaries(id1, id2)
        })
      },
      immediate: true
    },
  },
  computed: {
    ...mapState([
      'lang',
      'dictionaries',
    ]),
    ...mapGetters([
      'getDictionaryById',
    ])
  },
  data() {
    return {
      id1: null,
      id2: null,
      show: true,
      card: {
        activated: false,
        show: true,
      },
      dictionary: {
        activated: false,
        show: true,
      },
      editor: {
        activated: false,
        show: true,
      },
      origin: {
        activated: false,
        show: true,
      },
      settings: {
        activated: false,
        show: true,
      },
    }
  },
  methods: {
    getLang(key) {
      return this.$t(key)
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    disactiveAll() {
      this.card.activated = false
      this.dictionary.activated = false
      this.editor.activated = false
      this.origin.activated = false
      this.settings.activated = false
    },
    routerMainPage() {
      this.$router.push({
        path: "/"
      }).then(() => {
      }).catch(err => {
      })
    },
    activate(tab) {
      this.disactiveAll()
      this[tab].activated = true
    },

    activateDictionaries(id1, id2) {
      this.show = false
      this.id1 = id1
      this.id2 = id2
      this.$refs.editor.loadDictionary(id1, "id1")
      this.$refs.editor.loadDictionary(id2, "id2")
      this.show = true
    },
  }
}
</script>

<style scoped>

</style>
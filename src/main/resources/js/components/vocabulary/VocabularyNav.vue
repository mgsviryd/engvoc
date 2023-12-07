<template>
  <div v-if="show">
    <nav
        class="navbar navbar-expand-lg navbar-dark bg-dark py-1 mb-0 shadow border-bottom border-secondary"
        style="width:100%;"
    >
      <b-button-group class="btn-group-justified" size="sm">
        <b-button :class="{ active: card.activated}"
                  aria-controls="tab1"
                  aria-selected="false"
                  class="mr-1"
                  data-toggle="tab"
                  href="#tab1"
                  role="tab"
                  style="width: 130px;"
                  variant="light"
                  @click="activate('card')">
          <small>
            <b>
              {{ getCapitalizeLang('card') }}
            </b>
          </small>
        </b-button>

        <b-button :class="{ active: dictionary.activated}"
                  aria-controls="tab2"
                  aria-selected="false"
                  class="mr-1"
                  data-toggle="tab"
                  href="#tab2"
                  role="tab"
                  style="width: 130px;"
                  variant="light"
                  @click="activate('dictionary')">
          <small>
            <b>
              {{ getCapitalizeLang('dictionary') }}
            </b>
          </small>
        </b-button>

        <b-button :class="{ active: editor.activated}"
                  aria-controls="tab3"
                  aria-selected="false"
                  class="mr-sm-1"
                  data-toggle="tab"
                  href="#tab3"
                  role="tab"
                  style="width: 130px;"
                  variant="light"
                  @click="activate('editor')">
          <small>
            <b>
              {{ getCapitalizeLang('editor') }}
            </b>
          </small>
        </b-button>

        <b-button :class="{ active: origin.activated}"
                  aria-controls="tab4"
                  aria-selected="false"
                  class="mr-sm-1"
                  data-toggle="tab"
                  href="#tab4"
                  role="tab"
                  style="width: 130px;"
                  variant="light"
                  @click="activate('origin')">
          <small>
            <b>
              {{ getCapitalizeLang('origin') }}
            </b>
          </small>
        </b-button>

        <b-button :class="{ active: settings.activated}"
                  aria-controls="tab5"
                  aria-selected="false"
                  class="mr-1"
                  data-toggle="tab"
                  href="#tab5"
                  role="tab"
                  style="width: 130px;"
                  variant="light"
                  @click="activate('settings')">
          <small>
            <b>
              {{ getCapitalizeLang('settings') }}
            </b>
          </small>
        </b-button>
      </b-button-group>
    </nav>

    <div id="tab-content-0 d-inline-block" class="tab-content" style="width: 100%">
      <div id="tab1"
           :class="{ active: card.activated, show: card.activated}"
           aria-labelledby="..."
           class="tab-pane fade bg-light border-1 border-secondary"
           role="tabpanel"
           style="width: 100%"
      >
        <card></card>
      </div>

      <div id="tab2"
           :class="{ active: dictionary.activated, show: dictionary.activated}"
           aria-labelledby="..."
           class="tab-pane fade bg-light" role="tabpanel"
           style="width: 100%"
      >
        <dictionary></dictionary>
      </div>

      <div id="tab3"
           :class="{ active: editor.activated, show: editor.activated}"
           aria-labelledby="..."
           class="tab-pane fade bg-light"
           role="tabpanel"
           style="width: 100%"
      >
        <editor
            ref="editor"
        ></editor>
      </div>

      <div id="tab4"
           :class="{ active: origin.activated, show: origin.activated}"
           aria-labelledby="..."
           class="tab-pane fade bg-light"
           role="tabpanel"
           style="width: 100%"
      >
        <origin></origin>
      </div>
      <div id="tab5"
           :class="{ active: settings.activated, show: settings.activated}"
           aria-labelledby="..."
           class="tab-pane fade bg-light"
           role="tabpanel"
           style="width: 100%"
      >
        <settings></settings>
      </div>
    </div>
  </div>
</template>

<script>

import {mapState, mapGetters} from 'vuex'
import * as _ from 'lodash'
import Card from './Card.vue'
import Dictionary from './Dictionary.vue'
import Editor from './editor/Editor.vue'
import Origin from './Origin.vue'
import Settings from './Settings.vue'

export default {
  components: {
    Card,
    Dictionary,
    Editor,
    Origin,
    Settings,
  },
  created() {
  },
  watch: {
    '$route': {
      handler: function (to, from) {
        this.$forceNextTick(() => {
          const tab = to.query.tab
          if(tab){
            if (tab === 'editor')
            this.activate('editor')
            const left = this.$route.query.left
            const right = this.$route.query.right
            this.activateDictionaries(left, right)
          }
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

    activateDictionaries(left, right) {
      this.show = false
      this.$refs.editor.loadDictionary(left, "left")
      this.$refs.editor.loadDictionary(right, "right")
      this.show = true
    },
  }
}
</script>

<style scoped>

</style>
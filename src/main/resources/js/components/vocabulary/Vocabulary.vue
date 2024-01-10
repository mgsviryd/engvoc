<template>
  <b-row
      v-if="show"
      class="justify-content-between m-0 p-0"
      style="width: 100%"
  >
    <b-col v-if="left.show"
           v-show="left.displayNav || left.displayTool"
           :class="[activeLeft?'border-primary':'border-secondary']"
           :cols="left.displayNav?left.navSize:0"
           class="border border-3 m-0 p-0"
    >
      <b-row class="d-flex justify-content-between" no-gutters>
        <b-col
            v-show="left.displayNav"
            :id="ids.left.col.dictionaryNav"
            :ref="ids.left.col.dictionaryNav"
            class="align-items-stretch p-0 m-0"
            style="width: 100%;"
        >
          <dictionary-nav
              :id="ids.left.dictionaryNav"
              :ref="ids.left.dictionaryNav"
              :dictionary="left.dictionary"
              :instance="left"
              @onClickDictionary="onClickDictionary"
          ></dictionary-nav>
        </b-col>
        <b-col
            v-show="left.displayTool"
            :id="ids.left.col.verticalTools"
            :ref="ids.left.col.verticalTools"
            class="d-flex justify-content-between m-0 p-0"
            style="max-width: 24px;"
        >
          <vertical-tools
              :id="ids.left.verticalTools"
              :ref="ids.left.verticalTools"
              :instance="left"
              @hideFullNav="hideFullNav"
              @showFullNav="showFullNav"
              @showHideInstance="showHideInstance"
              @stepDownNav="stepDownNav"
              @stepUpNav="stepUpNav"
          >
          </vertical-tools>
        </b-col>
      </b-row>
    </b-col>
    <b-col
        v-if="left.show"
        v-show="left.displayTable"
        :id="ids.left.col.cardTable"
        :ref="ids.left.col.cardTable"
        :class="[activeLeft?'border-primary':'border-secondary']"
        :cols="left.tableSize"
        class="border border-left-0 border-3 m-0 p-0"
        style="width: 100%;"
    >
      <b-row class="justify-content-between" no-gutters>
        <b-col v-if="left.dictionary"
               :class="''"
               class="m-0 p-0">
          <card-table
              :id="ids.left.cardTable"
              :ref="ids.left.cardTable"
              :data="{watchId: left.watchId, instanceMark: left.instanceMark, dictionary: left.dictionary, cards: left.cards}"
              @onNavigateToDictionary="onNavigateToDictionary"
              @onNavigateToUnique="onNavigateToUnique"
          ></card-table>
        </b-col>
        <b-col v-else
               class="m-0 p-0"
        >
          <div
              class="empty-table- container d-flex justify-content-center align-items-center p-0 m-0">
            <div class="row no-gutters text-center">
              <h6>{{ getCapitalizeLang('chooseDictionary') }}</h6>
            </div>
          </div>
        </b-col>
      </b-row>
    </b-col>

    <b-col
        v-if="right.show"
        v-show="right.displayTable"
        :id="ids.right.col.cardTable"
        :ref="ids.right.col.cardTable"
        :class="[activeRight?'border-primary':'border-secondary']"
        :cols="right.tableSize"
        class="border border-right-0 border-3 m-0 p-0"
        style="width: 100%;"
    >
      <b-row class="justify-content-between" no-gutters>
        <b-col v-if="right.dictionary"
               :class="''"
               class="m-0 p-0">
          <card-table
              :id="ids.right.cardTable"
              :ref="ids.right.cardTable"
              :data="{watchId: right.watchId, instanceMark: right.instanceMark, dictionary: right.dictionary, cards: right.cards}"
              @onNavigateToDictionary="onNavigateToDictionary"
              @onNavigateToUnique="onNavigateToUnique"
          ></card-table>
        </b-col>
        <b-col v-else
               class="m-0 p-0"
        >
          <div
              class="empty-table- container d-flex justify-content-center align-items-center p-0 m-0">
            <div class="row no-gutters text-center">
              <h6>{{ getCapitalizeLang('chooseDictionary') }}</h6>
            </div>
          </div>
        </b-col>
      </b-row>
    </b-col>
    <b-col v-if="right.show"
           v-show="right.displayNav || right.displayTool"
           :class="[activeRight?'border-primary':'border-secondary']"
           :cols="right.displayNav?right.navSize:0"
           class="m-0 p-0 border border-3"
    >
      <b-row class="d-flex justify-content-between" no-gutters>
        <b-col v-show="right.displayTool"
               :id="ids.right.col.verticalTools"
               :ref="ids.right.col.verticalTools"
               class="d-flex justify-content-between m-0 p-0"
               style="max-width: 24px;"
        >
          <vertical-tools
              :id="ids.right.verticalTools"
              :ref="ids.right.verticalTools"
              :instance="right"
              @hideFullNav="hideFullNav"
              @showFullNav="showFullNav"
              @showHideInstance="showHideInstance"
              @stepDownNav="stepDownNav"
              @stepUpNav="stepUpNav"
          >
          </vertical-tools>
        </b-col>
        <b-col
            v-show="right.displayNav"
            :id="ids.right.col.dictionaryNav"
            :ref="ids.right.col.dictionaryNav"
            class="m-0 p-0"
            style="width: 100%;"
        >
          <dictionary-nav
              :id="ids.right.dictionaryNav"
              :ref="ids.right.dictionaryNav"
              :dictionary="right.dictionary"
              :instance="right"
              @onClickDictionary="onClickDictionary"
          ></dictionary-nav>
        </b-col>
      </b-row>
    </b-col>
  </b-row>
</template>

<script>
import {mapState, mapGetters} from "vuex"
import * as _ from "lodash"
import CardTable from "./CardTable.vue"
import DictionaryNav from "./DictionaryNav.vue"
import VerticalTools from "./VerticalTools.vue"

export default {
  props: [],
  components: {
    VerticalTools,
    CardTable,
    DictionaryNav,
  },
  created() {
    this.addListeners()
    this.fetchData()
    this.showHideInstance('left')
    this.$store.watch(this.$store.getters.getActionId, actionId => {
      this.fetchData()
    })
  },
  destroyed() {
    this.removeListeners()
  },
  watch: {
    '$route': {
      handler: function (to, from) {
        this.$forceNextTick(() => {
          const left = this.$route.query.left
          const right = this.$route.query.right
        })
      },
      immediate: true
    },
  },
  computed: {
    ...mapState([
      'cards',
      'lang',
      'height',
    ]),
    ...mapGetters([
      'getDictionaryById',
      'getCardsByDictionaryId',
    ]),
    ids() {
      return {
        left: {
          id: this.prefixId() + 'Left-',
          dictionaryNav: this.prefixId() + 'Left-' + 'dictionary-nav-id',
          verticalTools: this.prefixId() + 'Left-' + 'vertical-tools-id',
          cardTable: this.prefixId() + 'Left-' + 'card-table-id',
          col: {
            dictionaryNav: this.prefixId() + 'Left-' + 'Col-' + 'dictionary-nav-id',
            verticalTools: this.prefixId() + 'Left-' + 'Col-' + 'vertical-tools-id',
            cardTable: this.prefixId() + 'Left-' + 'Col-' + 'card-table-id',
          },
        },
        right: {
          id: this.prefixId() + 'Right-',
          dictionaryNav: this.prefixId() + 'Right-' + 'dictionary-nav-id',
          verticalTools: this.prefixId() + 'Right-' + 'vertical-tools-id',
          cardTable: this.prefixId() + 'Right-' + 'card-table-id',
          col: {
            dictionaryNav: this.prefixId() + 'Right-' + 'Col-' + 'dictionary-nav-id',
            verticalTools: this.prefixId() + 'Right-' + 'Col-' + 'vertical-tools-id',
            cardTable: this.prefixId() + 'Right-' + 'Col-' + 'card-table-id',
          },
        },
      }
    }
  },
  data() {
    return {
      name: 'Vocabulary',
      show: false,
      size: {
        all: 12,
        half: 6,
        parts: 2,
      },
      activeLeft: false,
      activeRight: false,

      left: {
        show: true,
        watchId: _.now(),
        active: false,
        displayNav: true,
        displayTool: true,
        displayTable: true,
        instanceMark: 'left',
        dictionary: null,
        cards: [],
        navSizes: [0, 2, 6],
        navSizeInx: 1,
        navSize: 2,
        tableSize: 4,
      },
      right: {
        show: true,
        watchId: _.now(),
        active: false,
        displayNav: true,
        displayTool: true,
        displayTable: true,
        instanceMark: 'right',
        dictionary: null,
        cards: [],
        navSizes: [0, 2, 6],
        navSizeInx: 1,
        navSize: 2,
        tableSize: 4,
      },
      listeners: [],
    }
  },
  methods: {
    fetchData() {
      this.show = false
      if (!this.isBlank(this.left.dictionary)) {
        this.loadDictionary(this.left.dictionary.id, "left")
      }
      if (!this.isBlank(this.right.dictionary)) {
        this.loadDictionary(this.right.dictionary.id, "right")
      }
      this.show = true
    },
    prefixId() {
      return this.name + '-'
    },
    isBlank(value) {
      return _.isNil(value) || _.isEmpty(value)
    },
    loadDictionary(id, instanceMark) {
      if (instanceMark === this.left.instanceMark) {
        this.left.dictionary = this.getDictionaryById(id)
        this.left.cards = this.getCardsByDictionaryId(id)
        this.left.watchId = _.now()
      }
      if (instanceMark === this.right.instanceMark) {
        this.right.dictionary = this.getDictionaryById(id)
        this.right.cards = this.getCardsByDictionaryId(id)
        this.right.watchId = _.now()
      }
      return []
    },
    isTwoSourcePresent() {
      return this.left.dictionary !== null && this.right.dictionary !== null
    },
    showHideInstance(instanceMark) {
      if (instanceMark === this.left.instanceMark) {
        this.right.displayNav = !this.right.displayNav
        this.right.displayTool = !this.right.displayTool
        this.right.displayTable = !this.right.displayTable
        if (this.right.displayNav) {
          this.left.tableSize = this.size.half - this.left.navSize
        } else {
          this.left.tableSize = this.size.all - this.left.navSize
        }
      }
      if (instanceMark === this.right.instanceMark) {
        this.left.displayNav = !this.left.displayNav
        this.left.displayTool = !this.left.displayTool
        this.left.displayTable = !this.left.displayTable
        if (this.left.displayNav) {
          this.right.tableSize = this.size.half - this.right.navSize
        } else {
          this.right.tableSize = this.size.all - this.right.navSize
        }
        console.info(this.left.displayNav)
        console.info(this.left.displayTool)
        console.info(this.left.displayTable)
      }
    },
    getLang(key) {
      return this.$t(key)
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    showFullNav(mark) {
      if (this.left.instanceMark === mark) {
        this.left.displayTable = true
        this.left.displayNav = true
        this.left.displayTool = true
        this.left.navSize += this.left.tableSize
        const diff = this.left.tableSize - this.size.half
        this.left.tableSize = diff > 0 ? diff : 0
        if (this.left.tableSize === 0) {
          this.left.displayTable = false
        }
      } else {
        this.right.displayTable = true
        this.right.displayNav = true
        this.right.displayTool = true
        this.right.navSize += this.right.tableSize
        const diff = this.right.tableSize - this.size.half
        this.right.tableSize = diff > 0 ? diff : 0
        if (this.right.tableSize === 0) {
          this.right.displayTable = false
        }
        console.info("show nav: " + this.right.navSize)
        console.info("show table: " + this.right.tableSize)
      }
    },
    hideFullNav(mark) {
      if (this.left.instanceMark === mark) {
        this.left.displayTable = true
        this.left.displayNav = false
        this.left.displayTool = true
        this.left.tableSize += this.left.navSize - 1
        this.left.navSize = 1
      } else {
        this.right.displayTable = true
        this.right.displayNav = false
        this.right.displayTool = true
        this.right.tableSize += this.right.navSize - 1
        this.right.navSize = 1
        console.info("hide nav: " + this.right.navSize)
        console.info("hide table: " + this.right.tableSize)
      }
    },
    stepUpNav(mark) {

    },
    stepDownNav(mark) {

    },
    activeSideListener() {
      return event => {
        const elementsLeft = [this.ids.left.col.dictionaryNav, this.ids.left.col.verticalTools, this.ids.left.col.cardTable]
        const elementsRight = [this.ids.right.col.dictionaryNav, this.ids.right.col.verticalTools, this.ids.right.col.cardTable]

        const countLeft = elementsLeft.filter(id => {
          const element = document.getElementById(id)
          if (_.isNil(element)) return false
          return element === event.target || element.contains(event.target)
        }).length

        const countRight = elementsRight.filter(id => {
          const element = document.getElementById(id)
          if (_.isNil(element)) return false
          return element === event.target || element.contains(event.target)
        }).length

        if (countLeft > 0) {
          this.activeLeft = true
          this.activeRight = false
          if (!_.isNil(this.$refs[this.ids.left.dictionaryNav])) {
            this.$refs[this.ids.left.dictionaryNav].activeParent = true
          }
          if (!_.isNil(this.$refs[this.ids.left.cardTable])) {
            this.$refs[this.ids.left.cardTable].activeParent = true
          }
          if (!_.isNil(this.$refs[this.ids.right.dictionaryNav])) {
            this.$refs[this.ids.right.dictionaryNav].activeParent = false
          }
          if (!_.isNil(this.$refs[this.ids.right.cardTable])) {
            this.$refs[this.ids.right.cardTable].activeParent = false
          }
        }
        if (countRight > 0) {
          this.activeLeft = false
          this.activeRight = true
          if (!_.isNil(this.$refs[this.ids.left.dictionaryNav])) {
            this.$refs[this.ids.left.dictionaryNav].activeParent = false
          }
          if (!_.isNil(this.$refs[this.ids.left.cardTable])) {
            this.$refs[this.ids.left.cardTable].activeParent = false
          }
          if (!_.isNil(this.$refs[this.ids.right.dictionaryNav])) {
            this.$refs[this.ids.right.dictionaryNav].activeParent = true
          }
          if (!_.isNil(this.$refs[this.ids.right.cardTable])) {
            this.$refs[this.ids.right.cardTable].activeParent = true
          }
        }
      }
    },
    addListeners() {
      const activeSideListener = this.activeSideListener()
      this.listeners.push({level: document, type: 'mousedown', listener: activeSideListener})
      this.listeners.push({level: document, type: 'mouseup', listener: activeSideListener})
      this.listeners.forEach(pair => {
        pair.level.addEventListener(pair.type, pair.listener)
      })
    },
    removeListeners() {
      this.listeners.forEach(pair => {
        pair.level.removeEventListener(pair.type, pair.listener)
      })
      this.listeners = []
    },
    onNavigateToDictionary(side) {
      this.$refs[this.ids[side].dictionaryNav].navigateToActiveDictionary()
    },
    onNavigateToUnique(side) {
      this.$refs[this.ids[side].dictionaryNav].navigateToActiveUnique()
    },
    onClickDictionary({id, instanceMark}) {
      this.loadDictionary(id, instanceMark)
    },
  },

}
</script>

<style scoped>
.empty-table- {
  height: 524px;
  overflow-y: auto;
}

.border-2 {
  border-width: 2px !important;
}

.border-3 {
  border-width: 3px !important;
}

</style>
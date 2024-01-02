<template>
  <div
      v-if="show"
      class="row m-0 p-0 justify-content-between"
      style="width: 100%"
  >
    <div v-if="left.show"
         v-show="left.displayNav || left.displayTool"
         :class="[left.displayNav?'col-'+left.navSize:'', activeLeft?'border-primary':'border-secondary']"
         class="m-0 p-0 border border-3 container-fluid"
    >
      <div class="row d-flex justify-content-between no-gutters">
        <div
            v-show="left.displayNav"
            :id="ids.left.col.dictionaryNav"
            :ref="ids.left.col.dictionaryNav"
            class="col align-items-stretch p-0 m-0"
            style="width: 100%;"
        >
          <dictionary-nav
              :id="ids.left.dictionaryNav"
              :ref="ids.left.dictionaryNav"
              :instance="left"
              :dictionary="left.dictionary"
          ></dictionary-nav>
        </div>
        <div
            v-show="left.displayTool"
            :id="ids.left.col.verticalTools"
            :ref="ids.left.col.verticalTools"
            class="col d-flex m-0 p-0 justify-content-between"
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
        </div>
      </div>
    </div>
    <div
        v-if="left.show"
        v-show="left.displayTable"
        :id="ids.left.col.cardTable"
        :ref="ids.left.col.cardTable"
        :class="['col-' + left.tableSize, activeLeft?'border-primary':'border-secondary']"
        class="m-0 p-0 border border-left-0 border-3"
        style="width: 100%;"
    >
      <div class="row justify-content-between no-gutters">
        <div v-if="left.dictionary"
             :class="isTwoSourcePresent()?'st-two-source':'st-one-source'"
             class="col m-0 p-0">
          <card-table
              :id="ids.left.cardTable"
              :ref="ids.left.cardTable"
              :dictionary="left.dictionary"
              :instanceMark="left.instanceMark"
              @onNavigateToDictionary="navigateToDictionary"
              @onNavigateToUnique="navigateToUnique"
          ></card-table>
        </div>
        <div v-else
             class="col m-0 p-0"
        >
          <div
              class="empty-table- container d-flex justify-content-center align-items-center p-0 m-0">
            <div class="row no-gutters text-center">
              <h6>{{ getCapitalizeLang('chooseDictionary') }}</h6>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div
        v-if="right.show"
        v-show="right.displayTable"
        :id="ids.right.col.cardTable"
        :ref="ids.right.col.cardTable"
        :class="['col-' + right.tableSize, activeRight?'border-primary':'border-secondary']"
        class="m-0 p-0 border border-right-0 border-3"
        style="width: 100%;"
    >
      <div class="row justify-content-between no-gutters">
        <div v-if="right.dictionary"
             :class="isTwoSourcePresent()?'st-two-source':'st-one-source'"
             class="col m-0 p-0">
          <card-table
              :id="ids.right.cardTable"
              :ref="ids.right.cardTable"
              :dictionary="right.dictionary"
              :instanceMark="right.instanceMark"
              @onNavigateToDictionary="navigateToDictionary"
              @onNavigateToUnique="navigateToUnique"
          ></card-table>
        </div>
        <div v-else
             class="col m-0 p-0"
        >
          <div
              class="empty-table- container d-flex justify-content-center align-items-center p-0 m-0">
            <div class="row no-gutters text-center">
              <h6>{{ getCapitalizeLang('chooseDictionary') }}</h6>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="right.show"
         v-show="right.displayNav || right.displayTool"
         :class="[right.displayNav?'col-' + right.navSize:'', activeRight?'border-primary':'border-secondary']"
         class="m-0 p-0 border border-3"
    >
      <div class="row d-flex justify-content-between no-gutters">
        <div v-show="right.displayTool"
             :id="ids.right.col.verticalTools"
             :ref="ids.right.col.verticalTools"
             class="col d-flex m-0 p-0 justify-content-between"
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
        </div>
        <div
            v-show="right.displayNav"
            :id="ids.right.col.dictionaryNav"
            :ref="ids.right.col.dictionaryNav"
            class="col m-0 p-0"
            style="width: 100%;"
        >
          <dictionary-nav
              :id="ids.right.dictionaryNav"
              :ref="ids.right.dictionaryNav"
              :instance="right"
              :dictionary="right.dictionary"
          ></dictionary-nav>
        </div>
      </div>
    </div>
  </div>
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
          this.activateDictionaries(left, right)
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
        active: false,
        displayNav: true,
        displayTool: true,
        displayTable: true,
        instanceMark: "left",
        dictionary: null,
        navSizes: [0, 2, 6],
        navSizeInx: 1,
        navSize: 2,
        tableSize: 4,
      },
      right: {
        show: true,
        active: false,
        displayNav: true,
        displayTool: true,
        displayTable: true,
        instanceMark: "right",
        dictionary: null,
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
      this.show = true
    },
    prefixId() {
      return this.name + '-'
    },
    activateDictionaries(left, right) {
      this.show = false
      this.loadDictionary(left, "left")
      this.loadDictionary(right, "right")
      this.show = true
    },
    loadDictionary(id, instanceMark) {
      if (instanceMark === this.left.instanceMark) {
        this.left.dictionary = this.getDictionaryById(id)
      }
      if (instanceMark === this.right.instanceMark) {
        this.right.dictionary = this.getDictionaryById(id)
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
    activeSideListener(){
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
          if (!_.isNil(this.$refs[this.ids.left.cardTable])){
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
      this.listeners.push({type: 'mousedown', listener: activeSideListener})
      this.listeners.push({type: 'mouseup', listener: activeSideListener})
      this.activateListeners()
    },
    removeListeners() {
      this.listeners.forEach(pair=>{
        document.removeEventListener(pair.type, pair.listener)
      })
      this.listeners = []
    },
    activateListeners(){
      this.listeners.forEach(pair=>{
        document.addEventListener(pair.type, pair.listener)
      })
    },
    navigateToDictionary(side){
      this.$refs[this.ids[side].dictionaryNav].navigateToActiveDictionary()
    },
    navigateToUnique(side){
      this.$refs[this.ids[side].dictionaryNav].navigateToActiveUnique()
    },
  },

}
</script>

<style scoped>
.empty-table- {
  height: 524px;
  overflow-y: auto;
}
.border-2{
  border-width: 2px !important;
}
.border-3{
  border-width: 3px !important;
}

</style>
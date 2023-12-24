<template>
  <div
      v-if="show"
      class="row m-0 p-0 justify-content-between"
      style="width: 100%"
  >
    <div v-if="instance1.show"
         v-show="instance1.displayNav || instance1.displayTool"
         :class="[instance1.displayNav?'col-'+instance1.navSize:'', activeLeft?'border-primary':'border-secondary']"
         class="m-0 p-0 border container-fluid"
    >
      <div class="row d-flex justify-content-between no-gutters">
        <div
            v-show="instance1.displayNav"
            :id="ids.left.col.dictionaryNav"
            :ref="ids.left.col.dictionaryNav"
            class="col align-items-stretch p-0 m-0"
            style="width: 100%;"
        >
          <dictionary-nav
              :id="ids.left.dictionaryNav"
              :ref="ids.left.dictionaryNav"
              :instance="instance1"
          ></dictionary-nav>
        </div>
        <div
            v-show="instance1.displayTool"
            :id="ids.left.col.verticalTools"
            :ref="ids.left.col.verticalTools"
            class="col d-flex m-0 p-0 border border-secondary justify-content-between"
            style="max-width: 25px;"
        >
          <vertical-tools
              :id="ids.left.verticalTools"
              :ref="ids.left.verticalTools"
              :instance="instance1"
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
        v-if="instance1.show"
        v-show="instance1.displayTable"
        :id="ids.left.col.cardTable"
        :ref="ids.left.col.cardTable"
        :class="['col-' + instance1.tableSize]"
        class="m-0 p-0 border border-secondary"
        style="width: 100%;"
    >
      <div class="row justify-content-between no-gutters">
        <div v-if="instance1.dictionary"
             :class="isTwoSourcePresent()?'st-two-source':'st-one-source'"
             class="col m-0 p-0">
          <card-table
              :id="ids.left.cardTable"
              :ref="ids.left.cardTable"
              :dictionary="instance1.dictionary"
              :instanceMark="instance1.instanceMark"
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
        v-if="instance2.show"
        v-show="instance2.displayTable"
        :id="ids.right.col.cardTable"
        :ref="ids.right.col.cardTable"
        :class="['col-' + instance2.tableSize]"
        class="m-0 p-0 border border-secondary"
        style="width: 100%;"
    >
      <div class="row justify-content-between no-gutters">
        <div v-if="instance2.dictionary"
             :class="isTwoSourcePresent()?'st-two-source':'st-one-source'"
             class="col m-0 p-0">
          <card-table
              :id="ids.right.cardTable"
              :ref="ids.right.cardTable"
              :dictionary="instance2.dictionary"
              :instanceMark="instance2.instanceMark"
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
    <div v-if="instance2.show"
         v-show="instance2.displayNav || instance2.displayTool"
         :class="[instance2.displayNav?'col-' + instance2.navSize:'', activeRight?'border-primary':'border-secondary']"
         class="m-0 p-0 border"
    >
      <div class="row d-flex justify-content-between no-gutters">
        <div v-show="instance2.displayTool"
             :id="ids.right.col.verticalTools"
             :ref="ids.right.col.verticalTools"
             class="col d-flex m-0 p-0 border border-secondary justify-content-between"
             style="max-width: 25px;"
        >
          <vertical-tools
              :id="ids.right.verticalTools"
              :ref="ids.right.verticalTools"
              :instance="instance2"
              @hideFullNav="hideFullNav"
              @showFullNav="showFullNav"
              @showHideInstance="showHideInstance"
              @stepDownNav="stepDownNav"
              @stepUpNav="stepUpNav"
          >
          </vertical-tools>
        </div>
        <div
            v-show="instance2.displayNav"
            :id="ids.right.col.dictionaryNav"
            :ref="ids.right.col.dictionaryNav"
            class="col m-0 p-0"
            style="width: 100%;"
        >
          <dictionary-nav
              :id="ids.right.dictionaryNav"
              :ref="ids.right.dictionaryNav"
              :instance="instance2"
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

      instance1: {
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
      instance2: {
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
      if (instanceMark === this.instance1.instanceMark) {
        this.instance1.dictionary = this.getDictionaryById(id)
      }
      if (instanceMark === this.instance2.instanceMark) {
        this.instance2.dictionary = this.getDictionaryById(id)
      }
      return []
    },
    isTwoSourcePresent() {
      return this.instance1.dictionary !== null && this.instance2.dictionary !== null
    },
    showHideInstance(instanceMark) {
      if (instanceMark === this.instance1.instanceMark) {
        this.instance2.displayNav = !this.instance2.displayNav
        this.instance2.displayTool = !this.instance2.displayTool
        this.instance2.displayTable = !this.instance2.displayTable
        if (this.instance2.displayNav) {
          this.instance1.tableSize = this.size.half - this.instance1.navSize
        } else {
          this.instance1.tableSize = this.size.all - this.instance1.navSize
        }
      }
      if (instanceMark === this.instance2.instanceMark) {
        this.instance1.displayNav = !this.instance1.displayNav
        this.instance1.displayTool = !this.instance1.displayTool
        this.instance1.displayTable = !this.instance1.displayTable
        if (this.instance1.displayNav) {
          this.instance2.tableSize = this.size.half - this.instance2.navSize
        } else {
          this.instance2.tableSize = this.size.all - this.instance2.navSize
        }
        console.info(this.instance1.displayNav)
        console.info(this.instance1.displayTool)
        console.info(this.instance1.displayTable)
      }
    },
    getLang(key) {
      return this.$t(key)
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    showFullNav(mark) {
      if (this.instance1.instanceMark === mark) {
        this.instance1.displayTable = true
        this.instance1.displayNav = true
        this.instance1.displayTool = true
        this.instance1.navSize += this.instance1.tableSize
        const diff = this.instance1.tableSize - this.size.half
        this.instance1.tableSize = diff > 0 ? diff : 0
        if (this.instance1.tableSize === 0) {
          this.instance1.displayTable = false
        }
      } else {
        this.instance2.displayTable = true
        this.instance2.displayNav = true
        this.instance2.displayTool = true
        this.instance2.navSize += this.instance2.tableSize
        const diff = this.instance2.tableSize - this.size.half
        this.instance2.tableSize = diff > 0 ? diff : 0
        if (this.instance2.tableSize === 0) {
          this.instance2.displayTable = false
        }
        console.info("show nav: " + this.instance2.navSize)
        console.info("show table: " + this.instance2.tableSize)
      }
    },
    hideFullNav(mark) {
      if (this.instance1.instanceMark === mark) {
        this.instance1.displayTable = true
        this.instance1.displayNav = false
        this.instance1.displayTool = true
        this.instance1.tableSize += this.instance1.navSize - 1
        this.instance1.navSize = 1
      } else {
        this.instance2.displayTable = true
        this.instance2.displayNav = false
        this.instance2.displayTool = true
        this.instance2.tableSize += this.instance2.navSize - 1
        this.instance2.navSize = 1
        console.info("hide nav: " + this.instance2.navSize)
        console.info("hide table: " + this.instance2.tableSize)
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
  },

}
</script>

<style scoped>
.empty-table- {
  height: 524px;
  overflow-y: auto;
}

</style>
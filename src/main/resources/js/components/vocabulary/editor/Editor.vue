<template>
  <div v-if="show" class="row m-0 p-0 justify-content-between" style="width: 100%">
    <div v-if="instance1.show"
         v-show="instance1.displayNav || instance1.displayTool"
         :class="instance1.displayNav?'col-'+instance1.navSize:''"
         class="m-0 p-0"
    >
      <div class="row justify-content-between no-gutters">
        <div class="col m-0 p-0"
             v-show="instance1.displayNav"
             style="width: 100%;"
        >
          <dictionary-nav
              :instance="instance1"
              @loadDictionary="loadDictionary"
          ></dictionary-nav>
        </div>
        <div class="col m-0 p-0"
             v-show="instance1.displayTool"
             style="max-width: 25px;"
        >
          <vertical-tools
              :instance="instance1"
              @showHideInstance="showHideInstance"
              @showFullNav="showFullNav"
              @hideFullNav="hideFullNav"
              @stepUpNav="stepUpNav"
              @stepDownNav="stepDownNav"
          >
          </vertical-tools>
        </div>
      </div>
    </div>
    <div v-if="instance1.show" v-show="instance1.displayTable"
         :class="'col-' + instance1.tableSize"
         class="m-0 p-0"
         style="width: 100%;"
    >
      <div class="row justify-content-between no-gutters">
        <div v-if="instance1.dictionary"
             class="col m-0 p-0"
             :class="isTwoSourcePresent()?'st-two-source':'st-one-source'">
          <card-table
              :dictionary="instance1.dictionary"
              :instanceMark="instance1.instanceMark"
          ></card-table>
        </div>
        <div v-else
             class="col m-0 p-0"
        >
          <div class="empty-table- container d-flex justify-content-center align-items-center p-0 m-0">
            <div class="row no-gutters text-center">
              <h6>{{ getCapitalizeLang('chooseDictionary') }}</h6>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="instance2.show" v-show="instance2.displayTable"
         :class="'col-' + instance2.tableSize"
         class="m-0 p-0"
         style="width: 100%;"
    >
      <div class="row justify-content-between no-gutters">
        <div v-if="instance2.dictionary"
             class="col m-0 p-0"
             :class="isTwoSourcePresent()?'st-two-source':'st-one-source'">
          <card-table
              :dictionary="instance2.dictionary"
              :instanceMark="instance2.instanceMark"
          ></card-table>
        </div>
        <div v-else
             class="col m-0 p-0"
        >
          <div class="empty-table- container d-flex justify-content-center align-items-center p-0 m-0">
            <div class="row no-gutters text-center">
              <h6>{{ getCapitalizeLang('chooseDictionary') }}</h6>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="instance2.show"
         v-show="instance2.displayNav || instance2.displayTool"
         :class="instance2.displayNav?'col-' + instance2.navSize:''"
         class="m-0 p-0"
    >
      <div class="row justify-content-between no-gutters">
        <div class="col m-0 p-0"
             v-show="instance2.displayTool"
             style="max-width: 25px;"
        >
          <vertical-tools
              :instance="instance2"
              @showHideInstance="showHideInstance"
              @showFullNav="showFullNav"
              @hideFullNav="hideFullNav"
              @stepUpNav="stepUpNav"
              @stepDownNav="stepDownNav"
          >
          </vertical-tools>
        </div>
        <div class="col m-0 p-0"
             v-show="instance2.displayNav"
             style="width: 100%;"
        >
          <dictionary-nav
              :instance="instance2"
              @loadDictionary="loadDictionary"
          ></dictionary-nav>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {mapState, mapGetters} from 'vuex'
import CardTable from './CardTable.vue'
import DictionaryNav from './DictionaryNav.vue'
import VerticalTools from "./VerticalTools.vue";
import * as _ from "lodash";

export default {
  props: [],
  components: {
    VerticalTools,
    CardTable,
    DictionaryNav,
  },
  created() {

  },
  computed: {
    ...mapState([
      'cards',
      'lang',
    ]),
    ...mapGetters([
      'getDictionaryById',
    ])
  },
  data() {
    return {
      show: true,
      size: {
        all: 12,
        half: 6,
        parts: 2,
      },
      instance1: {
        show: true,
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
    }
  },
  methods: {
    fetchData() {
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
  },

}
</script>

<style scoped>
.empty-table- {
  height: 550px;
  overflow-y: auto;
}

</style>
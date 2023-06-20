<template>
  <div v-if="show" class="row m-0 p-0 justify-content-between" style="width: 100%">
    <div v-if="instance1.show" class="col-2 w-10 m-0 p-0">
      <div class="row justify-content-start no-gutters">
        <div class="col-10 m-0 p-0">
          <dictionary-nav
              :instance="instance1"
              @loadDictionary="loadDictionary"
          ></dictionary-nav>
        </div>
        <div class="col-2 m-0 p-0 d-flex">
          <vertical-tools
              :instance="instance1"
              @showHideInstance="showHideInstance"
          >
          </vertical-tools>
        </div>
      </div>
    </div>
    <div v-if="instance1.show" class="col-4 w-40 m-0 p-0">
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

    <div v-if="instance2.show" class="col-4 w-40 m-0 p-0">
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
    <div v-if="instance2.show" class="col-2 w-10 m-0 p-0">
      <div class="row justify-content-end no-gutters">
        <div class="col-2 m-0 p-0 d-flex">
          <vertical-tools
              :instance="instance2"
              @showHideInstance="showHideInstance"
          >
          </vertical-tools>
        </div>
        <div class="col-10 m-0 p-0">
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
      instance1: {
        show: true,
        instanceMark: "id1",
        dictionary: null,
      },
      instance2: {
        show: true,
        instanceMark: "id2",
        dictionary: null,
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
        this.instance2.show = !this.instance2.show
      }
      if (instanceMark === this.instance2.instanceMark) {
        this.instance1.show = !this.instance1.show
      }
    },
    getLang(key) {
      return this.$t(key)
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
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
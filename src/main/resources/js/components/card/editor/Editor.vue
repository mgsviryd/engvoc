<template>
  <div class="row m-0 p-0 justify-content-between" style="width: 100%">
    <div class="col-1 m-0 p-0">
      <dictionary-nav
          :instance-mark="instance1.instanceMark"
          @loadDictionary="loadDictionary"
      ></dictionary-nav>
    </div>

    <div class="col-10 m-0 p-0">
      <div class="row m-0 p-0 justify-content-center" style="width: 100%">

        <div v-if="instance1.dictionary"
            class="col m-0 p-0"
             :class="isTwoSourcePresent()?'st-two-source':'st-one-source'">
          <card-table
                      :dictionary="instance1.dictionary"
                      :instanceMark="instance1.instanceMark"
          ></card-table>
        </div>

        <div v-if="instance2.dictionary"
             class="col m-0 p-0"
             :class="isTwoSourcePresent()?'st-two-source':'st-one-source'">
          <card-table
                      :dictionary="instance2.dictionary"
                      :instanceMark="instance2.instanceMark"
          ></card-table>
        </div>
      </div>
    </div>

    <div class="col-1 m-0 p-0">
      <dictionary-nav
          :instance-mark="instance2.instanceMark"
          @loadDictionary="loadDictionary"
      ></dictionary-nav>
    </div>
  </div>
</template>

<script>
import {mapState} from "vuex";
import CardTable from "./CardTable.vue";
import DictionaryNav from "./DictionaryNav.vue";

export default {
  components: {
    CardTable,
    DictionaryNav,
  },
  data() {
    return {
      instance1: {
        instanceMark: "table1",
        dictionary: null,
      },
      instance2: {
        instanceMark: "table2",
        dictionary: null,
      },
    }
  },
  computed: {
    ...mapState([
      'cards',
      'lang',
    ]),
  },
  methods: {
    fetchData() {
    },
    loadDictionary(d, instanceMark) {
      if (instanceMark === this.instance1.instanceMark) {
        this.instance1.dictionary = d
      }
      if (instanceMark === this.instance2.instanceMark) {
        this.instance2.dictionary = d
      }
      return []
    },
    isTwoSourcePresent(){
      return this.instance1.dictionary !== null && this.instance2.dictionary !== null
    },
  },

}
</script>

<style scoped>

</style>
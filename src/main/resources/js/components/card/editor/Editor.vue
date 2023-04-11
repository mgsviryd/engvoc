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
      <div class="=col m-0 p-0">
        <card-table v-if="instance1.sourceMark"
                    :sourceMark="instance1.sourceMark"
                    :sourceId="instance1.sourceId"
                    :rowToScrollId="instance1.rowToScrollId"
                    :instance="instance1.instance"
        ></card-table>
      </div>

      <div class="col m-0 p-0">
        <card-table v-if="instance2.sourceMark"
            :sourceMark="instance2.sourceMark"
                    :sourceId="instance2.sourceId"
                    :rowToScrollId="instance2.rowToScrollId"
                    :instance="instance2.instance"
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
        instanceMark: "l-",
        sourceMark: null,
        sourceId: -1,
        rowToScrollId: -1,
      },
      instance2: {
        instanceMark: "r-",
        sourceMark: null,
        sourceId: -1,
        rowToScrollId: -1,
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
    loadDictionary(sourceId, sourceMark, instanceMark){
        if(instanceMark === this.instance1.instanceMark){
          this.instance1.sourceMark = sourceMark
          this.instance1.sourceId = sourceId
        }
        if (instanceMark === this.instance2.instanceMark){
          this.instance2.sourceMark = sourceMark
          this.instance2.sourceId = sourceId
        }
      return []
    }
  },

}
</script>

<style scoped>

</style>
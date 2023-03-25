<template>
    <div class="row m-0 p-0 justify-content-between" style="width: 100%">
      <div class="col-1 m-0 p-0">
        <dictionary-nav
            :inst="instance1"
            @loadDictionary="loadDictionary"
        ></dictionary-nav>
      </div>

      <div class="col-10 m-0 p-0">
        <div class="row m-0 p-0 justify-content-center" style="width: 100%">
      <div class="=col m-0 p-0">
        <card-table v-if="props1.markSource"
                    :mark-source="props1.markSource"
                    :dictionaryId="props1.dictionaryId"
                    :rowToScrollId="props1.rowToScrollId"
        ></card-table>
      </div>

      <div class="col m-0 p-0">
        <card-table v-if="props2.markSource"
            :mark-source="props2.markSource"
                    :dictionaryId="props2.dictionaryId"
                    :rowToScrollId="props2.rowToScrollId"
        ></card-table>
      </div>
      </div>
      </div>

      <div class="col-1 m-0 p-0">
        <dictionary-nav
            :inst="instance2"
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
      upload: "upload",
      db: "db",
      instance1: "left",
      instance2: "right",
      props1: {
        markSource: null,
        dictionaryId: -1,
        rowToScrollId: -1,
      },
      props2: {
        markSource: null,
        dictionaryId: -1,
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
    loadDictionary(i, markSource, instance){
        if(instance === this.instance1){
          this.props1.markSource = markSource
          this.props1.dictionaryId = i
        }
        if (instance === this.instance2){
          this.props2.markSource = markSource
          this.props2.dictionaryId = i
        }
      return []
    }
  },

}
</script>

<style scoped>

</style>
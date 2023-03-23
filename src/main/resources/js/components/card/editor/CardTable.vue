<template>
  <div class="card-table-">
    <table class="table table-light table-hover table-bordered border-2 border-dark table-sm">
      <thead class="thead-dark b-table-no-border-collapse" style="position: sticky; top: 0;">
      <tr>
        <th class="st-squeeze border-2 border-secondary border-left-0">N</th>
        <th class="border-2 border-secondary">{{ word }}</th>
        <th class="border-2 border-secondary">{{ translation }}</th>
        <th class="border-2 border-secondary">{{ example }}</th>
        <th class="st-squeeze border-2 border-secondary border-right-0"></th>
      </tr>
      </thead>
      <tbody class="mt-4">

      <table-row v-for="(card,i) in dictionaryCards"
                 :key="`A-${i}`"
                 :card="card"
                 :cardN="i"
      >
      </table-row>
      </tbody>
    </table>
  </div>
</template>

<script>
import {mapState} from "vuex";
import TableRow from "./CardRow.vue";
import string from "../../../util/string";

export default {
  props: [
    'markSource',
    'dictionaryInx',
    'rowToScrollId',
  ],
  components: {
    TableRow,
  },
  watch: {
    $route: 'fetchData',
  },
  data() {
    return {}
  },
  computed: {
    ...mapState([
      'cards',
      'lang',
    ]),
    word() {
      return string.getWithFirstCapital(this.lang.map.word)
    },
    translation() {
      return string.getWithFirstCapital(this.lang.map.translation)
    },
    example() {
      return string.getWithFirstCapital(this.lang.map.example)
    },
    learned() {
      return string.getWithFirstCapital(this.lang.map.learned)
    },
    dictionaryCards() {
      if (this.markSource === "upload") {
        return this.cards.upload.cards[this.dictionaryInx]
      }
      if (this.markSource === "db") {
        return this.$store.getters.getCardsDBByDictionaryInx(this.dictionaryInx)
      }
      return []
    }
  },
  methods: {
    fetchData() {
    },
    drag(card, i) {
      console.info("DRAG")
      let payload = {
        type: "card",
        pull: "replace", // copy
        push: "add", // replace
        item: card,
        id: i,
        markSource: this.markSource,
        sourceInx: this.dictionaryInx
      }
      this.store.commit('dragMutation', payload)
    },
    drop(card, i) {
      console.info("DROP")
      let payload = {
        type: "card",
        pull: "replace", // copy
        push: "add", // replace
        item: card,
        id: i,
        markSource: this.markSource,
        sourceInx: this.dictionaryInx
      }
      this.store.commit('dropMutation', payload)
    }

  },
}
</script>

<style scoped>
.card-table- {
  height: 550px;
  overflow-y: auto;
}

table {
  font-size: 15px;
  font-family: Calibri;
}

th, td:not(.st-squeeze) {
  width: 8%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.st-squeeze {
  width: 1%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
<template>
  <div v-if="show" class="card-table-"
       @dragover="dragover(null, -1)"
       @dragleave="dragleave(null,-1)"
       draggable="true"
  >

    <div class="d-block bg-dark" style="width: 100%;">
      <div class="btn-group btn-group-sm btn-group-justified">
        <button class="btn btn-light m-1 py-0 px-1"
        >
          <small>
            <b>
              SELECTED
            </b>
          </small>
        </button>
      </div>
    </div>
    <table class="table caption-top bg-white table-hover table-bordered border-2 border-dark table-sm"
    >
      <thead class="thead-dark b-table-no-border-collapse" style="position: sticky; top: 0;">
      <tr>
        <th class="st-squeeze border-2 border-secondary border-left-0">N</th>
        <th class="border-2 border-secondary">{{ word }}</th>
        <th class="border-2 border-secondary">{{ translation }}</th>
        <th class="border-2 border-secondary">{{ example }}</th>
        <th class="st-squeeze border-2 border-secondary border-right-0"></th>
      </tr>
      </thead>
      <tbody class="mt-4"
             draggable="true"
             @dragenter="preventDragdropNowhere()"
      >

      <tr v-for="(card,i) in dictionaryCards"
          v-model="dictionaryCards"
          :key="`A-${card.id}`"
          :id="getCardElementId(card.id)"
          @dragstart="dragstart(card, i)"
          @dragover="dragover(card, i)"
          @dragleave="dragleave(card,i)"
          @click.prevent.stop="selectCard(card)"
          draggable="true"
      >
        <td class="st-squeeze border-secondary border-1 border-left-0">{{ i + 1 }}</td>
        <td class="st-text-shift border-secondary border-1">{{ card.word }}</td>
        <td class="st-text-shift border-secondary border-1">{{ card.translation }}</td>
        <td class="st-text-shift border-secondary border-1">
          {{ card.example }}
          <br/>
          {{ card.exampleTranslation }}
        </td>
        <td class="st-squeeze border-secondary border-1 border-right-0"><input type="checkbox">
        </td>
      </tr>
      </tbody>
    </table>
    <div v-if="showEmpty"
         class="blank container-fluid d-block"
         @dragover="dragover(null, -1)"
         @dragleave="dragleave(null,-1)"
         draggable="true"
    >
    </div>
  </div>
</template>

<script>
import {mapState} from "vuex";
import string from "../../../util/string";
import * as _ from 'lodash'

export default {
  created() {
    this.$root.$on('confirm-dragstart', (dragdrop) => {
      if (this.isCurrentDragstart(dragdrop) && !this.isInsideSameSource(dragdrop)) {
        this.confirmDragstart(dragdrop)
      }
    })
    this.$root.$on('confirm-dragleave', (dragdrop) => {
      if (this.isCurrentDragleave(dragdrop) && !this.isInsideSameSource(dragdrop)) {
        this.confirmDragleave(dragdrop)
      }
    })

    this.fetchData()
    this.showEmpty = typeof this.dictionaryCards === 'undefined' || this.dictionaryCards.length === 0;
  },
  props: [
    'markSource',
    'dictionaryId',
    'rowToScrollId',
  ],
  components: {},
  watch: {
    $route: [
      'fetchData',
    ],

    storeDictionaryCards() {
      this.fetchData()
    }
  },
  data() {
    return {
      card: {
        word: null,
        translation: null,
        example: null,
        exampleTranslation: null,
      },
      dictionaryCards: [],
      selectedCardIds: [],
      show: true,
      showEmpty: false,
      type: "card",
      pull: "delete",
      dragstartType: null,
      dragdrop: {
        start: {
          type: "",
          operation: "add", // add / update // add-update
          ldt: null,
          pull: "", // preserve / delete
          pullItems: [],
          actions: {
            removeAllAction: "",
            addAllAction: "",
            updateAllAction: "",
            addUpdateAllAction: "",
          },
          markSource: "",
          pullSourceId: -1,
        },
        over: {
          type: "",
          ldt: null,
        },
        leave: {
          type: "",
          ldt: null,
          push: "", // preserve / delete
          pushItems: [],
          removeAllAction: "",
          addAllAction: "",
          updateAllAction: "",
          addUpdateAllAction: "",
          markSource: "",
          pushSourceId: -1,
        },
      },
    }
  },
  computed: {
    ...mapState([
      'cards',
      'lang',
    ]),
    storeDictionaryCards() {
      if (this.markSource === "upload") {
        return this.$store.getters.getCardsUploadByDictionaryId(this.dictionaryId)
      }
      if (this.markSource === "db") {
        return this.$store.getters.getCardsDbByDictionaryId(this.dictionaryId)
      }
      return []
    },
    removeAllAction() {
      return string.convertLowerToCamelCase([this.type, this.markSource, "remove", "all", "action"])
    },
    addAllAction() {
      return string.convertLowerToCamelCase([this.type, this.markSource, "add", "all", "action"])
    },
    updateAllAction() {
      return string.convertLowerToCamelCase([this.type, this.markSource, "update", "all", "action"])
    },
    addUpdateAllAction() {
      return string.convertLowerToCamelCase([this.type, this.markSource, "add", "update", "all", "action"])
    },
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
  },
  methods: {
    fetchData() {
      this.show = false
      if (this.isSourceExists()) {
        this.show = true
        this.showEmpty = typeof this.storeDictionaryCards === 'undefined' || this.storeDictionaryCards.length === 0
        this.dictionaryCards = this.storeDictionaryCards
        this.updateSelected()
      }
    },
    updateSelected() {
      this.selectedCardIds = this.selectedCardIds.filter(id => this.dictionaryCards.findIndex(x => x.id === id) >= 0)
    },
    getCardElementId(id) {
      return "card" + id
    },
    isSelected(card) {
      return this.selectedCardIds.indexOf(card.id) >= 0
    },
    selectCard(card) {
      const inx = this.selectedCardIds.findIndex(id => id === card.id)
      if (inx < 0) {
        this.selectedCardIds.push(card.id)
        $("#" + this.getCardElementId(card.id)).addClass("card-select")
      } else {
        this.selectedCardIds.splice(inx, 1)
        $("#" + this.getCardElementId(card.id)).removeClass("card-select")
      }
    },
    isSourceExists() {
      if (this.markSource === "upload") {
        return this.$store.getters.isUploadDictionaryExists(this.dictionaryId)
      }
      if (this.markSource === "db") {
        return this.$store.getters.isDbDictionaryExists(this.dictionaryId)
      }
    },

    isInsideSameSource(dragdrop) {
      return dragdrop.start.markSource === dragdrop.leave.markSource && dragdrop.start.pullSourceId === dragdrop.leave.pushSourceId
    },
    isCurrentDragstart(dragdrop) {
      return this.dragdrop.start.type === dragdrop.start.type && this.dragdrop.start.ldt === dragdrop.start.ldt
    },
    isCurrentDragleave(dragdrop) {
      return this.dragdrop.leave.type === dragdrop.leave.type && this.dragdrop.leave.ldt === dragdrop.leave.ldt
    },
    confirmDragstart(dragdrop) {
      if (dragdrop.start.pull === "delete") {
        this.$store.dispatch(dragdrop.start.actions.removeAllAction,
            {
              cards: dragdrop.start.pullItems,
              id: dragdrop.start.pullSourceId
            })
      }
    },
    confirmDragleave(dragdrop) {
      if (dragdrop.leave.push === "delete") {
        this.$store.dispatch(dragdrop.leave.actions.removeAllAction,
            {
              cards: dragdrop.start.pushItems,
              id: dragdrop.start.pushSourceId
            })
      }
      if (dragdrop.start.operation === "add") {
        this.$store.dispatch(dragdrop.leave.actions.addAllAction,
            {
              cards: dragdrop.start.pullItems,
              id: dragdrop.leave.pushSourceId
            })
      }
      if (dragdrop.start.operation === "update") {
        this.$store.dispatch(dragdrop.leave.actions.updateAllAction,
            {
              cards: dragdrop.start.pullItems,
              id: dragdrop.leave.pushSourceId
            })
      }
      if (dragdrop.start.operation === "addUpdate") {
        this.$store.dispatch(dragdrop.leave.actions.addUpdateAllAction,
            {
              cards: dragdrop.start.pullItems,
              id: dragdrop.leave.pushSourceId
            })
      }
    },

    preventDragdropNowhere() {
      // console.info("dragenterNowhere")
      let payload = {
        type: "Nowhere",
        ldt: new Date(),
      }
      this.$root.$emit('dragover', payload)
    },
    dragstart(card, i) {
      // console.info("dragstart: " + this.dictionaryId)
      let items = []
      if (this.isSelected(card)) {
        items = this.dictionaryCards.filter(x => this.selectedCardIds.findIndex(id => id === x.id) >= 0)
      } else {
        items.push(card)
      }
      let payload = {
        type: "card",
        operation: "add", // add / update / add-update
        ldt: new Date(),
        pull: "delete", // preserve / delete
        pullItems: items,
        actions: {
          removeAllAction: this.removeAllAction,
          addAllAction: this.addAllAction,
          updateAllAction: this.updateAllAction,
          addUpdateAllAction: this.addUpdateAllAction,
        },
        markSource: this.markSource,
        pullSourceId: this.dictionaryId,
      }
      this.dragdrop.start = payload
      this.$root.$emit('dragstart', payload)
    },
    dragover: _.throttle(function (card, i) {
      // console.info("dragover: " + this.dictionaryId)
      let payload = {
        type: "card",
        ldt: new Date(),
      }
      this.dragdrop.over = payload
      this.$root.$emit('dragover', payload)
    }, 30),

    dragleave: _.throttle(function (card, i) {
      // console.info("dragleave: " + this.dictionaryId)
      const items = []
      if (card !== null) {
        items.push(card)
      }
      const payload = {
        type: "card",
        ldt: new Date(),
        push: "preserve", // preserve / delete
        pushItems: items,
        actions: {
          removeAllAction: this.removeAllAction,
          addAllAction: this.addAllAction,
          updateAllAction: this.updateAllAction,
          addUpdateAllAction: this.addUpdateAllAction,
        },
        markSource: this.markSource,
        pushSourceId: this.dictionaryId,
      }
      this.dragdrop.leave = payload
      this.$root.$emit('dragleave', payload)
    }, 30),
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

th, td:not(.st-squeeze, .st-text-shift) {
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

.st-text-shift {
  width: 150px;
  overflow-wrap: anywhere;
}

.blank {
  height: 500px;
  overflow-y: auto;
}

.card-select {
  background-color: #eaeaea;
}

.draw-dragover {
  background-color: red;
}

</style>
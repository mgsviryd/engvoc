<template>
  <div v-if="show" class="card-table-">
    <table class="table bg-white table-hover table-bordered border-2 border-dark table-sm">
      <thead class="thead-dark"
             style="position: sticky; top: 0;"

      >
      <tr class="bg-dark border-0"
          style="position: sticky;"
      >
        <th colspan="10" class="border-0 p-0">
          <div class="btn-group btn-group-sm btn-group-justified">
            <button class="btn btn-light m-1 py-0 px-1"
                    @mousedown.prevent.stop="mousedownSelected()">
              <span class="st-text-shift">
          <small><b>SELECTED</b></small>
        </span>
              <span class="st-right badge badge-light bg-white border badge-pill">{{ selectedCardIds.length }}</span>
            </button>
          </div>
        </th>
      </tr>
      <tr class="border-0">
        <th class="st-squeeze border-0 border-left-0">N</th>
        <th class="border-0">{{ word }}</th>
        <th class="border-0">{{ translation }}</th>
        <th class="border-0">{{ example }}</th>
        <th class="st-squeeze border-0 border-right-0"></th>
      </tr>
      </thead>
      <tbody class="mt-4">

      <tr v-for="(card,i) in dictionaryCards"
          v-model="dictionaryCards"
          :key="`A-${card.id}`"
          :id="getCardElementId(card.id)"
          @mousedown.prevent.stop="mousedown(card, i)"
          @mouseup.prevent.stop="mouseup(card, i)"
          draggable="true"
          @click.prevent.stop="selectCard(card)"
      >
        <td class="st-squeeze border-1 border-secondary border-left-0">{{ i + 1 }}</td>
        <td class="st-text-shift border-1 border-secondary">{{ card.word }}</td>
        <td class="st-text-shift border-1 border-secondary">{{ card.translation }}</td>
        <td class="st-text-shift border-1 border-secondary">
          {{ card.example }}
          <br/>
          {{ card.exampleTranslation }}
        </td>
        <td class="st-squeeze border-1 border-secondary border-right-0"><input type="checkbox">
        </td>
      </tr>
      </tbody>
    </table>
    <div v-if="showEmpty"
         :id="blankElementId"
         class="blank container-fluid d-block"
         @mouseup="mouseup(null,-1)"
    >
    </div>
    <GlobalEvents @mouseup="mouseupOutside()"/>
  </div>
</template>

<script>
import {mapGetters, mapState} from "vuex";
import string from "../../../util/string";
import * as _ from 'lodash'

export default {
  created() {
    this.$root.$on('dragdrop-init', (payload) => {
      this.dragdropInit(payload)
    })
    this.$root.$on('dragdrop-destroy', () => {
      this.dragdropDestroy()
    })

    this.fetchData()
    this.showEmpty = typeof this.dictionaryCards === 'undefined' || this.dictionaryCards.length === 0;
  },
  props: [
    'instance',
    'sourceId',
    'rowToScrollId',
  ],
  components: {},
  watch: {
    $route: [
      'fetchData',
    ],
    storeDictionaryCards() {
      this.fetchData()
    },
  },
  computed: {
    ...mapState([
      'cards',
      'lang',
    ]),
    ...mapGetters([
      'isDictionaryExists',
      'getCardsByDictionaryId',
    ]),
    storeDictionaryCards() {
      return this.getCardsByDictionaryId(this.sourceId)
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
  data() {
    return {
      name: "card-table-",
      card: {
        word: null,
        translation: null,
        example: null,
        exampleTranslation: null,
      },
      show: true,
      showEmpty: false,
      dictionaryCards: [],
      selectedCardIds: [],
      blankElementId: this.instance + this.name + "blank",

      groups: ["cardsChangeDictionary"],
      sourceMark: "cards",
      isMouseInClick: false,
      groupsInProcess: [],
      isDragdropInside: false,
      isDragdropSplice: false,
      dragCards: [],
    }
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
      return this.instance + this.name + id
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
      return this.isDictionaryExists(this.sourceId)
    },

    mousedown(card, i) {
      // console.info("mousedown: "+ this.sourceId)
      this.isMouseInClick = true
      setTimeout(() => {
        if (this.isMouseInClick) {
          this.groupsInProcess = this.groups
          let items = []
          if (card) {
            items.push(card)
            this.dragCards = items
          }
          this.activateDragstartStyle(items)
          this.$root.$emit("dragdrop-init", {groups: this.groups})
          const start = {
            groups: this.groups,
            data: {
              items: items,
              sourceMark: this.sourceMark,
              sourceId: this.sourceId,
            },
          }
          this.$store.dispatch("dragdropStartAction", start)
        }
      }, 2)
    },
    mousedownSelected() {
      // console.info("mousedownSelected: "+ this.sourceId)
      this.isMouseInClick = true
      setTimeout(() => {
        if (this.isMouseInClick) {
          if (this.selectedCardIds.length < 1) {
            return
          }
          this.groupsInProcess = this.groups
          const items = this.dictionaryCards.filter(card => this.selectedCardIds.findIndex(id => id === card.id) >= 0)
          this.dragCards = items
          this.activateDragstartStyle(items)
          this.$root.$emit("dragdrop-init", {groups: this.groups})
          const start = {
            groups: this.groups,
            data: {
              items: items,
              sourceMark: this.sourceMark,
              sourceId: this.sourceId,
            },
          }
          this.$store.dispatch("dragdropStartAction", start)
        }
      }, 2)
    },
    mouseup(card, i) {
      // console.info("mouseup: "+ this.sourceId)
      this.isMouseInClick = false
      if (this.groupsInProcess.length > 0) {
        this.$root.$emit("dragdrop-destroy")
        const items = []
        if (card) {
          items.push(card)
        }
        const end = {
          groups: this.groups,
          options: {
            pull: "delete", // preserve / delete
            push: "preserve", // preserve / delete
            operation: "add", // add / update / add-update
            isDragdropInside: false,
            isDragdropSplice: false,
          },
          data: {
            items: items,
            sourceMark: this.sourceMark,
            sourceId: this.sourceId,
          }
        }
        this.$store.dispatch("dragdropEndAndExecuteAction", end)
      }
    },
    dragdropInit(payload) {
      this.setFilteredGroupsInProcess(payload.groups)
      this.activateDragoverStyle(this.dictionaryCards)
    },
    dragdropDestroy() {
      this.deactivateDragstartStyle(this.dragCards)
      this.deactivateDragoverStyle(this.dictionaryCards)
      this.isMouseInClick = false
      this.groupsInProcess = []
      this.dragCards = []
    },
    mouseupOutside() {
      if (this.isDragdropInProcess()) {
        this.dragdropDestroy()
      }
    },
    isDragdropInProcess() {
      return this.groupsInProcess.length > 0
    },
    async setFilteredGroupsInProcess(groups) {
      this.groupsInProcess = this.groups.filter(x => groups.indexOf(x) >= 0)
    },
    async activateDragstartStyle(cards) {
      cards.forEach(card => $("#" + this.getCardElementId(card.id)).addClass("dragstart"))
    },
    async deactivateDragstartStyle(cards) {
      cards.forEach(card => $("#" + this.getCardElementId(card.id)).removeClass("dragstart"))
    },
    async activateDragoverStyle(cards) {
      cards.forEach(card => $("#" + this.getCardElementId(card.id)).addClass("dragover"))
      $("#" + this.blankElementId).addClass("dragover")
    },
    async deactivateDragoverStyle(cards) {
      cards.forEach(card => $("#" + this.getCardElementId(card.id)).removeClass("dragover"))
      $("#" + this.blankElementId).removeClass("dragover")
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

.dragover:hover {
  border-style: solid;
  border-color: green;
  background-color: greenyellow;
}

.dragstart {
  border-style: solid;
  border-color: red;
  background-color: pink;
}

</style>
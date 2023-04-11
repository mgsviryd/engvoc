<template>
  <div v-if="show" class="card-table-">
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
      <tbody class="mt-4">

      <tr v-for="(card,i) in dictionaryCards"
          v-model="dictionaryCards"
          :key="`A-${card.id}`"
          :id="getCardElementId(card.id)"
          @mouseup.prevent.stop="mouseup($event, card, i)"
          @mousedown.prevent.stop="mousedown($event, card, i)"
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
         @mouseup="mouseup($event,null,-1)"
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
    this.$root.$on('dragdrop-init', (payload) => {
      this.activateDragoverStyle()
      this.setFilteredGroupsInProcess(payload.groups)
    })
    this.$root.$on('dragdrop-destroy', () => {
      this.deactivateDragstartStyle()
      this.deactivateDragoverStyle()
      this.isMouseInClick = false
      this.groupsInProcess = []
      this.dragCards = []
    })

    this.fetchData()
    this.showEmpty = typeof this.dictionaryCards === 'undefined' || this.dictionaryCards.length === 0;
  },
  props: [
    'inst',
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
    },
  },
  data() {
    return {
      card: {
        word: null,
        translation: null,
        example: null,
        exampleTranslation: null,
      },
      name: "card-table",
      show: true,
      showEmpty: false,
      dictionaryCards: [],
      selectedCardIds: [],
      groups: ["card"],
      isMouseInClick: false,
      groupsInProcess: [],
      dragCards: [],
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
      return this.inst + this.name + "card" + id
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

    mousedown(event, card, i) {
      // console.info("mousedown: "+ this.dictionaryId)
      this.isMouseInClick = true
      setTimeout(() => {
        if (this.isMouseInClick) {
          this.$root.$emit("dragdrop-init", {groups: this.groups})
          this.groupsInProcess = this.groups
          const items = []
          if (card) {
            event.target.closest("tr").classList.add("dragstart")
            items.push(card)
            this.dragCards = items
          }
          const start = {
            groups: this.groups,
            data: {
              items: items,
              sourceMark: this.markSource,
              sourceId: this.dictionaryId,
            },
          }
          this.$store.dispatch("dragdropStartAction", start)
        }
      }, 2)
    },
    mouseup(event, card, i) {
      // console.info("mouseup: "+ this.dictionaryId)
      this.isMouseInClick = false
      if (this.groupsInProcess.length > 0) {
        this.deactivateDragoverStyle()
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
            isMoveInside: false,
          },
          data: {
            items: items,
            sourceMark: this.markSource,
            sourceId: this.dictionaryId,
          }
        }
        this.$store.dispatch("dragdropEndAndExecuteAction", end)
      }
    },
    deactivateDragstartStyle(){
      this.dragCards.forEach(card => $("#" + this.getCardElementId(card.id)).removeClass("dragstart"))
    },
    deactivateDragoverStyle() {
      this.dictionaryCards.forEach(card => $("#" + this.getCardElementId(card.id)).removeClass("dragover"))
    },
    activateDragoverStyle() {
      this.dictionaryCards.forEach(card => $("#" + this.getCardElementId(card.id)).addClass("dragover"))
    },
    setFilteredGroupsInProcess(groups) {
      this.groupsInProcess = this.groups.filter(x => groups.indexOf(x) >= 0)
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
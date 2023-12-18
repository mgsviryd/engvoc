<template>
  <div v-if="show"
       class="card-table- container p-0 m-0">
    <table class="table bg-white table-hover table-bordered border-2 border-dark table-sm">
      <thead class="thead-dark"
             style="position: sticky; top: 0;"
      >

      <tr class="bg-dark border-0"
          style="position: sticky;"
      >
        <th class="border-0 p-0" colspan="10">
          <b-button-toolbar>
            <b-button-group size="sm">
              <b-button
                  class="shadow-none px-1 py-0"
                  variant="light"
                  @click="$refs[ids.tableSettingsModal].showModal()"
              >
                <i class="fa fa-gear fa-xs text-secondary"></i>
              </b-button>
            </b-button-group>

            <b-button-group size="sm" class="mx-1">
              <b-button
                  v-if="selectedCardIds.length > 0"
                  :data-delay="deselectAllSetting.tooltip.delay"
                  :data-placement="deselectAllSetting.tooltip.placement"
                  :title="getCapitalizeLang(deselectAllSetting.tooltip.title)"
                  class="shadow-none px-1 py-0"
                  data-toggle="tooltip"
                  variant="light"
                  @click.prevent.s.stop="deselectAll()"
              >
                <i class="fa fa-xmark fa-xs text-danger"></i>
              </b-button>

              <b-button
                  v-if="selectedCardIds.length === 0"
                  :data-delay="selectAllSetting.tooltip.delay"
                  :data-placement="selectAllSetting.tooltip.placement"
                  :title="getCapitalizeLang(selectAllSetting.tooltip.title)"
                  class="shadow-none px-1 py-0"
                  data-toggle="tooltip"
                  variant="light"
                  @click.prevent.stop="selectAll()"
              >
                <i class="fa fa-check fa-xs text-success"></i>
              </b-button>

              <b-button
                  v-b-tooltip="{trigger: 'hover focus', delay: { 'show': 800, 'hide': 40 }, placement: 'bottomright'}"
                  :title="getCapitalizeLang('selected')"
                  class="px-0 py-0"
                  variant="light"
                  @mousedown.prevent.stop="mousedownSelected()"
              >
              <span class="st-text-shift">
                {{ getCapitalizeLang('abbrSelected') }}
              </span>
                <span class="st-right badge bg-white border border-secondary badge-pill">
                  <span v-if="selectedCardIds.length<100">&nbsp;</span>
                  <span v-if="selectedCardIds.length<10">&nbsp;</span>
                {{ selectedCardIds.length }}
              </span>
              </b-button>
            </b-button-group>

            <b-button-group size="sm">
              <b-button
                  class="shadow-none px-1 py-0"
                  variant="light"
                  @click="$refs[ids.addCardModal].showModal()"
              >
                <i class="fa fa-plus fa-xs text-success"></i>
                {{ getLang('card') }}
              </b-button>
            </b-button-group>

            <b-button-group size="sm" class="mx-1">
              <download-dropdown
                  :id="ids.downloadDropdown"
                  :ref="ids.downloadDropdown"
                  :dictionary="dictionary"
                  :side="instanceMark"
                  style="position:fixed;"
              ></download-dropdown>
            </b-button-group>
          </b-button-toolbar>
        </th>
      </tr>
      <tr class="border-0">
        <th class="st-squeeze border-0 border-left-0 py-0">
          {{ getUpperCaseLang('abbrNumber') }}
        </th>
        <template v-for="(property,i) in sortByColumnInx(propertySettings)">
          <th v-if="property.showColumn"
              :class="property.propertyType === 'boolean'? 'st-squeeze': 'st-text-shift'"
              class="border-0 py-0"
          >
            <div class="d-flex align-items-center">
              <div :data-delay="property.tooltip.delay"
                   :data-placement="property.tooltip.placement"
                   :title="getCapitalizeLang(property.tooltip.title)"
                   class="d-flex align-items-center pl-0"
                   data-toggle="tooltip"
              >
                <div v-if="property.showIcon" class="pl-0" v-html="property.icon"></div>
                <div v-if="property.showLabel" class="pl-2">
                  {{ getCapitalizeLang(property.label) }}
                </div>
              </div>
              <div v-if="property.sortable" class="d-flex flex-column pl-2">
                <i :class="[property.order === 'asc'? 'text-warning': 'text-white']"
                   class="fa fa-sort-up fa-sm st-cursor-pointer"
                   @click="orderCards(property.property, 'asc')"></i>
                <i :class="[property.order === 'desc'? 'text-warning': 'text-white']"
                   class="fa fa-sort-down fa-sm st-cursor-pointer mt-1"
                   @click="orderCards(property.property, 'desc')"></i>
              </div>
              <div v-if="property.sortable" :class="property.priorityOrder !== 0? 'text-warning':'text-dark'"
                   class="d-flex align-items-center pl-1">
                {{ property.priorityOrder }}
              </div>
            </div>
          </th>
        </template>
        <th class="st-squeeze border-0 border-right-0 py-0">
          <button :id="getCardDetailsButtonElemId(null)"
                  class="btn bg-white btn-sm border-1 border-secondary py-0"
                  role="button"
                  style="margin-bottom: 2px;"
                  @click.prevent.stop="toggleCardDetails()"
          >
            <i v-if="showCardDetails" class="fa fa-angle-up fa-xs text-dark"></i>
            <i v-else class="fa fa-angle-down fa-xs text-dark"></i>
          </button>
        </th>
        <th class="st-squeeze border-0 border-right-0 py-0">
          <button :id="getCardEditButtonElemId(null)"
                  class="btn bg-white btn-sm border-1 border-secondary py-0"
                  role="button"
                  style="margin-bottom: 2px;"
                  @click.prevent.stop="editCards()"
          >
            <i class="fa fa-pen-to-square fa-xs text-dark"></i>
          </button>
        </th>
        <th class="st-squeeze border-0 border-right-0 py-0">
          <button :id="getCardDeleteButtonElemId(null)"
                  class="btn bg-white btn-sm border-1 border-secondary py-0"
                  role="button"
                  style="margin-bottom: 2px;"
                  @click.prevent.stop="deleteCards()"
          >
            <i class="fa fa-trash fa-xs text-dark"></i>
          </button>
        </th>
      </tr>
      </thead>
      <tbody class="mt-4">

      <template v-for="(card,i) in dictionaryCards"
                v-model="dictionaryCards">
        <tr :id="getCardElemId(card.id)"
            :key="card.id"
            draggable="true"
            @mousedown.prevent.stop="mousedown(card, i)"
            @mouseup.prevent.stop="mouseup(card, i)"
            @click.prevent="selectCard(card)"
        >
          <td class="st-squeeze border-1 border-secondary border-left-0">{{ i + 1 }}</td>
          <template v-for="(property, ii) in sortByColumnInx(propertySettings)">
            <td v-if="property.showColumn"
                :class="property.propertyType === 'boolean'? 'st-squeeze':'st-text-shift'"
                class="border-1 border-secondary"
            >
              <input v-if="property.propertyType === 'boolean'" :value="getProperty(card, property.property)"
                     type="checkbox">
              <div v-else>{{ getProperty(card, property.property) }}</div>
            </td>
          </template>
          <td class="st-squeeze border-1 border-secondary">
            <button :id="getCardDetailsButtonElemId(card.id)" :aria-controls="getCardDetailsElemId(card.id)"
                    aria-expanded="false"
                    class="btn bg-white btn-sm border-1 border-secondary py-0"
                    data-toggle="collapse"
                    role="button"
                    @click.prevent.stop="toggleCardDetail(card, i)"
            >
              <i v-if="card.uiShowDetail" class="fa fa-angle-up fa-xs text-dark"></i>
              <i v-else class="fa fa-angle-down fa-xs text-dark"></i>
            </button>
          </td>
          <td class="st-squeeze border-1 border-secondary">
            <button :id="getCardEditButtonElemId(card.id)"
                    class="btn bg-white btn-sm border-1 border-secondary py-0"
                    role="button"
                    @click.prevent.stop="editCard(card, i)"
            >
              <i class="fa fa-pen-to-square fa-xs text-dark"></i>
            </button>
          </td>
          <td class="st-squeeze border-1 border-secondary">
            <button :id="getCardDeleteButtonElemId(card.id)"
                    class="btn bg-white btn-sm border-1 border-secondary py-0"
                    @click.prevent.stop="deleteCard(card, i)"
            >
              <i class="fa fa-trash fa-xs text-dark"></i>
            </button>
          </td>
        </tr>
        <tr :id="getCardDetailsElemId(card.id)" class="collapse">
          <td class="st-squeeze border-1 border-secondary border-right-0" colspan="5">
            {{ getUpperCaseLang('details') }}
          </td>
        </tr>
      </template>
      </tbody>
    </table>
    <div v-if="showEmpty"
         :id="blankElemId"
         class="blank container-fluid d-block"
         @mouseup="mouseup(null,-1)"
    >
    </div>
    <table-settings-modal
        :id="ids.tableSettingsModal"
        :ref="ids.tableSettingsModal"
        :closable="true"
        :instance-mark="instanceMark"
        :property-settings="propertySettings"
    >
    </table-settings-modal>
    <GlobalEvents @mouseup="mouseupOutside()"/>

    <add-card-modal
        :id="ids.addCardModal"
        :ref="ids.addCardModal"
        :closable="true"
        :dictionary="dictionary"
        :unrepeated="dictionary.unrepeated"
    ></add-card-modal>
  </div>
</template>

<script>
import {mapGetters, mapState} from "vuex"
import * as _ from "lodash"
import TableSettingsModal from "./TableSettingsModal.vue"
import AddCardModal from "./AddCardModal.vue"
import PictureStatic from "../../picture/PictureStatic.vue"
import DownloadDropdown from "../DownloadDropdown.vue"

export default {
  props: [
    'dictionary',
    'instanceMark',
  ],
  components: {
    TableSettingsModal,
    AddCardModal,
    PictureStatic,
    DownloadDropdown,
  },
  created() {
    this.$root.$on('dragdrop-init', (payload) => {
      this.dragdropInit(payload)
    })
    this.$root.$on('dragdrop-destroy', () => {
      this.dragdropDestroy()
    })
    this.$store.watch(this.$store.getters.getActionId, actionId => {
      this.fetchData()
    })
    this.fetchData()
    this.showEmpty = typeof this.dictionaryCards === 'undefined' || this.dictionaryCards.length === 0;
  },
  watch: {
    $route: [
      'fetchData',
    ],
    dictionary() {
      this.fetchData()
    },
  },
  computed: {
    ...mapState([
      'cards',
      'lang',
      'vocabulary',
    ]),
    ...mapGetters([
      'isDictionaryExists',
      'getCardsByDictionaryId',
      'getDictionaryInx',
      'getCardsByDictionaryInx',
    ]),

    deselectAllSetting() {
      return {
        tooltip: {
          placement: top,
          title: "deselectAll",
          delay: {show: 500, hide: 100}
        }
      }
    },
    selectAllSetting() {
      return {
        tooltip: {
          placement: top,
          title: "selectAll",
          delay: {show: 500, hide: 100}
        }
      }
    },
    propertySettings() {
      return [
        {
          property: "word",
          propertyType: "string",
          label: "word",
          icon: '<i class="fa-brands fa-wikipedia-w"></i>',
          showLabel: true,
          showIcon: true,
          tooltip: {
            placement: top,
            title: "word",
            delay: {show: 500, hide: 100}
          },
          order: null,
          priority: 0,
          priorityOrder: 0,
          sortable: true,
          showColumn: true,
          showDetail: false,
          showDetailLabel: false,
          columnInx: 0,
          detailInx: null,
          detailPosition: "vertical",
        },
        {
          property: "translation",
          propertyType: "string",
          label: "translation",
          icon: '<i class="fa-solid fa-t"></i>',
          showLabel: true,
          showIcon: true,
          tooltip: {
            placement: top,
            title: "translation",
            delay: {show: 500, hide: 100}
          },
          order: null,
          priority: 0,
          priorityOrder: 0,
          sortable: true,
          showColumn: true,
          showDetail: false,
          showDetailLabel: false,
          columnInx: 1,
          detailInx: null,
          detailPosition: "vertical",
        },
        {
          property: "example",
          propertyType: "string",
          label: "example",
          icon: '<i class="fa fa-book text-white"></i>',
          showLabel: true,
          showIcon: true,
          tooltip: {
            placement: top,
            title: "example",
            delay: {show: 500, hide: 100}
          },
          order: null,
          priority: 0,
          priorityOrder: 0,
          sortable: true,
          showColumn: true,
          showDetail: true,
          showDetailLabel: true,
          columnInx: 2,
          detailInx: 1,
          detailPosition: "vertical",
        },
        {
          property: "exampleTranslation",
          propertyType: "string",
          label: "exampleTranslation",
          icon: '<i class="fa fa-book-bookmark"></i>',
          showLabel: true,
          showIcon: true,
          tooltip: {
            placement: top,
            title: "exampleTranslation",
            delay: {show: 500, hide: 100}
          },
          order: null,
          priority: 0,
          priorityOrder: 0,
          sortable: true,
          showColumn: true,
          showDetail: true,
          showDetailLabel: true,
          columnInx: 3,
          detailInx: 1,
          detailPosition: "vertical",
        },
        {
          property: ['dictionary', 'name'],
          propertyType: "string",
          label: "dictionary",
          icon: '<i class="fa fa-folder text-white"></i>',
          showLabel: true,
          showIcon: true,
          tooltip: {
            placement: top,
            title: "dictionary",
            delay: {show: 500, hide: 100}
          },
          order: null,
          priority: 0,
          priorityOrder: 0,
          sortable: true,
          showColumn: true,
          showDetail: false,
          showDetailLabel: false,
          columnInx: 4,
          detailInx: null,
          detailPosition: "vertical",
        },
        {
          property: "learned",
          propertyType: "boolean",
          label: "learned",
          icon: '<i class="fas fa-circle-check text-white"></i>',
          showLabel: true,
          showIcon: true,
          tooltip: {
            placement: top,
            title: "learned",
            delay: {show: 500, hide: 100}
          },
          order: null,
          priority: 0,
          priorityOrder: 0,
          sortable: true,
          showColumn: true,
          showDetail: false,
          showDetailLabel: false,
          columnInx: null,
          detailInx: null,
          detailPosition: "vertical",
        },
      ]
    },
    ids() {
      return {
        addCardModal: this.prefixId() + 'add-card-modal-id',
        tableSettingsModal: this.prefixId() + "table-settings-modal-id",
        downloadDropdown: this.prefixId() + 'download-dropdown-id',
      }
    },
  },

  data() {
    return {
      name: 'CardTable',
      card: {
        word: null,
        translation: null,
        example: null,
        exampleTranslation: null,
      },
      show: true,
      showEmpty: false,
      showCardDetails: false,
      dictionaryCards: [],
      selectedCardIds: [],
      blankElemId: this.prefixId() + "blank",

      groups: ["cardsChangeDictionary"],
      sourceMark: "cards",
      isMouseInClick: false,
      groupsInProcess: [],
      dragCards: [],
    }
  },
  methods: {
    fetchData() {
      if (this.dictionary) {
        this.show = false
        if (this.isSourceExists()) {
          this.show = true
          const cards = this.getCardsByDictionaryId(this.dictionary.id)
          this.showEmpty = typeof cards === 'undefined' || cards.length === 0
          if (cards) {
            this.dictionaryCards = [...cards]
          } else {
            this.dictionaryCards = []
          }
          this.groupCards()
          this.updateSelected()
        }
      }
    },
    prefixId() {
      return this.name + '-' + this.instanceMark + '-'
    },
    getLang(key) {
      return this.$t(key)
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    getUpperCaseLang(key) {
      return _.upperCase(this.getLang(key))
    },
    getProperty(item, property) {
      return _.get(item, property)
    },
    getSortable(settings) {
      const sortedSettings = _.sortBy(settings.filter(s => s.sortable === true && s.priorityOrder > 0), ['priorityOrder'])
      return {
        properties: sortedSettings.map(s => {
          if (s.propertyType === "string") {
            return (item) => {
              const value = this.getProperty(item, s.property)
              if (value) {
                return value.toLowerCase()
              } else return null
            }
          }
          if (s.propertyType === "dateISOString") {
            return (item) => {
              const value = this.getProperty(item, s.property)
              if (value) {
                return new Date(value)
              } else return null
            }
          }
        }),
        orders: sortedSettings.map(s => s.order),
      }
    },
    sortByColumnInx(propertySettings) {
      return _.orderBy(propertySettings, ["columnInx"], ["asc"])
    },
    getCardDetailsElemId(id) {
      return this.prefixId() + "card-detail" + id
    },
    getCardDetailsButtonElemId(id) {
      return this.prefixId() + "card-detail-b" + id
    },
    getCardEditButtonElemId(id) {
      return this.prefixId() + "card-edit" + id
    },
    getCardDeleteButtonElemId(id) {
      return this.prefixId() + "card-delete" + id
    },
    toggleCardDetail(card, i) {
      card.uiShowDetail = typeof card.uiShowDetail === 'undefined' ? true : !card.uiShowDetail
      this.dictionaryCards.splice(i, 1, card)
      $("#" + this.getCardDetailsElemId(card.id)).collapse('toggle')
    },
    editCard(card, i) {

    },
    deleteCard(card, i) {
      this.$store.dispatch('deleteCardAction', {card: card})
    },

    toggleCardDetails() {
      if (this.showCardDetails) {
        this.dictionaryCards.forEach(c => {
          $("#" + this.getCardDetailsElemId(c.id)).removeClass('show')
          c.uiShowDetail = false
        })
        this.showCardDetails = false
      } else {
        this.dictionaryCards.forEach(c => {
          $("#" + this.getCardDetailsElemId(c.id)).addClass('show')
          c.uiShowDetail = true
        })
        this.showCardDetails = true
      }
    },
    editCards() {

    },
    deleteCards() {
      this.$store.dispatch('deleteCardsByDictionaryAction', {dictionaryId: this.dictionary.id})
    },
    orderCards(property, order) {
      for (let i = 0; i < this.propertySettings.length; i++) {
        const jsonSort = this.propertySettings[i]
        if (jsonSort.property === property) {
          if (jsonSort.order === order) {
            jsonSort.order = null
            jsonSort.priority = 0
            jsonSort.priorityOrder = 0
          } else if (jsonSort.order === null) {
            jsonSort.order = order
            jsonSort.priority = _.now()
          } else {
            jsonSort.order = order
          }
          break
        }
      }
      const priorities = this.propertySettings.map(s => s.priority).filter(p => p !== 0)
      priorities.sort()
      this.propertySettings.forEach(p => {
        const i = priorities.indexOf(p.priority)
        if (i >= 0) {
          p.priorityOrder = i + 1
        }
      })

      if (this.propertySettings.map(s => s.order).every(s => s === null)) {
        this.fetchData()
      } else {
        this.groupCards()
      }
    },
    groupCards() {
      if (this.dictionaryCards && this.dictionaryCards.length !== 0) {
        const sortable = this.getSortable(this.propertySettings)
        this.dictionaryCards = _.orderBy(this.dictionaryCards, sortable.properties, sortable.orders)
      }
    },
    updateSelected() {
      this.selectedCardIds = this.selectedCardIds.filter(id => this.dictionaryCards.findIndex(x => x.id === id) >= 0)
    },
    getCardElemId(id) {
      return this.prefixId() + 'card' + id
    },

    selectCard(card) {
      const inx = this.selectedCardIds.findIndex(id => id === card.id)
      if (inx < 0) {
        this.selectedCardIds.push(card.id)
        $("#" + this.getCardElemId(card.id)).addClass("card-select")
      } else {
        this.selectedCardIds.splice(inx, 1)
        $("#" + this.getCardElemId(card.id)).removeClass("card-select")
      }
    },
    isSelected() {
      return this.selectedCardIds.length > 0
    },
    isSelectAll() {
      return this.selectedCardIds.length === this.dictionaryCards.length
    },
    isDeselectAll() {
      return this.selectedCardIds.length === 0
    },
    selectAll() {
      this.selectedCardIds = this.dictionaryCards.map(c => c.id)
      this.selectedCardIds.forEach(id => $("#" + this.getCardElemId(id)).addClass("card-select"))
    },
    deselectAll() {
      this.selectedCardIds.forEach(id => $("#" + this.getCardElemId(id)).removeClass("card-select"))
      this.selectedCardIds = []
    },
    isSourceExists() {
      return this.isDictionaryExists(this.dictionary.id)
    },

    mousedown(card, i) {
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
              sourceId: this.dictionary.id,
            },
          }
          this.$store.dispatch("dragdropStartAction", start)
        }
      }, 60)
    },
    mousedownSelected() {
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
              sourceId: this.dictionary.id,
            },
          }
          this.$store.dispatch("dragdropStartAction", start)
        }
      }, 60)
    },
    mouseup(card, i) {
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
            isDragdropPosition: false,
          },
          data: {
            items: items,
            sourceMark: this.sourceMark,
            sourceId: this.dictionary.id,
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
      cards.forEach(card => $("#" + this.getCardElemId(card.id)).addClass("dragstart"))
    },
    async deactivateDragstartStyle(cards) {
      cards.forEach(card => $("#" + this.getCardElemId(card.id)).removeClass("dragstart"))
    },
    async activateDragoverStyle(cards) {
      cards.forEach(card => $("#" + this.getCardElemId(card.id)).addClass("dragover"))
      $("#" + this.blankElemId).addClass("dragover")
    },
    async deactivateDragoverStyle(cards) {
      cards.forEach(card => $("#" + this.getCardElemId(card.id)).removeClass("dragover"))
      $("#" + this.blankElemId).removeClass("dragover")
    },
    downloadExcelFile() {
      $('download-excel-file').removeClass("active")
      this.$store.dispatch("downloadExcelFileAction",
          {vocabulary: this.vocabulary.vocabulary, dictionary: this.dictionary})
    },
    downloadXmlFile() {
      $('download-xml-file').removeClass("active")
      this.$store.dispatch("downloadXmlFileAction",
          {vocabulary: this.vocabulary.vocabulary, dictionary: this.dictionary}
      )
    },
  },
}
</script>

<style scoped>

.dropdown-class {
  position: relative;
}

.dropdown-class > li {
  z-index: 1050;
}

.card-table- {
  height: 550px;
  overflow-y: auto;
}

table {
  font-size: 15px;
  font-family: Calibri;
  overflow-y: visible !important;
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
  /*width: 150px;*/
  width: 10%;
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

.st-cursor-pointer {
  cursor: pointer;
}

</style>
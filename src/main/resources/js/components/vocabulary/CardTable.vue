<template>
  <div v-if="show"
       :id="ids.id"
       :ref="ids.id"
       :style="{height: colHeight+'px'}"
       class="container p-0 m-0 parent-for-height-flex"
  >
    <b-row
        :id="ids.tools"
        class="bg-dark d-block border-0 flex-shrink-0 pl-1"
        no-gutters
        style="position: sticky; top:0; left: 0; z-index: 100;"
    >
      <b-row no-gutters>
        <b-breadcrumb class="p-0 mt-0 mb-1 bg-transparent text-white" size="sm">
          <b-breadcrumb-item>
            <b-button
                :variant="dictionary.unrepeated?'info':'warning'"
                class="py-0 shadow-none border border-dark"
                size="sm"
                @click.prevent.stop="navigateToUnique"
            >
              {{ dictionary.unrepeated ? getLang('unique') : getLang('notUnique') }}
            </b-button>
          </b-breadcrumb-item>
          <b-breadcrumb-item>
            <b-button
                class="py-0 shadow-none border border-dark"
                size="sm"
                variant="primary"
                @click.prevent.stop="navigateToDictionary"
            >
              {{ dictionary.name }}
            </b-button>
          </b-breadcrumb-item>
          <b-breadcrumb-item v-if="activeCard">
            <b-button
                class="py-0 shadow-none border-0 text-light"
                size="sm"
                variant="transparent"
                @click.prevent.stop="scrollToActiveCard"
            >
              {{ activeCard.word }}
            </b-button>
          </b-breadcrumb-item>
        </b-breadcrumb>
      </b-row>
      <b-row no-gutters>
        <b-button-toolbar>
          <b-button-group class="mr-1" size="sm">
            <b-button
                v-b-tooltip="{trigger: 'hover', delay: { 'show': 1200, 'hide': 100 }, placement: 'bottomright'}"
                :title="getCapitalizeLang('addCard')"
                class="px-1 py-0 shadow-none border border-secondary"
                variant="light"
                @click="$refs[ids.addCardModal].showModal()"
            >
              <i class="fa fa-plus text-success"></i>
            </b-button>
          </b-button-group>

          <b-button-group class="" size="sm">
            <b-button
                v-if="countSelected > 0"
                v-b-tooltip="{trigger: 'hover', delay: { 'show': 1200, 'hide': 100 }, placement: 'bottomright'}"
                :title="getCapitalizeLang('deselectAll')"
                class="px-1 py-0 shadow-none border border-secondary"
                data-toggle="tooltip"
                variant="light"
                @click.prevent.stop="deselectAll()"
            >
              <i class="fa-solid fa-square-check text-primary"></i>
            </b-button>

            <b-button
                v-else
                v-b-tooltip="{trigger: 'hover', delay: { 'show': 1200, 'hide': 100 }, placement: 'bottomright'}"
                :title="getCapitalizeLang('selectAll')"
                class="px-1 py-0 shadow-none border border-secondary"
                data-toggle="tooltip"
                variant="light"
                @click.prevent.stop="selectAll()"
            >
              <i class="fa-regular fa-square"></i>
            </b-button>

            <b-button
                v-b-tooltip="{trigger: 'hover', delay: { 'show': 1200, 'hide': 100 }, placement: 'bottomright'}"
                :title="getCapitalizeLang('selected')"
                class="px-0 py-0 shadow-none border border-secondary"
                variant="light"
                @mousedown.prevent="mousedownSelected()"
            >
            <span class="st-right badge bg-white border border-secondary badge-pill">
                  <span v-if="countSelected<100">&nbsp;</span>
                  <span v-if="countSelected<10">&nbsp;</span>
                {{ countSelected }}
              </span>
            </b-button>
          </b-button-group>

          <b-button-group class="mx-1" size="sm">
            <download-dropdown
                :id="ids.downloadDropdown"
                :ref="ids.downloadDropdown"
                :dictionary="dictionary"
                :side="instanceMark"
            ></download-dropdown>
            <upload-dropdown
                :id="ids.uploadDropdown"
                :ref="ids.uploadDropdown"
                :dictionary="dictionary"
                :side="instanceMark"
            ></upload-dropdown>
          </b-button-group>

          <b-button-group size="sm">
            <b-button
                v-b-tooltip="{trigger: 'hover', delay: { 'show': 1200, 'hide': 100 }, placement: 'bottomright'}"
                :title="getCapitalizeLang('settings')"
                class="px-1 py-0 shadow-none border border-secondary"
                variant="light"
                size="sm"
                @click="$refs[ids.tableSettingsModal].showModal()"
            >
              <i class="fa fa-gear text-secondary"></i>
            </b-button>
          </b-button-group>
        </b-button-toolbar>
      </b-row>
    </b-row>

    <div
        :id="ids.field"
        :ref="ids.field"
        :style="styleField"
         class="st-field container px-0 parent-for-height-flex"
    >
      <table class="table bg-white table-hover table-bordered table-sm mb-0 border-0"
             style="z-index: 1;"
      >
        <thead class="thead-dark"
               style="position: sticky; top:0; z-index: 1;"
        >
        <tr class="border-0">
          <th class="st-squeeze border-0 border-left-0 py-0">
            <b-button-group size="sm">
              <b-button
                  size="sm"
                  class="px-1 py-0 shadow-none border-0"
                  variant="transparent"
                  @click.prevent.stop="scrollToActiveCard"
              >
                <i class="fa-solid fa-location-crosshairs fa-sm fa-fade text-white"
                   style="--fa-animation-duration: 2s; --fa-fade-opacity: 0.6;"></i>
              </b-button>
            </b-button-group>
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
                  <div v-if="property.showLabel" :class="property.showIcon?'pl-2':''">
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

        <tbody>
        <template v-for="(card,i) in dictionaryCards"
                  v-model="dictionaryCards">
          <tr
              :id="getCardElemId(card.id)"
              :ref="getCardElemId(card.id)"
              :key="card.id"
              :class="[{'card-selected':card.selected}, {'card-active':activeCard === card} ]"
              draggable="true"
              @mousedown.prevent="mousedown(card, i)"
              @mouseup.prevent="mouseup(card, i)"
              @click="setActiveCard(card)"
          >
            <td class="st-squeeze border-1 border-secondary border-left-0">{{ i + 1 }}</td>
            <template v-for="(property, ii) in sortByColumnInx(propertySettings)"
            >
              <td v-if="property.showColumn"
                  :class="property.propertyType === 'boolean'? 'st-squeeze':'st-text-shift'"
                  class="border-1 border-secondary"
              >
                <input v-if="property.property === 'selected'" v-model="dictionaryCards[i][property.property]"
                       type="checkbox" @click="selectCard(card)"
                >
                <input v-else-if="property.propertyType === 'boolean'" v-model="dictionaryCards[i][property.property]"
                       type="checkbox"
                >
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
            <td class="st-squeeze border-1 border-secondary border-right-0" colspan="100%">
              {{ getUpperCaseLang('details') }}
            </td>
          </tr>
        </template>
        </tbody>
      </table>
      <b-row
          :id="ids.filler"
          class="child-for-height-flex bg-white p-0 m-0"
          @mouseup="mouseup(null,-1)"
      >
      </b-row>
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
  </div>
</template>

<script>
import {mapGetters, mapState} from "vuex"
import * as _ from "lodash"
import TableSettingsModal from "./TableSettingsModal.vue"
import AddCardModal from "./AddCardModal.vue"
import PictureStatic from "../picture/PictureStatic.vue"
import DownloadDropdown from "./DownloadDropdown.vue"
import UploadDropdown from "./UploadDropdown.vue"

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
    UploadDropdown,
  },
  created() {
    this.addListeners()
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
  },
  beforeDestroy () {
    this.TableSettingsModal.destroy()
    this.AddCardModal.destroy()
    this.PictureStatic.destroy()
    this.DownloadDropdown.destroy()
    this.UploadDropdown.destroy()
    clearInterval(this.dictionaryCards)
  },
  destroyed() {
    this.removeListeners()
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
      'height',
    ]),
    ...mapGetters([
      'isDictionaryExists',
      'getCardsByDictionaryId',
      'getDictionaryInx',
      'getCardsByDictionaryInx',
    ]),
    colHeight() {
      return window.innerHeight - this.height.header - this.height.footer - 6
    },
    propertySettings() {
      return [
        {
          property: "selected",
          propertyType: "boolean",
          label: this.getUpperCaseLang('abbrSelected'),
          icon: '<i class="fa-regular fa-square-check"></i>',
          showLabel: true,
          showIcon: false,
          tooltip: {
            placement: top,
            title: "selected",
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
          columnInx: 1,
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
          columnInx: 2,
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
          columnInx: 3,
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
          columnInx: 4,
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
          columnInx: 5,
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
          columnInx: 6,
          detailInx: null,
          detailPosition: "vertical",
        },
      ]
    },
    ids() {
      return {
        id: this.prefixId(),
        tools: this.prefixId() + 'tools-id',
        field: this.prefixId() + 'field-id',
        filler: this.prefixId() + 'filler-id',
        addCardModal: this.prefixId() + 'add-card-modal-id',
        tableSettingsModal: this.prefixId() + "table-settings-modal-id",
        downloadDropdown: this.prefixId() + 'download-dropdown-id',
        uploadDropdown: this.prefixId() + 'upload-dropdown-id',
      }
    },
    styleField() {
      return {
        height: this.style.height.table + 'px',
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
      activeParent: false,
      showCardDetails: false,
      dictionaryCards: [],
      countSelected: 0,
      activeCard: null,

      groups: ["cardsChangeDictionary"],
      sourceMark: "cards",
      isMouseInClick: false,
      groupsInProcess: [],
      dragCards: [],

      listeners: [],
      style: {
        height: {
          table: 0,
        },
      }
    }
  },
  methods: {
    fetchData() {
      if (this.dictionary) {
        this.show = false
        if (this.isSourceExists()) {
          this.$nextTick(() => {
            this.style.height.table = this.calcHeightTable()
          })
          this.show = true
          let cards = _.cloneDeep(this.getCardsByDictionaryId(this.dictionary.id))
          cards = this.updateSelected(cards, this.dictionaryCards)
          this.dictionaryCards = cards
          this.groupCards()
        }
      }
      this.activeCard = null
    },
    calcHeightTable() {
      return this.colHeight - document.getElementById(this.ids.tools).offsetHeight
    },
    prefixId() {
      return this.name + '-' + this.instanceMark + '-'
    },
    isBlank(value) {
      return _.isNil(value) || _.isEmpty(value)
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
          if (s.propertyType === "boolean") {
            return (item) => {
              const value = this.getProperty(item, s.property)
              if (value) {
                return new Date(value)
              } else return null
            }
          }
          if (s.propertyType === "dateISOString") {
            return (item) => {
              const value = this.getProperty(item, s.property)
              if (value) {
                return value
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
    updateSelected(newCards, oldCards) {
      let count = 0
      newCards.forEach(c=>c.selected = false)
      oldCards.filter(c => c.selected).forEach(c => {
        let inx = newCards.findIndex(x => x.id === c.id)
        if (inx >= 0) {
          count++
          newCards[inx].selected = true
        }
      })
      this.countSelected = count
      return newCards
    },
    getCardElemId(id) {
      return this.prefixId() + 'card' + id
    },

    selectCard(card) {
      if (card.selected) {
        this.countSelected--
      } else {
        this.countSelected++
      }
    },

    selectAll() {
      this.dictionaryCards.forEach(c => c.selected = true)
      this.countSelected = this.dictionaryCards.length
    },
    deselectAll() {
      this.dictionaryCards.forEach(c => c.selected = false)
      this.countSelected = 0
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
      }, 200)
    },
    mousedownSelected() {
      this.isMouseInClick = true
      setTimeout(() => {
        if (this.isMouseInClick) {
          this.groupsInProcess = this.groups
          if (this.countSelected === 0) {
            return
          }
          const items = this.dictionaryCards.filter(c => c.selected)
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
      }, 200)
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
      $("#" + this.ids.filler).addClass("dragover")
    },
    async deactivateDragoverStyle(cards) {
      cards.forEach(card => $("#" + this.getCardElemId(card.id)).removeClass("dragover"))
      $("#" + this.ids.filler).removeClass("dragover")
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
    keydownListener() {
      return ({repeat, altKey, which}) => {
        if (this.activeParent) {
          if (repeat) return
          if (altKey && which === 67) {
            this.$refs[this.ids.addCardModal].showModal()
          }
        }
      }
    },
    addListeners() {
      const keydownListener = this.keydownListener()
      this.listeners.push({type: 'keydown', listener: keydownListener})
      this.activateListeners()
    },
    removeListeners() {
      this.listeners.forEach(pair => {
        document.removeEventListener(pair.type, pair.listener)
      })
      this.listeners = []
    },
    activateListeners() {
      this.listeners.forEach(pair => {
        document.addEventListener(pair.type, pair.listener)
      })
    },
    navigateToDictionary() {
      this.$emit('onNavigateToDictionary', this.instanceMark)
    },
    navigateToUnique() {
      this.$emit('onNavigateToUnique', this.instanceMark)
    },
    scrollToActiveCard(){
      if (this.activeCard) {
        const elem = "#" + this.getCardElemId(this.activeCard.id)
        this.scrollToElemInField(elem)
      }
    },
    setActiveCard(card){
      this.activeCard = card
    },
    scrollToElemInField(elem) {
      const options = {
        container: '#' + this.ids.field,
        easing: 'ease-in',
        lazy: false,
        offset: -60,
        force: true,
        cancelable: true,
        onStart(element) {
          // scrolling started
        },
        onDone(element) {
          // scrolling is done
        },
        onCancel() {
          // scrolling has been interrupted
        },
        x: false,
        y: true
      }
      this.$scrollTo(elem, 500, options)
    },
  },
}
</script>

<style scoped>
.border-2 {
  border-width: 2px !important;
}

.st-field {
  overflow: scroll;
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
  width: 10%;
  overflow-wrap: anywhere;
}


.card-selected {
  background-color: #BBDEFB;
}
.card-active{
  background-color: #aedcae;
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

.parent-for-height-flex {
  width: 100%;
  display: flex;
  flex-direction: column;
}

.child-for-height-flex {
  min-height: 0;
  width: 100%;
  flex: 1;
}

</style>
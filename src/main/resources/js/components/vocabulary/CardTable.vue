<template>
  <div v-if="show"
       :id="ids.id"
       :ref="ids.id"
       :style="{height: style.height.col+'px'}"
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
                class="shadow-none border border-secondary px-1 py-0"
                size="sm"
                variant="light"
                @click.prevent.stop="$refs[ids.tableSettingsModal].showModal()"
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
        @scroll="handleVS"
    >
      <table class="table bg-white table-hover table-bordered table-sm border-0 flex-shrink-0 mb-0"
             style="z-index: 1;"
      >
        <thead class="thead-dark"
               style="position: sticky; top:0; z-index: 1; height: 26px;"
        >
        <tr class="border-0">
          <th class="st-sm border-0 border-left-0 py-0">
            <b-button-group size="sm">
              <b-button
                  class="px-1 py-0 shadow-none border-0"
                  size="sm"
                  variant="transparent"
                  @click.prevent.stop="scrollToActiveCard"
              >
                <i class="fa-solid fa-location-crosshairs fa-sm fa-fade text-white"
                   style="--fa-animation-duration: 2s; --fa-fade-opacity: 0.6;"></i>
              </b-button>
            </b-button-group>
          </th>
          <template v-for="(property,i) in sortedPropertySettings">
            <th v-if="property.showColumn"
                :class="''"
                :style="[{width: `${property.width}px`}]"
                class="border-0 py-0"
            >
              <span class="d-flex align-items-center">
                <span :data-delay="property.tooltip.delay"
                      :data-placement="property.tooltip.placement"
                      :title="getCapitalizeLang(property.tooltip.title)"
                      class="st-ellipsis d-flex align-items-center pl-0"
                      data-toggle="tooltip"
                >
                  <span v-if="property.showIcon" class="pl-0" v-html="property.icon"></span>
                  <span v-if="property.showLabel" :class="property.showIcon?'pl-2':''" class="">
                    {{ getCapitalizeLang(property.label) }}
                  </span>
                </span>
                <span v-if="property.sortable"
                      :class="[property.showIcon?'pl-2':'pl-1']"
                      class="d-flex flex-column">
                  <i :class="[property.order === 'asc'? 'text-warning': 'text-white']"
                     class="fa fa-sort-up fa-sm st-cursor-pointer"
                     @click="orderCards(property.property, 'asc')"></i>
                  <i :class="[property.order === 'desc'? 'text-warning': 'text-white']"
                     class="fa fa-sort-down fa-sm st-cursor-pointer mt-1"
                     @click="orderCards(property.property, 'desc')"></i>
                </span>
                <span v-if="property.sortable" :class="property.priorityOrder !== 0? 'text-warning':'text-dark'"
                      class="st-ellipsis d-flex align-items-center pl-1">
                  {{ property.priorityOrder }}
                </span>
              </span>
            </th>
          </template>
          <th class="st-sm border-0 border-right-0 py-0">
            <b-button
                :id="getCardEditButtonElemId(null)"
                :ref="getCardEditButtonElemId(null)"
                class="border border-secondary py-0"
                size="sm"
                style="margin-bottom: 2px;"
                variant="light"
                @click.prevent.stop="editCards()"
            >
              <i class="fa fa-pen-to-square fa-xs text-dark"></i>
            </b-button>
          </th>
          <th class="st-sm border-0 border-right-0 py-0">
            <b-button
                :id="getCardDeleteButtonElemId(null)"
                :ref="getCardDeleteButtonElemId(null)"
                class="border border-secondary py-0"
                size="sm"
                style="margin-bottom: 2px;"
                variant="light"
                @click.prevent.stop="deleteCards()"
            >
              <i class="fa fa-trash fa-xs text-dark"></i>
            </b-button>
          </th>
        </tr>
        </thead>

        <tbody
            :id="ids.tbody"
            :ref="ids.tbody"
            :style="{height: `${vtHeight}px`}"
        >
        <tr :style="{height: `${vt.firstRowHeight}px`}" class="firstRow border-0 p-0">
          <td class="border-0 p-0" colspan="100%"></td>
        </tr>
        <template v-for="(card,i) in renderCards">
          <tr
              :id="getCardElemId(card.id)"
              :key="card.id"
              :ref="getCardElemId(card.id)"
              :class="[{'card-selected':card.selected}, {'card-active':!isBlank(activeCard) && activeCard.id === card.id} ]"
              class="calcRow"
              draggable="true"
              @click="setActiveCard(card)"
              @mousedown.prevent="mousedown(card, i)"
              @mouseup.prevent="mouseup(card, i)"
          >
            <td class="st-sm border-1 border-secondary border-left-0">{{ (findIndex(card.id) + 1) }}</td>
            <template v-for="(property, ii) in sortedPropertySettings"
            >
              <td v-if="property.showColumn"
                  :class="''"
                  :style="[{width: `${property.width}px`}]"
                  class="border-1 border-secondary"
              >
                <input v-if="property.property === 'selected'" v-model="getCardById(card.id)[property.property]"
                       type="checkbox" @click="selectCard(card)"
                >
                <input v-else-if="property.propertyType === 'boolean'" v-model="getCardById(card.id)[property.property]"
                       type="checkbox"
                >
                <span v-else>{{ getProperty(card, property.property) }}</span>
              </td>
            </template>
            <td class="st-sm border-1 border-secondary">
              <b-button
                  :id="getCardEditButtonElemId(card.id)"
                  :ref="getCardEditButtonElemId(card.id)"
                  class="border border-secondary py-0"
                  size="sm"
                  variant="light"
                  @click.prevent.stop="editCard(card, i)"
              >
                <i class="fa fa-pen-to-square fa-xs text-dark"></i>
              </b-button>
            </td>
            <td class="st-sm border-1 border-secondary">
              <b-button
                  :id="getCardDeleteButtonElemId(card.id)"
                  class="btn bg-white btn-sm border-1 border-secondary py-0"
                  @click.prevent.stop="deleteCard(card, i)"
              >
                <i class="fa fa-trash fa-xs text-dark"></i>
              </b-button>
            </td>
          </tr>
        </template>
        <tr :style="{height: `${vt.lastRowHeight}px`}" class="lastRow border-0 p-0">
          <td class="border-0 p-0" colspan="100%"></td>
        </tr>
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
    'data',
  ],
  components: {
    TableSettingsModal,
    AddCardModal,
    PictureStatic,
    DownloadDropdown,
    UploadDropdown,
  },
  mounted() {
  },
  created() {
    this.addListeners()
    this.$root.$on('dragdrop-init', (payload) => {
      this.dragdropInit(payload)
    })
    this.$root.$on('dragdrop-destroy', () => {
      this.dragdropDestroy()
    })
  },
  beforeDestroy() {
  },
  destroyed() {
    this.removeListeners()
  },
  watch: {
    '$route': {
      handler: function (to, from) {

      },
      immediate: true
    },
    data: {
      handler: function () {
        this.fetchData()
        this.initVT()
      },
      immediate: true,
      deep: true,
    },
    height: {
      handler: function () {
        this.buildHeight()
      },
      immediate: true,
      deep: true,
    },
  },
  computed: {
    ...mapState([
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

    renderCards() {
      return this.cards.slice(this.vt.startIndex, this.vt.startIndex + this.vt.step)
    },
    vtHeight() {
      return this.vt.elementHeight * this.cards.length
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
          width: 45,
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
          width: 120,
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
          width: 120,
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
          width: 160,
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
          width: 160,
        },
        {
          property: ['dictionary', 'name'],
          propertyType: 'string',
          label: 'dictionary',
          icon: '<i class="fa fa-folder text-white"></i>',
          showLabel: false,
          showIcon: true,
          tooltip: {
            placement: top,
            title: 'dictionary',
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
          detailPosition: 'vertical',
          width: 100,
        },
        {
          property: "learned",
          propertyType: "boolean",
          label: "abbrLearned",
          icon: '<i class="fas fa-circle-check text-white"></i>',
          showLabel: true,
          showIcon: false,
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
          width: 45,
        },
      ]
    },
    sortedPropertySettings() {
      return _.orderBy(this.propertySettings, ["columnInx"], ["asc"])
    },
    ids() {
      return {
        id: this.prefixId(),
        tbody: this.prefixId() + 'tbody',
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
        height: this.style.height.field + 'px',
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

      dictionary: null,
      cards: [],
      instanceMark: null,

      show: false,
      activeParent: false,
      countSelected: 0,
      activeCard: null,

      activeCardMap: new Map(),
      startIndexMap: new Map(),

      vt: {
        startIndex: 0,
        step: 1,
        elementHeight: 56,
        firstRowHeight: 0,
        lastRowHeight: 0,
      },

      groups: ["cardsChangeDictionary"],
      sourceMark: "cards",
      isMouseInClick: false,
      groupsInProcess: [],
      dragCards: [],

      listeners: [],
      style: {
        height: {
          col: 0,
          field: 0,
          tools: 0,
          beforeField: 0,
        },
      }
    }
  },
  methods: {
    fetchData() {
      this.show = false
      let cards = _.cloneDeep(this.data.cards)
      this.updateSelected(cards, this.cards)
      this.cards = cards
      this.dictionary = this.data.dictionary
      this.instanceMark = this.data.instanceMark
      this.groupCards()
      this.show = true
      this.buildHeight()
      this.buildActiveCardFromMap()
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
    editCard(card, i) {

    },
    deleteCard(card, i) {
      this.$store.dispatch('deleteCardAction', {card: card})
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
      this.scrollToIndex(0,0)
    },
    groupCards() {
      if (this.cards && this.cards.length !== 0) {
        const sortable = this.getSortable(this.propertySettings)
        this.cards = _.orderBy(this.cards, sortable.properties, sortable.orders)
      }
    },
    updateSelected(newCards, oldCards) {
      let count = 0
      newCards.forEach(c => c.selected = false)
      oldCards.filter(c => c.selected).forEach(c => {
        let inx = newCards.findIndex(x => x.id === c.id)
        if (inx >= 0) {
          count++
          newCards[inx].selected = true
        }
      })
      this.countSelected = count
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
      this.cards.forEach(c => c.selected = true)
      this.countSelected = this.cards.length
    },
    deselectAll() {
      this.cards.forEach(c => c.selected = false)
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
          const items = this.cards.filter(c => c.selected)
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
      this.activateDragoverStyle(this.cards)
    },
    dragdropDestroy() {
      this.deactivateDragstartStyle(this.dragCards)
      this.deactivateDragoverStyle(this.cards)
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
      this.listeners.push({level: document, type: 'keydown', listener: keydownListener})
      this.listeners.forEach(pair => {
        pair.level.addEventListener(pair.type, pair.listener)
      })
    },
    removeListeners() {
      this.listeners.forEach(pair => {
        pair.level.removeEventListener(pair.type, pair.listener)
      })
      this.listeners = []
    },
    navigateToDictionary() {
      this.$emit('onNavigateToDictionary', this.instanceMark)
    },
    navigateToUnique() {
      this.$emit('onNavigateToUnique', this.instanceMark)
    },
    scrollToActiveCard() {
      if (this.activeCard) {
        const inx = this.cards.findIndex(c => c.id === this.activeCard.id)
        this.scrollToIndex(inx, this.vt.elementHeight*2)
      }
    },
    scrollToIndex(inx, offset){
      if (!inx) inx = 0
      const y = this.vt.elementHeight * inx - offset
      const elem = document.getElementById(this.ids.field)
      elem.scrollTo({top: y, left: elem.scrollX, behavior: 'auto'})
    },
    setActiveCard(card) {
      this.activeCard = card
      this.activeCardMap.set(this.dictionary.id, card)
    },
    initVT() {
      this.$nextTick(() => {
        if (this.cards.length === 0) {
          this.setDefaultVT()
          return
        }
        const tableTop = this.$refs[this.ids.tbody].getBoundingClientRect().top -this.vt.elementHeight -this.style.height.beforeField
        const viewPortY = document.documentElement.clientHeight
        if (tableTop > 0) {
          this.vt.step = Math.floor((viewPortY - tableTop) / this.vt.elementHeight)
        } else {
          this.vt.step = Math.floor(viewPortY / this.vt.elementHeight)
          this.vt.startIndex = Math.floor(-tableTop / this.vt.elementHeight)
        }
        this.vt.firstRowHeight = this.vt.startIndex * this.vt.elementHeight
        this.vt.lastRowHeight = this.cards.length * this.vt.elementHeight - this.vt.step * this.vt.elementHeight
        this.scrollToIndex(this.startIndexMap.get(this.dictionary.id), 0)
      })
    },
    handleVS: _.debounce(function () {
      if (this.show) {
        const top = this.$refs[this.ids.tbody].getBoundingClientRect().top -this.vt.elementHeight -this.style.height.beforeField
        const viewportY = document.documentElement.clientHeight
        let step = Math.floor(viewportY / this.vt.elementHeight)
        let startIndex = Math.floor(-top / this.vt.elementHeight)
        if ((startIndex + step) >= this.cards.length) {
          startIndex = this.cards.length - step
        }
        if (startIndex === this.vt.startIndex) {
          return
        }
        if (top < 0) {
          this.vt.step = step
          this.vt.startIndex = startIndex
        } else {
          this.vt.startIndex = 0
          this.vt.step = Math.floor((viewportY - top) / this.vt.elementHeight)
        }
        this.vt.firstRowHeight = this.vt.startIndex * this.vt.elementHeight
        this.vt.lastRowHeight =
            this.cards.length * this.vt.elementHeight -
            this.vt.step * this.vt.elementHeight -
            this.vt.firstRowHeight
        this.startIndexMap.set(this.dictionary.id,
            this.vt.startIndex
        )
      }
    }, 5),
    findIndex(cardId) {
      return this.cards.findIndex(c => c.id === cardId)
    },
    buildActiveCardFromMap() {
      this.activeCard = this.activeCardMap.get(this.dictionary.id)
    },
    setDefaultVT() {
      this.vt = {
        startIndex: 0,
        step: 1,
        elementHeight: 56,
        firstRowHeight: 0,
        lastRowHeight: 0,
        tableTop: 158,
      }
    },
    getCardById(id) {
      return this.cards.find(c => c.id === id)
    },
    buildHeight() {
      this.$nextTick(() => {
        this.style.height.col = window.innerHeight - this.height.header - this.height.footer - 6
        this.style.height.tools = document.getElementById(this.ids.tools).offsetHeight
        this.style.height.field = this.style.height.col - this.style.height.tools
        this.style.height.beforeField = this.height.header + this.style.height.tools + 3
      })
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
  table-layout: fixed;
  width: 100%;
}

.calcRow {
  width: 100%;
  height: 56px;
}

.st-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.st-sm {
  width: 45px;
  overflow-wrap: break-word;
  word-break: break-all;
}

.st-norm {
  width: 100px;
  overflow-wrap: break-word;
  word-break: normal;
}

.st-lg {
  width: 150px;
  overflow-wrap: break-word;
  word-break: normal;
}

.card-selected {
  background-color: #BBDEFB;
}

.card-active {
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
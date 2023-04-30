<template>
  <div v-if="show" class="dictionary-nav- btn-group-vertical btn-group-sm d-inline-block">
    <button class="btn  btn-primary text-left rounded-0  border-1 border-secondary" data-toggle="collapse"
            :href="'#'+getUniqueDictionariesElemId()"
            role="button"
            aria-expanded="false"
            :aria-controls="getUniqueDictionariesElemId()"
            @contextmenu.prevent="$refs.uniqueDictionaries.open"
    >
      <context-menu ref="uniqueDictionaries">
        <div class="btn-group-vertical btn-group-sm d-block">
          <button class="btn btn-outline-danger" @click="deleteDictionariesByUnique(true)">DELETE</button>
        </div>
      </context-menu>
      <span class="st-text-shift">{{ lang.map.db }}</span>
      <span class="st-right badge badge-light bg-white badge-pill">{{ getUniqueDictionaries().length }}</span>
    </button>
    <button class="btn  btn-outline-success d-flex justify-content-center align-items-center"
            data-toggle="modal"
            :data-target="'#' + this.prefixId + 'add-dictionary-unique-modal'"
    >
      <i class="fas fa-plus"></i>
    </button>
    <div class="collapse" :id="getUniqueDictionariesElemId()">
      <div class="btn-group-vertical btn-group-sm d-block">
        <button v-for="(d,i) in uniqueDictionaries"
                :key="d.id"
                :id="getDictionaryElemId(d.id)"
                class="btn  btn-outline-secondary text-left rounded-0  border-1 border-secondary" role="button"
                @mousedown.prevent.stop="mousedown(d.id)"
                @mouseup.prevent.stop="mouseup(d.id)"
                draggable="true"
                @click.prevent.stop="parentLoadDictionary(d)"
                @contextmenu.prevent="openUniqueDictionaryContextMenu(i)"
        >
          <context-menu ref="uniqueDictionaryContextMenu">
            <div class="btn-group-vertical btn-group-sm d-block">
              <button class="btn btn-outline-danger" @click="deleteDictionaryById(d.id)">DELETE</button>
            </div>
          </context-menu>
          <span class="st-text-shift">{{ d.name }}</span>
          <span class="st-right badge badge-light bg-white border badge-pill"> {{
              getCountCardsInDictionaryById(d.id)
            }} </span>
        </button>
      </div>
    </div>

    <button class="btn  btn-primary text-left rounded-0 m-0  border-1 border-secondary" data-toggle="collapse"
            :href="'#'+getNonUniqueDictionariesElemId()"
            role="button"
            aria-expanded="false"
            :aria-controls="getNonUniqueDictionariesElemId()"
            @contextmenu.prevent="$refs.nonUniqueDictionaries.open"
    >
      <context-menu ref="nonUniqueDictionaries">
        <div class="btn-group-vertical btn-group-sm d-block">
          <button class="btn btn-outline-danger" @click="deleteDictionariesByUnique(false)">DELETE</button>
        </div>
      </context-menu>
      <span class="st-text-shift">{{ lang.map.upload }}</span>
      <span class="st-right badge badge-light badge-pill">{{ nonUniqueDictionaries.length }}</span>
    </button>
    <button class="btn  btn-outline-success d-flex justify-content-center align-items-center"
            data-toggle="modal"
            :data-target="'#' + this.prefixId + 'add-dictionary-nonunique-modal'">
      <i class="fas fa-plus"></i>
    </button>
    <div class="collapse" :id="getNonUniqueDictionariesElemId()">
      <div v-for="(ldt,i) in nonUniqueShortLDTs"
           :key="ldt"
           class="btn-group-vertical btn-group-sm d-block">
        <button class="btn  btn-warning mr-sm-1 text-left rounded-0 m-0  border-1 border-secondary"
                data-toggle="collapse"
                :href="'#'+getNonUniqueDictionariesCreationShortLDTElemId(i)" role="button"
                aria-expanded="false"
                :aria-controls="getNonUniqueDictionariesCreationShortLDTElemId(i)">
          <span class="st-text-shift">{{ ldt }}</span>
          <span class="st-right badge badge-light badge-pill">{{ getCountUploadDictionaries(ldt) }}</span>
        </button>
        <div class="collapse" :id="getNonUniqueDictionariesCreationShortLDTElemId(i)">
          <div class="btn-group-vertical btn-group-sm d-block">
            <button v-for="(d,ii) in getUploadDictionaries(ldt)"
                    :key="d.id"
                    :id="getDictionaryElemId(d.id)"
                    class="btn  btn-outline-secondary text-left rounded-0  border-1 border-secondary" role="button"
                    draggable="true"
                    @mousedown.prevent.stop="mousedown(d.id)"
                    @mouseup.prevent.stop="mouseup(d.id)"
                    @click.prevent.stop="parentLoadDictionary(d)"
                    @contextmenu.prevent="openNonUniqueDictionaryContextMenu(i)"
            >
              <context-menu ref="nonUniqueDictionaryContextMenu">
                <div class="btn-group-vertical btn-group-sm d-block">
                  <button class="btn btn-outline-danger" @click="deleteDictionaryById(d.id)">DELETE</button>
                </div>
              </context-menu>
              <span class="st-text-shift">{{ d.name }}</span>
              <span
                  class="st-right badge badge-light border bg-white badge-pill">
                {{getCountCardsInDictionaryById(d.id) }}
              </span>
            </button>
          </div>

        </div>
      </div>
    </div>
    <add-dictionary-modal
        :id="this.prefixId + 'add-dictionary-unique-modal'"
        :unique="true"
    ></add-dictionary-modal>
    <add-dictionary-modal
        :id="this.prefixId + 'add-dictionary-nonunique-modal'"
        :unique="false"
    ></add-dictionary-modal>
    <GlobalEvents @mouseup="mouseupOutside()"/>
  </div>
</template>

<script>
import contextMenu from 'vue-context-menu'
import {mapState, mapGetters} from "vuex";
import addDictionaryModal from "./AddDictionaryModal.vue";
import date from "../../../util/date"
import * as _ from "lodash";

export default {
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
  },
  components: {
    contextMenu,
    addDictionaryModal,
  },
  props: [
    'instanceMark',
  ],
  watch: {
    $route: [
      'fetchData',
    ],
    dictionaries() {
      this.fetchData()
    },
  },
  computed: {
    ...mapState([
      'dictionaries',
      'lang',
    ]),
    ...mapGetters([
      'getUniqueDictionaries',
      'getNonUniqueDictionaries',
      'getCardsByDictionaryId',
      'getNonUniqueDictionariesPropertyValues',
      'getCountCardsInDictionaryById',
      'sortArrayByStringProperty',
    ]),
    prefixId() {
      return this.name + "-" + this.instanceMark + "-"
    },
  },
  data() {
    return {
      name: "dictionaryNav",
      show: true,
      uniqueDictionaries: [],
      nonUniqueDictionaries: [],
      nonUniqueShortLDTs: [],
      activeDictionaryElemId: null,

      groups: ["cardsChangeDictionary"],
      sourceMark: "cards",
      isMouseInClick: false,
      groupsInProcess: [],
      dragDictionaryId: null,
    }
  },
  methods: {
    fetchData() {
      this.show = false
      const uniqueDictionaries = this.getUniqueDictionaries()
      this.sortArrayByStringProperty(uniqueDictionaries, "name")
      this.uniqueDictionaries = uniqueDictionaries
      const nonUniqueDictionaries = this.getNonUniqueDictionaries()
      this.sortArrayByStringProperty(nonUniqueDictionaries, "name")
      this.nonUniqueDictionaries = nonUniqueDictionaries
      this.nonUniqueShortLDTs = this.getNonUniqueShortLDTs()
      this.show = true
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    getLang(key) {
      return this.lang.map[key]
    },
    getNonUniqueShortLDTs() {
      return [...new Set(this.getNonUniqueDictionariesPropertyValues("creationLDT").map(ldt => this.getShortLDT(ldt)))]
    },
    getDictionaryElemId(id) {
      return this.prefixId + "dictionary" + id
    },
    getShortLDT(ldt) {
      return date.parseISOString(ldt).toLocaleString()
    },
    async updateActiveDictionaryElemId(id) {
      if (this.activeDictionaryElemId) {
        $("#" + this.activeDictionaryElemId).removeClass("active-dictionary")
      }
      this.activeDictionaryElemId = this.getDictionaryElemId(id)
      $("#" + this.activeDictionaryElemId).addClass("active-dictionary")
    },
    getCountUploadDictionaries(shortLDT) {
      return this.getUploadDictionaries(shortLDT).length
    },
    getUploadDictionaries(shortLDT) {
      return this.nonUniqueDictionaries.filter(d => this.getShortLDT(d.creationLDT) === shortLDT)
    },
    getUniqueDictionariesElemId() {
      return this.prefixId + "uniqueDictionaries"
    },
    getNonUniqueDictionariesElemId() {
      return this.prefixId + "nonUniqueDictionaries"
    },
    getNonUniqueDictionariesCreationShortLDTElemId(i) {
      return this.prefixId + "nonUniqueDictionariesCreationLDT" + i
    },
    parentLoadDictionary(d) {
      this.updateActiveDictionaryElemId(d.id)
      return this.$emit('loadDictionary', d, this.instanceMark)
    },

    deleteDictionariesByUnique(unique) {
      this.$store.dispatch('deleteDictionariesByUniqueAndCascadeCardsAction', {unique: unique})
    },
    deleteDictionaryById(id) {
      this.$store.dispatch('deleteDictionaryByIdAction', {id: id})
    },
    openUniqueDictionaryContextMenu(i) {
      this.$refs.uniqueDictionaryContextMenu[i].open()
    },
    openNonUniqueDictionaryContextMenu(i) {
      this.$refs.nonUniqueDictionaryContextMenu[i].open()
    },

    mousedown(id) {
      this.isMouseInClick = true
      setTimeout(() => {
        if (this.isMouseInClick) {
          this.groupsInProcess = this.groups
          let items = this.getCardsByDictionaryId(id)
          if (items.length === 0) return
          this.dragDictionaryId = id
          this.activateDragstartStyle(id)
          this.$root.$emit("dragdrop-init", {groups: this.groups})
          const start = {
            groups: this.groups,
            data: {
              items: items,
              sourceMark: this.sourceMark,
              sourceId: id,
            },
          }
          this.$store.dispatch("dragdropStartAction", start)
        }
      }, 2)
    },
    mouseup(id) {
      this.isMouseInClick = false
      if (this.groupsInProcess.length > 0) {
        this.$root.$emit("dragdrop-destroy")
        const items = []
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
            sourceId: id,
          }
        }
        this.$store.dispatch("dragdropEndAndExecuteAction", end)
      }
    },
    dragdropInit(payload) {
      this.setFilteredGroupsInProcess(payload.groups)
      this.activateDragoverStyle(this.dictionaries)
    },
    dragdropDestroy() {
      this.deactivateDragstartStyle(this.dragDictionaryId)
      this.deactivateDragoverStyle(this.dictionaries)
      this.isMouseInClick = false
      this.groupsInProcess = []
      this.dragDictionaryId = null
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
    async activateDragstartStyle(id) {
      $("#" + this.getDictionaryElemId(id)).addClass("dragstart")
    },
    async deactivateDragstartStyle(id) {
      $("#" + this.getDictionaryElemId(id)).removeClass("dragstart")
    },
    async activateDragoverStyle(dictionaries) {
      dictionaries.forEach(d => $("#" + this.getDictionaryElemId(d.id)).addClass("dragover"))
    },
    async deactivateDragoverStyle(dictionaries) {
      dictionaries.forEach(d => $("#" + this.getDictionaryElemId(d.id)).removeClass("dragover"))
    },
  },
}
</script>

<style scoped>
.dictionary-nav- {
  height: 550px;
  overflow-y: auto;
}

.st-text-shift {
  overflow-wrap: anywhere;
}

.st-right {
  float: right
}

i {
  float: right;
}

.active-dictionary {
  background-color: gray;
  color: white;
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
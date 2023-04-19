<template>
  <div v-if="show" class="dictionary-nav- btn-group-vertical btn-group-sm d-inline-block">
    <button class="btn  btn-primary text-left rounded-0  border-1 border-secondary" data-toggle="collapse"
            :href="'#'+getCollapseForUnique()"
            role="button"
            aria-expanded="false"
            :aria-controls="getCollapseForUnique()"
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
    <button class="btn  btn-outline-success d-flex justify-content-center align-items-center" data-toggle="modal"
            data-target="#modal-addNewUnique">
      <i class="fas fa-plus"></i>
    </button>
    <add-dictionary-modal
        :id="'modal-addNewUnique'"
        :dictionaries="getUniqueDictionaries()"
        :unique="true"
    ></add-dictionary-modal>
    <div class="collapse" :id="getCollapseForUnique()">
      <div class="btn-group-vertical btn-group-sm d-block">
        <button v-for="(d,i) in getUniqueDictionaries()"
                :key="`A-${d.id}`"
                :id="getDictionaryElementId(d.id)"
                class="btn  btn-outline-secondary text-left rounded-0  border-1 border-secondary" role="button"
                @mousedown.prevent.stop="mousedown(d.id)"
                @mouseup.prevent.stop="mouseup(d.id)"
                draggable="true"
                @click.prevent.stop="parentLoadDictionary(d.id)"
                @contextmenu.prevent="openDictionaryContextMenu(d.id)"
        >
          <context-menu :ref="getDictionaryContextMenuRefById(d.id)">
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
            :href="'#'+getCollapseForNonUnique()"
            role="button"
            aria-expanded="false"
            :aria-controls="getCollapseForNonUnique()"
            @contextmenu.prevent="$refs.nonUniqueDictionaries.open"
    >
      <context-menu ref="nonUniqueDictionaries">
        <div class="btn-group-vertical btn-group-sm d-block">
          <button class="btn btn-outline-danger" @click="deleteDictionariesByUnique(false)">DELETE</button>
        </div>
      </context-menu>
      <span class="st-text-shift">{{ lang.map.upload }}</span>
      <span class="st-right badge badge-light badge-pill">{{ getNonUniqueDictionaries().length }}</span>
    </button>
    <button class="btn  btn-outline-success d-flex justify-content-center align-items-center" data-toggle="modal"
            data-target="#modal-addNewUpload">
      <i class="fas fa-plus"></i>
    </button>
    <add-dictionary-modal
        :id="'modal-addNewUpload'"
        :dictionaries="getNonUniqueDictionaries()"
        :unique="false"
    ></add-dictionary-modal>
    <div class="collapse" :id="getCollapseForNonUnique()">
      <div v-for="(ldt,i) in creationLDTs"
           :key="`A-${ldt}`"
           class="btn-group-vertical btn-group-sm d-block">
        <button class="btn  btn-warning mr-sm-1 text-left rounded-0 m-0  border-1 border-secondary"
                data-toggle="collapse"
                :href="'#'+getCollapseForNonUniqueCreationLDTs(i)" role="button"
                aria-expanded="false"
                :aria-controls="getCollapseForNonUniqueCreationLDTs(i)">
          <span class="st-text-shift">{{ getShortLdt(ldt) }}</span>
          <span class="st-right badge badge-light badge-pill">{{ getCountUploadDictionaries(ldt) }}</span>
        </button>
        <div class="collapse" :id="getCollapseForNonUniqueCreationLDTs(i)">
          <div class="btn-group-vertical btn-group-sm d-block">
            <button v-for="(d,ii) in getUploadDictionaries(ldt)"
                    :key="`B-${d.id}`"
                    :id="getDictionaryElementId(d.id)"
                    class="btn  btn-outline-secondary text-left rounded-0  border-1 border-secondary" role="button"
                    draggable="true"
                    @mousedown.prevent.stop="mousedown(d.id)"
                    @mouseup.prevent.stop="mouseup(d.id)"
                    @click.prevent.stop="parentLoadDictionary(d.id)"
            >
              <span class="st-text-shift">{{ d.name }}</span>
              <span
                  class="st-right badge badge-light border bg-white badge-pill">{{ getCountCardsInDictionaryById(d.id) }}</span>
            </button>
          </div>

        </div>
      </div>
    </div>
    <GlobalEvents @mouseup="mouseupOutside()"/>
  </div>
</template>

<script>
import contextMenu from 'vue-context-menu'
import {mapState, mapGetters} from "vuex";
import addDictionaryModal from "./AddDictionaryModal.vue";
import date from "../../../util/date"

export default {
  created() {
    this.$root.$on('dragdrop-init', (payload) => {
      this.dragdropInit(payload)
    })
    this.$root.$on('dragdrop-destroy', () => {
      this.dragdropDestroy()
    })
  },
  components: {
    contextMenu,
    addDictionaryModal,
  },
  props: ['instanceMark'],
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
      'getNonUniqueDictionariesUniquePropertyValues',
      'getCountCardsInDictionaryById',
    ]),
  },
  data() {
    return {
      show: true,
      creationLDTs: [],
      name: "dictionary-nav-",
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
      this.creationLDTs = this.getNonUniqueDictionariesUniquePropertyValues("creationLDT")
      this.show = true
    },
    getDictionaryElementId(id) {
      return this.instanceMark + this.name + id
    },
    getShortLdt(ldt) {
      return date.parseISOString(ldt).toLocaleString()
    },
    async updateActiveDictionaryElemId(id) {
      if (this.activeDictionaryElemId) {
        $("#" + this.activeDictionaryElemId).removeClass("active-dictionary")
      }
      this.activeDictionaryElemId = this.getDictionaryElementId(id)
      $("#" + this.activeDictionaryElemId).addClass("active-dictionary")
    },
    getCountUploadDictionaries(ldt) {
      return this.getUploadDictionaries(ldt).length
    },
    getUploadDictionaries(ldt) {
      return this.getNonUniqueDictionaries().filter(d => d.creationLDT === ldt)
    },
    getCollapseForUnique() {
      return this.instanceMark + "collapseUnique"
    },
    getCollapseForNonUnique() {
      return this.instanceMark + "collapseNonUnique"
    },
    getCollapseForNonUniqueCreationLDTs(i) {
      return this.instanceMark + "collapseCreationLDT" + i
    },
    parentLoadDictionary(id) {
      this.updateActiveDictionaryElemId(id)
      return this.$emit('loadDictionary', id, this.instanceMark)
    },

    deleteDictionariesByUnique(unique) {
      this.$store.dispatch('deleteDictionariesByUniqueAndCascadeCardsAction', {unique: unique})
    },
    deleteDictionaryById(id) {
      this.$store.dispatch('deleteDictionaryByIdAction', {id: id})
    },
    getDictionaryContextMenuRefById(id) {
      return "dictionaryContextMenu-" + id
    },
    openDictionaryContextMenu(id) {
      this.$refs[this.getDictionaryContextMenuRefById(id)].open()
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
            isDragdropSplice: false,
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
      $("#" + this.getDictionaryElementId(id)).addClass("dragstart")
    },
    async deactivateDragstartStyle(id) {
      $("#" + this.getDictionaryElementId(id)).removeClass("dragstart")
    },
    async activateDragoverStyle(dictionaries) {
      dictionaries.forEach(d => $("#" + this.getDictionaryElementId(d.id)).addClass("dragover"))
    },
    async deactivateDragoverStyle(dictionaries) {
      dictionaries.forEach(d => $("#" + this.getDictionaryElementId(d.id)).removeClass("dragover"))
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
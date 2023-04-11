<template>
  <div class="dictionary-nav- btn-group-vertical btn-group-sm d-inline-block">
    <button class="btn  btn-primary text-left rounded-0  border-1 border-secondary" data-toggle="collapse"
            :href="'#'+getCollapseForDb()"
            role="button"
            aria-expanded="false"
            :aria-controls="getCollapseForDb()"
            @contextmenu.prevent="$refs.deleteDb.open"
    >
      <context-menu ref="deleteDb">
        <div class="btn-group-vertical btn-group-sm d-block">
          <button class="btn btn-outline-danger" @click="deleteDb">DELETE</button>
        </div>
      </context-menu>
      <span class="st-text-shift">{{ lang.map.db }}</span>
      <span class="st-right badge badge-light bg-white badge-pill">{{ dbDictionaries.length }}</span>
    </button>
    <button class="btn  btn-outline-success d-flex justify-content-center align-items-center" data-toggle="modal"
            data-target="#modal-addNewDb">
      <i class="fas fa-plus"></i>
    </button>
    <add-dictionary-modal
        :id="'modal-addNewDb'"
        :dictionaries="dbDictionaries"
        :store-action="dbStoreAction"
    ></add-dictionary-modal>
    <div class="collapse" :id="getCollapseForDb()">
      <div class="btn-group-vertical btn-group-sm d-block">
        <button v-for="(d,i) in dbDictionaries"
                :key="`A-${d.id}`"
                :id="getDictionaryDbElementId(d)"
                class="btn  btn-outline-secondary text-left rounded-0  border-1 border-secondary" role="button"
                @mousedown.prevent.stop="mousedown(d, cardDbSourceMark)"
                @mouseup.prevent.stop="mouseup(d , cardDbSourceMark)"
                draggable="true"
                @click.prevent.stop="parentLoadDictionary(d, cardDbSourceMark)"
                @contextmenu.prevent="openRefDeleteDb(i)"
        >
          <context-menu ref="deleteDbDictionary">
            <div class="btn-group-vertical btn-group-sm d-block">
              <button class="btn btn-outline-danger" @click="deleteDbById(d.id)">DELETE</button>
            </div>
          </context-menu>
          <span class="st-text-shift">{{ d.name }}</span>
          <span class="st-right badge badge-light bg-white border badge-pill"> {{ getCountDbDictionary(d) }} </span>
        </button>
      </div>
    </div>

    <button class="btn  btn-primary text-left rounded-0 m-0  border-1 border-secondary" data-toggle="collapse"
            :href="'#'+getCollapseForUpload()"
            role="button"
            aria-expanded="false"
            :aria-controls="getCollapseForUpload()"
            @contextmenu.prevent="$refs.deleteUpload.open"
    >
      <context-menu ref="deleteUpload">
        <div class="btn-group-vertical btn-group-sm d-block">
          <button class="btn btn-outline-danger" @click="deleteUpload">DELETE</button>
        </div>
      </context-menu>
      <span class="st-text-shift">{{ lang.map.upload }}</span>
      <span class="st-right badge badge-light badge-pill">{{ uploadDictionaries.length }}</span>
    </button>
    <button class="btn  btn-outline-success d-flex justify-content-center align-items-center" data-toggle="modal"
            data-target="#modal-addNewUpload">
      <i class="fas fa-plus"></i>
    </button>
    <add-dictionary-modal
        :id="'modal-addNewUpload'"
        :dictionaries="uploadDictionaries"
        :store-action="uploadStoreAction"
    ></add-dictionary-modal>
    <div class="collapse" :id="getCollapseForUpload()">
      <div v-for="(ldt,i) in uploadLDTs"
           :key="`A-${ldt}`"
           class="btn-group-vertical btn-group-sm d-block">
        <button class="btn  btn-warning mr-sm-1 text-left rounded-0 m-0  border-1 border-secondary"
                data-toggle="collapse"
                :href="'#'+getCollapseForUploadLDTs(i)" role="button"
                aria-expanded="false"
                :aria-controls="getCollapseForUploadLDTs(i)">
          <span class="st-text-shift">{{ getShortLdt(ldt) }}</span>
          <span class="st-right badge badge-light badge-pill">{{ getCountUploadDictionaries(ldt) }}</span>
        </button>
        <div class="collapse" :id="getCollapseForUploadLDTs(i)">
          <div class="btn-group-vertical btn-group-sm d-block">
            <button v-for="(d,ii) in getUploadDictionaries(ldt)"
                    :key="`B-${d.id}`"
                    :id="getDictionaryUploadElementId(d)"
                    class="btn  btn-outline-secondary text-left rounded-0  border-1 border-secondary" role="button"
                    draggable="true"
                    @mousedown.prevent.stop="mousedown(d, cardUploadSourceMark)"
                    @mouseup.prevent.stop="mouseup(d , cardUploadSourceMark)"
                    @click.prevent.stop="parentLoadDictionary(d, cardUploadSourceMark)"
            >
              <span class="st-text-shift">{{ d.name }}</span>
              <span class="st-right badge badge-light border bg-white badge-pill">{{getCountUploadDictionary(d)}}</span>
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
  computed: {
    ...mapState([
      'cards',
      'lang',
    ]),
    ...mapGetters([
      'getCardsByDictionaryDbId',
      'getCardsByDictionaryUploadId',
      'isDbSource',
      'isUploadSource',
      'getCardsUploadCreationLDTs',
      'getUploadDictionaryInx',
      'getDbDictionaryInx',
    ]),
    uploadLDTs() {
      return this.getCardsUploadCreationLDTs
    },
    uploadDictionaries() {
      return this.cards.upload.dictionaries
    },
    dbDictionaries() {
      return this.cards.db.dictionaries
    },
    uploadCards() {
      return this.cards.upload.cards
    },
    dbCards() {
      return this.cards.db.cards
    },
    cardDbSourceMark(){
      return this.cards.db.sourceMark
    },
    cardUploadSourceMark(){
      return this.cards.upload.sourceMark
    }
  },
  data() {
    return {
      name: "dictionary-nav",
      activeDictionaryElemId: null,
      uploadStoreAction: 'addDictionaryUploadAction',
      dbStoreAction: 'addDictionaryDbWithPictureAction',

      groups: ["card"],
      isMouseInClick: false,
      groupsInProcess: [],
      dragDictionary: null,
      dragSourceMark: null,
    }
  },
  methods: {
    fetchData() {

    },
    getDictionaryDbElementId(dictionary){
      return this.instanceMark + this.name + this.cardDbSourceMark + dictionary.id
    },
    getDictionaryUploadElementId(dictionary){
      return this.instanceMark + this.name + this.cardUploadSourceMark + dictionary.id
    },
    getShortLdt(ldt){
      if(ldt){
        return ldt.toLocaleString()
      }else{
        return "custom"
      }
    },
    async updateActiveDictionaryElemId(d, sourceMark) {
      if (this.activeDictionaryElemId) {
        $("#" + this.activeDictionaryElemId).removeClass("active-dictionary")
      }
      if (sourceMark === this.cardDbSourceMark) this.activeDictionaryElemId = this.getDictionaryDbElementId(d)
      if (sourceMark === this.cardUploadSourceMark) this.activeDictionaryElemId = this.getDictionaryUploadElementId(d)
      $("#" + this.activeDictionaryElemId).addClass("active-dictionary")
    },
    getCountDbDictionary(d) {
      return this.dbCards.filter(item => item.dictionary.id === d.id).length
    },
    getCountUploadDictionary(d) {
      const inx = this.getUploadDictionaryInx(d.id)
      return this.uploadCards[inx].length
    },
    getCountUploadDictionaries(ldt) {
      return this.getUploadDictionaries(ldt).length
    },
    getUploadDictionaries(ldt) {
      return this.uploadDictionaries.filter(item => item.creationLDT === ldt)
    },
    getCollapseForDb() {
      return this.instanceMark + "collapseDb"
    },
    getCollapseForUpload() {
      return this.instanceMark + "collapseUpload"
    },
    getCollapseForUploadLDTs(i) {
      return this.instanceMark + "collapseFilenames" + i
    },
    parentLoadDictionary(d, sourceMark) {
      this.updateActiveDictionaryElemId(d, sourceMark)
      return this.$emit('loadDictionary', d.id, sourceMark, this.instanceMark)
    },

    deleteUpload() {
      this.$store.dispatch('deleteCardsUploadAction')
    },
    deleteDb() {
      this.$store.dispatch('deleteCardsDbAction')
    },
    deleteDbById(id) {
      this.$store.dispatch('deleteDictionaryDbAction', {id: id})
    },
    openRefDeleteDb(i) {
      this.$refs.deleteDbDictionary[i].open()
    },

    mousedown(d, sourceMark) {
      this.isMouseInClick = true
      setTimeout(() => {
        if (this.isMouseInClick) {
          this.groupsInProcess = this.groups
          let items = []
          if (sourceMark === this.cardDbSourceMark) items = this.getCardsByDictionaryDbId(d.id)
          if (sourceMark === this.cardUploadSourceMark) items = this.getCardsByDictionaryUploadId(d.id)
          if (items.length === 0) return
          this.dragDictionary = d
          this.dragSourceMark = sourceMark
          this.activateDragstartStyle(d, sourceMark)
          this.$root.$emit("dragdrop-init", {groups: this.groups})
          const start = {
            groups: this.groups,
            data: {
              items: items,
              sourceMark: sourceMark,
              sourceId: d.id,
            },
          }
          this.$store.dispatch("dragdropStartAction", start)
        }
      }, 2)
    },
    mouseup(d, sourceMark) {
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
            sourceMark: sourceMark,
            sourceId: d.id,
          }
        }
        this.$store.dispatch("dragdropEndAndExecuteAction", end)
      }
    },
    dragdropInit(payload){
      this.setFilteredGroupsInProcess(payload.groups)
      this.activateDragoverStyle(this.dbDictionaries, this.uploadDictionaries)
    },
    dragdropDestroy(){
      this.deactivateDragstartStyle(this.dragDictionary, this.dragSourceMark)
      this.deactivateDragoverStyle(this.dbDictionaries, this.uploadDictionaries)
      this.isMouseInClick = false
      this.groupsInProcess = []
      this.dragDictionary = null
      this.dragSourceMark = null
    },
    mouseupOutside(){
      if (this.isDragdropInProcess()){
        this.dragdropDestroy()
      }
    },
    isDragdropInProcess(){
      return this.groupsInProcess.length > 0
    },
    async setFilteredGroupsInProcess(groups) {
      this.groupsInProcess = this.groups.filter(x => groups.indexOf(x) >= 0)
    },
    async activateDragstartStyle(d, sourceMark) {
      if (sourceMark === this.cardUploadSourceMark){
        $("#" + this.getDictionaryUploadElementId(d)).addClass("dragstart")
      }
      if (sourceMark === this.cardDbSourceMark){
        $("#" + this.getDictionaryDbElementId(d)).addClass("dragstart")
      }
    },
    async deactivateDragstartStyle(d, sourceMark) {
      if (sourceMark === this.cardUploadSourceMark){
        $("#" + this.getDictionaryUploadElementId(d)).removeClass("dragstart")
      }
      if (sourceMark === this.cardDbSourceMark){
        $("#" + this.getDictionaryDbElementId(d)).removeClass("dragstart")
      }
    },
    async activateDragoverStyle(dictionariesDb, dictionariesUpload) {
      dictionariesDb.forEach(d => $("#" + this.getDictionaryDbElementId(d)).addClass("dragover"))
      dictionariesUpload.forEach(d => $("#" + this.getDictionaryUploadElementId(d)).addClass("dragover"))
    },
    async deactivateDragoverStyle(dictionariesDb, dictionariesUpload) {
      dictionariesDb.forEach(d => $("#" + this.getDictionaryDbElementId(d)).removeClass("dragover"))
      dictionariesUpload.forEach(d => $("#" + this.getDictionaryUploadElementId(d)).removeClass("dragover"))
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
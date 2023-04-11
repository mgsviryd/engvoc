<template>
  <div class="dictionary-nav- btn-group-vertical btn-group-sm d-inline-block">
    <button class="btn  btn-primary text-left rounded-0  border-1 border-secondary" data-toggle="collapse"
            :href="hash+getCollapseForDb()"
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
                :id="inst + 'dbDictionary' + d.id"
                class="btn  btn-outline-secondary text-left rounded-0  border-1 border-secondary" role="button"
                draggable="true"
                @dragstart="dragstartDb(d)"
                @dragover="dragoverDb(d)"
                @dragleave="dragleaveDb(d)"
                @contextmenu.prevent="openRefDeleteDb(i)"
                @click.prevent.stop="parentLoadDbDictionary(d.id)"
        >
          <context-menu ref="deleteDbDictionary">
            <div class="btn-group-vertical btn-group-sm d-block">
              <button class="btn btn-outline-danger" @click="deleteDbById(d.id)">DELETE</button>
            </div>
          </context-menu>
          <span class="st-text-shift">{{ d.name }}</span>
          <span class="st-right badge badge-light border badge-pill"> {{ getCountDbDictionary(d) }} </span>
        </button>
      </div>
    </div>

    <button class="btn  btn-primary text-left rounded-0 m-0  border-1 border-secondary" data-toggle="collapse"
            :href="hash+getCollapseForUpload()"
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
                :href="hash+getCollapseForUploadLDTs(i)" role="button"
                aria-expanded="false"
                :aria-controls="getCollapseForUploadLDTs(i)">
          <span class="st-text-shift">{{ getShortLdt(ldt) }}</span>
          <span class="st-right badge badge-light badge-pill">{{ getCountUploadDictionaries(ldt) }}</span>
        </button>
        <div class="collapse" :id="getCollapseForUploadLDTs(i)">
          <div class="btn-group-vertical btn-group-sm d-block"
               draggable="true"
               @dragenter="preventDragdropNowhere()"
          >
            <button v-for="(d,ii) in getUploadDictionaries(ldt)"
                    :key="`B-${d.id}`"
                    :id="inst + 'uploadDictionary'+ d.id"
                    class="btn  btn-outline-secondary text-left rounded-0  border-1 border-secondary" role="button"
                    draggable="true"
                    @dragstart="dragstartUpload(d)"
                    @dragover="dragoverUpload(d)"
                    @dragleave="dragleaveUpload(d)"
                    @click.prevent.stop="parentLoadUploadDictionary(d.id)"
            >
              <span class="st-text-shift">{{ d.name }}</span>
              <span class="st-right badge badge-light border bg-white badge-pill">{{getCountUploadDictionary(d)}}</span>
            </button>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script>
import contextMenu from 'vue-context-menu'
import {mapState, mapGetters} from "vuex";
import addDictionaryModal from "./AddDictionaryModal.vue";

export default {
  created() {
    this.$root.$on('confirm-dragstart', (dragdrop) => {
      if(this.isCurrentDragstart(dragdrop) && !this.isInsideSameSource(dragdrop)){
        this.confirmDragstart(dragdrop)
      }
    })
    this.$root.$on('confirm-dragleave', (dragdrop) => {
      if(this.isCurrentDragleave(dragdrop) && !this.isInsideSameSource(dragdrop)){
        this.confirmDragleave(dragdrop)
      }
    })
  },
  components: {
    contextMenu,
    addDictionaryModal,
  },
  props: ['inst'],
  computed: {
    ...mapState([
      'cards',
      'lang',
    ]),
    ...mapGetters([
      'getCardsDbByDictionaryId',
      'getCardsUploadByDictionaryId',
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
    db() {
      return {
        markSource: "db",
        actions: {
          removeAllAction: 'cardDbRemoveAllAction',
          addAllAction: 'cardDbAddAllAction',
          updateAllAction: 'cardDbUpdateAllAction',
          addUpdateAllAction: 'cardDbAddUpdateAllAction',
        },
      }
    },
    upload() {
      return {
        markSource: "upload",
        actions: {
          removeAllAction: 'cardUploadRemoveAllAction',
          addAllAction: 'cardUploadAddAllAction',
          updateAllAction: 'cardUploadUpdateAllAction',
          addUpdateAllAction: 'cardUploadAddUpdateAllAction',
        },
      }
    },
  },
  data() {
    return {
      hash: "#",
      activeDictionaryElemId: null,
      uploadStoreAction: 'addDictionaryUploadAction',
      dbStoreAction: 'addDictionaryDbWithPictureAction',
      type: "card",
      dragdrop: {
        start: {
          type: "",
          operation: "add", // add / update // addUpdate
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
          actions: {
            removeAllAction: "",
            addAllAction: "",
            updateAllAction: "",
            addUpdateAllAction: "",
          },
          markSource: "",
          pushSourceId: -1,
        },
      },
    }
  },
  methods: {
    fetchData() {

    },
    getShortLdt(ldt){
      if(ldt){
        return ldt.toLocaleString()
      }else{
        return "custom"
      }
    },
    updateActiveDbElemId(id) {
      if (this.activeDictionaryElemId) {
        $("#" + this.activeDictionaryElemId).removeClass("active-dictionary")
      }
      $("#" + this.inst + "dbDictionary" + id).addClass("active-dictionary")
      this.activeDictionaryElemId = this.inst + "dbDictionary" + id
    },
    updateActiveUploadElemId(id) {
      if (this.activeDictionaryElemId) {
        $("#" + this.activeDictionaryElemId).removeClass("active-dictionary")
      }
      $("#" + this.inst + "uploadDictionary" + id).addClass("active-dictionary")
      this.activeDictionaryElemId = this.inst + "uploadDictionary" + id
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
      return this.inst + "collapseDb"
    },
    getCollapseForUpload() {
      return this.inst + "collapseUpload"
    },
    getCollapseForUploadLDTs(i) {
      return this.inst + "collapseFilenames" + i
    },
    parentLoadDbDictionary(id) {
      this.updateActiveDbElemId(id)
      return this.$emit('loadDictionary', id, "db", this.inst)
    },
    parentLoadUploadDictionary(id) {
      this.updateActiveUploadElemId(id)
      return this.$emit('loadDictionary', id, "upload", this.inst)
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

    isInsideSameSource(dragdrop) {
      return dragdrop.start.markSource === dragdrop.leave.markSource && dragdrop.start.pullSourceId === dragdrop.leave.pushSourceId
    },
    isCurrentDragstart(dragdrop){
      return this.dragdrop.start.type === dragdrop.start.type && this.dragdrop.start.ldt === dragdrop.start.ldt
    },
    isCurrentDragleave(dragdrop){
      return this.dragdrop.leave.type === dragdrop.leave.type && this.dragdrop.leave.ldt === dragdrop.leave.ldt
    },
    confirmDragstart(dragdrop){
      if (dragdrop.start.pull === "delete") {
        this.$store.dispatch(dragdrop.start.actions.removeAllAction,
            {
              cards: dragdrop.start.pullItems,
              id: dragdrop.start.pullSourceId
            })
      }
    },
    confirmDragleave(dragdrop){
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
      let payload = {
        type: "Nowhere",
        ldt: new Date(),
      }
      this.$root.$emit('dragover', payload)
    },
    dragstartDb(d) {
      // console.info("dragstart: " + d.id)
      const items = this.getCardsDbByDictionaryId(d.id)
      let payload = {
        type: "card",
        operation: "add", // add / update / add-update
        ldt: new Date(),
        pull: "delete", // preserve / delete
        pullItems: items,
        actions: this.db.actions,
        markSource: this.db.markSource,
        pullSourceId: d.id,
      }
      this.dragdrop.start = payload
      this.$root.$emit('dragstart', payload)
    },
    dragoverDb: _.throttle(function (d) {
      // console.info("dragover: " + d.id)
      let payload = {
        type: "card",
        ldt: new Date(),
      }
      this.dragdrop.over = payload
      this.$root.$emit('dragover', payload)
    }, 40),

    dragleaveDb: _.throttle(function (d) {
      // console.info("dragleave: " + d.id)
      const items = []
      const payload = {
        ldt: new Date(),
        push: "preserve", // preserve / delete
        pushItems: items,
        actions: this.db.actions,
        markSource: this.db.markSource,
        pushSourceId: d.id,
      }
      this.dragdrop.leave = payload
      this.$root.$emit('dragleave', payload)
    }, 40),
    dragstartUpload(d) {
      // console.info("dragstart: " + d.id)
      const items = this.getCardsUploadByDictionaryId(d.id)
      let payload = {
        type: "card",
        operation: "add", // add / update / add-update
        ldt: new Date(),
        pull: "delete", // preserve / delete
        pullItems: items,
        actions: this.upload.actions,
        markSource: this.upload.markSource,
        pullSourceId: d.id,
      }
      this.dragdrop.start = payload
      this.$root.$emit('dragstart', payload)
    },
    dragoverUpload: _.throttle(function (d) {
      // console.info("dragover: " + d.id)
      let payload = {
        type: "card",
        ldt: new Date(),
      }
      this.dragdrop.over = payload
      this.$root.$emit('dragover', payload)
    }, 40),

    dragleaveUpload: _.throttle(function (d) {
      // console.info("dragleave: " + d.id)
      const items = []
      const payload = {
        type: "card",
        ldt: new Date(),
        push: "preserve", // preserve / delete
        pushItems: items,
        actions: this.upload.actions,
        pushSourceId: d.id,
      }
      this.dragdrop.leave = payload
      this.$root.$emit('dragleave', payload)
    }, 40),
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

.st-text-shift:hover {

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
</style>
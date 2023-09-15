<template>
  <div v-if="show" class="dictionary-nav- btn-group-vertical btn-group-sm d-inline-block" style="width: 100%">
    <button :id="'button'+getUnrepeatedDictionariesElemId()"
            :aria-controls="getUnrepeatedDictionariesElemId()"
            :href="'#'+getUnrepeatedDictionariesElemId()"
            aria-expanded="false"
            class="btn btn-primary text-left rounded-0  border-1 border-secondary"
            data-toggle="collapse"
            role="button"
            @contextmenu.prevent="$refs.unrepeatedDictionaries.open"
    >
      <context-menu ref="unrepeatedDictionaries">
        <b-button-group class="d-block" size="sm" vertical>
          <b-button
              variant="outline-danger"
              @click="deleteDictionariesByUnrepeated(true)">
            {{ getUpperCaseLang('delete') }}
          </b-button>
          <b-button
              variant="outline-success"
              @click="downloadDictionariesXmlFilesByUnrepeated(true)">
            <span>{{ getCapitalizeLang('downloadTo') + ' ' }}
            <img alt="..." height="24" src="/static/picture/icon/xml-extension.png" width="24">
              </span>
          </b-button>
        </b-button-group>
      </context-menu>
      <span class="st-text-shift">{{ getLang('db') }}</span>
      <span class="st-right badge badge-light bg-white badge-pill">{{ getUnrepeatedDictionaries().length }}</span>
    </button>
    <button class="btn  btn-outline-success d-flex justify-content-center align-items-center"
            @click="$refs[id.addDictionaryUnrepeatedModal].showModal()"
    >
      <i class="fas fa-plus"></i>
    </button>
    <div :id="getUnrepeatedDictionariesElemId()" class="collapse">
      <div class="btn-group-vertical btn-group-sm d-block">
        <button v-for="(d,i) in unrepeatedDictionaries"
                :id="getDictionaryElemId(d.id)"
                :key="d.id"
                class="btn  btn-outline-secondary text-left rounded-0  border-1 border-secondary" draggable="true"
                role="button"
                @mousedown.prevent.stop="mousedown(d.id)"
                @mouseup.prevent.stop="mouseup(d.id)"
                @click.prevent.stop="parentLoadDictionary(d)"
                @contextmenu.prevent="openUnrepeatedDictionaryContextMenu(i)"
        >
          <context-menu ref="unrepeatedDictionaryContextMenu">
            <div class="btn-group-vertical btn-group-sm d-block">
              <button class="btn btn-outline-danger" @click="deleteDictionaryById(d.id)">
                {{ getUpperCaseLang('delete') }}
              </button>
            </div>
          </context-menu>
          <span class="st-text-shift">{{ d.name }}</span>
          <span class="st-right badge badge-light bg-white border badge-pill">
            {{ getCountCardsInDictionaryById(d.id) }}
          </span>
        </button>
      </div>
    </div>

    <button :id="'button'+getNonUnrepeatedDictionariesElemId()" :aria-controls="getNonUnrepeatedDictionariesElemId()"
            :href="'#'+getNonUnrepeatedDictionariesElemId()"
            aria-expanded="false"
            class="btn  btn-primary text-left rounded-0 m-0  border-1 border-secondary"
            data-toggle="collapse"
            role="button"
            @contextmenu.prevent="$refs.nonUnrepeatedDictionaries.open"
    >
      <context-menu ref="nonUnrepeatedDictionaries">
        <b-button-group class="d-block" size="sm" vertical>
          <b-button
              variant="outline-danger"
              @click="deleteDictionariesByUnrepeated(false)">
            {{ getUpperCaseLang('delete') }}
          </b-button>
          <b-button
              variant="outline-success"
              @click="downloadDictionariesXmlFilesByUnrepeated(false)">
          <span>
          {{ getCapitalizeLang('downloadTo') + ' ' }}
          <img alt="..." height="24" src="/static/picture/icon/xml-extension.png" width="24">
            </span>
          </b-button>
        </b-button-group>
      </context-menu>
      <span class="st-text-shift">{{ getLang('upload') }}</span>
      <span class="st-right badge badge-light badge-pill">{{ nonUnrepeatedDictionaries.length }}</span>
    </button>
    <b-button
        class="d-flex justify-content-center align-items-center"
        variant="outline-success"
        @click="$refs[id.addDictionaryNonUnrepeatedModal].showModal()"
    >
      <i class="fas fa-plus"></i>
    </b-button>
    <div :id="getNonUnrepeatedDictionariesElemId()" class="collapse">
      <div v-for="(ldt,i) in nonUnrepeatedShortLDTs"
           :key="ldt"
           class="btn-group-vertical btn-group-sm d-block">
        <button :aria-controls="getNonUnrepeatedDictionariesCreationShortLDTElemId(i)"
                :href="'#'+getNonUnrepeatedDictionariesCreationShortLDTElemId(i)"
                aria-expanded="false"
                class="btn  btn-warning mr-sm-1 text-left rounded-0 m-0  border-1 border-secondary"
                data-toggle="collapse"
                role="button">
          <span class="st-text-shift">{{ ldt }}</span>
          <span class="st-right badge badge-light badge-pill">{{ getCountUploadDictionaries(ldt) }}</span>
        </button>
        <div :id="getNonUnrepeatedDictionariesCreationShortLDTElemId(i)" class="collapse">
          <div class="btn-group-vertical btn-group-sm d-block">
            <button v-for="(d,ii) in getUploadDictionaries(ldt)"
                    :id="getDictionaryElemId(d.id)"
                    :key="d.id"
                    class="btn  btn-outline-secondary text-left rounded-0  border-1 border-secondary" draggable="true"
                    role="button"
                    @mousedown.prevent.stop="mousedown(d.id)"
                    @mouseup.prevent.stop="mouseup(d.id)"
                    @click.prevent.stop="parentLoadDictionary(d)"
                    @contextmenu.prevent="openNonUnrepeatedDictionaryContextMenu(i)"
            >
              <context-menu ref="nonUnrepeatedDictionaryContextMenu">
                <div class="btn-group-vertical btn-group-sm d-block">
                  <button class="btn btn-outline-danger" @click="deleteDictionaryById(d.id)">
                    {{ getUpperCaseLang('delete') }}
                  </button>
                </div>
              </context-menu>
              <span class="st-text-shift">{{ d.name }}</span>
              <span
                  class="st-right badge badge-light border bg-white badge-pill">
                {{ getCountCardsInDictionaryById(d.id) }}
              </span>
            </button>
          </div>

        </div>
      </div>
    </div>
    <add-dictionary-modal
        :id="id.addDictionaryUnrepeatedModal"
        :ref="id.addDictionaryUnrepeatedModal"
        :closable="true"
        :unrepeated="true"
    ></add-dictionary-modal>
    <add-dictionary-modal
        :id="id.addDictionaryNonUnrepeatedModal"
        :ref="id.addDictionaryNonUnrepeatedModal"
        :closable="true"
        :unrepeated="false"
    ></add-dictionary-modal>
    <GlobalEvents @mouseup="mouseupOutside()"/>
  </div>
</template>

<script>
import {mapState, mapGetters} from 'vuex'
import ContextMenu from 'vue-context-menu'
import AddDictionaryModal from './AddDictionaryModal.vue'
import date from '../../../util/date'
import * as _ from 'lodash'

export default {
  components: {
    ContextMenu,
    AddDictionaryModal,
  },
  props: [
    'instance',
  ],
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
    this.$store.watch(this.$store.getters.getVocabularyId, vocabularyId => {
      this.$forceNextTick(() => {
        this.fetchData()
        this.goToDictionary()
      })
    })
  },
  watch: {
    $route: [
      'fetchData',
    ],
    dictionaries: {
      handler: function () {
        this.$forceNextTick(() => {
          this.fetchData()
        })
      },
      deep: true
    },
    dictionary() {
    }
  },
  computed: {
    ...mapState([
      'dictionaries',
      'lang',
    ]),
    ...mapGetters([
      'getDictionaryIdsByUnrepeated',
      'getUnrepeatedDictionaries',
      'getNonUnrepeatedDictionaries',
      'getCardsByDictionaryId',
      'getNonUnrepeatedDictionariesPropertyValues',
      'getCountCardsInDictionaryById',
      'sortArrayByStringProperty',
      'isDictionaryUnrepeated',
    ]),
    prefixId() {
      return this.name + "-" + this.instance.instanceMark + "-"
    },
  },
  data() {
    return {
      id: {
        addDictionaryUnrepeatedModal: this.prefixId + 'add-dictionary-unrepeated-modal',
        addDictionaryNonUnrepeatedModal: this.prefixId + 'add-dictionary-non-repeated-modal',
      },
      name: "dictionaryNav",
      show: true,
      unrepeatedDictionaries: [],
      nonUnrepeatedDictionaries: [],
      nonUnrepeatedShortLDTs: [],
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
      const unrepeatedDictionaries = this.getUnrepeatedDictionaries()
      this.sortArrayByStringProperty(unrepeatedDictionaries, "name")
      this.unrepeatedDictionaries = unrepeatedDictionaries
      const nonUnrepeatedDictionaries = this.getNonUnrepeatedDictionaries()
      this.sortArrayByStringProperty(nonUnrepeatedDictionaries, "name")
      this.nonUnrepeatedDictionaries = nonUnrepeatedDictionaries
      this.nonUnrepeatedShortLDTs = this.getNonUnrepeatedShortLDTs()
      this.show = true
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
    getNonUnrepeatedShortLDTs() {
      return [...new Set(this.getNonUnrepeatedDictionariesPropertyValues("creationLDT").map(ldt => this.getShortLDT(ldt)))]
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
      return this.nonUnrepeatedDictionaries.filter(d => this.getShortLDT(d.creationLDT) === shortLDT)
    },
    getUnrepeatedDictionariesElemId() {
      return this.prefixId + "unrepeatedDictionaries"
    },
    getNonUnrepeatedDictionariesElemId() {
      return this.prefixId + "nonUnrepeatedDictionaries"
    },
    getNonUnrepeatedDictionariesCreationShortLDTElemId(i) {
      return this.prefixId + "nonUnrepeatedDictionariesCreationLDT" + i
    },
    parentLoadDictionary(d) {
      this.updateActiveDictionaryElemId(d.id)
      this.routerEditorDictionaries(d.id)
    },
    routerEditorDictionaries(id) {
      let query = {left: this.$route.query.left, right: this.$route.query.right}
      query[this.instance.instanceMark] = id
      this.$router.replace({
        query
      }).then(() => {
      }).catch(err => {
      })
    },

    deleteDictionariesByUnrepeated(unrepeated) {
      this.$store.dispatch('deleteDictionariesByUnrepeatedAndCascadeCardsAction', {unrepeated: unrepeated})
    },
    deleteDictionaryById(id) {
      this.$store.dispatch('deleteDictionaryByIdAction', {id: id})
    },
    openUnrepeatedDictionaryContextMenu(i) {
      this.$refs.unrepeatedDictionaryContextMenu[i].open()
    },
    openNonUnrepeatedDictionaryContextMenu(i) {
      this.$refs.nonUnrepeatedDictionaryContextMenu[i].open()
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
    goToDictionary() {
      if (this.instance) {
        if (this.instance.dictionary) {
          if (this.instance.dictionary.unrepeated) {
            $('#' + 'button' + this.getUnrepeatedDictionariesElemId()).click()
            this.updateActiveDictionaryElemId(this.instance.dictionary.id)
          } else {
            $('#' + 'button' + this.getNonUnrepeatedDictionariesElemId()).click()
            // console.info(this.getShortLDT(this.instance.dictionary.creationLDT))
            // $('#' + this.getNonUnrepeatedDictionariesCreationShortLDTElemId(this.getShortLDT(this.instance.dictionary["creationLDT"]))).collapse('show')
            this.updateActiveDictionaryElemId(this.instance.dictionary.id)
          }
        }
      }
    },
    downloadDictionariesXmlFilesByUnrepeated(unrepeated) {
      const ids = this.getDictionaryIdsByUnrepeated(unrepeated)
      this.$store.dispatch('downloadDictionaryXmlFilesByIdsAction', {ids: ids})
    }
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
<template>
  <div
      v-if="show"
      :id="ids.id"
      class="dictionary-nav- btn-group-vertical btn-group-sm d-inline-block"
      style="width: 100%"
  >
    <b-dropdown
        :id="'button'+ids.vocabularyDropdown"
        :aria-controls="ids.vocabularyDropdown"
        :href="'#'+ids.vocabularyDropdown"
        aria-expanded="false"
        block
        data-toggle="collapse"
        menu-class="w-100"
        role="button"
        size="sm"
        split
        split-class="text-left shadow-none rounded-0 border-1 border-secondary p-0"
        split-variant="light"
        toggle-class="shadow-none rounded-0 border-1 border-secondary"
        variant="light"
        class="sticky-top"
        style="z-index: 100;"
    >
      <template slot="button-content"
      >
        <vocabulary-multiselect
            :id="ids.vocabularyMultiselect"
            :ref="ids.vocabularyMultiselect"
            :side="instance.instanceMark"
        ></vocabulary-multiselect>
        <vocabulary-modal
            :id="ids.vocabularyModal"
            :ref="ids.vocabularyModal"
            :closable="true"
        >
        </vocabulary-modal>
      </template>
      <b-dropdown-item size="sm"
                       @click.prevent.stop="$refs[ids.vocabularyModal].showModal()"
      >
        <b-row no-gutters>
          <b-col class="col-2 d-flex align-items-center justify-content-left text-left"><i
              class="fa fa-plus fa-1x text-success"></i></b-col>
          <b-col class="col-10"><small>{{ getCapitalizeLang('addVocabulary') }}</small></b-col>
        </b-row>
      </b-dropdown-item>

      <b-dropdown-divider></b-dropdown-divider>
      <b-dropdown-group
          :header="getCapitalizeLang('dangerZone')"
          header-classes="text-danger"
      >
        <b-dropdown-item size="sm"
                         @click.prevent.stop="$refs[ids.deleteVocabularyDangerModal].showModal()">
          <b-row no-gutters>
            <b-col class="col-2 d-flex align-items-center justify-content-left text-left"><i
                class="fa fa-trash fa-1x text-danger"></i></b-col>
            <b-col class="col-10"><small>{{ getCapitalizeLang('delete') }}</small></b-col>
          </b-row>
        </b-dropdown-item>
      </b-dropdown-group>
    </b-dropdown>

    <b-dropdown
        :id="'button'+ids.unrepeatedDictionaries"
        :aria-controls="ids.unrepeatedDictionaries"
        :href="'#'+ids.unrepeatedDictionaries"
        :right="instance.instanceMark === 'right'"
        aria-expanded="false"
        block
        data-toggle="collapse"
        menu-class="w-100"
        role="button"
        size="sm"
        split
        split-class="text-left shadow-none rounded-0 border-1 border-dark"
        split-variant="secondary"
        toggle-class="shadow-none rounded-0 border-1 border-dark"
        variant="secondary"
    >
      <template slot="button-content">
        <span class="st-text-shift">{{ getLang('unique') }}</span>
        <span class="st-right badge badge-light bg-white badge-pill">{{ getUnrepeatedDictionaries().length }}</span>
      </template>
      <b-dropdown-item size="sm"
                       @click.prevent.stop="$refs[ids.addDictionaryUnrepeatedModal].showModal()"
      >
        <b-row no-gutters>
          <b-col class="col-2 d-flex align-items-center justify-content-left text-left">
            <i class="fa fa-plus fa-1x text-success"></i>
          </b-col>
          <b-col class="col-10"><small>{{ getCapitalizeLang('addDictionary') }}</small></b-col>
        </b-row>
      </b-dropdown-item>
      <b-dropdown-item size="sm" @click.prevent.stop="downloadDictionariesXmlFilesByUnrepeated(true)">
        <b-row no-gutters>
          <b-col class="col-2 d-flex align-items-center justify-content-left text-left">
            <img alt="..." height="24"
                 src="/static/picture/icon/xml-extension.png"
                 width="24">
          </b-col>
          <b-col class="col-10"><small>{{ getCapitalizeLang('download') }}</small></b-col>
        </b-row>
      </b-dropdown-item>

      <b-dropdown-divider></b-dropdown-divider>
      <b-dropdown-group
          :header="getCapitalizeLang('dangerZone')"
          header-classes="text-danger"
      >
        <b-dropdown-item size="sm" @click.prevent.stop="$refs[ids.deleteDictionariesUniqueDangerModal].showModal()">
          <b-row no-gutters>
            <b-col class="col-2 d-flex align-items-center justify-content-left text-left"><i
                class="fa fa-trash fa-1x text-danger"></i></b-col>
            <b-col class="col-10"><small>{{ getCapitalizeLang('delete') }}</small></b-col>
          </b-row>
        </b-dropdown-item>
      </b-dropdown-group>
    </b-dropdown>


    <div :id="ids.unrepeatedDictionaries" class="collapse">
      <b-dropdown
          v-for="(d,i) in unrepeatedDictionaries"
          :id="getDictionaryElemId(d.id)"
          :key="d.id"
          :ref="getDictionaryElemId(d.id)"
          :right="instance.instanceMark === 'right'"
          aria-expanded="false"
          block
          data-toggle="collapse"
          draggable="true"
          menu-class="w-100"
          role="button"
          size="sm"
          split
          split-class="text-left shadow-none rounded-0 border-1 border-secondary"
          split-variant="light"
          toggle-class="shadow-none rounded-0 border-1 border-secondary"
          variant="light"
          @hide="hideDropdown($event, {ref:getDictionaryElemId(d.id), level: 0})"
          @show="showDropdown($event, {ref:getDictionaryElemId(d.id), level: 0})"
          @toggle="toggleDropdownRef({ref:getDictionaryElemId(d.id), level: 0})"
          @click.prevent.stop="parentLoadDictionary(d)"
      >
        <template slot="button-content">
          <div
              @mousedown.prevent.stop="mousedown(d.id)"
              @mouseup.prevent.stop="mouseup(d.id)"
          >
            <span class="st-text-shift">{{ d.name }}</span>
            <span class="st-right badge badge-light bg-white border badge-pill">
            {{ getCountCardsInDictionaryById(d.id) }}
          </span>
          </div>
        </template>

        <b-dropdown
            :id="getDictionaryElemId(d.id)+'-download-id'"
            :ref="getDictionaryElemId(d.id)+'-download-id'"
            :dropleft="instance.instanceMark === 'right'"
            :dropright="instance.instanceMark === 'left'"
            :right="instance.instanceMark === 'right'"
            block
            no-caret
            size="sm"
            variant="light"
            @hide="hideDropdown($event, {ref: getDictionaryElemId(d.id)+'-download-id', level: 1})"
            @show="showDropdown($event, {ref: getDictionaryElemId(d.id)+'-download-id', level: 1})"
        >
          <template #button-content>
            <b-row class="px-3"
                   no-gutters
                   @click="clickDropdownRef({ref: getDictionaryElemId(d.id)+'-download-id', level: 1})"
            >
              <b-col class="col-2 d-flex align-items-center justify-content-left text-left"><i
                  class="fa-solid fa-download"></i></b-col>
              <b-col class="col-10 d-flex align-items-center justify-content-between">
                <span>{{ getCapitalizeLang('download') }}</span>
                <span><i :class="'fa-solid fa-caret-right fa-xs'"></i></span>
              </b-col>
            </b-row>
          </template>

          <b-dropdown-item
              @click.prevent.stop="downloadXmlFile(d)"
          >
            <b-row no-gutters>
              <b-col class="col-3 d-flex align-items-center justify-content-left text-left">
                <img alt="..." height="24"
                     src="/static/picture/icon/xml-extension.png"
                     width="24">
              </b-col>
              <b-col class="col-9"><small>{{ getCapitalizeLang('xml') }}</small></b-col>
            </b-row>
          </b-dropdown-item>
          <b-dropdown-item
              @click.prevent.stop="downloadExcelFile(d)"
          >
            <b-row no-gutters>
              <b-col class="col-3 d-flex align-items-center justify-content-left text-left">
                <img alt="..." height="24"
                     src="/static/picture/icon/excel.png"
                     width="24">
              </b-col>
              <b-col class="col-9"><small>{{ getCapitalizeLang('excel') }}</small></b-col>
            </b-row>
          </b-dropdown-item>
        </b-dropdown>

        <b-dropdown
            :id="getDictionaryElemId(d.id)+'-upload-id'"
            :ref="getDictionaryElemId(d.id)+'-upload-id'"
            :dropleft="instance.instanceMark === 'right'"
            :dropright="instance.instanceMark === 'left'"
            :right="instance.instanceMark === 'right'"
            block
            no-caret
            size="sm"
            variant="light"
            @hide="hideDropdown($event, {ref: getDictionaryElemId(d.id)+'-upload-id', level: 1})"
            @show="showDropdown($event, {ref: getDictionaryElemId(d.id)+'-upload-id', level: 1})"
        >
          <template #button-content>
            <b-row class="px-3"
                   no-gutters
                   @click="clickDropdownRef({ref: getDictionaryElemId(d.id)+'-upload-id', level: 1})"
            >
              <b-col class="col-2 d-flex align-items-center justify-content-left text-left"><i
                  class="fa-solid fa-upload"></i></b-col>
              <b-col class="col-10 d-flex align-items-center justify-content-between">
                <span>{{ getCapitalizeLang('upload') }}</span>
                <span><i :class="'fa-solid fa-caret-right fa-xs'"></i></span>
              </b-col>
            </b-row>
          </template>

          <b-dropdown-item
              @click.prevent.stop="uploadXmlFile(d)"
          >
            <b-row no-gutters>
              <b-col class="col-3 d-flex align-items-center justify-content-left text-left"><img alt="..." height="24"
                                                                                                 src="/static/picture/icon/xml-extension.png"
                                                                                                 width="24"></b-col>
              <b-col class="col-9"><small>{{ getCapitalizeLang('xml') }}</small></b-col>
            </b-row>
          </b-dropdown-item>
          <b-dropdown-item
              @click.prevent.stop="uploadExcelFile(d)"
          >
            <b-row no-gutters>
              <b-col class="col-3 d-flex align-items-center justify-content-left text-left"><img alt="..." height="24"
                                                                                                 src="/static/picture/icon/excel.png"
                                                                                                 width="24"></b-col>
              <b-col class="col-9"><small>{{ getCapitalizeLang('excel') }}</small></b-col>
            </b-row>
          </b-dropdown-item>
        </b-dropdown>

        <b-dropdown-divider></b-dropdown-divider>
        <b-dropdown-group
            :header="getCapitalizeLang('dangerZone')"
            header-classes="text-danger"
        >
          <b-dropdown-item
              size="sm"
              @click.prevent.stop="deleteDictionaryById(getDictionaryElemId(d.id), d.id)"
          >
            <b-row no-gutters>
              <b-col class="col-2 d-flex align-items-center justify-content-left text-left"><i
                  class="fa fa-trash fa-1x text-danger"></i></b-col>
              <b-col class="col-10"><small>{{ getCapitalizeLang('delete') }}</small></b-col>
            </b-row>
          </b-dropdown-item>
        </b-dropdown-group>
      </b-dropdown>
    </div>

    <b-dropdown
        :id="'button'+ids.nonUnrepeatedDictionaries"
        :ref="'button'+ids.nonUnrepeatedDictionaries"
        :aria-controls="ids.nonUnrepeatedDictionaries"
        :href="'#'+ids.nonUnrepeatedDictionaries"
        :right="instance.instanceMark === 'right'"
        aria-expanded="false"
        block
        data-toggle="collapse"
        menu-class="w-100"
        role="button"
        size="sm"
        split
        split-class="text-left shadow-none rounded-0 border-1 border-dark"
        split-variant="secondary"
        toggle-class="shadow-none rounded-0 border-1 border-dark"
        variant="secondary"
    >
      <template slot="button-content">
        <span class="st-text-shift">{{ getLang('notUnique') }}</span>
        <span class="st-right badge badge-light badge-pill">{{ nonUnrepeatedDictionaries.length }}</span>
      </template>
      <b-dropdown-item
          size="sm"
          @click.prevent.stop="$refs[ids.addDictionaryNonUnrepeatedModal].showModal()"
      >
        <b-row no-gutters>
          <b-col class="col-2 d-flex align-items-center justify-content-left text-left"><i
              class="fa fa-plus fa-1x text-success"></i></b-col>
          <b-col class="col-10"><small>{{ getCapitalizeLang('addDictionary') }}</small></b-col>
        </b-row>
      </b-dropdown-item>
      <b-dropdown-item size="sm"
                       @click.prevent.stop="downloadDictionariesXmlFilesByUnrepeated(false)">
        <b-row no-gutters>
          <b-col class="col-2 d-flex align-items-center justify-content-left text-left"><img alt="..." height="24"
                                                                                             src="/static/picture/icon/xml-extension.png"
                                                                                             width="24"></b-col>
          <b-col class="col-10"><small>{{ getCapitalizeLang('download') }}</small></b-col>
        </b-row>
      </b-dropdown-item>

      <b-dropdown-divider></b-dropdown-divider>
      <b-dropdown-group
          :header="getCapitalizeLang('dangerZone')"
          header-classes="text-danger"
      >
        <b-dropdown-item size="sm"
                         @click.prevent.stop="$refs[ids.deleteDictionariesNotUniqueDangerModal].showModal()"
        >
          <b-row no-gutters>
            <b-col class="col-2 d-flex align-items-center justify-content-left text-left"><i
                class="fa fa-trash fa-1x text-danger"></i></b-col>
            <b-col class="col-10"><small>{{ getCapitalizeLang('delete') }}</small></b-col>
          </b-row>
        </b-dropdown-item>
      </b-dropdown-group>
    </b-dropdown>


    <div :id="ids.nonUnrepeatedDictionaries" class="collapse">
      <b-dropdown
          v-for="(d,ii) in nonUnrepeatedDictionaries"
          :id="getDictionaryElemId(d.id)"
          :key="d.id"
          :ref="getDictionaryElemId(d.id)"
          :right="instance.instanceMark === 'right'"
          aria-expanded="false"
          block
          data-toggle="collapse"
          draggable="true"
          menu-class="w-100"
          role="button"
          size="sm"
          split
          split-class="text-left shadow-none rounded-0 border-1 border-secondary"
          split-variant="light"
          toggle-class="shadow-none rounded-0 border-1 border-secondary"
          variant="light"
          @hide="hideDropdown($event, {ref: getDictionaryElemId(d.id), level: 0})"
          @show="showDropdown($event, {ref: getDictionaryElemId(d.id), level: 0})"
          @toggle="toggleDropdownRef({ref: getDictionaryElemId(d.id), level: 0})"
          @click.prevent.stop="parentLoadDictionary(d)"
      >
        <template slot="button-content">
          <div
              @mousedown.prevent.stop="mousedown(d.id)"
              @mouseup.prevent.stop="mouseup(d.id)"
          >
            <span class="st-text-shift">{{ d.name }}</span>
            <span class="st-right badge badge-light bg-white border badge-pill">
            {{ getCountCardsInDictionaryById(d.id) }}
          </span>
          </div>
        </template>

        <b-dropdown
            :id="getDictionaryElemId(d.id)+'-download-id'"
            :ref="getDictionaryElemId(d.id)+'-download-id'"
            :dropleft="instance.instanceMark === 'right'"
            :dropright="instance.instanceMark === 'left'"
            :right="instance.instanceMark === 'right'"
            block
            no-caret
            size="sm"
            variant="light"
            @hide="hideDropdown($event, {ref: getDictionaryElemId(d.id)+'-download-id', level: 1})"
            @show="showDropdown($event, {ref: getDictionaryElemId(d.id)+'-download-id', level: 1})"
        >
          <template #button-content>
            <b-row class="px-3"
                   no-gutters
                   @click="clickDropdownRef({ref: getDictionaryElemId(d.id)+'-download-id', level: 1})"
            >
              <b-col class="col-2 d-flex align-items-center justify-content-left text-left"><i
                  class="fa-solid fa-download"></i></b-col>
              <b-col class="col-10 d-flex align-items-center justify-content-between">
                <span>{{ getCapitalizeLang('download') }}</span>
                <span><i :class="'fa-solid fa-caret-right fa-xs'"></i></span>
              </b-col>
            </b-row>
          </template>

          <b-dropdown-item
              @click.prevent.stop="downloadXmlFile(d)"
          >
            <b-row no-gutters>
              <b-col class="col-3 d-flex align-items-center justify-content-left text-left">
                <img alt="..." height="24"
                     src="/static/picture/icon/xml-extension.png"
                     width="24">
              </b-col>
              <b-col class="col-9"><small>{{ getCapitalizeLang('xml') }}</small></b-col>
            </b-row>
          </b-dropdown-item>
          <b-dropdown-item
              @click.prevent.stop="downloadExcelFile(d)"
          >
            <b-row no-gutters>
              <b-col class="col-3 d-flex align-items-center justify-content-left text-left">
                <img alt="..." height="24"
                     src="/static/picture/icon/excel.png"
                     width="24">
              </b-col>
              <b-col class="col-9"><small>{{ getCapitalizeLang('excel') }}</small></b-col>
            </b-row>
          </b-dropdown-item>
        </b-dropdown>

        <b-dropdown
            :id="getDictionaryElemId(d.id)+'-upload-id'"
            :ref="getDictionaryElemId(d.id)+'-upload-id'"
            :dropleft="instance.instanceMark === 'right'"
            :dropright="instance.instanceMark === 'left'"
            :right="instance.instanceMark === 'right'"
            block
            no-caret
            size="sm"
            variant="light"
            @hide="hideDropdown($event, {ref: getDictionaryElemId(d.id)+'-upload-id', level: 1})"
            @show="showDropdown($event, {ref: getDictionaryElemId(d.id)+'-upload-id', level: 1})"
        >
          <template #button-content>
            <b-row class="px-3"
                   no-gutters
                   @click="clickDropdownRef({ref: getDictionaryElemId(d.id)+'-upload-id', level: 1})"
            >
              <b-col class="col-2 d-flex align-items-center justify-content-left text-left"><i
                  class="fa-solid fa-upload"></i></b-col>
              <b-col class="col-10 d-flex align-items-center justify-content-between">
                <span>{{ getCapitalizeLang('upload') }}</span>
                <span><i :class="'fa-solid fa-caret-right fa-xs'"></i></span>
              </b-col>
            </b-row>
          </template>

          <b-dropdown-item
              @click.prevent.stop="uploadXmlFile(d)"
          >
            <b-row no-gutters>
              <b-col class="col-3 d-flex align-items-center justify-content-left text-left"><img alt="..." height="24"
                                                                                                 src="/static/picture/icon/xml-extension.png"
                                                                                                 width="24"></b-col>
              <b-col class="col-9"><small>{{ getCapitalizeLang('xml') }}</small></b-col>
            </b-row>
          </b-dropdown-item>
          <b-dropdown-item
              @click.prevent.stop="uploadExcelFile(d)"
          >
            <b-row no-gutters>
              <b-col class="col-3 d-flex align-items-center justify-content-left text-left"><img alt="..." height="24"
                                                                                                 src="/static/picture/icon/excel.png"
                                                                                                 width="24"></b-col>
              <b-col class="col-9"><small>{{ getCapitalizeLang('excel') }}</small></b-col>
            </b-row>
          </b-dropdown-item>
        </b-dropdown>


        <b-dropdown-divider></b-dropdown-divider>
        <b-dropdown-group
            :header="getCapitalizeLang('dangerZone')"
            header-classes="text-danger"
        >
          <b-dropdown-item
              size="sm"
              @click.prevent.stop="deleteDictionaryById(getDictionaryElemId(d.id), d.id)"
          >
            <b-row no-gutters>
              <b-col class="col-2 d-flex align-items-center justify-content-left text-left"><i
                  class="fa fa-trash fa-1x text-danger"></i></b-col>
              <b-col class="col-10"><small>{{ getCapitalizeLang('delete') }}</small></b-col>
            </b-row>
          </b-dropdown-item>
        </b-dropdown-group>
      </b-dropdown>
    </div>

    <add-dictionary-modal
        :id="ids.addDictionaryUnrepeatedModal"
        :ref="ids.addDictionaryUnrepeatedModal"
        :closable="true"
        :parent="0"
        :unrepeated="true"
    ></add-dictionary-modal>
    <add-dictionary-modal
        :id="ids.addDictionaryNonUnrepeatedModal"
        :ref="ids.addDictionaryNonUnrepeatedModal"
        :closable="true"
        :parent="0"
        :unrepeated="false"
    ></add-dictionary-modal>
    <delete-vocabulary-danger-modal
        :id="ids.deleteVocabularyDangerModal"
        :ref="ids.deleteVocabularyDangerModal"
        :closable="true"
        :vocabulary="vocabulary.vocabulary"
    ></delete-vocabulary-danger-modal>
    <delete-dictionaries-danger-modal
        :id="ids.deleteDictionariesUniqueDangerModal"
        :ref="ids.deleteDictionariesUniqueDangerModal"
        :closable="true"
        :unique="true"
    ></delete-dictionaries-danger-modal>
    <delete-dictionaries-danger-modal
        :id="ids.deleteDictionariesNotUniqueDangerModal"
        :ref="ids.deleteDictionariesNotUniqueDangerModal"
        :closable="true"
        :unique="false"
    ></delete-dictionaries-danger-modal>
    <confirm-action-with-timer-modal
        :id="ids.confirmDeleteDictionaryModal"
        :ref="ids.confirmDeleteDictionaryModal"
        :closable="true"
        :is-for-no="true"
        :message="getCapitalizeLang('confirmDeleteDictionary')"
        :seconds="confirmAction.seconds"
    ></confirm-action-with-timer-modal>

    <GlobalEvents @mouseup="mouseupOutside()"/>
  </div>
</template>

<script>
import {mapState, mapGetters} from "vuex"
import * as _ from "lodash"
import date from "../../../util/date"
import AddDictionaryModal from "./AddDictionaryModal.vue"
import VocabularyMultiselect from "../VocabularyMultiselect.vue"
import VocabularyModal from "../VocabularyModal.vue"
import DeleteVocabularyDangerModal from "../../modal/DeleteVocabularyDangerModal.vue"
import DeleteDictionariesDangerModal from "../../modal/DeleteDictionariesDangerModal.vue"
import ConfirmActionWithTimerModal from "../../modal/ConfirmActionWithTimerModal.vue"

export default {
  components: {
    AddDictionaryModal,
    VocabularyMultiselect,
    VocabularyModal,
    DeleteVocabularyDangerModal,
    DeleteDictionariesDangerModal,
    ConfirmActionWithTimerModal,
  },
  props: [
    'instance',
  ],
  mounted() {
    this.dropdownClickOutsideListener()
  },
  created() {
    this.setConfirmActionToDefault()
    this.$root.$on('confirm-action-with-timer-modal', (payload) => {
      this.confirmAction.confirmId = payload.id
    })
    this.$root.$on('reject-action-with-timer-modal', (payload) => {
      this.confirmAction.rejectId = payload.id
    })
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
        this.fetchData().then(() => this.goToDictionary())
      })
    })
  },
  computed: {
    ...mapState([
      'dictionaries',
      'lang',
      'vocabulary',
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
    ids() {
      return {
        id: this.prefixId(),
        addDictionaryUnrepeatedModal: this.prefixId() + 'add-dictionary-unrepeated-modal-id',
        addDictionaryNonUnrepeatedModal: this.prefixId() + 'add-dictionary-non-repeated-modal-id',
        vocabularyModal: this.prefixId() + 'vocabulary-modal-id',
        unrepeatedDictionaries: this.prefixId() + "unrepeated-dictionaries-id",
        nonUnrepeatedDictionaries: this.prefixId() + "non-unrepeated-dictionaries-id",
        nonUnrepeatedDictionariesDownload: this.prefixId() + "non-unrepeated-dictionaries-download-id",
        vocabularyMultiselect: this.prefixId() + "vocabulary-multiselect-id",
        vocabularyDropdown: this.prefixId() + "vocabulary-dropdown-id",
        deleteVocabularyDangerModal: this.prefixId() + "delete-vocabulary-danger-modal-id",
        deleteDictionariesUniqueDangerModal: this.prefixId() + "delete-dictionaries-unique-danger-modal-id",
        deleteDictionariesNotUniqueDangerModal: this.prefixId() + "delete-dictionaries-not-unique-danger-modal-id",
        confirmDeleteDictionaryModal: this.prefixId() + 'confirm-delete-dictionary-modal-id',
      }
    },
    defaultConfirmAction() {
      return {
        seconds: 10,
        confirmId: '',
        rejectId: '',
      }
    }
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
  data() {
    return {
      name: "DictionaryNav",
      show: true,
      dropdownRefs: [],
      dropdownRef: null,
      confirmAction: {},

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
    async fetchData() {
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
    prefixId() {
      return this.name + "-" + this.instance.instanceMark + "-"
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
      return this.prefixId() + "dictionary" + id
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
      if (this.nonUnrepeatedDictionaries) {
        return this.nonUnrepeatedDictionaries.filter(d => this.getShortLDT(d.creationLDT) === shortLDT)
      } else return []
    },
    getNonUnrepeatedDictionariesCreationShortLDTElemId(i) {
      return this.prefixId() + "non-unrepeated-dictionaries-creationLDT" + i
    },
    parentLoadDictionary(d) {
      this.updateActiveDictionaryElemId(d.id)
      this.routerEditorDictionaries(d.id)
    },
    routerEditorDictionaries(id) {
      let query = {
        tab: 'editor',
        left: this.$route.query.left,
        right: this.$route.query.right
      }
      query[this.instance.instanceMark] = id
      this.$router.replace({
        query
      }).then(() => {
      }).catch(err => {
      })
    },

    deleteDictionaryById(ref, id) {
      this.hideDropdownsOnClick()
      this.$refs[this.ids.confirmDeleteDictionaryModal].showModal()
      let f = setInterval(
          () => {
            if (this.confirmAction.confirmId === this.ids.confirmDeleteDictionaryModal) {
              this.$store.dispatch(
                  'deleteDictionaryByIdAction',
                  {id: id}
              )
              clearInterval(f)
              this.setConfirmActionToDefault()
            }
            if (this.confirmAction.rejectId === this.ids.confirmDeleteDictionaryModal) {
              clearInterval(f)
              this.setConfirmActionToDefault()
            }
          },
          100,
      )
      setTimeout(
          () => {
            clearInterval(f)
            this.setConfirmActionToDefault()
          },
          this.confirmAction.seconds * 1000
      )
    },
    setConfirmActionToDefault() {
      Object.assign(this.confirmAction, this.defaultConfirmAction)
    },
    downloadExcelFile(d) {
      this.hideDropdownsOnClick()
      this.$store.dispatch("downloadExcelFileAction",
          {vocabulary: this.vocabulary.vocabulary, dictionary: d}
      )
    },
    downloadXmlFile(d) {
      this.hideDropdownsOnClick()
      this.$store.dispatch("downloadXmlFileAction",
          {vocabulary: this.vocabulary.vocabulary, dictionary: d}
      )
    },
    uploadExcelFile(d) {
      this.hideDropdownsOnClick()
      //TODO
    },
    uploadXmlFile(d) {
      this.hideDropdownsOnClick()
      //TODO
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
            $('#' + 'button' + this.ids.unrepeatedDictionaries).click()
            this.updateActiveDictionaryElemId(this.instance.dictionary.id)
          } else {
            $('#' + 'button' + this.ids.nonUnrepeatedDictionaries).click()
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
    },
    clickDropdownRef(ref) {
      this.dropdownRef = ref
    },
    toggleDropdownRef(ref) {
      this.dropdownRefs = []
      this.dropdownRef = ref
    },
    hideDropdown(event, ref) {
      if (ref.level >= this.dropdownRef.level) {
        this.removeDropdownRef(ref)
        return
      }
      if (this.isBeforeInclDropdownRef(ref)) {
        event.preventDefault()
      } else {
        this.removeDropdownRef(ref)
      }
    },
    isBeforeInclDropdownRef(ref) {
      const iDropdown = this.dropdownRefs.findIndex(r => _.isEqual(r, this.dropdownRef))
      const iRef = this.dropdownRefs.findIndex(r => _.isEqual(r, ref))
      if (iDropdown < 0 || iRef < 0) {
        return false
      } else {
        return iRef < iDropdown
      }
    },
    removeDropdownRef(ref) {
      const inx = this.dropdownRefs.findIndex(r => _.isEqual(r, ref))
      if (inx >= 0) {
        this.dropdownRefs = [...this.dropdownRefs.slice(0, inx), ...this.dropdownRefs.slice(inx + 1)]
      }
    },
    showDropdown(event, ref) {
      this.dropdownRefs.push(ref)
      if (!_.isEqual(ref, this.dropdownRef)) {
        event.preventDefault()
      }
    },
    hideDropdownsOnClick() {
      document.getElementById(this.ids.id).click()
    },
    hideDropdowns() {
      for (let i = this.dropdownRefs.length - 1; i >= 0; i--) {
        try {
          this.$refs[this.dropdownRefs[i].ref].hide()
        } catch (e) {
        }
      }
      this.dropdownRefs = []
    },
    dropdownClickOutsideListener() {
      const outsideClickListener = event => {
        if (this.dropdownRefs.length === 0) return
        const count = this.dropdownRefs.filter(r => {
          const element = document.getElementById(r.ref)
          return element === event.target || element.contains(event.target)
        }).length
        if (count === 0) {
          this.hideDropdowns()
        }
      }
      document.addEventListener('click', outsideClickListener);
    },
  },
}
</script>

<style scoped>

.dictionary-nav- {
  height: 550px;
  overflow-y: auto;
  /*overflow: visible !important;*/
}

.st-text-shift {
  overflow-wrap: anywhere;
}

.st-right {
  float: right
}

.st-left {
  float: right
}

.st-ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis
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
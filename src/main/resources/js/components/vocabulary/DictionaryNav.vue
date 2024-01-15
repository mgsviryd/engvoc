<template>
  <div
      v-if="show"
      :id="ids.id"
      :style="{height: style.height.col+'px'}"
      class="dictionary-nav- parent-for-height-flex"
  >

    <b-container
        class="p-0 m-0 flex-shrink-0"
        fluid
    >
      <b-dropdown
          :id="ids.userDropdown"
          :ref="ids.userDropdown"
          :right="instance.instanceMark === 'right'"
          :split-variant="activeParent?'primary':'muted'"
          :variant="activeParent?'primary':'muted'"
          aria-expanded="false"
          block
          data-toggle="collapse"
          disabled
          menu-class="w-100"
          role="button"
          size="sm"
          split
          split-class="text-left shadow-none rounded-0 border-1 border-dark"
          toggle-class="shadow-none rounded-0 border-1 border-dark"
      >
        <template slot="button-content">
          <span class="st-text-shift">{{ authentication.user ? authentication.user.username : '' }}</span>
        </template>

      </b-dropdown>
    </b-container>


    <b-dropdown
        :id="ids.dropdownVocabularies"
        :aria-controls="ids.vocabularies"
        :href="'#'+ids.vocabularies"
        :split-variant="'light'"
        :variant="'light'"
        aria-expanded="false"
        block
        class="sticky-top flex-shrink-0"
        data-toggle="collapse"
        menu-class="w-100"
        role="button"
        size="sm"
        split
        split-class="p-0 text-left rounded-0 shadow-none border-1 border-secondary"
        style="z-index: 1;"
        toggle-class="rounded-0 shadow-none border-1 border-secondary"
    >
      <template slot="button-content"
      >
        <vocabulary-multiselect-store
            :id="ids.vocabularyMultiselect"
            :ref="ids.vocabularyMultiselect"
            :side="instance.instanceMark"
        >
        </vocabulary-multiselect-store>
        <vocabulary-modal
            :id="ids.vocabularyModal"
            :ref="ids.vocabularyModal"
            :closable="true"
        >
        </vocabulary-modal>
      </template>
      <b-dropdown-item
          size="sm"
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
        <b-dropdown-item
            size="sm"
            @click.prevent.stop="$refs[ids.deleteVocabularyDangerModal].showModal()"
        >
          <b-row no-gutters>
            <b-col class="col-2 d-flex align-items-center justify-content-left text-left"><i
                class="fa fa-trash fa-1x text-danger"></i></b-col>
            <b-col class="col-10"><small>{{ getCapitalizeLang('delete') }}</small></b-col>
          </b-row>
        </b-dropdown-item>
      </b-dropdown-group>
    </b-dropdown>

    <search-dictionary-multiselect
        :id="ids.searchDictionaryMultiselect"
        :data="{watchId: watchIds.searchDictionaryMultiselect, value: dictionary, options: dictionaries}"
        @onSelect="onSelectDictionary"
    >
    </search-dictionary-multiselect>

    <b-container class="p-0 m-0 bg-secondary border border-dark flex-shrink-0" fluid>
      <b-button-toolbar class="bg-secondary mx-1">
        <b-button-group class="" size="sm">
          <b-button
              class="px-1 py-1 shadow-none border border-dark"
              size="sm"
              variant="secondary"
              @click.prevent.stop="toggleAllDictionaries"
          >
            <i v-if="isAllDictionariesCollapsed" class="fa-solid fa-angle-down text-white"></i>
            <i v-else class="fa-solid fa-angle-up text-white"></i>
          </b-button>
        </b-button-group>
        <b-button-group class="" size="sm">
          <b-button
              class="shadow-none border border-dark px-1 py-1"
              size="sm"
              variant="secondary"
              @click.prevent.stop="navigateToActiveDictionary"
          >
            <i class="fa-solid fa-location-crosshairs fa-sm fa-fade text-white"
               style="--fa-animation-duration: 2s; --fa-fade-opacity: 0.6;"></i>
          </b-button>
        </b-button-group>
      </b-button-toolbar>
    </b-container>

    <b-container
        :id="ids.field"
        :ref="ids.field"
        class="p-1 m-0 bg-white border border-dark child-for-height-flex"
        fluid
        style="overflow-y: auto;"
    >
      <b-dropdown
          :id="ids.dropdownUnrepeatedDictionaries"
          :ref="ids.dropdownUnrepeatedDictionaries"
          :aria-controls="ids.unrepeatedDictionaries"
          :class="[{'shadow-sm':!isAllDictionariesCollapsed && isDropdownUnrepeatedDictionariesCollapsed}]"
          :href="'#'+ids.unrepeatedDictionaries"
          :right="instance.instanceMark === 'right'"
          aria-expanded="false"
          block
          class="unrepeated-button"
          data-toggle="collapse"
          menu-class="w-100"
          role="button"
          size="sm"
          split
          split-class="text-left shadow-none rounded-0 border-1 border-dark pl-1"
          split-variant="info"
          toggle-class="shadow-none rounded-0 border-1 border-dark"
          variant="info"
      >
        <template slot="button-content">
          <b-button
              class="py-0 px-1 shadow-none rounded-0 border-0 text-white"
              size="sm"
              variant="transparent"
              @click.prevent.stop="toggleDropdownUnrepeatedDictionaries"
          >
            <i v-if="isDropdownUnrepeatedDictionariesCollapsed" class="fa-solid fa-angle-down fa-sm text-white"></i>
            <i v-else class="fa-solid fa-angle-up fa-sm text-white"></i>
          </b-button>
          <span class="st-text-shift">{{ getLang('unique') }}</span>
          <span class="st-float-right badge badge-light bg-white badge-pill">{{
              getUnrepeatedDictionaries().length
            }}</span>
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
                   src="/picture/icon/xml-extension.png"
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


      <div
          :id="ids.unrepeatedDictionaries"
          class="collapse"
      >
        <b-container :class="[{'shadow-sm':!isDropdownUnrepeatedDictionariesCollapsed}, 'p-0 m-0']" fluid>
          <b-dropdown
              v-for="(d,i) in unrepeatedDictionaries"
              :id="getDictionaryElemId(d.id)"
              :key="d.id"
              :ref="getDictionaryElemId(d.id)"
              :right="instance.instanceMark === 'right'"
              aria-expanded="false"
              block
              class="unrepeated"
              data-toggle="collapse"
              draggable="true"
              menu-class="w-100"
              role="button"
              size="sm"
              split
              split-class="text-left shadow-none rounded-0 border-1 border-secondary border-top-0 bg-white text-dark"
              tabindex="1"
              toggle-class="shadow-none rounded-0 border-1 border-secondary border-top-0 bg-white text-dark"
              @click="clickDictionary(d.id)"
              @hide="hideDropdown($event, {ref:getDictionaryElemId(d.id), level: 0})"
              @show="showDropdown($event, {ref:getDictionaryElemId(d.id), level: 0})"
              @toggle="toggleDropdownRef({ref:getDictionaryElemId(d.id), level: 0})"
          >
            <template slot="button-content">
              <div
                  @mousedown.prevent="mousedown(d.id)"
                  @mouseup.prevent="mouseup(d.id)"
              >
                <span class="st-text-shift">{{ d.name }}</span>
                <span class="st-float-right badge badge-light bg-white border badge-pill">
            {{ getCountCardsInDictionaryById(d.id) }}
          </span>
              </div>
            </template>

            <b-dropdown
                :id="getDictionaryElemId(d.id)+'-download-id'"
                :ref="getDictionaryElemId(d.id)+'-download-id'"
                :dropleft="instance.instanceMark === 'left'"
                :dropright="instance.instanceMark === 'right'"
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
                      class="fa-solid fa-download text-success"></i></b-col>
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
                         src="/picture/icon/xml-extension.png"
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
                         src="/picture/icon/excel.png"
                         width="24">
                  </b-col>
                  <b-col class="col-9"><small>{{ getCapitalizeLang('excel') }}</small></b-col>
                </b-row>
              </b-dropdown-item>
            </b-dropdown>

            <b-dropdown
                :id="getDictionaryElemId(d.id)+'-upload-id'"
                :ref="getDictionaryElemId(d.id)+'-upload-id'"
                :dropleft="instance.instanceMark === 'left'"
                :dropright="instance.instanceMark === 'right'"
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
                      class="fa-solid fa-upload text-primary"></i></b-col>
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
                  <b-col class="col-3 d-flex align-items-center justify-content-left text-left"><img alt="..."
                                                                                                     height="24"
                                                                                                     src="/picture/icon/xml-extension.png"
                                                                                                     width="24"></b-col>
                  <b-col class="col-9"><small>{{ getCapitalizeLang('xml') }}</small></b-col>
                </b-row>
              </b-dropdown-item>
              <b-dropdown-item
                  @click.prevent.stop="uploadExcelFile(d)"
              >
                <b-row no-gutters>
                  <b-col class="col-3 d-flex align-items-center justify-content-left text-left"><img alt="..."
                                                                                                     height="24"
                                                                                                     src="/picture/icon/excel.png"
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
                  @click.prevent.stop="confirmDeleteDictionaryById(getDictionaryElemId(d.id), d.id)"
              >
                <b-row no-gutters>
                  <b-col class="col-2 d-flex align-items-center justify-content-left text-left"><i
                      class="fa fa-trash fa-1x text-danger"></i></b-col>
                  <b-col class="col-10"><small>{{ getCapitalizeLang('delete') }}</small></b-col>
                </b-row>
              </b-dropdown-item>
            </b-dropdown-group>
          </b-dropdown>
        </b-container>
      </div>

      <b-dropdown
          :id="ids.dropdownNonUnrepeatedDictionaries"
          :ref="ids.dropdownNonUnrepeatedDictionaries"
          :aria-controls="ids.nonUnrepeatedDictionaries"
          :class="[{'mt-3':!isAllDictionariesCollapsed}, {'shadow-sm':!isAllDictionariesCollapsed && isDropdownNonUnrepeatedDictionariesCollapsed}]"
          :href="'#'+ids.nonUnrepeatedDictionaries"
          :right="instance.instanceMark === 'right'"
          aria-expanded="false"
          block
          class="non-unrepeated-button"
          data-toggle="collapse"
          menu-class="w-100"
          role="button"
          size="sm"
          split
          split-class="text-left shadow-none rounded-0 border-1 border-dark pl-1"
          split-variant="warning"
          toggle-class="shadow-none rounded-0 border-1 border-dark"
          variant="warning"
      >
        <template slot="button-content">
          <b-button
              class="py-0 px-1 shadow-none rounded-0 border-0 text-white"
              size="sm"
              variant="transparent"
              @click.prevent.stop="toggleDropdownNonUnrepeatedDictionaries"
          >
            <i v-if="isDropdownNonUnrepeatedDictionariesCollapsed" class="fa-solid fa-angle-down fa-sm text-white"></i>
            <i v-else class="fa-solid fa-angle-up fa-sm text-white"></i>
          </b-button>
          <span class="st-text-shift">{{ getLang('notUnique') }}</span>
          <span class="st-float-right badge badge-light badge-pill">{{ nonUnrepeatedDictionaries.length }}</span>
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
                                                                                               src="/picture/icon/xml-extension.png"
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

      <div
          :id="ids.nonUnrepeatedDictionaries"
          class="collapse nonUnrepeated"
      >
        <b-container :class="[{'shadow-sm':!isDropdownNonUnrepeatedDictionariesCollapsed}, 'p-0 m-0']" fluid>
          <b-dropdown
              v-for="(d,ii) in nonUnrepeatedDictionaries"
              :id="getDictionaryElemId(d.id)"
              :key="d.id"
              :ref="getDictionaryElemId(d.id)"
              :right="instance.instanceMark === 'right'"
              aria-expanded="false"
              block
              class="non-unrepeated"
              data-toggle="collapse"
              draggable="true"
              menu-class="w-100"
              role="button"
              size="sm"
              split
              split-class="text-left shadow-none rounded-0 border-1 border-secondary border-top-0 bg-white text-dark"
              tabindex="1"
              toggle-class="shadow-none rounded-0 border-1 border-secondary border-top-0 bg-white text-dark"
              @click="clickDictionary(d.id)"
              @hide="hideDropdown($event, {ref: getDictionaryElemId(d.id), level: 0})"
              @show="showDropdown($event, {ref: getDictionaryElemId(d.id), level: 0})"
              @toggle="toggleDropdownRef({ref: getDictionaryElemId(d.id), level: 0})"
          >
            <template slot="button-content">
              <div
                  @mousedown.prevent="mousedown(d.id)"
                  @mouseup.prevent="mouseup(d.id)"
              >
                <span class="st-text-shift">{{ d.name }}</span>
                <span class="st-float-right badge badge-light bg-white border badge-pill">
            {{ getCountCardsInDictionaryById(d.id) }}
          </span>
              </div>
            </template>

            <b-dropdown
                :id="getDictionaryElemId(d.id)+'-download-id'"
                :ref="getDictionaryElemId(d.id)+'-download-id'"
                :dropleft="instance.instanceMark === 'left'"
                :dropright="instance.instanceMark === 'right'"
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
                      class="fa-solid fa-download text-success"></i></b-col>
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
                         src="/picture/icon/xml-extension.png"
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
                         src="/picture/icon/excel.png"
                         width="24">
                  </b-col>
                  <b-col class="col-9"><small>{{ getCapitalizeLang('excel') }}</small></b-col>
                </b-row>
              </b-dropdown-item>
            </b-dropdown>

            <b-dropdown
                :id="getDictionaryElemId(d.id)+'-upload-id'"
                :ref="getDictionaryElemId(d.id)+'-upload-id'"
                :dropleft="instance.instanceMark === 'left'"
                :dropright="instance.instanceMark === 'right'"
                :right="instance.instanceMark === 'right'"
                block
                no-caret
                size="sm"
                variant="light"
                @hide="hideDropdown($event, {ref: getDictionaryElemId(d.id)+'-upload-id', level: 1})"
                @show="showDropdown($event, {ref: getDictionaryElemId(d.id)+'-upload-id', level: 1})"
            >
              >
              <template #button-content>
                <b-row class="px-3"
                       no-gutters
                       @click="clickDropdownRef({ref: getDictionaryElemId(d.id)+'-upload-id', level: 1})"
                >
                  <b-col class="col-2 d-flex align-items-center justify-content-left text-left"><i
                      class="fa-solid fa-upload text-primary"></i></b-col>
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
                  <b-col class="col-3 d-flex align-items-center justify-content-left text-left">
                    <img alt="..."
                         height="24"
                         src="/picture/icon/xml-extension.png"
                         width="24">
                  </b-col>
                  <b-col class="col-9"><small>{{ getCapitalizeLang('xml') }}</small></b-col>
                </b-row>
              </b-dropdown-item>
              <b-dropdown-item
                  @click.prevent.stop="uploadExcelFile(d)"
              >
                <b-row no-gutters>
                  <b-col class="col-3 d-flex align-items-center justify-content-left text-left">
                    <img alt="..."
                         height="24"
                         src="/picture/icon/excel.png"
                         width="24">
                  </b-col>
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
                  @click.prevent.stop="confirmDeleteDictionaryById(getDictionaryElemId(d.id), d.id)"
              >
                <b-row no-gutters>
                  <b-col class="col-2 d-flex align-items-center justify-content-left text-left"><i
                      class="fa fa-trash fa-1x text-danger"></i></b-col>
                  <b-col class="col-10"><small>{{ getCapitalizeLang('delete') }}</small></b-col>
                </b-row>
              </b-dropdown-item>
            </b-dropdown-group>
          </b-dropdown>
        </b-container>
      </div>
    </b-container>

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
        @onConfirm="onConfirmAction"
        @onReject="onRejectAction"
    ></confirm-action-with-timer-modal>
    <GlobalEvents @mouseup="mouseupOutside()"/>
  </div>
</template>

<script>
import {mapState, mapGetters} from "vuex"
import * as _ from "lodash"
import date from "../../util/date"
import AddDictionaryModal from "./AddDictionaryModal.vue"
import VocabularyMultiselectStore from "./VocabularyMultiselectStore.vue"
import VocabularyModal from "./AddVocabularyModal.vue"
import DeleteVocabularyDangerModal from "../modal/DeleteVocabularyDangerModal.vue"
import DeleteDictionariesDangerModal from "../modal/DeleteDictionariesDangerModal.vue"
import ConfirmActionWithTimerModal from "../modal/ConfirmActionWithTimerModal.vue"
import SearchDictionaryMultiselect from "./SearchDictionaryMultiselect.vue"

export default {
  components: {
    AddDictionaryModal,
    VocabularyMultiselectStore,
    VocabularyModal,
    DeleteVocabularyDangerModal,
    DeleteDictionariesDangerModal,
    ConfirmActionWithTimerModal,
    SearchDictionaryMultiselect,
  },
  props: [
    'instance',
    'dictionary',
  ],
  mounted() {
    this.dropdownClickOutsideListener()
    this.addBootstrapListeners()
  },
  created() {
    this.addListeners()
    this.setConfirmActionToDefault()
    this.$store.watch(this.$store.getters.getActionId, actionId => {
      this.fetchData()
    })
    this.$root.$on('dragdrop-init', (payload) => {
      this.dragdropInit(payload)
    })
    this.$root.$on('dragdrop-destroy', () => {
      this.dragdropDestroy()
    })
    this.isAfterCreated = true
  },
  destroyed() {
    this.removeListeners()
  },
  computed: {
    ...mapState([
      'dictionaries',
      'lang',
      'vocabulary',
      'authentication',
      'height',
    ]),
    ...mapGetters([
      'getDictionaryById',
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
        field: this.prefixId() + 'field-id',
        addDictionaryUnrepeatedModal: this.prefixId() + 'add-dictionary-unrepeated-modal-id',
        addDictionaryNonUnrepeatedModal: this.prefixId() + 'add-dictionary-non-repeated-modal-id',
        vocabularyModal: this.prefixId() + 'vocabulary-modal-id',
        dropdownUnrepeatedDictionaries: this.prefixId() + "dropdown-unrepeated-dictionaries-id",
        unrepeatedDictionaries: this.prefixId() + "unrepeated-dictionaries-id",
        dropdownNonUnrepeatedDictionaries: this.prefixId() + "dropdown-non-unrepeated-dictionaries-id",
        nonUnrepeatedDictionaries: this.prefixId() + "non-unrepeated-dictionaries-id",
        nonUnrepeatedDictionariesDownload: this.prefixId() + "non-unrepeated-dictionaries-download-id",
        vocabularyMultiselect: this.prefixId() + "vocabulary-multiselect-id",
        dropdownVocabularies: this.prefixId() + 'dropdown-vocabularies-id',
        vocabularies: this.prefixId() + "vocabularies-id",
        deleteVocabularyDangerModal: this.prefixId() + "delete-vocabulary-danger-modal-id",
        deleteDictionariesUniqueDangerModal: this.prefixId() + "delete-dictionaries-unique-danger-modal-id",
        deleteDictionariesNotUniqueDangerModal: this.prefixId() + "delete-dictionaries-not-unique-danger-modal-id",
        confirmDeleteDictionaryModal: this.prefixId() + 'confirm-delete-dictionary-modal-id',
        userDropdown: this.prefixId() + 'user-dropdown-id',
        searchDictionaryMultiselect: this.prefixId() + 'search-dictionary-multiselect-id',
      }
    },
    watchIds() {
      return {
        searchDictionaryMultiselect: 0,
      }
    },
    defaultConfirmAction() {
      return {
        isConfirm: false,
        isReject: false,
      }
    },
    isAllDictionariesCollapsed() {
      return this.isDropdownUnrepeatedDictionariesCollapsed && this.isDropdownNonUnrepeatedDictionariesCollapsed
    },
  },
  watch: {
    dictionary: {
      handler: async function (newVal, oldVal) {
        await this.fetchData()
        this.setDictionaryActive(newVal, oldVal)
        this.navigateToActiveDictionary()
        this.watchIds.searchDictionaryMultiselect = _.now()
      },
      deep: true,
    },
    dictionaries: {
      handler: async function () {
        await this.fetchData()
        if (this.isAfterCreated) {
          this.isAfterCreated = false
          this.navigateToActiveDictionary()
          this.watchIds.searchDictionaryMultiselect = _.now()
        }
      },
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
  data() {
    return {
      name: "DictionaryNav",
      show: true,
      isDropdownUnrepeatedDictionariesCollapsed: true,
      isDropdownNonUnrepeatedDictionariesCollapsed: true,
      isAfterCreated: false,
      activeParent: false,
      dropdownRefs: [],
      dropdownRef: null,
      confirmAction: {},

      unrepeatedDictionaries: [],
      nonUnrepeatedDictionaries: [],
      nonUnrepeatedShortLDTs: [],

      groups: ["cardsChangeDictionary"],
      sourceMark: "cards",
      isMouseInClick: false,
      groupsInProcess: [],
      dragDictionaryId: null,

      listeners: [],
      activeDictionary: null,
      style: {
        height: {
          col: 0,
        },
      }
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
      this.buildHeight()
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
      return this.prefixId() + "dictionary-" + id
    },
    getShortLDT(ldt) {
      return date.parseISOString(ldt).toLocaleString()
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
    clickDictionary(id) {
      this.$emit('onClickDictionary', {id: id, instanceMark: this.instance.instanceMark})
    },
    deleteDictionaryById(id) {
      this.$store.dispatch('deleteDictionaryByIdAction', {id: id})
    },
    confirmDeleteDictionaryById(ref, id) {
      this.hideDropdownsOnClick()
      const sec = 10
      this.$refs[this.ids.confirmDeleteDictionaryModal].showModal(
          this.getCapitalizeLang('confirmDeleteDictionary'),
          sec
      )
      let f = setInterval(
          () => {
            if (this.confirmAction.isConfirm) {
              this.deleteDictionaryById(id)
              clearInterval(f)
              this.setConfirmActionToDefault()
            }
            if (this.confirmAction.isReject) {
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
          sec * 1000
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
      }, 100)
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
      const random = document.getElementById(this.ids.id)
      if (random) {
        random.click()
      }
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
      return event => {
        if (this.dropdownRefs.length === 0) return
        const count = this.dropdownRefs.filter(r => {
          const element = document.getElementById(r.ref)
          return element === event.target || element.contains(event.target)
        }).length
        if (count === 0) {
          this.hideDropdowns()
        }
      }
    },
    keydownListener() {
      return ({repeat, altKey, which}) => {
        if (this.activeParent) {
          if (repeat) return
          if (altKey && which === 85) {
            this.$refs[this.ids.addDictionaryUnrepeatedModal].showModal()
          }
          if (altKey && which === 78) {
            this.$refs[this.ids.addDictionaryNonUnrepeatedModal].showModal()
          }
        }
      }
    },
    addBootstrapListeners() {
      $("#" + this.ids.unrepeatedDictionaries).on('shown.bs.collapse', () => {
        this.isDropdownUnrepeatedDictionariesCollapsed = false
      })
      $("#" + this.ids.unrepeatedDictionaries).on('hidden.bs.collapse', () => {
        this.isDropdownUnrepeatedDictionariesCollapsed = true
      })

      $("#" + this.ids.nonUnrepeatedDictionaries).on('shown.bs.collapse', () => {
        this.isDropdownNonUnrepeatedDictionariesCollapsed = false
      })
      $("#" + this.ids.nonUnrepeatedDictionaries).on('hidden.bs.collapse', () => {
        this.isDropdownNonUnrepeatedDictionariesCollapsed = true
      })
    },
    addListeners() {
      const dropdownClickOutsideListener = this.dropdownClickOutsideListener()
      const keydownListener = this.keydownListener()
      this.listeners.push({level: document, type: 'click', listener: dropdownClickOutsideListener})
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
    isBlank(value) {
      return _.isNil(value) || _.isEmpty(value)
    },
    navigateToDictionary(d) {
      if (d) {
        if (d.unrepeated) {
          $("#" + this.ids.unrepeatedDictionaries).collapse('show')
        } else {
          $("#" + this.ids.nonUnrepeatedDictionaries).collapse('show')
        }
      }
    },
    navigateToUnique(d) {
      if (d) {
        if (d.unrepeated) {
          $("#" + this.ids.unrepeatedDictionaries).collapse('show')
        } else {
          $("#" + this.ids.nonUnrepeatedDictionaries).collapse('show')
        }
      }
    },
    navigateToActiveDictionary() {
      this.navigateToDictionary(this.dictionary)
      this.scrollToActiveDictionary()
    },
    navigateToActiveUnique() {
      this.navigateToUnique(this.dictionary)
      this.scrollToActiveUnique()
    },
    setDictionaryActive(newD, oldD) {
      let elemId = null
      if (oldD) {
        elemId = this.getDictionaryElemId(oldD.id)
        $("#" + elemId + '__BV_toggle_').removeClass('bg-secondary border-dark text-white')
        $("#" + elemId + '__BV_button_').removeClass('bg-secondary border-dark text-white')
        $("#" + elemId + '__BV_toggle_').addClass('bg-white border-secondary text-dark')
        $("#" + elemId + '__BV_button_').addClass('bg-white border-secondary text-dark')
      }
      if (newD) {
        elemId = this.getDictionaryElemId(newD.id)
        $("#" + elemId + '__BV_toggle_').removeClass('bg-white border-secondary text-dark')
        $("#" + elemId + '__BV_button_').removeClass('bg-white border-secondary text-dark')
        $("#" + elemId + '__BV_toggle_').addClass('bg-secondary border-dark text-white')
        $("#" + elemId + '__BV_button_').addClass('bg-secondary border-dark text-white')
      }
    },
    toggleAllDictionaries() {
      if (this.isAllDictionariesCollapsed) {
        $("#" + this.ids.unrepeatedDictionaries).collapse('show')
        $("#" + this.ids.nonUnrepeatedDictionaries).collapse('show')
      } else {
        $("#" + this.ids.unrepeatedDictionaries).collapse('hide')
        $("#" + this.ids.nonUnrepeatedDictionaries).collapse('hide')
      }
    },
    toggleDropdownUnrepeatedDictionaries() {
      if (this.isDropdownUnrepeatedDictionariesCollapsed) {
        $("#" + this.ids.unrepeatedDictionaries).collapse('show')
      } else {
        $("#" + this.ids.unrepeatedDictionaries).collapse('hide')
      }
    },
    toggleDropdownNonUnrepeatedDictionaries() {
      if (this.isDropdownNonUnrepeatedDictionariesCollapsed) {
        $("#" + this.ids.nonUnrepeatedDictionaries).collapse('show')
      } else {
        $("#" + this.ids.nonUnrepeatedDictionaries).collapse('hide')
      }
    },
    scrollToActiveUnique() {
      if (this.dictionary) {
        let elem = null
        if (this.dictionary.unrepeated) {
          elem = "#" + this.ids.unrepeatedDictionaries
        } else {
          elem = "#" + this.ids.nonUnrepeatedDictionaries
        }
        this.scrollToElemInField(elem)
      }
    },
    scrollToActiveDictionary() {
      if (this.dictionary) {
        const elem = "#" + this.getDictionaryElemId(this.dictionary.id)
        this.scrollToElemInField(elem)
      }
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
    buildHeight() {
      this.$nextTick(() => {
        this.style.height.col = window.innerHeight - this.height.header - this.height.footer - 6
      })
    },
    onSelectDictionary(d) {
      this.clickDictionary(d.id)
      this.navigateToDictionary(d)
    },
    onConfirmAction(flag){
      this.confirmAction.isConfirm = flag
    },
    onRejectAction(flag){
      this.confirmAction.isReject = flag
    },
  },
}
</script>

<style scoped>

.dictionary-nav- {
  width: 100%;
  display: flex;
  flex-direction: column;
}

.st-text-shift {
  overflow-wrap: anywhere;
}

.st-float-right {
  float: right
}

.st-ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis
}

.st-button-ellipsis {
  display: block;
  width: 50px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  text-align: left;
}

i {
  float: right;
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

.unrepeated-button.dropdown:hover {
  z-index: 3;
  box-shadow: 0 0 0 1px white, 0 0 0 5px #2eabbf !important;
}

.unrepeated.dropdown:hover {
  z-index: 3;
  box-shadow: 0 0 0 1px white, 0 0 0 5px #5dbecd !important;
}

.non-unrepeated-button.dropdown:hover {
  z-index: 3;
  box-shadow: 0 0 0 1px white, 0 0 0 5px #fbbd47 !important;
}

.non-unrepeated.dropdown:hover {
  z-index: 3;
  box-shadow: 0 0 0 1px white, 0 0 0 5px #fccc70 !important;
}

.border-2 {
  border-width: 2px !important;
}

</style>
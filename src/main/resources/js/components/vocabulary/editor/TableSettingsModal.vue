<template>
  <b-modal
      v-if="show"
      :id="id"
      :ref="id"
      :header-class="'p-3'"
      :body-class="'py-0'"
      no-fade
      :no-close-on-backdrop="!closable"
      :no-close-on-esc="!closable"
  >

    <template #modal-header="{ close }">
      <b-container fluid class="px-1">
        <close-row v-if="closable"
                   :title="getCapitalizeLang('tableSettings')"
                   @close="reject()"
        ></close-row>
      </b-container>
    </template>

    <template #modal-footer>
      <b-button variant="secondary" @click.prevent.stop="reject()">
        {{ getCapitalizeLang('no') }}
      </b-button>
      <b-button variant="primary" @click.prevent.stop="confirm()">
        {{ getCapitalizeLang('yes') }}
      </b-button>
    </template>

    <div class="d-flex align-items-start">
      <div class="nav flex-column nav-pills me-3" role="tablist" aria-orientation="vertical">
        <button class="nav-link active"
                :id="currentPropertySettingsElemId + '-tab'"
                role="tab"
                type="button"
                data-toggle="pill"
                :data-target="'#' + currentPropertySettingsElemId"
                :aria-controls="currentPropertySettingsElemId"
                aria-selected="true">
          {{ getCapitalizeLang('propertySettings') }}
        </button>
      </div>
      <div class="tab-content">
        <div class="tab-pane fade show active"
             :id="currentPropertySettingsElemId"
             role="tabpanel"
             :aria-labelledby="currentPropertySettingsElemId + '-tab'">

          <div class="container text-left">
            <div class="row align-items-center">
              <div class="col" style="width: 150px;">
                {{ getCapitalizeLang('hiddenColumns') }}
                <div class="st-setting-group"
                     @mouseup.prevent.stop="mouseupColumns(false)"
                     :id="getColumnsElemId(false)"
                >
                  <ul class="list-group">
                    <li v-for="(setting, i) in currentPropertySettings"
                        v-if="!setting.showColumn"
                        class="d-block rounded-0 list-group-item list-group-item-action py-0 my-0 border-1 border-muted"
                        @mousedown.prevent.stop="mousedownColumn(i, false)"
                        @mouseup.prevent.stop="mouseupColumn(i, false)"
                        :id="getPropertySettingElementId(i)"
                    >
                      <small>{{ getCapitalizeLang(setting.label) }}</small>
                    </li>
                  </ul>
                </div>
              </div>
              <div class="col" style="width: 150px;">
                {{ getCapitalizeLang('activeColumns') }}
                <div class="st-setting-group"
                     @mouseup.prevent.stop="mouseupColumns(true)"
                     :id="getColumnsElemId(true)"
                >
                  <ul class="list-group">
                    <li v-for="(setting, i) in currentPropertySettings"
                        v-if="setting.showColumn"
                        class="d-block rounded-0 list-group-item list-group-item-action py-0 my-0 border-1 border-muted"
                        @mousedown.prevent.stop="mousedownColumn(i, true)"
                        @mouseup.prevent.stop="mouseupColumn(i, true)"
                        :id="getPropertySettingElementId(i)"
                    >
                      <small>{{ getCapitalizeLang(setting.label) }}</small>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <GlobalEvents @mouseup="mouseupOutside()"/>
  </b-modal>
</template>

<script>
import {mapState} from 'vuex'
import arrayJS from "../../../util/array"
import CloseRow from "../../close/CloseRow.vue"
import * as _ from "lodash"

export default {
  props: [
    'id',
    'closable',
    'instanceMark',
    'propertySettings',
  ],
  components: {
    CloseRow,
  },
  created() {
    this.fetchData()
  },
  watch: {
    $route: [
      'fetchData',
    ],
  },
  computed: {
    ...mapState([
      'action',
      'lang',
    ]),
    currentPropertySettingsElemId() {
      return this.prefixId + "propertySettings"
    },
    prefixId() {
      return this.name + "-" + this.instanceMark + "-"
    },
  },
  data() {
    return {
      name: "tableSettingsModal",
      show: true,
      currentPropertySettings: [],
      isMouseInClick: false,
      actionLocal: {
        id: -1,
        errors: {},
      },
      dto: {
        column: null,
      }
    }
  },
  methods: {
    fetchData() {
      this.currentPropertySettings = this.propertySettings
      this.orderCurrentPropertySettings()
    },
    showModal() {
      this.$refs[this.id].show()
    },
    hideModal() {
      this.$refs[this.id].hide()
    },
    getLang(key) {
      return this.$t(key)
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    getLastInxNotShowColumn() {
      return this.currentPropertySettings.findLastIndex(s => s.showColumn === false)
    },
    getLastInxShowColumn() {
      return this.currentPropertySettings.findLastIndex(s => s.showColumn === true)
    },
    orderCurrentPropertySettings() {
      this.currentPropertySettings = _.orderBy(this.currentPropertySettings, ['columnInx', 'label'], ['asc', 'asc'])
    },
    updateAction() {
      this.actionLocal.id = this.action.id
      this.actionLocal.errors = this.action.errors
    },
    reject() {
      this.hideModal()
    },
    getFirstCapitalize(string) {
      return _.capitalize(string)
    },
    mousedownColumn(inx, show) {
      this.isMouseInClick = true
      setTimeout(() => {
        if (this.isMouseInClick) {
          this.dto.column = {inx: inx, show: show}
        }
      }, 2)
    },
    mouseupColumns(show) {
      if (this.isMouseInClick) {
        if (this.dto.column.show === true && show === true) {
          this.dragEndColumn()
          return
        }
        if (show) {
          this.mouseupColumn(this.getLastInxShowColumn() + 1, show)
        } else {
          this.mouseupColumn(this.currentPropertySettings.length - 1, show)
        }
      }
      this.orderCurrentPropertySettings()
    },
    mouseupColumn(inx, show) {
      if (this.dto.column === null) {
        this.dragEndColumn()
        return
      }
      if (this.dto.column.inx === inx) {
        this.dragEndColumn()
        return
      }
      if (this.dto.column.show === false && show === false) {
        this.dragEndColumn()
        return
      }
      this.currentPropertySettings[this.dto.column.inx].showColumn = show
      arrayJS.move(this.currentPropertySettings, this.dto.column.inx, inx)
      this.currentPropertySettings.forEach((s, i) => {
        if (s.showColumn === true) {
          s.columnInx = i
        } else {
          s.columnInx = null
        }
      })
      this.orderCurrentPropertySettings()
      this.dragEndColumn()
    },
    mouseupOutside() {
      this.dragEndColumn()
    },
    dragEndColumn() {
      this.isMouseInClick = false
      this.setToDefaultDTO()
    },
    setToDefaultDTO() {
      this.dto.column = null
    },
    getColumnsElemId(show) {
      return this.prefixId + "columns" + "-" + show
    },
    getPropertySettingElementId(i) {
      return this.prefixId + "propertySetting" + i
    },
  }
}
</script>

<style scoped>
.st-setting-group {
  height: 200px;
  overflow-y: auto;
  border-style: solid;
  border-color: darkgrey;
  border-width: thin;
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
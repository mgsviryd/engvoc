<template>
  <div v-if="show" class="modal fade" :id="id">
    <div class="modal-dialog modal-dialog-centered" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">
            {{ getFirstCapitalize(lang.map.tableSettings) }}
          </h5>
          <button type="button" class="close" aria-label="Close" @click.prevent.stop="reject()">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">

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
                {{ getFirstCapitalize(lang.map.propertySettings) }}
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
                      {{ getFirstCapitalize(lang.map.hiddenColumns) }}
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
                      {{ getFirstCapitalize(lang.map.activeColumns) }}
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
        </div>
      </div>
    </div>
    <GlobalEvents @mouseup="mouseupOutside()"/>
  </div>
</template>

<script>
import {mapState} from 'vuex'
import arrayJS from "../../../util/array";
import * as _ from "lodash"

export default {
  created() {
    this.fetchData()
  },
  components: {},
  props: [
    'id',
    'instanceMark',
    'propertySettings',
  ],
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
  watch: {
    $route: [
      'fetchData',
    ],
  },
  data() {
    return {
      name: "tableSettings",
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
    hideModal() {
      $("#" + this.id).modal('hide')
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
<template>
  <div v-if="showComponent" class="card-origin-">
    <nav
        class="navbar navbar-fixed-top navbar-expand-lg navbar-dark bg-dark py-2 shadow border-bottom border-secondary"
        style="width:100%;">
      <div class="btn-group btn-group-md btn-group-justified">

        <button class="btn btn-light mr-sm-1 disabled">
          <small>
            <b>
              {{ uploadFrom }} :
            </b>
          </small>
        </button>

        <button class="btn btn-light mr-sm-1" v-bind:class="{ active: isButtonActive1}"
                href="#card-origin-tab1" data-toggle="tab" role="tab" aria-controls="card-origin-tab1"
                id="card-origin-button1"
                aria-selected="false"
                @click="activateLevel1()">
          <small>
            <b>
              {{ server }}
            </b>
          </small>
        </button>

        <button class="btn btn-light mr-sm-1 text-capitalize" v-bind:class="{ active: isButtonActive2}"
                href="#card-origin-tab2" data-toggle="tab" role="tab" aria-controls="card-origin-tab2"
                id="card-origin-button2"
                aria-selected="false"
                @click="activateLevel2()">
          <small>
            <b>
              {{ xml }}
            </b>
          </small>
        </button>

        <button class="btn btn-light mr-sm-1 text-capitalize" v-bind:class="{ active: isButtonActive3}"
                href="#card-origin-tab3" data-toggle="tab" role="tab" aria-controls="card-origin-tab3"
                id="card-origin-button3"
                aria-selected="false"
                @click="activateLevel3()">
          <small>
            <b>
              {{ excel }}
            </b>
          </small>
        </button>
      </div>
    </nav>


    <div class="tab-content" id="card-origin-tab-content-0">
      <div class="tab-pane fade " v-bind:class="{ active: isButtonActive1, show: isButtonActive1}"
           id="card-origin-tab1"
           role="tabpanel" aria-labelledby="...">
        <div class="container-fluid">
          <div class="row">
            <div class="col-4 col-sm-3 border-right border-muted px-0">

            </div>
          </div>
        </div>
      </div>
      <div class="tab-pane fade"
           id="card-origin-tab2" v-bind:class="{ active: isButtonActive2, show: isButtonActive2}"
           role="tabpanel" aria-labelledby="...">
        <div class="container-fluid">
          <div class="row">
            <div class="col-4 col-sm-3 border-right border-muted px-0">
              <drop-zone :props="propsDropZoneXml"></drop-zone>
            </div>
          </div>
        </div>
      </div>
      <div class="tab-pane fade" v-bind:class="{ active: isButtonActive3, show: isButtonActive3}"
           id="card-origin-tab3"
           role="tabpanel" aria-labelledby="...">
        <div class="container-fluid">
          <div class="row">
            <div class="col-4 col-sm-3 border-right border-muted px-0">
              <drop-zone :props="propsDropZoneExcel"></drop-zone>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {mapState} from 'vuex'
import DropZone from "../upload/DropZone.vue";
import string from "../../util/string";

export default {
  components: {
    DropZone,
  },
  data() {
    return {
      propsDropZoneExcel: {
        accept: '.xlsx',
        size: '20MB',
        storeAction: 'getUploadExcelFileAction'
      },
      propsDropZoneXml: {
        accept: '.xml',
        size: '20MB',
        storeAction: 'getUploadXmlFileAction'
      },
      showComponent: true,
      isButtonActive1: false,
      isButtonActive2: false,
      isButtonActive3: false,
    }
  },
  created() {
    this.fetchData()

  },
  watch: {
    $route: 'fetchData',
  },
  computed: {
    ...mapState([
      'lang',
    ]),
    uploadFrom() {
      return string.getWithFirstCapital(this.lang.map.uploadFrom)
    },
    server() {
      return string.getWithFirstCapital(this.lang.map.server)
    },
    xml() {
      return string.getWithFirstCapital(this.lang.map.xml)
    },
    excel() {
      return string.getWithFirstCapital(this.lang.map.excel)
    },
  },
  methods: {
    fetchData() {
    },
    activateLevel1() {
      this.disactiveAll()
      this.isButtonActive1 = true
    },
    activateLevel2() {
      this.disactiveAll()
      this.isButtonActive2 = true
    },
    activateLevel3() {
      this.disactiveAll()
      this.isButtonActive3 = true
    },
    disactiveAll() {
      this.isButtonActive1 = false
      this.isButtonActive2 = false
      this.isButtonActive3 = false
    }
  }
}
</script>

<style scoped>

</style>
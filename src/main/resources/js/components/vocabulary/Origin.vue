<template>
  <div v-if="show">
    <nav
        class="navbar navbar-fixed-top navbar-expand-lg navbar-dark bg-dark py-1 shadow border-bottom border-secondary"
        style="width:100%;">
      <b-button-group size="sm" class="btn-group-justified">

        <b-button variant="light"
                  class="mr-1 disabled">
          <small>
            <b>
              {{ getCapitalizeLang('uploadFrom') }} :
            </b>
          </small>
        </b-button>

        <b-button variant="light"
                  class="mr-1" :class="{ active: isButtonActive1}"
                  href="#card-origin-tab1" data-toggle="tab" role="tab" aria-controls="card-origin-tab1"
                  id="card-origin-button1"
                  aria-selected="false"
                  @click="activateLevel1()">
          <small>
            <b>
              {{ getCapitalizeLang('server') }}
            </b>
          </small>
        </b-button>

        <b-button variant="light"
                  class="mr-1" :class="{ active: isButtonActive2}"
                  href="#card-origin-tab2" data-toggle="tab" role="tab" aria-controls="card-origin-tab2"
                  id="card-origin-button2"
                  aria-selected="false"
                  @click="activateLevel2()">
          <small>
            <b>
              {{ getCapitalizeLang('xml') }}
            </b>
          </small>
        </b-button>

        <b-button variant="light"
                  class="mr-1" :class="{ active: isButtonActive3}"
                  href="#card-origin-tab3" data-toggle="tab" role="tab" aria-controls="card-origin-tab3"
                  id="card-origin-button3"
                  aria-selected="false"
                  @click="activateLevel3()">
          <small>
            <b>
              {{ getCapitalizeLang('excel') }}
            </b>
          </small>
        </b-button>
      </b-button-group>
    </nav>


    <div class="tab-content" id="card-origin-tab-content-0">
      <div class="tab-pane fade " :class="{ active: isButtonActive1, show: isButtonActive1}"
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
           id="card-origin-tab2" :class="{ active: isButtonActive2, show: isButtonActive2}"
           role="tabpanel" aria-labelledby="...">
        <div class="container-fluid">
          <div class="row">
            <div class="col-4 col-sm-3 border-right border-muted px-0">
              <drop-zone :props="propsDropZoneXml"></drop-zone>
            </div>
          </div>
        </div>
      </div>
      <div class="tab-pane fade" :class="{ active: isButtonActive3, show: isButtonActive3}"
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
import DropZone from "../upload/DropZone.vue"
import * as _ from "lodash"

export default {
  components: {
    DropZone,
  },
  data() {
    return {
      show: true,
      propsDropZoneExcel: {
        accept: '.xlsx',
        size: '10MB',
        storeAction: 'uploadCardsByExcelFileAction'
      },
      propsDropZoneXml: {
        accept: '.xml',
        size: '10MB',
        storeAction: 'uploadCardsByXmlFileAction'
      },
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
  },
  methods: {
    fetchData() {
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.$t(key))
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
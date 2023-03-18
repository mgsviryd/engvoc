<template>
  <div v-if="showComponent" class="navbar-navbar">
    <div class="parts-navbar-navbar">
      <nav
          class="navbar navbar-expand-lg navbar-dark bg-dark navbar-fixed-top py-2 shadow border-bottom border-secondary"
          style="width:100%;">

        <div class="btn-group btn-group-md btn-group-justified">

          <button class="btn btn-light mr-sm-1 text-capitalize disabled">
            <small>
              <b>
                {{ lang.map.logo}}
              </b>
            </small>
          </button>

          <button class="btn btn-light mr-sm-1 text-capitalize" v-bind:class="{ active: isButtonActive1}"
                  href="#tab1" data-toggle="tab" role="tab" aria-controls="tab1"
                  id="button1"
                  aria-selected="false"
                  @click="activateLevel1()" style="width: 130px; min-height: 40px;">
            <small>
              <b>
                {{ lang.map.card }}
              </b>
            </small>
          </button>
          <button class="btn btn-light mr-sm-1 text-capitalize" v-bind:class="{ active: isButtonActive2}"
                  href="#tab2" data-toggle="tab" role="tab" aria-controls="tab2"
                  id="button2"
                  aria-selected="false"
                  @click="activateLevel2()" style="width: 130px; min-height: 40px;">
            <small>
              <b>
                {{ lang.map.origin }}
              </b>
            </small>
          </button>
          <button class="btn btn-light mr-sm-1 text-capitalize" v-bind:class="{ active: isButtonActive3}"
                  href="#tab3" data-toggle="tab" role="tab" aria-controls="tab3"
                  id="button3"
                  aria-selected="false"
                  @click="activateLevel3()" style="width: 130px; min-height: 40px;">
            <small>
              <b>
                {{ lang.map.settings }}
              </b>
            </small>
          </button>
        </div>
        <div class="btn-group btn-group-md btn-group-justified mx-2">
          <lang-dropdown></lang-dropdown>
        </div>
      </nav>
      <div class="tab-content" id="tab-content-0">
        <div class="tab-pane fade bg-light" v-bind:class="{ active: isButtonActive1, show: isButtonActive1}"
             id="tab1"
             role="tabpanel" aria-labelledby="...">
            <card></card>
        </div>
        <div class="tab-pane fade bg-light"
             id="tab2" v-bind:class="{ active: isButtonActive2, show: isButtonActive2}"
             role="tabpanel" aria-labelledby="...">
            <origin></origin>
        </div>
        <div class="tab-pane fade bg-light" v-bind:class="{ active: isButtonActive3, show: isButtonActive3}"
             id="tab3"
             role="tabpanel" aria-labelledby="...">
            <settings></settings>
        </div>
      </div>
    </div>
  </div>

</template>

<script>

import Card from '../../components/card/Card.vue'
import Origin from '../../components/card/Origin.vue'
import Settings from '../../components/card/Settings.vue'
import LangDropdown from '../../components/lang/LangDropdown.vue'
import {mapState} from "vuex";


export default {
  components: {
    Card,
    Origin,
    Settings,
    LangDropdown,
  },
  computed: {
    ...mapState([
      'lang',
    ]),
  },
  data() {
    return {
      showComponent: true,
      isButtonActive1: false,
      isButtonActive2: false,
      isButtonActive3: false,
    }
  },
  methods: {
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
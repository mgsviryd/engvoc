<template>
  <div class="navbar-navbar"
  >
    <div v-if="showComponent" class="parts-navbar-navbar"
    >
      <nav
          class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top py-2 mb-0 shadow border-bottom border-secondary"
          style=" width:100%;"
      >
        <div class="btn-group btn-group-md btn-group-justified">
          <button class="btn btn-light mr-sm-1 text-capitalize disabled">
            <small>
              <b>
                {{ lang.map.logo }}
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

          <button class="btn btn-light mr-sm-1 text-capitalize" v-bind:class="{ active: isButtonActive4}"
                  href="#tab4" data-toggle="tab" role="tab" aria-controls="tab4"
                  id="button4"
                  aria-selected="false"
                  @click="activateLevel4()" style="width: 130px; min-height: 40px;">
            <small>
              <b>
                {{ lang.map.dictionary }}
              </b>
            </small>
          </button>

          <button class="btn btn-light mr-sm-1 text-capitalize" v-bind:class="{ active: isButtonActive5}"
                  href="#tab5" data-toggle="tab" role="tab" aria-controls="tab5"
                  id="button5"
                  aria-selected="false"
                  @click="activateLevel5()" style="width: 130px; min-height: 40px;">
            <small>
              <b>
                {{ lang.map.editor }}
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
      <div class="tab-content" id="tab-content-0 d-inline-block" style="width: 100%">
        <div class="tab-pane fade bg-light border-1 border-secondary" style="width: 100%"
             v-bind:class="{ active: isButtonActive1, show: isButtonActive1}"
             id="tab1"
             role="tabpanel" aria-labelledby="...">
          <card></card>
        </div>

        <div class="tab-pane fade bg-light" style="width: 100%"
             v-bind:class="{ active: isButtonActive4, show: isButtonActive4}"
             id="tab4"
             role="tabpanel" aria-labelledby="...">
          <dictionary></dictionary>
        </div>

        <div class="tab-pane fade bg-light" style="width: 100%"
             v-bind:class="{ active: isButtonActive5, show: isButtonActive5}"
             id="tab5"
             role="tabpanel" aria-labelledby="...">
          <editor></editor>
        </div>

        <div class="tab-pane fade bg-light" style="width: 100%"
             id="tab2" v-bind:class="{ active: isButtonActive2, show: isButtonActive2}"
             role="tabpanel" aria-labelledby="...">
          <origin></origin>
        </div>
        <div class="tab-pane fade bg-light" style="width: 100%"
             v-bind:class="{ active: isButtonActive3, show: isButtonActive3}"
             id="tab3"
             role="tabpanel" aria-labelledby="...">
          <settings></settings>
        </div>
      </div>
    </div>
  </div>

</template>

<script>
import {mapState} from "vuex";
import Card from '../../components/card/Card.vue'
import Dictionary from '../../components/card/Dictionary.vue'
import Editor from './editor/Editor.vue'
import Origin from '../../components/card/Origin.vue'
import Settings from '../../components/card/Settings.vue'
import LangDropdown from '../../components/lang/LangDropdown.vue'

export default {
  created() {
    this.$root.$on('dragover', payload => {

    });
  },
  components: {
    Card,
    Dictionary,
    Editor,
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
      isButtonActive4: false,
      isButtonActive5: false,
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
    activateLevel4() {
      this.disactiveAll()
      this.isButtonActive4 = true
    },
    activateLevel5() {
      this.disactiveAll()
      this.isButtonActive5 = true
    },
    disactiveAll() {
      this.isButtonActive1 = false
      this.isButtonActive2 = false
      this.isButtonActive3 = false
      this.isButtonActive4 = false
      this.isButtonActive5 = false
    },
  }
}
</script>

<style scoped>


</style>
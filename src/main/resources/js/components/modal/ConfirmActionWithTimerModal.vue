<template>
  <b-modal
      v-if="show"
      :id="ids.id"
      :ref="ids.id"
      :body-class="'py-0'"
      :header-class="'p-3'"
      :no-close-on-backdrop="!closable"
      :no-close-on-esc="!closable"
      no-fade
      @hide="onHide()"
      @show="onShow()"
  >

    <template #modal-header="{ close }">
      <b-container class="px-1" fluid>
        <close-row
            v-if="closable"
            :title="getCapitalizeLang('confirmAction')"
            @close="reject()"
        ></close-row>
      </b-container>
    </template>

    <p>{{ message }}</p>

    <template #modal-footer="{ ok, cancel, hide }">
      <b-button
          variant="secondary"
          @click.prevent.stop="reject()"
      >
        {{ getCapitalizeLang("no") }}{{ '(' + countDown + ')' }}
      </b-button>
      <b-button
          variant="outline-success"
          @click.prevent.stop="confirm()"
          @keyup.enter="confirm()"
      >
        {{ getCapitalizeLang("yes") }}
      </b-button>
    </template>
  </b-modal>
</template>

<script>
import {mapState} from "vuex"
import * as _ from "lodash"
import CloseRow from "../close/CloseRow.vue"

export default {
  props: [
    'id',
    'isForNo',
    'closable',
  ],
  components: {
    CloseRow,
  },
  computed: {
    ...mapState([
      'lang',
    ]),
    ids() {
      return {
        id: this.prefixId(),
      }
    }
  },
  data() {
    return {
      show: true,
      seconds: 0,
      message: '',
      countDown: 0,
      countDownFunction: null,
      timerFunction: null,
    }
  },
  methods: {
    prefixId() {
      return this.name + '-' + this.id + '-'
    },
    showModal(message, seconds) {
      this.message = message
      this.seconds = seconds
      this.$refs[this.ids.id].show()
    },
    hideModal() {
      this.$refs[this.ids.id].hide()
    },
    getLang(key) {
      return this.$t(key)
    },
    confirm() {
      this.$emit('onConfirm', true)
      this.hideModal()
    },
    reject() {
      this.$emit('onReject', true)
      this.hideModal()
    },
    onShow() {
      this.startTimer()
    },
    onHide() {
      this.stopTimer()
    },
    startTimer() {
      this.countDown = this.seconds
      this.countDownFunction = setInterval(
          () => {
            this.countDown -= 1
          },
          1000
      )
      this.timerFunction = _.delay(
          () => {
            if (this.isForNo) {
              this.reject()
            } else {
              this.confirm()
            }
          },
          this.seconds * 1000)
    },
    stopTimer() {
      clearTimeout(this.timerFunction)
      clearTimeout(this.countDownFunction)
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
  }
}
</script>

<style scoped>

</style>
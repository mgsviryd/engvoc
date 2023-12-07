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
        {{ getCapitalizeLang("no") }}
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
    'closable',
    'message',
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
      name: 'ConfirmActionModal',
      show: true,
    }
  },
  methods: {
    prefixId() {
      return this.name + '-' + this.id + '-'
    },
    showModal() {
      this.$refs[this.ids.id].show()
    },
    hideModal() {
      this.$refs[this.ids.id].hide()
    },
    confirm() {
      this.$emit('confirm-action-modal', {id: this.id})
    },
    reject() {
      this.$emit('reject-action-modal', {id: this.id})
    },
    getLang(key) {
      return this.$t(key)
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
  }
}
</script>

<style scoped>

</style>
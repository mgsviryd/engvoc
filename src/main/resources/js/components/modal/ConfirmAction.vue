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
                   :title="getCapitalizeLang('confirmAction')"
                   @close="reject()"
        ></close-row>
      </b-container>
    </template>

    <p>{{ message }}</p>

    <template #modal-footer="{ ok, cancel, hide }">
      <button type="button" class="btn btn-secondary" @click.prevent.stop="reject()">
        {{ getCapitalizeLang("no") }}
      </button>
      <button type="button" class="btn btn-primary" @click.prevent.stop="confirm()">
        {{ getCapitalizeLang("yes") }}
      </button>
    </template>
  </b-modal>
</template>

<script>
import {mapState} from 'vuex'
import CloseRow from "../close/CloseRow.vue"
import * as _ from "lodash"

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
  },
  data() {
    return {
      show: true,
    }
  },
  methods: {
    getLang(key) {
      return this.$t(key)
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    confirm() {
      this.$emit('confirm')
    },
    reject() {
      this.$emit('reject')
    },
  }
}
</script>

<style scoped>

</style>
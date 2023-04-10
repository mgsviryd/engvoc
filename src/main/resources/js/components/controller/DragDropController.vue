<template>
</template>

<script>
import {mapState} from "vuex"
import date from "../../util/date"

export default {
  created() {
    this.$root.$on('dragstart', payload => {
      this.$root.$emit('start-dragdrop', {type: payload.type})
      this.dragdrop.start = payload
    });
    this.$root.$on('dragover', payload => {
      // if (this.dragdrop.over) this.$root.$emit('change-dragover', this.dragdrop.over)
      this.dragdrop.over = payload
    });
    this.$root.$on('dragleave', payload => {
      this.dragdrop.leave = payload
    });
    this.$root.$on('dragend', payload => {
      this.$root.$emit('end-dragdrop', {type: payload.type})
    });
  },

  data() {
    return {
      dragdrop: {
        start: {
          type: "",
          operation: "add", // add / update / addUpdate
          ldt: null,
          pull: "", // preserve / delete
          pullItems: [],
          removeAllAction: "",
          addAllAction: "",
          updateAllAction: "",
          addUpdateAllAction: "",
          markSource: "",
          pullSourceId: -1,
        },
        over: {
          type: "",
          ldt: null,
        },
        leave: {
          type: "",
          ldt: null,
          push: "", // preserve / delete
          removeAllAction: "", // action from store
          addAllAction: "", // action from store
          updateAllAction: "", // action from store
          addUpdateAllAction: "", // action from store
          markSource: "",
          pushSourceId: -1,
        },
      },
      delayMillis: 200,
      currentMillis: 0,
    }
  },

  watch: {
    $route: [
      'fetchData',
    ],
    dragdrop: {
      handler: function (newVal, oldVal) {
        if (newVal.leave.ldt === null && oldVal.leave.ldt === null) return;
        if (newVal.leave.ldt !== null) {
          let millisOver = 0;
          if (newVal.over.ldt !== null) {
            millisOver = date.getUTCMilliseconds(newVal.over.ldt)
          }
          const millisLeave = date.getUTCMilliseconds(newVal.leave.ldt)
          const millis = millisLeave > millisOver ? millisLeave : millisOver
          if (millis === this.currentMillis) return;
          this.currentMillis = millis
          setTimeout(() => this.executeDragDrop(millis), this.delayMillis)
        }
      },
      deep: true,
    },
  },
  methods: {
    fetchData() {
    },
    executeDragDrop(millis) {
      if (this.currentMillis === millis) {
        if (this.isSameType()) {
          this.$root.$emit('confirm-dragstart', this.dragdrop)
          this.$root.$emit('confirm-dragleave', this.dragdrop)
        }
      }
    },
    isSameType() {
      return this.dragdrop.start.type === this.dragdrop.over.type && this.dragdrop.over.type === this.dragdrop.leave.type
    },
  },

}
</script>

<style scoped>

</style>
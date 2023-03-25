<template>
</template>

<script>
import {mapState} from "vuex"
import date from "../../util/date"

export default {
  created() {
    this.$root.$on('dragstart', payload => {
      this.dragdrop.start = payload
    });
    this.$root.$on('dragover', payload => {
      this.dragdrop.over = payload
    });
    this.$root.$on('dragleave', payload => {
      this.dragdrop.leave = payload
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
        if (newVal.leave.ldt !== null
            // && newVal.over.ldt !== null
        ) {
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
        if (this.isNotSameType() || this.isInsideSameSource()) {
          return;
        }
        // console.info("executeDragDrop")
        if (this.dragdrop.start.pull === "delete") {
          this.$store.dispatch(this.dragdrop.start.actions.removeAllAction,
              {
                cards: this.dragdrop.start.pullItems,
                id: this.dragdrop.start.pullSourceId
              })
        }
        if (this.dragdrop.leave.push === "delete") {
          this.$store.dispatch(this.dragdrop.leave.actions.removeAllAction,
              {
                cards: this.dragdrop.start.pushItems,
                id: this.dragdrop.start.pushSourceId
              })
        }
        if (this.dragdrop.start.operation === "add") {
          this.$store.dispatch(this.dragdrop.leave.actions.addAllAction,
              {
                cards: this.dragdrop.start.pullItems,
                id: this.dragdrop.leave.pushSourceId
              })
        }
        if (this.dragdrop.start.operation === "update") {
          this.$store.dispatch(this.dragdrop.leave.actions.updateAllAction,
              {
                cards: this.dragdrop.start.pullItems,
                id: this.dragdrop.leave.pushSourceId
              })
        }
        if (this.dragdrop.start.operation === "addUpdate") {
          this.$store.dispatch(this.dragdrop.leave.actions.addUpdateAllAction,
              {
                cards: this.dragdrop.start.pullItems,
                id: this.dragdrop.leave.pushSourceId
              })
        }
      }
    },
    isNotSameType() {
      return this.dragdrop.start.type !== this.dragdrop.over.type || this.dragdrop.over.type !== this.dragdrop.leave.type
    },
    isInsideSameSource() {
      return this.dragdrop.start.actions.addAllAction === this.dragdrop.leave.actions.addAllAction && this.dragdrop.start.pullSourceId === this.dragdrop.leave.pushSourceId
    },
    abortDragstart() {
      this.$root.$emit('abort-dragstart', {ldt: this.dragdrop.start.ldt})
    },
    preDeleteDragstart() {
      this.$root.$emit('pre-delete-dragstart', {ldt: this.dragdrop.start.ldt})
    },
  },

}
</script>

<style scoped>

</style>
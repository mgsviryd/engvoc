<template>
    <tr>
      <td class="st-squeeze border-secondary border-1 border-left-0">{{ cardN+1 }}</td>
      <td class="st-text-shift border-secondary border-1">{{ card.word }}</td>
      <td class="st-text-shift border-secondary border-1">{{ card.translation }}</td>
      <td class="st-text-shift border-secondary border-1">
        {{ card.example }}
        <br/>
        {{ card.exampleTranslation }}
      </td>

<!--      <b-form-textarea-->
<!--          id="textarea"-->
<!--          v-model="card.example"-->
<!--          rows="1"-->
<!--          max-rows="15"-->
<!--      ></b-form-textarea>-->
      <td class="st-squeeze border-secondary border-1 border-right-0"><input type="checkbox" v-model="card.learned"></td>
    </tr>
</template>

<script>
export default {
  props: [
    'card',
    'cardN'
  ],
  watch: {
    $route: [
      'fetchData',
    ],
  },
  methods: {
    fetchData() {
    },
    // resize() {
    //   let element = this.$refs["textarea"];
    //   element.style.height = element.scrollHeight + "px";
    // },
  },
}
</script>

<style scoped>
.st-squeeze{
  width: 1%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.st-text-shift {
  width: 150px;
  overflow-wrap: anywhere;
}
</style>
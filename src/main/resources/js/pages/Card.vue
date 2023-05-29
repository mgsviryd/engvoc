<template>
  <div>
    <card-nav></card-nav>
  </div>
</template>

<script>
import CardNav from '../components/card/CardNav.vue'


export default {
  components: {
    CardNav,
  },
  created(){
    this.$store.dispatch('findDictionariesAndCards')
  },
}
</script>

<style>
</style>
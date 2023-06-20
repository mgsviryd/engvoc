<template>
  <div>
    <greeting-nav></greeting-nav>
    <vocabulary-nav></vocabulary-nav>
  </div>
</template>

<script>
import GreetingNav from '../components/greeting/GreetingNav.vue'
import VocabularyNav from '../components/vocabulary/VocabularyNav.vue'


export default {
  components: {
    GreetingNav,
    VocabularyNav,
  },
  created(){
    this.$store.dispatch('findDictionariesAndCards')
  },
}
</script>

<style>
</style>
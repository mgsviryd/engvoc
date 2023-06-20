<template>
  <div>
    <sign
        :closable="false"
        :show="true"
    ></sign>
  </div>
</template>

<script>
import Sign from '../components/sign/SignModal.vue'


export default {
  components: {
    Sign,
  },
}
</script>

<style>
</style>
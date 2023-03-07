<template>
    <span>
        {{text}}
    </span>
</template>

<script>
    export default {
        props: ['unit'],
        computed: {
            text(){
                switch (this.unit) {
                    case "ITEM":
                        return this.$store.getters.getMessage('item')
                    case "METER":
                        return this.$store.getters.getMessage('meter')
                    case "LITER":
                        return this.$store.getters.getMessage('liter')
                    case "KG":
                        return this.$store.getters.getMessage('kg')
                    default:
                        return this.$store.getters.getMessage('item')
                }
            }
        },
    }
</script>

<style>

</style>
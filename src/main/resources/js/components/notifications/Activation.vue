<template>
    <div>

    </div>
</template>

<script>
    export default {
        props:['id'],
        data() {
            return {

            }
        },
        created(){
            this.fetchData()
        },
        methods:{
            async fetchData(){
                let user = await this.$store.dispatch('getActivationAction', id)
                if(user == null){
                    this.$store.dispatch()
                }
                    this.$router.push('/').catch(err => {})
            }
        }

    }

</script>

<style>

</style>
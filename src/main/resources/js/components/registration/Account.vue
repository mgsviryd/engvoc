<template>
    <div class="registration-account">
        <div v-if="isAuthenticated">
            <account-cabinet></account-cabinet>
        </div>
        <div v-else="isAuthenticated">
            <account-new :tooltip="[[lang.map.goToRegistration]]"></account-new>
        </div>
    </div>
</template>

<script>
    import { mapState } from 'vuex'
    import AccountCabinet from 'components/registration/AccountCabinet.vue'
    import AccountNew from 'components/registration/AccountNew.vue'
    export default{
        components: {
            AccountCabinet,
            AccountNew
        },
        computed: {
            ...mapState([
                'lang',
            ]),
            isAuthenticated(){
                return this.$store.getters.isAuthenticated
            },
        },
    }

</script>

<style>

</style>
<template>
    <div>
        <router-view></router-view>
    </div>
</template>
<script>
    import {mapState} from 'vuex'
    import vlf from "../util/vlf"

    export default{
        async created(){
            let frontend = await this.$store.dispatch('getFrontendAction')
            let version = frontend.version
            let keys = Object.keys(version)
            for (let k in keys) {
                if (this.objectPresent(this.frontend) && this.objectPresent(this.frontend.version)) {
                    if (this.needLoad(keys[k], version)) {
                        let action = 'get' + keys[k] + 'Action'
                        this.$store.dispatch(action)
                    }
                } else {
                    this.$store.dispatch('get' + keys[k] + 'Action')
                }
            }
            this.$store.dispatch('setFrontendAction', frontend)
            this.$store.dispatch('getLanguageMapAction', this.lang.current)
            this.$store.dispatch('getAuthenticationAction', this.$store.getters.getUsersTokens)
            this.$cookies.config('365d')
            this.sync()
        },
        computed: {
            ...mapState([
                'frontend',
                'lang',
                'basket',
                'favorite',
            ]),
            basketId(){
                return this.basket.id
            },
            favoriteId(){
                return this.favorite.id
            },

        },
        methods: {
            async sync(){
                let result = false
                await this.sleep(1)
                this.syncAll()
                while (true) {
                    await this.sleep(1000)
                    await this.syncAll()
                }
                return result
            },
            sleep(ms) {
                return new Promise(resolve => setTimeout(resolve, ms));
            },
            async syncAll(){
                await this.syncBasket()
                await this.syncFavorite()
                await this.syncRecent()
                await this.syncAuthentication()
            },
            async syncBasket(){
                await this.$store.dispatch('syncBasketStateWithLocalAction')
            },

            async syncFavorite(){
                await this.$store.dispatch('syncFavoriteStateWithLocalAction')
            },

            async syncRecent(){
                await this.$store.dispatch('syncRecentStateWithLocalAction')
            },

            async syncAuthentication(){
                await this.$store.dispatch('syncAuthenticationStateWithLocalAction')
            },

            needLoad(key, version){
                return !this.objectPresent(this.frontend.version[key])
                    || this.frontend.version[key] != version[key]
            },
            objectPresent(object){
                return typeof object != "undefined" && object != null
            },
        },
    }
</script>


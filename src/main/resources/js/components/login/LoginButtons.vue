<template>
    <div v-if="showComponent" class="login-loginButtons">
        <div class="btn-group btn-group-md btn-group-justified btn-group-md">
            <form v-if="isAuthenticated" action="/logout" method="post">
                <button type="submit" class="btn btn-sm btn-outline-primary pt-2 pb-2 px-4 shadow-none">
                    {{lang.map.logoutUpper}}
                </button>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>

            <div v-else="!isAuthenticated">
                <div class="btn-group btn-group-md">
                    <a href="/login" class="btn btn-sm btn-outline-primary pt-2 px-4 shadow-none">
                        {{lang.map.loginUpper}}
                    </a>

                    <a href="/login/github"
                       class="btn btn-sm btn-outline-primary py-2 px-2"
                       data-toggle="tooltip"
                       data-placement="bottom"
                       :title="[[lang.map.signInThroughGithub]]"
                    >
                        <i class="fab fa-github fa-1x" aria-hidden="true"></i>
                    </a>
                    <a href="/login/facebook"
                       class="btn btn-sm btn-outline-primary py-2 px-2"
                       data-toggle="tooltip"
                       data-placement="bottom"
                       :title="[[lang.map.signInThroughFacebook]]"
                    >
                        <i class="fab fa-facebook-f fa-1x" aria-hidden="true"></i>
                    </a>
                    <a href="/login/google"
                       class="btn btn-sm btn-outline-primary py-2 px-2"
                       data-toggle="tooltip"
                       data-placement="bottom"
                       :title="[[lang.map.signInThroughGoogle]]"
                    >
                        <i class="fab fa-google fa-1x" aria-hidden="true"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    export default{
        computed: {
            ...mapState([
                'lang',
            ]),
            isAuthenticated(){
                return this.$store.getters.isAuthenticated
            },
        },
        watch:{
            $route: [
                'fetchData',
            ],
        },
        created() {
            this.fetchData()
        },
        data(){
            return {
                showComponent: false,
            }
        },
        methods:{
            fetchData(){
                this.showComponent = false
                this.showComponent = true
            },
        },
    }

</script>

<style>

</style>
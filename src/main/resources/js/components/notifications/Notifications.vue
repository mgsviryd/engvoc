<template>
    <div v-if="showComponent">
        <button type="button" class="btn btn-light dropdown rounded-0 border border-1 border-white"
                data-toggle="dropdown"
                aria-haspopup="true"
                aria-expanded="false"
                data-placement="bottom"
                :title="[[lang.map.notifications]]"
                id="notificationsDropdown"
        >
            <small>
                <i class="fas fa-bell" :class="isFull? 'text-primary' : '' "></i>
            </small>
        </button>
        <form class="dropdown-menu" aria-labelledby="notificationsDropdown"
              @click.stop.prevent
        >
            <div class="form-group">
                <div v-if="isFull" v-for="(notification, index) in notifications">
                    <div class="container">
                        <div class="row">
                            <div class="ml-auto mx-3 mb-3">
                                <button type="button" class="close" @click.stop.prevent="hideDropdown()">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-2">
                                {{index + 1}}
                            </div>
                            <div class="col-8" >
                                <div class="row" @click.stop.prevent="goToModal(index)">
                                    <div class="col-1">
                                        <h6><b>{{lang.map[notification.mark]}}</b></h6>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-1 text-warning">
                                        <small>{{notification.creationLDT}}</small>
                                    </div>
                                </div>
                                <div v-if="notification.description" class="row">
                                    <div class="col text-justify">
                                        <small>{{lang.map[notification.description]}}</small>
                                    </div>
                                </div>
                                <div v-if="notification.path" class="row">
                                    <div class="col">
                                        <button class="btn btn-sm btn-block btn-transparent text-primary"
                                                @click.prevent.stop="goTo(notification.path)">
                                            {{hostname}}{{notification.path}}
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <div v-if="canRemove(index)" class="col-2 mr-1">
                                <button  type="button" @click.stop.prevent="removeNotification(index)">
                                    <i class="fas fa-trash-alt text-primary"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                    <div class="dropdown-divider"></div>
                </div>
                <div v-else="isFull">
                    {{lang.map.noNotifications}}
                </div>
            </div>
        </form>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    export default{
        data(){
            return {
                showComponent: false,
            }
        },
        computed: {
            ...mapState([
                'lang',
                'notifications'
            ]),
            isFull(){
                return this.notifications.length > 0
            },
            hostname(){
                return window.location.hostname
            }
        },
        created(){
            this.fetchData()
        },

        methods: {
            fetchData(){
                this.showComponent = false

                this.showComponent = true
            },
            goTo(path){
                this.$router.push({
                    path: path
                }).then(() => {
                }).catch(err => {
                })
            },
            hideDropdown(){
                $('#notificationsDropdown').dropdown('hide')
            },
            removeNotification(index){
                this.$store.dispatch('removeNotificationAction', index)
                if(!this.isFull){
                    this.hideDropdown()
                }
            },
            canRemove(index){
                return this.notifications[index].canRemove == 'true'
            },
        },
    }
</script>

<style scoped>
    .dropdown-menu {
        width: 300px;
        max-height: 400px;
        overflow: auto;
    }
</style>
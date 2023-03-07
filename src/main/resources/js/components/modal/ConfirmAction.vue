<template>
    <div class="modal fade" :id="idModal">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel">{{lang.map.confirmAction}}</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <p>{{message}}</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal" @click="reject">{{lang.map.no}}</button>
                    <button type="button" class="btn btn-primary" data-dismiss="modal" @click="confirm">{{lang.map.yes}}</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import {mapState} from 'vuex'

    export default{
        props: ['message', 'id'],
        data(){
            return {}
        },
        methods: {
            confirm(){
                this.$emit('confirm')
            },
            reject(){
                this.$emit('reject')
            },
        },
        computed: {
            ...mapState([
                'lang',
            ]),
            idModal(){
                return this.id
            }
        }
    }
</script>

<style scoped>

</style>
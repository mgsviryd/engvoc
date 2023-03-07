<template>
    <picture v-if="isDataPresent">
        <source v-for="(item, index) in pictureMedia.pictureMedias" :srcset="srcsetItem(item.path)" :media="item.media">

        <source :srcset="srcset">
        <img :srcset="srcset" :alt="alt" :class="pictureClass" :style="pictureStyle"/>
    </picture>
</template>

<script>
    import { mapState } from 'vuex'
    export default{
        props: ['pathPic', 'alt', 'pictureClass', 'pictureStyle'],
        data(){
            return {
                root: "/static/picture",
                slash: "/",
            }
        },
        computed: {
            ...mapState(['pictureMedia']),
            isDataPresent(){
                return this.pathPic != null
            },
            srcset(){
                return this.root + this.pathPic
            },
        },
        methods:{
            srcsetItem(path){
                return this.root + path + this.pathPic
            }
        },
    }
</script>

<style>

</style>
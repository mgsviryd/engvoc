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
        props: ['pathPic', 'marker', 'alt', 'pictureClass', 'pictureStyle'],
        data(){
            return {
                slash: "/",
            }
        },
        computed: {
            ...mapState([
                'pictureMedia',
                'frontend',
            ]),

            root(){
                if(this.isDefaultPictureFileName(this.pathPic)){
                    return  "/static/picture"
                }else {
                    return  this.frontend.config['uploadResource'] + this.frontend.config['uploadPicture']
                }
            },

            isDataPresent(){
               return this.pathPic != null && this.marker != null
            },
            srcset(){
                return this.root + this.slash + this.markerPath
            },
            markerPath(){
                let split = this.pathPic.split(".")
                return split[0] + this.marker + "." + split[1]
            },
        },
        methods:{
            srcsetItem(path){
                return this.root + path + this.slash + this.markerPath
            },
            isDefaultPictureFileName(name){
                return this.pictureMedia.defaultPictureFileName === name
            },
        },
    }
</script>

<style>

</style>
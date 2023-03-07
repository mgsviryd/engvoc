<template>
    <div class="group-CategoryMainCentre">
        <nav class="navbar navbar-expand-lg navbar-light bg-light navbar-static-top border-top border-bottom border-secondary pb-5">
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarProductMainCentre"
                    aria-controls="navbarProductMainCentre" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse flex-column " id="navbarProductMainCentre">
                <ul class="navbar-nav d-flex flex-row flex-wrap justify-content-center align-items-center">
                    <li v-for="(picture,index) in pictures" class="nav-item hover-zoomin mx-1 mb-1 shadow">
                        <a class="btn btn-light btn-md btn-block border border-white px-0 py-0" role="button" href="#">
                            <div>
                            <picture-static :pathPic="picture" :marker="marker" :alt="alt"
                                            :pictureClass="pictureClass" :pictureStyle="pictureStyle"/>
                            </div>
                            Группа {{index}}</a>
                    </li>
                </ul>
            </div>
        </nav>
    </div>
</template>

<script>
    import PictureStatic from 'components/picture/PictureStatic.vue'
    export default {
        props: ['pictures', 'marker', 'alt', 'pictureClass', 'pictureStyle'],
        components: {
            PictureStatic
        },
    }

</script>

<style>

</style>
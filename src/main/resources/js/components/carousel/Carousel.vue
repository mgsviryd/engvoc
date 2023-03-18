<template>
    <div class="carousel-Carousel">
        <div id="carouselExampleIndicators" class="carousel slide carousel-fade" data-ride="carousel">
            <ol class="carousel-indicators">
                <li v-for="(picture, index) in pictures" data-target="#carouselExampleIndicators" :data-slide-to="index"
                    :class="index == 0 ? 'active' : ''"></li>
            </ol>
            <div class="carousel-inner">
                <div v-for="(picture, index) in pictures" class="carousel-item" :class="index == 0 ? 'active' : ''"
                     :data-interval="index == 0 ? 6000 : 4000">
                    <picture-static :pathPic="picture" :marker="marker" :alt="alt"
                                    :pictureClass="pictureClassMethod(index)" :pictureStyle="pictureStyle"/>
                </div>
            </div>

            <a class="carousel-control-prev" href="#carouselExampleIndicators" role="button" data-slide="prev">
                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                <span class="sr-only">Previous</span>
            </a>
            <a class="carousel-control-next" href="#carouselExampleIndicators" role="button" data-slide="next">
                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                <span class="sr-only">Next</span>
            </a>
        </div>
    </div>
</template>

<script>
    import PictureStatic from '../../components/picture/PictureStatic.vue'
    export default {
        components: {
            PictureStatic,
        },
        data() {
            return {
                pictures: [
                    '/banner/banner-maz.jpg',
                    '/banner/banner-dostavka.jpg',
                    '/banner/banner-lp-eco.jpg',
                    '/banner/banner-rexenergo5.jpg',
                    '/banner/banner-sklad-era-tex.jpg'
                ],
                marker: "",
                alt: "...", //TODO
                pictureClass: "d-block w-100 active",
                pictureStyle: ""
            }
        },
        methods:{
            pictureClassMethod(index){
                if (index == 0){
                    return this.pictureClass + ' active'
                }else {
                    return this.pictureClass
                }
            }
        }
    }

</script>

<style>
    .carousel-Carousel .carousel-indicators li {
        height: 15px;
        width: 15px;
        border: 5px solid white;
        background-color: grey;
        margin-left: 10px;
        margin-right: 10px;
    }

    .carousel-Carousel .carousel-indicators .active {
        border: 5px solid white;
        background-color: grey;
        margin-left: 10px;
        margin-right: 10px;
    }

    .carousel-Carousel .carousel-control-prev-icon {
        height: 50px;
        width: 50px;
    }

    .carousel-Carousel .carousel-control-next-icon {
        height: 50px;
        width: 50px;
    }

    @media (max-width: 576px) {
        .carousel-Carousel .carousel-indicators li {
            height: 5px;
            width: 5px;
            border: 2px solid white;
            background-color: grey;
            margin-left: 10px;
            margin-right: 10px;
        }

        .carousel-Carousel .carousel-indicators .active {
            border: 2px solid white;
            background-color: grey;
            margin-left: 10px;
            margin-right: 10px;
        }

        .carousel-Carousel .carousel-control-prev-icon {
            height: 20px;
            width: 20px;
        }

        .carousel-Carousel .carousel-control-next-icon {
            height: 20px;
            width: 20px;
        }
    }

</style>
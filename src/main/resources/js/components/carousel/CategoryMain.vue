<template>
    <div class="carousel-CategoryMain">
        <div id="carouselProductMainIndicators"
             class="carousel slide carousel-fade "
             data-ride="carousel"
             data-interval="false"
             data-wrap="false">
            <ol class="carousel-indicators">
                <li data-target="#carouselProductMainIndicators" data-slide-to="0" class="active"></li>
                <li data-target="#carouselProductMainIndicators" data-slide-to="1"></li>
                <li data-target="#carouselProductMainIndicators" data-slide-to="2"></li>
            </ol>
            <div class="carousel-inner">
                <div class="carousel-item active">
                    <category-main-group
                            :pictures="pictures0"
                            :marker="marker"
                            :alt="alt"
                            :pictureClass="pictureClass"
                            :pictureStyle="pictureStyle">
                    </category-main-group>
                </div>
                <div class="carousel-item">
                    <category-main-group
                            :pictures="pictures1"
                            :marker="marker"
                            :alt="alt"
                            :pictureClass="pictureClass"
                            :pictureStyle="pictureStyle">
                    </category-main-group>
                </div>
                <div class="carousel-item">
                    <category-main-group
                            :pictures="pictures2"
                            :marker="marker"
                            :alt="alt"
                            :pictureClass="pictureClass"
                            :pictureStyle="pictureStyle">
                    </category-main-group>
                </div>
            </div>

            <a class="carousel-control-prev" href="#carouselProductMainIndicators" role="button" data-slide="prev">
                <span class="fas fa-chevron-circle-left fa-2x" aria-hidden="true"></span>
                <span class="sr-only">Previous</span>
            </a>
            <a class="carousel-control-next" href="#carouselProductMainIndicators" role="button" data-slide="next">
                <span class="fas fa-chevron-circle-right fa-2x" aria-hidden="true"></span>
                <span class="sr-only">Next</span>
            </a>
        </div>
    </div>
</template>

<script>
    import CategoryMainGroup from 'components/group/CategoryMainGroup.vue'
    export default {
        components: {
            CategoryMainGroup
        },
        data() {
            return {
                pictures0: [
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                ],
                pictures1: [
                    '/category/reles.jpg',
                    '/category/reles.jpg',
                    '/category/reles.jpg',
                    '/category/reles.jpg',
                    '/category/reles.jpg',
                    '/category/reles.jpg',
                    '/category/reles.jpg',
                    '/category/reles.jpg',
                    '/category/reles.jpg',
                    '/category/reles.jpg',
                ],
                pictures2: [
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                    '/category/izmerit.jpg',
                ],
                marker: "",
                alt: "...", //TODO
                pictureClass: "",
                pictureStyle: "width:220px;"
            }
        },
    }

</script>

<style>
    .carousel-CategoryMain .carousel-indicators li {
        height: 15px;
        width: 15px;
        border: 5px solid white;
        background-color: grey;
        margin-bottom: -5px;
        margin-left: 10px;
        margin-right: 10px;
    }

    .carousel-CategoryMain .carousel-indicators .active {
        border: 5px solid white;
        background-color: grey;
        margin-bottom: -5px;
        margin-left: 10px;
        margin-right: 10px;
    }

    .carousel-CategoryMain .carousel-control-prev {
        display: flex;
        height: 81%;
        width: 4%;
    }

    .carousel-CategoryMain .carousel-control-next {
        display: flex;
        height: 81%;
        width: 4%;
    }

    .carousel-CategoryMain .fa-chevron-circle-right {
        color: grey;
    }

    .carousel-CategoryMain .fa-chevron-circle-left {
        color: grey;
    }

    @media (max-width: 576px) {
        .carousel-CategoryMain .carousel-indicators li {
            border: 2px solid white;
            background-color: grey;
            margin-left: 10px;
            margin-right: 10px;
        }

        .carousel-CategoryMain .carousel-indicators .active {
            border: 2px solid white;
            background-color: grey;
            margin-left: 10px;
            margin-right: 10px;
        }

        .carousel-CategoryMain .carousel-control-prev {
            display: flex;
            height: 100%;
            width: 5%;
        }

        .carousel-CategoryMain .carousel-control-next {
            display: flex;
            height: 100%;
            width: 5%;
        }

        .carousel-CategoryMain .fa-chevron-circle-right {
            margin-right: 40px;
        }

        .carousel-CategoryMain .fa-chevron-circle-left {
            margin-left: 40px;
        }
    }
</style>
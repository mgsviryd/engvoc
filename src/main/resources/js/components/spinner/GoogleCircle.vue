<template>
    <div class="googleCircle" :style="style"></div>
</template>

<script>
    export default{
        props: [
          'width',
          'height',
        ],
        data(){
            return {}
        },
        computed: {
            style() {
                return {
                    'width': this.width,
                    'height': this.height,
                }
            }
        },
    }
</script>

<style scoped>
    .googleCircle {
        border: 4px rgba(0, 0, 0, 0.25) solid;
        border-top: 4px #008744 solid;
        border-right: 4px #0057e7 solid;
        border-bottom: 4px #d62d20 solid;
        border-radius: 50%;
        -webkit-animation: spin5 1s infinite linear;
        animation: spin5 1s infinite linear;
    }

    @-webkit-keyframes spin5 {
        from {
            -webkit-transform: rotate(0deg);
            transform: rotate(0deg);
        }
        to {
            -webkit-transform: rotate(359deg);
            transform: rotate(359deg);
        }
    }

    @keyframes spin5 {
        from {
            -webkit-transform: rotate(0deg);
            transform: rotate(0deg);
            -webkit-transform: rotate(0deg);
            transform: rotate(0deg);
        }
        to {
            -webkit-transform: rotate(359deg);
            transform: rotate(359deg);
            -webkit-transform: rotate(359deg);
            transform: rotate(359deg);
        }
    }
</style>
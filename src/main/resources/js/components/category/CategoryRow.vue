<template>
    <div class="row mx-2">
        <button class="btn btn-white dropdown-item rounded-0" @click.prevent.stop="goToCategory()">
            <div class="row my-1">
                <div class="col-1 d-flex align-items-center">
                    <div class="row no-gutters d-flex justify-content-center">
                        <i class="fas fa-angle-right text-muted"></i>
                    </div>
                </div>
                <div class="col-11 d-flex align-items-center">
                    <div class="row justify-content-left">
                        {{ category.name }}&emsp;&emsp;
                        <small class="text-muted">
                            товаров:&nbsp;
                            <span class="badge badge-white">{{category.productCount}}</span>
                        </small>
                    </div>
                </div>
            </div>
        </button>
    </div>
</template>

<script>
    export default {
        computed: {

        },
        props: ['category'],
        methods: {
            goToCategory(){
                this.$emit('goToCategory', this.category)
            },
        }
    }
</script>

<style scoped>
    .btn {
        background-color: white;
    }

    .btn:hover {
        background-color: #eaeaea;
    }
</style>
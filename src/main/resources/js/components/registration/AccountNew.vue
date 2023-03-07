<template>
    <div class="registration-account_new">
        <a href="/registration"
           class="btn btn-sm btn-outline-success mr-2 py-2 shadow-none"
           data-toggle="tooltip"
           data-placement="bottom"
           :title="tooltip"
        >
            <i class="fa fa-user fa-1x" aria-hidden="true"></i>
        </a>
    </div>
</template>

<script>
    export default{
        props: ['tooltip'],
    }
</script>

<style>

</style>
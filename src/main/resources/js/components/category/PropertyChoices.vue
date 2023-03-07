<template>
    <div v-if="showComponent">
        <div class="container px-4">
            <div v-for="(propertyChoice, index) in propertyChoices" class="py-2">
                <div class="mb-1">
                    <small><b>{{propertyChoice.property.name}}</b></small>
                </div>
                <div v-if="isStringPropertyType(propertyChoice.property) && isMultiselect(propertyChoice.choices)">
                    <multiselect
                            v-model="activeOptions[index]"
                            :options="stringOptions(propertyChoice.choices)"
                            :multiple="true"
                            :close-on-select="false"
                            :clear-on-select="false"
                            :preserve-search="true"
                            :show-labels="false"
                            :height="300"
                            :placeholder="placeHolder(propertyChoice.choices)"
                            @select="onSelect($event, index)"
                            @remove="onRemove($event, index)"
                    >
                        <!--<template slot="selection" slot-scope="{ values, search, isOpen }"><span class="multiselect__single" v-if="values.length &amp;&amp; !isOpen">Выбрано: {{ values.length }}</span></template>-->
                    </multiselect>
                </div>
                <div v-if="isStringPropertyType(propertyChoice.property) && !isMultiselect(propertyChoice.choices)">
                    <div v-for="(choice, index1) in stringOptions(propertyChoice.choices)" class="text-muted">
                        <div class="checkbox">
                            <label class="my-0" style="font-size: .8em">
                                <input type="checkbox" :checked="isActiveChoice(choice, index)"
                                       @click="addOrRemoveActiveOption(choice, index)">
                                <span class="cr text-success"><i class="cr-icon fa fa-check"></i></span>
                                {{choice}}
                            </label>
                        </div>
                    </div>
                </div>
                <div v-if="isDoublePropertyType(propertyChoice.property) && isMultiselect(propertyChoice.choices)"
                     class="mt-4 pt-2">
                    <div class="mx-3">
                    <vue-slider
                            v-model="activeMinMaxOptions[index]"
                            :data="doubleOptions(propertyChoice.choices)"
                            :enable-cross="false"
                            :tooltip="'always'"
                            :change="onChange(index)"
                    >
                    </vue-slider>
                    </div>
                    <multiselect
                            v-model="activeOptions[index]"
                            :options="doubleOptions(propertyChoice.choices)"
                            :multiple="true"
                            :close-on-select="false"
                            :clear-on-select="false"
                            :preserve-search="true"
                            :show-labels="false"
                            :height="300"
                            :placeholder="placeHolder(propertyChoice.choices)"
                            @select="onSelect($event, index)"
                            @remove="onRemove($event, index)"
                    >
                    </multiselect>
                </div>
                <div v-if="isDoublePropertyType(propertyChoice.property) && !isMultiselect(propertyChoice.choices)">
                    <div v-for="(choice, index1) in doubleOptions(propertyChoice.choices)" class="text-muted">
                        <div class="checkbox">
                            <label class="my-0" style="font-size: .8em">
                                <input type="checkbox" :checked="isActiveChoice(choice, index)"
                                       @click="addOrRemoveActiveOption(choice, index)">
                                <span class="cr text-success"><i class="cr-icon fa fa-check"></i></span>
                                {{choice}}
                            </label>
                        </div>
                    </div>
                </div>
                <div v-if="isIntegerPropertyType(propertyChoice.property) && isMultiselect(propertyChoice.choices)">
                    <multiselect
                            v-model="activeOptions[index]"
                            :options="integerOptions(propertyChoice.choices)"
                            :multiple="true"
                            :close-on-select="false"
                            :clear-on-select="false"
                            :preserve-search="true"
                            :show-labels="false"
                            :height="300"
                            :placeholder="placeHolder(propertyChoice.choices)"
                            @select="onSelect($event, index)"
                            @remove="onRemove($event, index)"
                    >
                    </multiselect>
                </div>
                <div v-if="isIntegerPropertyType(propertyChoice.property) && !isMultiselect(propertyChoice.choices)">
                    <div v-for="(choice, index1) in integerOptions(propertyChoice.choices)" class="text-muted">
                        <div class="checkbox">
                            <label class="my-0" style="font-size: .8em">
                                <input type="checkbox" :checked="isActiveChoice(choice, index)"
                                       @click="addOrRemoveActiveOption(choice, index)">
                                <span class="cr text-success"><i class="cr-icon fa fa-check"></i></span>
                                {{choice}}
                            </label>
                        </div>
                    </div>
                </div>
                <div v-if="isBooleanPropertyType(propertyChoice.property) && isMultiselect(propertyChoice.choices)">
                    <multiselect
                            v-model="activeOptions[index]"
                            :options="booleanOptions(propertyChoice.choices)"
                            :multiple="true"
                            :close-on-select="false"
                            :clear-on-select="false"
                            :preserve-search="true"
                            :show-labels="false"
                            :height="300"
                            :placeholder="placeHolder(propertyChoice.choices)"
                            @select="onSelect($event, index)"
                            @remove="onRemove($event, index)"
                    >
                    </multiselect>
                </div>
                <div v-if="isBooleanPropertyType(propertyChoice.property) && !isMultiselect(propertyChoice.choices)">
                    <div v-for="(choice, index1) in booleanOptions(propertyChoice.choices)" class="text-muted">
                        <div class="checkbox">
                            <label class="my-0" style="font-size: .8em">
                                <input type="checkbox" :checked="isActiveChoice(choice, index)"
                                       @click="addOrRemoveActiveOption(choice, index)">
                                <span class="cr text-success"><i class="cr-icon fa fa-check"></i></span>
                                {{choice}}
                            </label>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <div v-if="flushActiveOptionsBar && !loading" v-on-clickaway="awayFlushActiveOptionsBar"
             class="row justify-content-center mx-0 border border-muted rounded-0 stickyBottom">
            <a type="button" class="col align-self-center text-center bg-danger text-white px-0 py-3"
               @click="flushActiveOptions()">
                {{lang.map.flushFilter}}
            </a>
        </div>
        <div v-else-if="!flushActiveOptionsBar && !loading"
             class="row justify-content-center mx-0 bg-light border-top border-bottom border-muted rounded-0 stickyBottom">
            <a type="button" :class="hasAnyOptions && !debounced? 'col-9':'col'"
               class="align-self-center bg-light rounded-0 text-center py-3 px-0"
               @click="scrollToCatalogueNavigation()">
                <i class="fas fa-chevron-circle-up text-muted"></i>&emsp;
                {{lang.map.findItems}}:&emsp;{{page.totalElements}}
            </a>
            <div v-if="hasAnyOptions && !debounced" class="col-3 align-self-center text-center px-0">
                <button type="button" class="btn btn-lg btn-light btn-block border-left border-muted rounded-0 py-2"
                        @click="flushActiveOptionsBarTrue()">
                    <i class="fas fa-times text-muted"></i>
                </button>
            </div>
        </div>
        <div v-else="loading" class="row justify-content-center mx-0 border border-muted rounded-0 stickyBottom">
            <div class="col px-0">
                <div class="progress" style="border-radius: 0px; height: 56px;">
                    <div class="progress-bar progress-bar-striped progress-bar-animated bg-warning"
                         role="progressbar"
                         aria-valuenow="100"
                         aria-valuemin="0"
                         aria-valuemax="100"
                         style="width: 100%"></div>
                </div>
            </div>
        </div>

    </div>
</template>

<script>
    import {mapState} from 'vuex'
    import * as _ from 'lodash'
    import {mixin as clickaway} from 'vue-clickaway';
    import compare from "../../util/compare";

    export default{
        mixins: [clickaway],
        ...mapState([
            'scrollDuration',
            'scrollOptions',
        ]),
        data() {
            return {
                value: [1, 100],
                debounced: false,
                flushActiveOptionsBar: false,
                afterFetchDataForActiveOptions: false,
                afterFetchDataForActiveMinMaxOptions: false,
                showComponent: false,
                countMultiselect: 4,
                typeString: 'STRING',
                typeDouble: 'DOUBLE',
                typeInteger: 'INTEGER',
                typeBoolean: 'BOOLEAN',
                minMaxMark: 'min-max',
                pathChoices: [],
                minMaxOptions: [],
                activeOptions: [],
                activeMinMaxOptions: [],
                excludedKeys: ['size', 'page', 'property', 'direction'],
            }
        },
        props: ['loading', 'page', 'propertyChoices'],
        watch: {
            $route: [
                'fetchData',
            ],
            propertyChoices(newVal){
                this.fetchData()
            },
            activeOptions: {
                handler: function (newVal, oldVal) {
                    if (this.afterFetchDataForActiveOptions) {
                        this.afterFetchDataForActiveOptions = false
                        return
                    }
                    if (this.showComponent) {
                        this.debounced = true
                        this.goTo(newVal, this.activeMinMaxOptions)
                    }
                },
                deep: true
            },

            activeMinMaxOptions: {
                handler: function (newVal, oldVal) {
                    if (this.afterFetchDataForActiveMinMaxOptions) {
                        this.afterFetchDataForActiveMinMaxOptions = false
                        return
                    }
                    if (this.showComponent) {
                        this.debounced = true
                        this.goTo(this.activeOptions, newVal)
                    }
                },
                deep: true
            },
        },
        created() {
            this.fetchData()
        },
        computed: {
            ...mapState([
                'lang',
            ]),
            hasActiveOptions(){
                for (let i = 0; i < this.activeOptions.length; i++) {
                    if (this.activeOptions[i].length != 0) {
                        return true
                    }
                }
                return false
            },
            hasChangedMinMaxActiveOptions(){
                for (let i = 0; i < this.activeMinMaxOptions.length; i++) {
                    if (this.activeMinMaxOptions[i].length != 0
                        && this.isActiveMinMaxOptionsChanged(this.activeMinMaxOptions, i)) {
                        return true
                    }
                }
                return false
            },
            hasAnyOptions(){
                return this.hasActiveOptions || this.hasChangedMinMaxActiveOptions
            },
        },
        methods: {
            fetchData(){
                this.showComponent = false
                this.resetActiveOptions()
                this.resetActiveMinMaxOptions()
                this.resetMinMaxOptions()
                this.fillPathChoices()
                this.fillActiveChoicesUsingRouterQuery()
                this.fillMinMaxOptions()
                this.setMinMaxOptionsAsActiveIfAbsent()
                this.afterFetchDataForActiveOptions = true
                this.afterFetchDataForActiveMinMaxOptions = true
                this.showComponent = true
            },
            resetActiveOptions(){
                let activeOptions = []
                for (let propertyChoice in this.propertyChoices) {
                    activeOptions.push([])
                }
                this.activeOptions = activeOptions
            },
            resetActiveMinMaxOptions(){
                let activeMinMaxOptions = []
                for (let propertyChoice in this.propertyChoices) {
                    activeMinMaxOptions.push([])
                }
                this.activeMinMaxOptions = activeMinMaxOptions
            },
            removeActiveOptions(index){
                this.activeOptions[index] = []
            },
            resetMinMaxOptions(){
                let minMaxOptions = []
                for (let propertyChoice in this.propertyChoices) {
                    minMaxOptions.push([])
                }
                this.minMaxOptions = minMaxOptions
            },

            fillMinMaxOptions(){
                for (let i = 0; i < this.propertyChoices.length; i++) {
                    let choices = this.propertyChoices[i].choices
                    if (this.propertyChoices[i].property.type == "DOUBLE") {
                        if (choices.length > 0) {
                            let options = choices.map((x) => {
                                return parseFloat(x.doubleData)
                            })
                            options = this.sortNatural(options, "DOUBLE")
                            this.minMaxOptions[i] = [options[0], options[options.length - 1]]
                        }
                    }
                    if (this.propertyChoices[i].property.type == "INTEGER") {
                        if (choices.length > 0) {
                            let options = choices.map((x) => {
                                return parseInt(x.integerData, 10)
                            })
                            options = this.sortNatural(options, "INTEGER")
                            this.minMaxOptions[i] = [options[0], options[options.length - 1]]
                        }
                    }
                }
            },

            setMinMaxOptionsAsActiveIfAbsent(){
                for (let i = 0; i < this.minMaxOptions.length; i++) {
                    if (this.activeMinMaxOptions[i].length <= 0) {
                        this.activeMinMaxOptions[i] = this.minMaxOptions[i]
                    }
                }
            },
            setMinMaxOptionsAsActive(){
                for (let i = 0; i < this.minMaxOptions.length; i++) {
                    this.activeMinMaxOptions[i] = this.minMaxOptions[i]
                }
            },

            fillPathChoices(){
                for (let i = 0; i < this.propertyChoices.length; i++) {
                    this.pathChoices[i] = this.propertyChoices[i].choices.map((x) => {
                        return decodeURIComponent(x.path)
                    })
                }
            },

            fillActiveChoicesUsingRouterQuery(){
                let queries = Object.keys(this.$route.query)
                queries = this.removeExcludedQueries(queries)
                for (let i = 0; i < queries.length; i++) {
                    let choicePaths = this.$route.query[queries[i]]
                    let index = this.getPropertyChoiceIndexByPropertyPath(queries[i])
                    if (index == -1) continue
                    if (this.isMinMaxChoice(choicePaths)) {
                        this.addChoicesToActiveMinMaxOptions(choicePaths, index)
                    } else {
                        this.addChoicesToActiveOptions(choicePaths, index)
                    }
                }
            },

            removeExcludedQueries(queries){
                return queries.filter((value, index, arr) => {
                    return this.excludedKeys.indexOf(value) == -1;
                })
            },

            getPropertyChoiceIndexByPropertyPath(propertyPath){
                for (let i = 0; i < this.propertyChoices.length; i++) {
                    if (this.propertyChoices[i].property.path == propertyPath) {
                        return i
                    }
                }
                return -1
            },

            addChoicesToActiveOptions(choicePaths, index){
                if (Array.isArray(choicePaths)) {
                    for (let i = 0; i < choicePaths.length; i++) {
                        let choice = this.getChoiceByPath(choicePaths[i], index)
                        if (choice == null) continue
                        this.activeOptions[index].push(choice)
                    }
                } else {
                    let choice = this.getChoiceByPath(choicePaths, index)
                    if (choice == null) return
                    this.activeOptions[index].push(choice)
                }
            },

            addChoicesToActiveMinMaxOptions(optionPaths, index){
                let options = []
                for (let i = 0; i < optionPaths.length; i++) {
                    let path = optionPaths[i]
                    if (this.isMinMaxMark(path)) continue
                    let option = null
                    if (this.propertyChoices[index].property.type == "DOUBLE") {
                        option = parseFloat(path)
                    }
                    if (this.propertyChoices[index].property.type == "INTEGER") {
                        option = parseInt(path)

                    }
                    options.push(option)
                }
                if (this.propertyChoices[index].property.type == "DOUBLE") {
                    this.activeMinMaxOptions[index] = this.sortNatural(options, "DOUBLE")
                }
                if (this.propertyChoices[index].property.type == "INTEGER") {
                    this.activeMinMaxOptions[index] = this.sortNatural(options, "INTEGER")
                }
            },

            isMinMaxMark(path){
                return path == this.minMaxMark
            },

            getChoiceByPath(path, index){
                let i = this.pathChoices[index].indexOf(path)
                if (i == -1) return null
                return this.getChoiceByType(i, index)
            },

            getChoiceByType(i, index){
                switch (this.propertyChoices[index].property.type) {
                    case "STRING":
                        return this.propertyChoices[index].choices[i].name
                    case "DOUBLE":
                        return this.propertyChoices[index].choices[i].doubleData
                    case "INTEGER":
                        return this.propertyChoices[index].choices[i].integerData
                    case "BOOLEAN":
                        return this.propertyChoices[index].choices[i].booleanData
                }
            },

            sortNatural(array, type){
                switch (type) {
                    case "STRING":
                        return array.sort((a, b) => {
                            return a.localeCompare(b)
                        })
                    case "DOUBLE":
                        return array.sort(function (a, b) {
                            return compare.compareNumberNatural(a, b)
                        })
                    case "INTEGER":
                        return array.sort(function (a, b) {
                            return compare.compareNumberNatural(a, b)
                        })
                    case "BOOLEAN":
                        return array.sort(function (a, b) {
                            return compare.compareBoolean(a, b)
                        })
                }
            },

            addOrRemoveActiveOption(choice, index){
                let i = this.activeOptions[index].indexOf(choice)
                if (i == -1) {
                    this.activeOptions[index].push(choice)
                } else {
                    this.activeOptions[index].splice(i, 1)
                }
            },
            isActiveChoice(choice, index){
                if (this.activeOptions[index].indexOf(choice) != -1) {
                    return 'checked'
                }
                return ''
            },

            isMinMaxChoice(choices){
                return choices.includes(this.minMaxMark)
            },

            isMultiselect(options){
                return options.length > this.countMultiselect
            },

            isStringPropertyType(property){
                return property.type == this.typeString
            },
            isDoublePropertyType(property){
                return property.type == this.typeDouble
            },
            isIntegerPropertyType(property){
                return property.type == this.typeInteger
            },
            isBooleanPropertyType(property){
                return property.type == this.typeBoolean
            },

            onSelect (event, index) {
                this.setDefaultMinMaxOptions(index)
            },

            onRemove (event, index) {
                this.setDefaultMinMaxOptions(index)
            },

            onChange(index){

            },

            stringOptions(options){
                return this.sortNatural(options.map((x) => x.name), "STRING")
            },
            integerOptions(options){
                return this.sortNatural(options.map((x) => x.integerData), "INTEGER")
            },
            doubleOptions(options){
                return this.sortNatural(options.map((x) => x.doubleData), "DOUBLE")
            },
            booleanOptions(options){
                return this.sortNatural(options.map((x) => x.booleanData), "BOOLEAN")
            },
            placeHolder(options){
                return 'Все ' + options.length + ' вариантов'
            },
            setDefaultMinMaxOptions(index){
                this.activeMinMaxOptions[index] = this.minMaxOptions[index]
            },
            getStringQueryUsingActiveOptions(activeOptions, activeMinMaxOptions){
                let query = ''
                let and = '&'
                for (let i = 0; i < activeOptions.length; i++) {
                    let path = null
                    let key = this.propertyChoices[i].property.path
                    if (this.isActiveMinMaxOptionsPresent(activeMinMaxOptions, i)
                        && this.isMinMaxOptionsNotSameValue(i)
                        && this.isActiveMinMaxOptionsChanged(activeMinMaxOptions, i)) {
                        query += this.getMinMaxPath(activeMinMaxOptions, key, i)
                    } else {
                        for (let k = 0; k < activeOptions[i].length; k++) {
                            path = this.getPath(activeOptions[i][k], i)
                            if (path == null) continue
                            query += key + '=' + path + and
                        }
                    }
                }
                return query
            },
            isActiveMinMaxOptionsChanged(activeMinMaxOptions, index){
                return this.minMaxOptions[index][0] != activeMinMaxOptions[index][0]
                    || this.minMaxOptions[index][1] != activeMinMaxOptions[index][1]
            },
            isActiveMinMaxOptionsPresent(activeMinMaxOptions, index){
                return activeMinMaxOptions[index].length > 0
            },
            isMinMaxOptionsNotSameValue(index){
                return this.minMaxOptions[index][0] != this.minMaxOptions[index][1]
            },
            getStringQueryUsingExcludedKeys(){
                let query = ''
                let and = '&'
                let queryKeys = Object.keys(this.$route.query)
                for (let i = 0; i < this.excludedKeys.length; i++) {
                    let key = this.excludedKeys[i]
                    if (key != 'page' && queryKeys.includes(key)) {
                        let path = this.$route.query[key]
                        query += key + '=' + path + and
                    }
                }
                return query
            },

            getMinMaxPath(activeMinMaxOptions, key, i){
                let and = '&'
                let path = key + '=' + this.minMaxMark + and
                for (let k = 0; k < activeMinMaxOptions[i].length; k++) {
                    path += key + '=' + decodeURIComponent(activeMinMaxOptions[i][k]) + and
                }
                return path
            },

            getPath(option, index){
                let i = -1;
                switch (this.propertyChoices[index].property.type) {
                    case "STRING":
                        i = this.propertyChoices[index].choices.map((x) => {
                            return x.name
                        }).indexOf(option)
                        break
                    case "DOUBLE":
                        i = this.propertyChoices[index].choices.map((x) => {
                            return x.doubleData
                        }).indexOf(option)
                        break
                    case "INTEGER":
                        i = this.propertyChoices[index].choices.map((x) => {
                            return x.integerData
                        }).indexOf(option)
                        break
                    case "BOOLEAN":
                        i = this.propertyChoices[index].choices.map((x) => {
                            return x.booleanData
                        }).indexOf(option)
                        break
                }
                if (i != -1) {
                    return this.propertyChoices[index].choices[i].path
                } else {
                    return null
                }
            },

            goTo: _.debounce(function (activeOptions, activeMinMaxOptions) {
                let query = '?'
                let queryActiveOptions = this.getStringQueryUsingActiveOptions(activeOptions, activeMinMaxOptions)
                let queryExcludedKeys = this.getStringQueryUsingExcludedKeys()
                query += queryActiveOptions + queryExcludedKeys
                let path = this.$route.path + query
                this.debounced = false
                this.$router.push(path).catch(err => {
                })
            }, 1000),

            flushActiveOptionsBarTrue(){
                this.flushActiveOptionsBar = true
            },
            awayFlushActiveOptionsBar(){
                this.flushActiveOptionsBar = false
            },
            flushActiveOptions(){
                this.resetActiveOptions()
                this.setMinMaxOptionsAsActive()
                this.awayFlushActiveOptionsBar()
            },
            scrollToCatalogueNavigation(){
                this.$scrollTo('#catalogue-navigation', this.scrollDuration, this.scrollOptions)
            },
        }
    }
</script>

<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>
<style scoped>

    .stickyBottom {
        position: -webkit-sticky;
        position: sticky;
        bottom: 0;
        z-index: 10;
    }

    /* Checkbox boolean */
    input[type="checkbox"].checkboxBoolean {
        font-size: 10px;
        -webkit-appearance: none;
        -moz-appearance: none;
        appearance: none;
        width: 3.5em;
        height: 1.5em;
        background: #ddd;
        border-radius: 3em;
        position: relative;
        cursor: pointer;
        outline: none;
        -webkit-transition: all .2s ease-in-out;
        transition: all .2s ease-in-out;
    }

    input[type="checkbox"].checkboxBoolean:checked {
        background: #0ebeff;
    }

    input[type="checkbox"].checkboxBoolean:after {
        position: absolute;
        content: "";
        width: 1.5em;
        height: 1.5em;
        border-radius: 50%;
        background: #fff;
        -webkit-box-shadow: 0 0 .25em rgba(0, 0, 0, .3);
        box-shadow: 0 0 .25em rgba(0, 0, 0, .3);
        -webkit-transform: scale(.7);
        transform: scale(.7);
        left: 0;
        -webkit-transition: all .2s ease-in-out;
        transition: all .2s ease-in-out;
    }

    input[type="checkbox"].checkboxBoolean:checked:after {
        left: calc(100% - 1.5em);
    }

    /* Checkbox multiselect*/
    .checkbox label:after,
    .radio label:after {
        content: '';
        display: table;
        clear: both;
    }

    .checkbox .cr,
    .radio .cr {
        position: relative;
        display: inline-block;
        border: 1px solid #a9a9a9;
        border-radius: .25em;
        width: 1.3em;
        height: 1.3em;
        float: left;
        margin-right: .5em;
    }

    .radio .cr {
        border-radius: 50%;
    }

    .checkbox .cr .cr-icon,
    .radio .cr .cr-icon {
        position: absolute;
        font-size: .8em;
        line-height: 0;
        top: 50%;
        left: 20%;
    }

    .radio .cr .cr-icon {
        margin-left: 0.04em;
    }

    .checkbox label input[type="checkbox"],
    .radio label input[type="radio"] {
        display: none;
    }

    .checkbox label input[type="checkbox"] + .cr > .cr-icon,
    .radio label input[type="radio"] + .cr > .cr-icon {
        transform: scale(3) rotateZ(-20deg);
        opacity: 0;
        transition: all .3s ease-in;
    }

    .checkbox label input[type="checkbox"]:checked + .cr > .cr-icon,
    .radio label input[type="radio"]:checked + .cr > .cr-icon {
        transform: scale(1) rotateZ(0deg);
        opacity: 1;
    }

    .checkbox label input[type="checkbox"]:disabled + .cr,
    .radio label input[type="radio"]:disabled + .cr {
        opacity: .5;
    }

</style>
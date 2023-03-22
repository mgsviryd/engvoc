<template>
  <div class="dictionary-nav- btn-group-vertical d-inline-block">
    <button class="btn btn-primary text-left rounded-0  border-1 border-secondary" data-toggle="collapse" :href="hash+getCollapseForDB()"
            role="button"
            aria-expanded="false"
            :aria-controls="getCollapseForDB()">
      <span class="st-text-shift">{{ lang.map.db }}</span>
      <span class="st-right badge badge-light badge-pill">{{ dbDictionaries.length }}</span>
    </button>
    <div class="collapse" :id="getCollapseForDB()">
      <button v-for="(d,i) in dbDictionaries"
              :key="`A-${i}`"
              class="btn btn-primary text-left rounded-0  border-1 border-secondary" role="button"
              @click="parentLoadDBDictionaryWithInx(i)"
      >
        <span class="st-text-shift">{{ d.name }}</span>
      </button>
    </div>

    <button class="btn btn-primary text-left rounded-0 m-0  border-1 border-secondary" data-toggle="collapse" :href="hash+getCollapseForUpload()"
            role="button"
            aria-expanded="false"
            :aria-controls="getCollapseForUpload()">
      <span class="st-text-shift">{{ lang.map.upload }}</span>
      <span class="st-right badge badge-light badge-pill">{{ uploadDictionaries.length }}</span>
    </button>
    <div class="collapse" :id="getCollapseForUpload()">
      <div v-for="(f,i) in uploadUniqueFilenames"
           :key="`A-${i}`"
           class="btn-group-vertical d-block">
        <button class="btn btn-warning text-left rounded-0 m-0  border-1 border-secondary" data-toggle="collapse"
                :href="hash+getCollapseForFilenames(i)" role="button"
                aria-expanded="false"
                :aria-controls="getCollapseForFilenames(i)">
          <span class="st-text-shift">{{ f.name }}</span>
          <span class="st-right badge badge-light badge-pill">{{ getCountUploadDictionariesByFilename(f) }}</span>
        </button>
        <div class="collapse" :id="getCollapseForFilenames(i)">


          <div v-for="(d,ii) in getUploadDictionariesByFilename(f)"
               :key="`A-${ii}`"
               class="btn-group-vertical d-block">
            <button class="btn btn-outline-secondary  text-left rounded-0  border-1 border-secondary" role="button"
            @click="parentLoadUploadDictionaryWithInx(ii)"
            >
              <span>{{ d.name }}</span>
            </button>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {mapState} from "vuex";
import compare from "../../../util/compare"
import json from "../../../util/json"

export default {
  props: ['inst'],
  computed: {
    ...mapState([
      'cards',
      'lang',
    ]),
    uploadUniqueFilenames() {
      let unique = json.getUniqueByProperties(this.uploadFilenames, "name", "date")
      return unique.sort((a, b) => compare.compareDateReverse(a, b, "date"))
    },
    uploadFilenames() {
      return this.cards.upload.filenames
    },
    uploadDictionaries() {
      return this.cards.upload.dictionaries
    },
    dbDictionaries() {
      return this.cards.db.dictionaries
    },
  },
  data() {
    return {
      hash: "#"
    }
  },
  methods: {
    fetchData() {

    },
    getCountUploadDictionariesByFilename(filename) {
      return this.getUploadDictionariesByFilename(filename).length
    },
    getUploadDictionariesByFilename(filename) {
      return this.uploadFilenames.map((item, i) => json.isEqualByProperties(item, filename, "name", "date") ? this.uploadDictionaries[i] : null).filter((d) => d !== null)
    },
    getCollapseForDB() {
      return this.inst + "collapseDB"
    },
    getCollapseForUpload() {
      return this.inst + "collapseUpload"
    },
    getCollapseForFilenames(i) {
      return this.inst + "collapseFilenames" + i
    },
    parentLoadDBDictionaryWithInx(i){
      return this.$emit('loadDictionary', i, "db", this.inst)
    },
    parentLoadUploadDictionaryWithInx(i){
      return this.$emit('loadDictionary', i, "upload", this.inst)
    },
  },
}
</script>

<style scoped>
.dictionary-nav- {
  height: 550px;
  overflow-y: auto;
}

.st-text-shift {
  overflow-wrap: anywhere;
}

.st-text-shift:hover {

}

.st-right {
  float: right
}

i {
  float: right;
}
</style>
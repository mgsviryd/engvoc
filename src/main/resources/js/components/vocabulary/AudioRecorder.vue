<template>
  <b-row :id="ids.id"
         :ref="ids.id"
         class="pb-1" no-gutters>
    <b-overlay :show="showOverlay" no-wrap></b-overlay>
    <b-col :id="ids.col"
           :ref="ids.col"
           class=""
           sm="2"
    >
      <b-row class="d-flex flex-column"
             no-gutters>
        <b-col>
          <b-row no-gutters>
            <b-col :style="{height: `${24}px`}" cols="6">
              <audio v-if="newAudioURL && !showOverlay"
                     :id="ids.audio"
                     :ref="ids.audio"
                     crossorigin
                     playsinline
                     preload="metadata"
              >
                <source :src="newAudioURL" :type="newAudio.mediaType"/>
              </audio>
              <div v-if="isUpdatable && !newAudioURL">
                <i
                    :class="hold?'fa-beat-fade text-danger':'text-primary'"
                    :style="{width: `${24}px`, height: `${24}px`, cursor: 'pointer'}"
                    class="fa-solid fa-microphone fa-lg d-flex justify-content-center align-items-center"
                    @mousedown="tryRecord()"
                    @mouseup="tryStop()"
                >
                </i>
              </div>
              <div v-else>
                <b-button
                    :style="{width: `${24}px`, height: `${24}px`}"
                    :variant="'outline-primary'"
                    class="border-0 shadow-none p-0 m-0"
                    size="sm"
                    @click.prevent.stop="playStopAudio()"
                >
                  <i v-if="play" class="fa-solid fa-pause fa-lg align-self-center"></i>
                  <i v-else class="fa-solid fa-play fa-lg align-self-center"></i>
                </b-button>
              </div>
            </b-col>
            <b-col :style="{height: `${24}px`}" cols="6">
              <small v-if="isUpdatable && !newAudioURL"><small>
                <span>{{ getMinutes(count) + ':' + getSeconds(count) }}</span>
              </small></small>
              <div v-else-if="isUpdatable">
                <b-button v-if="isAuto"
                          :class="isCannotReload?'no-cursor':''"
                          :disabled="isCannotReload"
                          :style="{width: `${20}px`, height: `${20}px`}"
                          class="border-0 shadow-none p-0"
                          size="sm"
                          variant="transparent"
                          @click.prevent.stop="reloadAuto()"
                >
                  <i :class="isCannotReload?'text-muted':'text-primary'"
                     class="fa-solid fa-rotate-right"
                  ></i>
                </b-button>
                <b-button v-else
                          :style="{width: `${20}px`, height: `${20}px`}"
                          class="border-0 shadow-none p-0"
                          size="sm"
                          variant="transparent"
                          @click.prevent.stop="newAudio.blob = null"
                >
                  <i class="fa-solid fa-xmark text-danger"></i>
                </b-button>
              </div>
            </b-col>
          </b-row>
        </b-col>
        <b-col class="d-flex align-items-start" style="height: 20px">
          <b-button-group class="pt-1" size="sm">
            <b-button
                v-if="isUpdatable && isAutoActive"
                :class="[!activeWord?'no-cursor':'']"
                :disabled="!activeWord"
                :style="{width: `${24}px`, height: `${18}px`}"
                class="d-flex justify-content-center align-items-center shadow-none p-0"
                size="sm"
                variant="outline-danger"
                @click.prevent.stop="toggleAuto()"
            >
              <i v-if="isAuto" class="fa-solid fa-microphone fa-sm"></i>
              <i v-else class="fa-solid fa-a fa-sm"></i>
            </b-button>
            <b-button
                v-if="newAudioURL && isPlayer"
                :id="ids.sliders"
                :ref="ids.sliders"
                :class="[!newAudioURL?'no-cursor':'']"
                :disabled="!newAudioURL"
                :style="{width: `${24}px`, height: `${18}px`}"
                class="d-flex justify-content-center align-items-center shadow-none p-0"
                size="sm"
                variant="outline-secondary"
                @click.prevent.stop="isShowPlayer=!isShowPlayer"
            >
              <i class="fa-solid fa-sliders fa-sm"></i>
            </b-button>
            <b-popover v-if="isPlayer && isShowPlayer && newAudioURL && !showOverlay"
                       :placement="'right'"
                       :target="ids.col"
                       custom-class="no-popover-body"
                       no-fade
                       show
            >
              <b-row no-gutters>
                <vue-plyr :options="{}">
                  <audio controls crossorigin playsinline preload="metadata">
                    <source
                        :src="newAudioURL"
                        :type="newAudio.mediaType"
                    />
                  </audio>
                </vue-plyr>
              </b-row>
            </b-popover>
          </b-button-group>
        </b-col>
      </b-row>
      <GlobalEvents @mouseup="tryStop()"/>
    </b-col>
  </b-row>
</template>

<script>
import {mapGetters, mapState} from "vuex"
import * as _ from "lodash"
import FileJS from "../../util/file"

export default {
  props: [
    'id',
    'url',
    'updatable',
    'player',
    'showPlayer',
    'word',
    'locale',
  ],
  components: {},
  mounted() {
  },
  created() {
    this.fetchData()
  },
  beforeDestroy() {
  },
  computed: {
    ...mapState([
      'lang',
    ]),
    ...mapGetters([
      'isAudioGenerateLocalePresent',
    ]),
    ids() {
      return {
        id: this.prefixId(),
        col: this.prefixId() + 'col-id',
        sliders: this.prefixId() + 'sliders-id',
        audio: this.prefixId() + 'audio-id',
      }
    },
    watchIds() {
      return {}
    },
    isCannotReload() {
      return this.isBlank(this.activeWord) || this.activeWord === this.autoWord
    },
  },
  watch: {
    $route: [
      'fetchData',
    ],
    newAudio: {
      handler: function (newVal, oldVal) {
        if (newVal && newVal.size !== 0) {
          this.newAudioURL = this.getNewAudioUrl(newVal)
        }
      },
      immediate: true,
      deep: true,
    },
    word: {
      handler: function (newVal, oldVal) {
        if (newVal !== oldVal) {
          this.activeWord = newVal
        }
      },
    }
  },
  data() {
    return {
      name: 'AudioRecorder',
      show: false,
      showOverlay: false,
      limit: 15,
      count: 15,
      hold: false,
      play: false,
      isPlayer: false,
      isUpdatable: false,
      isShowPlayer: false,
      isAutoActive: false,
      isAuto: false,
      activeWord: null,
      autoWord: null,
      newAudioURL: null,

      newAudio: {blob: null, mediaType: 'audio/webm', extension: '.webm'},
      bufferAudio: {blob: null, mediaType: 'audio/webm', extension: '.webm'},
      recorder: null,
      percentageOfAudio: 0,
      currentTime: 0,
      duration: 0,
      recordSeconds: 0,
      recordFunc: null,
    }
  },
  methods: {
    fetchData() {
      this.show = false
      this.isUpdatable = this.updatable
      this.isPlayer = this.player
      this.isShowPlayer = this.showPlayer
      this.newAudioURL = this.url
      this.isAutoActive = this.isAudioGenerateLocalePresent(this.locale)
      this.show = true
    },
    prefixId() {
      return this.name + '-' + this.id + '-'
    },

    tryRecord() {
      this.record()
    },
    async record() {
      this.isShowPlayer = false
      this.newAudio = {blob: null, mediaType: 'audio/webm', extension: '.webm'}
      const stream = await navigator.mediaDevices.getUserMedia({
        audio: true,
        video: false,
      })
      const options = {mimeType: 'audio/webm'}
      this.recorder = new MediaRecorder(stream, options)
      const recordedChunks = []
      this.recorder.addEventListener('dataavailable', e => {
        if (e.data.size > 0) {
          recordedChunks.push(e.data)
        }
      })
      this.recorder.addEventListener('stop', () => {
        this.newAudio.blob = new Blob(recordedChunks)
      })
      this.duration = 0
      this.recordFunc = setInterval(() => {
        this.duration += 1
        this.count -= 1
      }, 1000)
      await this.recorder.start()
      this.hold = true
    },
    tryStop() {
      let timerId = setInterval(() => this.stop(timerId), 10)
      setTimeout(() => {
        clearInterval(timerId)
      }, 500)
    },
    async stop(func) {
      if (this.hold) {
        this.hold = false
        this.count = this.limit
        if (this.recorder) {
          await this.recorder.stop()
        }
        this.recorder = null
        clearInterval(this.recordFunc)
        this.isShowPlayer = true
        clearInterval(func)
      }
    },
    playStopAudio() {
      if (this.play) {
        this.$refs[this.ids.audio].pause()
        this.play = false
      } else {
        this.$refs[this.ids.audio].play()
        this.play = true
        const timerFunc = setInterval(() => {
          if (this.play) {
            this.updateTime()
            if (this.percentageOfAudio === 1) {
              this.play = false
              this.percentageOfAudio = 0
              clearInterval(timerFunc)
            }
          } else {
            clearInterval(timerFunc)
          }
        }, 100)
      }
    },
    updateTime() {
      const elem = document.getElementById(this.ids.audio)
      if (elem) {
        this.currentTime = elem.currentTime
        this.percentageOfAudio = (elem.currentTime / elem.duration)
      }
    },
    async toggleAuto() {
      this.showOverlay = true
      let timeout = 0 // to compute newAudioURL
      if (!this.isAuto && !this.bufferAudio.blob && !this.isCannotReload) {
        const buffer = await this.$store.dispatch('generateAudioAction', {text: this.activeWord, locale: this.locale})
        this.bufferAudio = buffer
        this.autoWord = this.activeWord
      } else {
        timeout = 100
      }
      const buffer = this.newAudio
      this.newAudio = this.bufferAudio
      this.bufferAudio = buffer
      this.isAuto = !this.isAuto
      setTimeout(() => {
        this.showOverlay = false
      }, timeout)
    },
    getNewAudioUrl(newAudio) {
      if (newAudio.blob) {
        return URL.createObjectURL(newAudio.blob)
      } else return null
    },
    async reloadAuto() {
      this.showOverlay = true
      const buffer = await this.$store.dispatch('generateAudioAction', {text: this.activeWord, locale: this.locale})
      this.autoWord = this.activeWord
      this.newAudio = buffer
      this.showOverlay = false
    },
    getMinutes(timeSeconds) {
      if (timeSeconds === 0) return '0'
      return Math.floor(timeSeconds / 60)
    },
    getSeconds(timeSeconds) {
      if (timeSeconds === 0) return '00'
      return (Math.floor(timeSeconds % 60) < 10 ? '0' : '') + Math.floor(timeSeconds % 60)
    },
    getCapitalizeLang(key) {
      return _.capitalize(this.getLang(key))
    },
    getLang(key) {
      return this.$t(key)
    },
    isBlank(value) {
      return _.isNil(value) || _.isEmpty(value)
    },
    getFile() {
      const filename = 'temp' + this.newAudio.extension
      if (this.newAudio.blob) {
        return FileJS.blobToFile(this.newAudio.blob, this.newAudio.mediaType, filename)
      } else return null
    },
    makeUpdatable() {
      this.isUpdatable = true
    },
    makeShowPlayer() {
      this.isShowPlayer = true
    },
  }
}
</script>

<style scoped>
.no-cursor {
  cursor: not-allowed;
}

.no-popover-body >>> .popover-body {
  padding: 0;
}

>>> .plyr .plyr__controls {
  padding: 0;
}

</style>
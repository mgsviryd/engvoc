import Vue from "vue"
import Vuex from "vuex"
import frontendApi from "../api/frontend"
import pictureMediaApi from "../api/pictureMedia"
import languageApi from "../api/language"
import signApi from "../api/sign"
import cardApi from "../api/card"
import dictionaryApi from "../api/dictionary"
import vocabularyApi from "../api/vocabulary"
import VuexPersistence from "vuex-persist"
import vlf from "../util/vlf"
import compare from "../util/compare"
import documentJS from "../util/document"
import {loadLanguageAsync} from "../setup/i18n-setup"
import * as _ from "lodash"
import localforage from 'localforage'

Vue.use(Vuex)

let AsyncLock = require('async-lock')
let lock = new AsyncLock()


const persist = new VuexPersistence(
    {
        key: 'root',
        // storage: window.localStorage,
        storage: localforage,
        asyncStorage: true,

        reducer: (state) => (
            {
                authentication: state.authentication,
                config: state.config,
                version: state.version,
                pictureMedia: state.pictureMedia,
                lang: state.lang,
                vocabulary: state.vocabulary,
                cards: state.cards,
                dictionaries: state.dictionaries,
                action: state.action,
                pictures: state.pictures,
                props: state.props,
            }
        )
    }
)
export default new Vuex.Store(
    {
        plugins: [
            persist.plugin, // can be timing problem with loading page
        ],
        state: {
            pageAttributes: {},
            dragdrop: {},
            pictures: [],
            action: {
                id: 0,
                errors: {},
            },
            dictionaries: [],
            cards: [],
            cardsNotSaved: [],
            authentication: {
                id: 0,
                user: null,
                users: [],
                isNew: false,
            },

            config: {},
            version: {},
            pictureMedia: {},
            lang: {
                id: 0,
                lang: {lang: "en", country: "US", locale: "en_US", "id": "1033"},
                langs: [],
                map: {},
            },

            vocabulary: {
                id: 0,
                default: {
                    source: {lang: "en", country: "US", locale: "en_US", "id": "1033"},
                    target: {lang: "en", country: "US", locale: "en_US", "id": "1033"}
                },
                vocabulary: null,
                vocabularies: [],
            },

            props: {
                download: {
                    lang: {
                        source: {lang: "en", country: "US", locale: "en_US", "id": "1033"},
                        target: {lang: "en", country: "US", locale: "en_US", "id": "1033"},
                    }
                },
                upload: {
                    lang: {
                        source: {lang: "en", country: "US", locale: "en_US", "id": "1033"},
                        target: {lang: "en", country: "US", locale: "en_US", "id": "1033"},
                    }
                },
            },
        },
        getters: {
            getActionId: state => () => state.action.id,
            getAuthenticationId: state => () => state.authentication.id,
            getLangId: state => () => state.lang.id,
            getVocabularyId: state => () => state.vocabulary.id,

            isDictionaryUnrepeated: (state, getters) => (id) => {
                return getters.getDictionaryById(id).unrepeated
            },
            getCardsByDictionaryInx: state => i => {
                if (state.dictionaries.length <= i) return []
                return state.cards[i]
            },
            getCardsByDictionaryId: (state, getters) => id => {
                return state.cards[getters.getDictionaryInx(id)]
            },
            isDictionaryExists: state => (id) => {
                return state.dictionaries.findIndex(d => d.id === id) >= 0
            },
            getDictionaryInx: state => (id) => {
                return state.dictionaries.findIndex(d => d.id === id)
            },
            getNewDictionaryInxByUnrepeated: state => (unrepeated) => {
                return state.dictionaries.findIndex(d => d.name === "new" && d.unrepeated === unrepeated)
            },
            getDictionaryById: (state, getters) => (id) => {
                return state.dictionaries[getters.getDictionaryInx(id)]
            },
            getCountCardsInDictionaryById: (state, getters) => (id) => {
                if (state.cards[getters.getDictionaryInx(id)]) {
                    return state.cards[getters.getDictionaryInx(id)].length
                } else return 0
            },
            getUnrepeatedDictionaries: (state) => () => {
                if (!_.isNil(state.dictionaries) && !_.isEmpty(state.dictionaries)) {
                    return state.dictionaries.filter(d => d.unrepeated === true)
                } else return []
            },
            getNonUnrepeatedDictionaries: (state) => () => {
                if (!_.isNil(state.dictionaries) && !_.isEmpty(state.dictionaries)) {
                    return state.dictionaries.filter(d => d.unrepeated === false)
                } else return []
            },
            getDictionaryIdsByUnrepeated: (state) => (unrepeated) => state.dictionaries.filter(d => d.unrepeated === unrepeated).map(d => d.id),
            getDictionariesInxsByUnrepeated: (state) => (unrepeated) => {
                const inxs = []
                state.dictionaries.forEach((d, i) => {
                    if (d.unrepeated === unrepeated) inxs.push(i)
                })
                return inxs;
            },
            getNonUnrepeatedDictionariesInxs: (state) => () => {
                const inxs = []
                state.dictionaries.forEach((d, i) => {
                    if (d.unrepeated === false) inxs.push(i)
                })
                return inxs
            },
            getUnrepeatedDictionariesUnrepeatedPropertyValues: (state, getters) => (property) => {
                return [...new Set(getters.getUnrepeatedDictionaries.map(d => d[property]))]
            },
            getUnrepeatedDictionariesPropertyValues: (state, getters) => (property) => {
                return getters.getUnrepeatedDictionaries.map(d => d[property])
            },
            getNonUnrepeatedDictionariesUnrepeatedPropertyValues: (state, getters) => (property) => {
                return [...new Set(getters.getNonUnrepeatedDictionaries().map(d => d[property]))]
            },
            getNonUnrepeatedDictionariesPropertyValues: (state, getters) => (property) => {
                return getters.getNonUnrepeatedDictionaries().map(d => d[property])
            },
            isNeedCheckDictionaryUnrepeated: (state, getters) => (sourceId, destId) => {
                return !getters.getDictionaryById(sourceId).unrepeated && getters.getDictionaryById(destId).unrepeated
            },
            sortArrayByStringProperty: (state) => (dictionaries, property) => dictionaries.sort((a, b) => compare.compareStringNaturalByProperty(a, b, property)),
            getUrl: state => part => decodeURI(encodeURI(state.config.url)).concat(part),
            isAuthenticated: (state) => {
                return typeof state.authentication.user !== 'undefined' && state.authentication.user !== null
            },
            getUsersTokens: (state) => {
                if (state.authentication.users.length === 0) {
                    return new Array(0)
                } else {
                    return state.authentication.users.map((x) => {
                        return x.token
                    })
                }
            },
            isUserInUsers: (state) => (user) => {
                return state.authentication.users.map(u => u.id).indexOf(user.id) >= 0
            },
            isNoAuthentication: (state, getters) => {
                return getters.isNoUser && getters.isNoUsers
            },
            isNoUser: (state) => {
                return _.isNil(state.authentication.user) || _.isEmpty(state.authentication.user)
            },
            isNoUsers: (state) => {
                return _.isNil(state.authentication.users) || _.isEmpty(state.authentication.users)
            },
            isUserPresent: (state) => {
                return !_.isNil(state.authentication.user)
            },
            isVocabulariesPresent: (state) => !_.isNil(state.vocabulary.vocabularies) && !_.isEmpty(state.vocabulary.vocabularies),
            isVocabularyPresent: (state) => !_.isNil(state.vocabulary.vocabulary),
            isVocabularyExists: (state) => () => {
                if (!_.isNil(state.vocabulary.vocabulary) && !_.isNil(state.authentication.user.vocabularies) && !_.isEmpty(state.authentication.user.vocabularies)) {
                    return state.authentication.user.vocabularies.findIndex(v => v.id === state.vocabulary.vocabulary.id) >= 0
                } else return false
            },
        },
        mutations: {
            setVocabularyMutation(state, payload) {
                state.vocabulary.vocabulary = payload.vocabulary
                state.vocabulary.id = _.now()
            },
            resetVocabularyMutation(state) {
                state.vocabulary.id = 0
                state.vocabulary.vocabulary = null
                state.vocabulary.vocabularies = []
            },
            renewVocabularyMutation(state) {
                state.vocabulary.vocabularies = state.authentication.user.vocabularies
                if (!this.getters.isVocabularyExists() && !_.isEmpty(state.authentication.user.vocabularies)) {
                    state.vocabulary.vocabulary = state.authentication.user.vocabularies[0]
                }
                state.vocabulary.id = _.now()
            },
            deleteVocabularyMutation(state) {
                state.cards = []
                state.dictionaries = []
                state.vocabulary.vocabulary = {}
                state.vocabulary.id = _.now()
            },
            addAndSetUserMutation(state, payload) {
                if (payload.user && !this.getters.isUserInUsers(payload.user)) {
                    state.authentication.users = [
                        ...state.authentication.users,
                        payload.user,
                    ]
                }
                state.authentication.user = payload.user
                state.authentication.id = _.now()
            },
            logoutMutation(state) {
                state.authentication.user = null
                state.authentication.isNew = false
                state.authentication.id = _.now()
            },
            authenticationIsNewMutation(state, payload) {
                state.authentication.isNew = payload.isNew
                _.delay(() => {
                    state.authentication.isNew = false
                }, 1500)
                state.authentication.id = _.now()
            },

            //dragdrop
            dragdropDefaultMutation(state) {
                state.dragdrop = {}
            },
            setDragdropStartMutation(state, payload) {
                state.dragdrop.start = payload
            },
            setDragdropEndMutation(state, payload) {
                state.dragdrop.end = payload
            },

            // errors
            setActionMutation(state, payload) {
                state.action.errors = payload.errors
                state.action.id = payload.id
            },


            // pictures
            setPicturesMutation(state, payload) {
                state.pictures = payload
            },
            deletePicturesMutation(state) {
                state.pictures = []
            },

            // cardsNotSaved
            addCardNotSavedMutation(state, payload) {
                if (payload.card === null) return
                state.cardsNotSaved = [
                    ...state.cardsNotSaved,
                    payload.card]
            },
            addCardsNotSavedMutation(state, payload) {
                if (payload.cards === null || payload.cards.length === 0) return
                state.cardsNotSaved = [
                    ...state.cardsNotSaved,
                    ...payload.cards
                ]
            },
            defaultCardsNotSavedMutation(state) {
                state.cardsNotSaved = []
            },
            removeCardsNotSavedMutation(state, payload) {
                if (payload.cards === null || payload.cards.length === 0) return
                const ids = payload.cards.map(c => c.id)
                state.cardsNotSaved = state.cardsNotSaved.filter(c => ids.findIndex(c.id) < 0)
            },

            // cards
            addCardMutation(state, payload) {
                const card = payload.card
                if (card === null) return
                const inx = this.getters.getDictionaryInx(card.dictionary.id)
                state.cards[inx] = [
                    ...state.cards[inx],
                    payload.card
                ]
                state.action.id = _.now()
            },

            cardChangeDictionaryMutation(state, payload) {
                const card = payload.card
                const destId = payload.destId
                const sourceId = payload.sourceId
                if (card === null) return
                const newInx = this.getters.getDictionaryInx(destId)
                const oldInx = this.getters.getDictionaryInx(sourceId)
                const oldI = state.cards[oldInx].findIndex(c => c.id === card.id)
                state.cards[oldInx] = [
                    ...state.cards[oldInx].slice(0, oldI),
                    ...state.cards[oldInx].slice(oldI + 1)
                ]
                state.cards[newInx] = [
                    ...state.cards[newInx],
                    card
                ]
            },

            deleteCardMutation(state, payload) {
                const card = payload.card
                const inx = this.getters.getDictionaryInx(card.dictionary.id)
                const i = state.cards[inx].indexOf(card)
                if (i >= 0) {
                    state.cards[inx] = [
                        ...state.cards[inx].slice(0, i),
                        ...state.cards[inx].slice(i + 1)
                    ]
                }
            },
            setCardsMutation(state, payload) {
                state.cards = []
                state.dictionaries.forEach((d, i) => {
                    state.cards[i] = payload.cards.filter(c => c.dictionary.id === d.id)
                })
                state.vocabulary.id = _.now()
            },

            defaultCardsMutation(state) {
                state.cards = []
            },

            deleteCardsInDictionaryMutation(state, payload) {
                const inx = this.getters.getDictionaryInx(payload.dictionaryId)
                const ids = payload.cards.map((c) => c.id)
                state.cards[inx] = state.cards[inx].filter((c) => {
                    return ids.indexOf(c.id) < 0;
                })
            },
            deleteCardsByDictionaryMutation(state, payload) {
                const inx = this.getters.getDictionaryInx(payload.dictionaryId)
                state.cards[inx] = []
            },

            cardsChangeDictionaryMutation(state, payload) {
                const cards = payload.cards
                const destId = payload.destId
                const sourceId = payload.sourceId
                if (cards.length === 0) return
                const newInx = this.getters.getDictionaryInx(destId)
                const oldInx = this.getters.getDictionaryInx(sourceId)

                const oldIs = []
                const cardsIds = cards.map(c => c.id)
                state.cards[oldInx].map(c => c.id).forEach((id, i) => {
                    if (cardsIds.indexOf(id) >= 0) {
                        oldIs.push(i)
                    }
                })
                for (let i = oldIs.length - 1; i >= 0; i--) {
                    const inx = oldIs[i]
                    state.cards[oldInx].splice(inx, 1)
                }
                state.cards[newInx] = [
                    ...state.cards[newInx],
                    ...cards,
                ]
            },

            // upload dictionaries and cards
            saveNewUnrepeatedCards(state, payload) {
                let inx = this.getters.getNewDictionaryInxByUnrepeated(true);
                if (inx < 0) this.commit('saveDictionaryMutation', {dictionary: payload.saveNewUnrepeatedDictionary})
                inx = this.getters.getNewDictionaryInxByUnrepeated(true);
                state.cards[inx] = [
                    ...state.cards[inx],
                    ...payload.saveNewUnrepeatedCards
                ]
            },
            saveDictionaryMutation(state, payload) {
                state.dictionaries = [
                    ...state.dictionaries,
                    payload.dictionary,
                ]
                state.cards = [
                    ...state.cards,
                    [],
                ]
                state.vocabulary.id = _.now()
            },
            updateCardsMutation(state, payload) {
                const updatedCards = payload.updateLearnedStatusUnrepeatedCards
                const countDictionaries = state.dictionaries.length
                for (let i = 0; i < countDictionaries; i++) {
                    const dictionary = state.dictionaries[i]
                    const dictionaryUpdatedCards = updatedCards.filter(c => c.dictionary.id === dictionary.id)
                    for (let j = 0; j < state.cards[i].length; j++) {
                        if (!_.isNil(dictionaryUpdatedCards) && !_.isEmpty(dictionaryUpdatedCards)) {
                            const inx = dictionaryUpdatedCards.findIndex(c => c.id === state.cards[i][j].id)
                            state.cards[i].splice(j, 1, dictionaryUpdatedCards[inx])
                        }
                    }
                }
            },
            uploadNewDictionariesAndCardsMutation(state, payload) {
                state.dictionaries = [
                    ...state.dictionaries,
                    ...payload.dictionaries,
                ]
                for (let i = state.cards.length; i < state.dictionaries.length; i++) {
                    state.cards = [
                        ...state.cards,
                        [],
                    ]
                }
                state.dictionaries.forEach((d, i) => {
                    state.cards[i] = [
                        ...state.cards[i],
                        ...payload.cards.filter(c => c.dictionary.id === d.id),
                    ]
                })
            },

            // dictionary
            addDictionaryMutation(state, payload) {
                state.dictionaries = [
                    ...state.dictionaries,
                    payload.dictionary,
                ]
                state.cards = [
                    ...state.cards,
                    [],
                ]
            },

            updateDictionaryMutation(state, payload) {
                const i = state.dictionaries.findIndex(d => d.id === payload.dictionary.id)
                state.dictionaries = [
                    ...state.dictionaries.slice(0, i),
                    payload.dictionary,
                    ...state.dictionaries.slice(i + 1)
                ]
            },
            deleteDictionaryByIdMutation(state, payload) {
                const inx = this.getters.getDictionaryInx(payload.id)
                if (inx >= 0) {
                    state.dictionaries = [
                        ...state.dictionaries.slice(0, inx),
                        ...state.dictionaries.slice(inx + 1)
                    ]
                    state.cards[inx] = []
                }
            },
            deleteDictionariesByUnrepeatedMutation(state, payload) {
                const inxs = this.getters.getDictionariesInxsByUnrepeated(payload.unrepeated)
                for (let i = state.dictionaries.length - 1; i >= 0; i--) {
                    if (inxs.indexOf(i) >= 0) state.cards.splice(i, 1)
                }
                state.dictionaries = state.dictionaries.filter(d => d.unrepeated !== payload.unrepeated)
            },

            resetVocabularyDatabaseMutation(state) {
                state.dictionaries = []
                state.cards = []
            },

            setDictionariesMutation(state, payload) {
                state.dictionaries = payload.dictionaries
                state.vocabulary.id = _.now()
            },
            setVocabularyDataMutation(state, payload) {
                state.dictionaries = payload.dictionaries
                state.cards = []
                state.dictionaries.forEach((d, i) => {
                    state.cards[i] = payload.cards.filter(c => c.dictionary.id === d.id)
                })
            },

            updateFrontendMutation(state, payload) {
                state.config = payload.config
                state.version = payload.version
                state.lang.langs = payload.langLangs
                state.lang.map = payload.langMap
            },
            setPageAttributesMutation(state, payload) {
                state.pageAttributes = documentJS.getPageAttributes(payload.id, payload.attr)
            },
            getPictureMediaMutation(state, data) {
                state.pictureMedia = data
            },

            syncAuthenticationStateWithLocalMutation(state, authenticationLocal) {
                lock.acquire('authentication', () => {
                    if (typeof authenticationLocal === 'undefined' || authenticationLocal === null) {
                        //
                    } else {
                        if (authenticationLocal.id > state.authentication.id) {
                            state.authentication = authenticationLocal
                        }
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            authenticationSyncLocalWithStateMutation(state) {
                let id = _.now()
                let authenticationCopy = Object.assign({}, state.authentication)
                authenticationCopy.id = id
                vlf.setItem('authentication', authenticationCopy).then(() => {
                    state.authentication.id = id
                })
            },

            getLanguageMapMutation(state, payload) {
                state.lang.map = payload.data
                state.lang.lang = payload.lang
                state.lang.id = _.now()
            },
            getLanguageListMutation(state, data) {
                state.lang.list = data
            },
            setAuthenticationMutation(state, payload) {
                state.authentication.user = payload.user
                state.authentication.users = payload.users
                state.authentication.id = _.now()
            },
            watchAuthenticationMutation(state) {
                if (state.authentication.user) {
                    this.commit('renewVocabularyMutation')
                } else {
                    this.commit('resetVocabularyMutation')
                }
            },
            changePropsDownloadLangMutation(state, payload) {
                state.props.download.lang.source = payload.source
                state.props.download.lang.target = payload.target
            },
            changePropsUploadLangMutation(state, payload) {
                state.props.upload.lang.source = payload.source
                state.props.upload.lang.target = payload.target
            },
            updateUserMutation(state, payload) {
                state.authentication.user = payload.user
                state.authentication.id = _.now()
            },
            updateVocabularyMutation(state, payload) {
                state.vocabulary.vocabulary = payload.vocabulary
                state.vocabulary.vocabularies = payload.vocabularies
                state.vocabulary.id = _.now()
            },
        },
        actions: {
            async updateFrontendAction({commit, state, getters}) {
                await loadLanguageAsync(state.lang.lang.locale)
                const result = await frontendApi.getFrontend(
                    getters.getUsersTokens,
                    state.lang.lang.locale
                )
                const data = await result.data
                if (result.ok) {
                    commit('updateFrontendMutation', data)
                    lock.acquire('authentication', () => {
                        if (result.ok) {
                            commit('setAuthenticationMutation', data)
                            commit('authenticationSyncLocalWithStateMutation')
                        }
                    }).catch((err) => {
                        console.log(err) // output: error
                    })
                }
            },

            async setPageAttributesAction({commit}, payload) {
                commit('setPageAttributesMutation', payload)
            },

            async getPictureMediaAction({commit}) {
                const result = await pictureMediaApi.get()
                const data = await result.data
                if (result.ok) {
                    commit('getPictureMediaMutation', data)
                }
            },

            async removeCookiesAndStateAction({commit, dispatch}) {
                await dispatch('removeBasketAction')
                await dispatch('removeFavoriteAction')
            },

            async syncAuthenticationStateWithLocalAction({commit}) {
                let authenticationLocal = await vlf.getItem('authentication')
                commit('syncAuthenticationStateWithLocalMutation', authenticationLocal)
            },

            async changeLangAction({commit}, lang) {
                await loadLanguageAsync(lang.locale)
                const result = await languageApi.changeLang(lang.locale)
                const data = await result.data
                if (result.ok) {
                    commit('getLanguageMapMutation', {lang: lang, data: data})
                }
            },

            async getLanguageListAction({commit}) {
                const result = await languageApi.getList()
                const data = await result.data
                if (result.ok) {
                    commit('getLanguageListMutation', data)
                }
            },
            // async getAuthenticationAction({commit, getters}) {
            //     const result = await signApi.getUsers(getters.getUsersTokens)
            //     const data = await result.data
            //     lock.acquire('authentication', () => {
            //         if (result.ok) {
            //             commit('setAuthenticationMutation', data)
            //             commit('authenticationSyncLocalWithStateMutation')
            //         }
            //     }).catch((err) => {
            //         console.log(err) // output: error
            //     })
            // },

            // cards
            async uploadCardsByExcelFilesAction({commit}, payload) {
                const formData = payload.formData
                formData.append('vocabularyId',
                    new Blob([payload.vocabulary.id], {type: "application/json"}))
                formData.append('options',
                    new Blob([JSON.stringify(payload.options)], {type: "application/json"}))
                const result = await cardApi.uploadExcelFiles(formData)
                const data = await result.data
                lock.acquire('uploadCards', () => {
                    if (result.ok) {
                        if (payload.options.updateLearnedStatusUnrepeatedCards) {
                            commit('updateCardsMutation', data)
                        }
                        if (payload.options.saveNewUnrepeatedCards) {
                            commit('saveNewUnrepeatedCards', data)
                        }
                        if (payload.options.saveAllUploadCards) {
                            commit('uploadNewDictionariesAndCardsMutation', data)
                        }
                        if (payload.options.updateCardsWithAbsentSound) {
                            commit('updateCardsMutation', data)
                        }
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            async uploadCardsByXmlFilesAction({commit}, payload) {
                const formData = payload.formData
                formData.append('vocabularyId',
                    new Blob([payload.vocabulary.id], {type: "application/json"}))
                formData.append('options',
                    new Blob([JSON.stringify(payload.options)], {type: "application/json"}))
                const result = await cardApi.uploadXmlFiles(formData)
                const data = await result.data
                lock.acquire('uploadCards', () => {
                    if (result.ok) {
                        if (payload.options.updateLearnedStatusUnrepeatedCards) {
                            commit('updateCardsMutation', data)
                        }
                        if (payload.options.saveNewUnrepeatedCards) {
                            commit('saveNewUnrepeatedCards', data)
                        }
                        if (payload.options.saveAllUploadCards) {
                            commit('uploadNewDictionariesAndCardsMutation', data)
                        }
                        if (payload.options.updateCardsWithAbsentSound) {
                            commit('updateCardsMutation', data)
                        }
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            async uploadCardsByExcelFileAction({commit}, payload) {
                const formData = payload.formData
                formData.append('vocabularyId',
                    new Blob([payload.vocabulary.id], {type: "application/json"}))
                formData.append('options',
                    new Blob([JSON.stringify(payload.options)], {type: "application/json"}))
                const result = await cardApi.uploadExcelFile(formData)
                const data = await result.data
                lock.acquire('uploadCards', () => {
                    if (result.ok) {
                        if (payload.options.updateLearnedStatusUnrepeatedCards) {
                            commit('updateCardsMutation', data)
                        }
                        if (payload.options.saveNewUnrepeatedCards) {
                            commit('saveNewUnrepeatedCards', data)
                        }
                        if (payload.options.saveAllUploadCards) {
                            commit('uploadNewDictionariesAndCardsMutation', data)
                        }
                        if (payload.options.updateCardsWithAbsentSound) {
                            commit('updateCardsMutation', data)
                        }

                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            async uploadCardsByXmlFileAction({commit}, payload) {
                const formData = payload.formData
                formData.append('vocabularyId',
                    new Blob([payload.vocabulary.id], {type: "application/json"}))
                formData.append('options',
                    new Blob([JSON.stringify(payload.options)], {type: "application/json"}))
                const result = await cardApi.uploadXmlFile(formData)
                const data = await result.data
                lock.acquire('uploadCards', () => {
                    if (result.ok) {
                        if (payload.options.updateLearnedStatusUnrepeatedCards) {
                            commit('updateCardsMutation', data)
                        }
                        if (payload.options.saveNewUnrepeatedCards) {
                            commit('saveNewUnrepeatedCards', data)
                        }
                        if (payload.options.saveAllUploadCards) {
                            commit('uploadNewDictionariesAndCardsMutation', data)
                        }
                        if (payload.options.updateCardsWithAbsentSound) {
                            commit('updateCardsMutation', data)
                        }
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            async deleteCardAction({commit}, payload) {
                const result = await cardApi.remove(payload.card.id)
                const data = await result.data
                if (result.ok) {
                    commit('deleteCardMutation', payload)
                    commit('setActionMutation', {id: _.now(), errors: []})
                }
            },
            async deleteCardsInDictionaryAction({commit}, payload) {
                const result = await cardApi.deleteByIdIn({ids: payload.cards.map(c => c.id)})
                if (result.ok) {
                    commit('deleteCardsInDictionaryMutation', {
                        cards: payload.cards,
                        dictionaryId: payload.dictionaryId
                    })
                    commit('setActionMutation', {id: _.now(), errors: []})
                }
            },
            async deleteCardsByDictionaryAction({commit}, payload) {
                const result = await cardApi.deleteByDictionary({id: payload.dictionaryId})
                if (result.ok) {
                    commit('deleteCardsByDictionaryMutation', {dictionaryId: payload.dictionaryId})
                    commit('setActionMutation', {id: _.now(), errors: []})
                }
            },

            async cardsChangeDictionariesAction({commit, state, getters, dispatch}, payload) {
                if (payload.cards && payload.cards.length > 0) {
                    if (payload.cards.length === 1) {
                        const result = await cardApi.changeDictionary(payload.cards[0], payload.destId)
                        const data = await result.data
                        if (result.ok) {
                            commit('cardChangeDictionaryMutation', {
                                card: data.saved,
                                sourceId: payload.sourceId,
                                destId: payload.destId
                            })
                            commit('addCardNotSavedMutation', {card: data.notSaved})
                            commit('setActionMutation', {id: _.now(), errors: []})
                        }
                    } else {
                        const result = await cardApi.changeDictionaries(payload.cards, payload.destId)
                        const data = await result.data
                        if (result.ok) {
                            commit('cardsChangeDictionaryMutation', {
                                cards: data.saved,
                                sourceId: payload.sourceId,
                                destId: payload.destId
                            })
                            commit('addCardsNotSavedMutation', {cards: data.notSaved})
                            commit('setActionMutation', {id: _.now(), errors: []})
                        }
                    }
                }
            },

            async findVocabularyDataAction({commit, dispatch}, payload) {
                const result = await vocabularyApi.findData(payload)
                const data = await result.data
                if (result.ok) {
                    commit('setVocabularyDataMutation',
                        {dictionaries: data.dictionaries, cards: data.cards}
                    )
                }
            },

            async findDictionaries({commit}) {
                const result = await dictionaryApi.findAll()
                const data = await result.data
                if (result.ok) {
                    commit('setDictionariesMutation', {dictionaries: data})
                }
            },
            async findCards({commit}) {
                const result = await cardApi.findAll()
                const data = await result.data
                if (result.ok) {
                    commit('setCardsMutation', {cards: data})
                }
            },


            // pictureMedia
            async addPictureAction({commit}, payload) {
                if (payload.formData !== null) {
                    const result = await pictureMediaApi.savePicture(payload.formData)
                    const picture = await result.data
                    if (result.ok) {
                        return picture
                    }
                }
                return null
            },

            // dictionary
            async addDictionaryWithPictureAction({commit}, payload) {
                let result = null
                if (payload.formData) {
                    payload.formData.append('dictionary',
                        new Blob([JSON.stringify(payload.dictionary)], {type: "application/json"}))
                    payload.formData.append('vocabularyId',
                        new Blob([JSON.stringify(payload.vocabulary.id)], {type: "application/json"}))
                    result = await dictionaryApi.saveWithPicture(payload.formData)
                } else {
                    let jsonStringMap = {}
                    jsonStringMap.dictionary = JSON.stringify(payload.dictionary)
                    jsonStringMap.vocabularyId = payload.vocabulary.id
                    result = await dictionaryApi.saveWithoutPicture(jsonStringMap)
                }
                const data = await result.data
                if (result.ok) {
                    if (data.dictionary) {
                        commit('addDictionaryMutation', {dictionary: data.dictionary})
                    }
                    return data.errors
                }
            },
            async addCardWithPictureAction({commit}, payload) {
                let result = null
                if (payload.formData) {
                    payload.formData.append('card', new Blob([JSON.stringify(payload.card)], {type: "application/json"}),)
                    result = await cardApi.saveWithPicture(payload.formData)
                } else {
                    result = await cardApi.saveWithoutPicture(payload.card)
                }
                const data = await result.data
                if (result.ok) {
                    if (data.card) {
                        commit('addCardMutation', {card: data.card})
                    }
                    return data.errors
                }
            },

            async deleteDictionaryByIdAction({commit, state}, payload) {
                const result = await dictionaryApi.remove(payload.id)
                if (result.ok) {
                    commit('deleteDictionaryByIdMutation', {id: payload.id})
                    commit('setActionMutation', {id: payload.actionId, errors: []})
                }
            },

            async deleteDictionariesByUnrepeatedAction({commit}, payload) {
                let jsonStringMap = {}
                jsonStringMap.vocabulary = JSON.stringify(payload.vocabulary)
                jsonStringMap.unrepeated = payload.unrepeated
                jsonStringMap.actual = payload.actual
                jsonStringMap.expected = payload.expected
                console.info(jsonStringMap)
                const result = await dictionaryApi.deleteByUnrepeatedDanger(jsonStringMap)
                const data = await result.data
                if (result.ok) {
                    commit('deleteDictionariesByUnrepeatedMutation', payload)
                    if (data.dictionary) {
                        commit('addDictionaryMutation', data.dictionary)
                    }
                    commit('setActionMutation', {id: _.now(), errors: []})
                    return data.errors
                }
            },


            //dragdrop
            dragdropStartAction({commit}, payload) {
                commit('setDragdropStartMutation', payload)
            },
            dragdropEndAndExecuteAction({commit, state, dispatch}, payload) {
                // console.info("dragend: " + _.now())
                commit('setDragdropEndMutation', payload)
                if (!state.dragdrop.start
                    || !state.dragdrop.end
                    || state.dragdrop.start.groups.filter(x => state.dragdrop.end.groups.findIndex(y => y === x) >= 0).length === 0
                    || (!state.dragdrop.end.options.isDragdropInside
                        && state.dragdrop.start.data.sourceMark === state.dragdrop.end.data.sourceMark
                        && state.dragdrop.start.data.sourceId === state.dragdrop.end.data.sourceId)
                ) {
                    commit('dragdropDefaultMutation')
                    return
                }
                if (state.dragdrop.start.groups.indexOf("cardsChangeDictionary") >= 0) {
                    dispatch('cardsChangeDictionariesAction',
                        {
                            cards: state.dragdrop.start.data.items,
                            sourceId: state.dragdrop.start.data.sourceId,
                            destId: state.dragdrop.end.data.sourceId,
                        })
                }
            },

            async signUpUserAction({commit}, payload) {
                let result = await signApi.up(payload)
                const data = await result.data
                if (result.ok) {
                    return data.errors
                }
            },
            async signInAction({commit}, payload) {
                let result = await signApi.in(payload)
                const data = await result.data
                if (result.ok) {
                    if (data.user) {
                        commit('addAndSetUserMutation', {user: data.user})
                    }
                    return data.errors
                }
            },
            async logoutAction({commit}) {
                let result = await signApi.logout()
                if (result.ok) {
                    commit('logoutMutation')
                }
            },

            async downloadExcelFileAction({commit}, payload) {
                let result = await cardApi.downloadExcelFile(payload.dictionary.id)
                const data = await result.data
                if (result.ok) {
                    let blob = new Blob([result.blob()], {type: result.headers['content-type']})
                    let link = document.createElement('a')
                    link.href = window.URL.createObjectURL(blob)
                    const source = payload.vocabulary.source.locale
                    const target = payload.vocabulary.target.locale
                    link.download = payload.dictionary.name + '-' + source + '-' + target + '.xlsx'
                    link.click();
                }
            },
            async downloadXmlFileAction({commit}, payload) {
                let result = await cardApi.downloadXmlFile(payload.dictionary.id)
                const data = await result.data
                if (result.ok) {
                    let blob = new Blob([data], {type: result.headers['content-type']})
                    let link = document.createElement('a')
                    link.href = window.URL.createObjectURL(blob)
                    const source = payload.vocabulary.source.locale
                    const target = payload.vocabulary.target.locale
                    link.download = payload.dictionary.name + '-' + source + '-' + target + '.xml'
                    link.click();
                }
            },
            async changePropsDownloadLangAction({commit}, payload) {
                commit('changePropsDownloadLangMutation', payload)
            },
            async changePropsUploadLangAction({commit}, payload) {
                commit('changePropsUploadLangMutation', payload)
            },

            async saveVocabularyAction({commit, dispatch}, payload) {
                let result = await vocabularyApi.save(payload)
                const data = await result.data
                if (result.ok) {
                    if (data.user) {
                        commit('updateUserMutation', {user: data.user})
                    }
                    if (data.vocabulary) {
                        commit('updateVocabularyMutation', {
                            vocabulary: data.vocabulary,
                            vocabularies: data.user.vocabularies
                        })
                    }
                    return data.errors
                }
            },

            async deleteVocabularyAction({commit}, payload) {
                let jsonStringMap = {}
                jsonStringMap.vocabulary = JSON.stringify(payload.vocabulary)
                jsonStringMap.actual = payload.actual
                jsonStringMap.expected = payload.expected
                let result = await vocabularyApi.deleteDanger(jsonStringMap)
                const data = await result.data
                if (result.ok) {
                    if (_.isEmpty(data.errors)) {
                        commit('deleteVocabularyMutation')
                        commit('updateUserMutation', {user: data.user})
                    }
                    return data.errors
                }
            },

            async downloadDictionaryXmlFilesByIdsAction({commit}, payload) {
                let result = await cardApi.downloadXmlFiles(payload.ids)
                const data = await result.data
                if (result.ok) {
                    let blob = new Blob([data], {type: result.headers['content-type']})
                    let link = document.createElement('a')
                    link.href = window.URL.createObjectURL(blob)
                    link.download = 'dictionaries' + '.zip'
                    link.click();
                }
            }
        },
    }
)
import Vue from "vue"
import Vuex from "vuex"
import frontendApi from "../api/frontend"
import pictureMediaApi from "../api/pictureMedia"
import languageApi from "../api/language"
import signApi from "../api/sign"
import cardApi from "../api/card"
import dictionaryApi from "../api/dictionary"
import VuexPersistence from "vuex-persist"
import vlf from "../util/vlf"
import date from "../util/date"
import compare from "../util/compare"
import documentJS from "../util/document"
import {loadLanguageAsync} from "../setup/i18n-setup"
import * as _ from "lodash"
import storeMethods from "./storeMethods";

Vue.use(Vuex)

let AsyncLock = require('async-lock');
let lock = new AsyncLock();


const persist = new VuexPersistence(
    {
        key: 'root',
        storage: window.localStorage,
        // storage: localforage,
        reducer: (state) => (
            {
                authentication: state.authentication,
                frontend: state.frontend,
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
                users: [],
                user: null,
            },

            frontend: {},
            pictureMedia: {},
            lang: {
                id: 0,
                lang: {lang: "en", country: "US", locale: "en_US", "id": "1033"},
                langs: [],
                map: {},
            },

            vocabulary: {
                id: 0,
                lang: {},
                langs: [],
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
            }
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
            getCountCardsInDictionaryById: (state, getters) => (id) => state.cards[getters.getDictionaryInx(id)].length,
            getUnrepeatedDictionaries: (state) => () => state.dictionaries.filter(d => d.unrepeated === true),
            getNonUnrepeatedDictionaries: (state) => () => state.dictionaries.filter(d => d.unrepeated === false),
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
            getUrl: state => part => decodeURI(encodeURI(state.frontend.config.url)).concat(part),
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
            isUserExists: (state) => (user) => {
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
            isVocabularyLangPresent: (state) => !_.isNil(state.vocabulary.lang) && !_.isEmpty(state.vocabulary.lang),
            getVocabularyLangsInx: (state) => (lang) => {
                return state.vocabulary.langs.indexOf(lang)
            }
        },
        mutations: {
            addAndSetUserMutation(state, payload) {
                if (payload.user && !this.getters.isUserExists(payload.user)) {
                    state.authentication.users = [
                        ...state.authentication.users,
                        payload.user,
                    ]
                }
                state.authentication.user = payload.user
                state.authentication.id = date.getUTCMilliseconds(new Date())
            },
            logoutMutation(state) {
                state.authentication.user = null
                state.authentication.id = date.getUTCMilliseconds(new Date())
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
                state.vocabulary.id = date.getUTCMilliseconds(new Date())
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
                if (inx < 0) this.saveDictionary({dictionary: payload.saveNewUnrepeatedDictionary})
                inx = this.getters.getNewDictionaryInxByUnrepeated(true);
                state.cards[inx] = [
                    ...state.cards[inx],
                    ...payload.saveNewUnrepeatedCards
                ]
            },
            saveDictionary(state, payload) {
                state.dictionaries = [
                    ...state.dictionaries,
                    payload.dictionary,
                ]
                state.cards = [
                    ...state.cards,
                    [],
                ]
                state.vocabulary.id = date.getUTCMilliseconds(new Date())
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
            deleteDictionariesByUnrepeatedAndCascadeCardsMutation(state, payload) {
                const inxs = this.getters.getDictionariesInxsByUnrepeated(payload.unrepeated)
                for (let i = state.dictionaries.length - 1; i >= 0; i--) {
                    if (inxs.indexOf(i) >= 0) state.cards.splice(i, 1)
                }
                state.dictionaries = state.dictionaries.filter(d => d.unrepeated !== payload.unrepeated)
            },

            setDictionariesMutation(state, payload) {
                state.dictionaries = payload.dictionaries
                state.vocabulary.id = date.getUTCMilliseconds(new Date())
            },
            setDictionariesAndCardsMutation(state, payload) {
                state.dictionaries = payload.dictionaries
                state.cards = []
                state.dictionaries.forEach((d, i) => {
                    state.cards[i] = payload.cards.filter(c => c.dictionary.id === d.id)
                })
                state.vocabulary.id = date.getUTCMilliseconds(new Date())
            },

            updateFrontendMutation(state, data) {
                state.frontend = data
                state.lang.langs = data.langLangs
                state.lang.map = data.langMap
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
                let id = date.getUTCMilliseconds(new Date())
                let authenticationCopy = Object.assign({}, state.authentication)
                authenticationCopy.id = id
                vlf.setItem('authentication', authenticationCopy).then(() => {
                    state.authentication.id = id
                })
            },

            getLanguageMapMutation(state, payload) {
                let id = date.getUTCMilliseconds(new Date())
                state.lang.map = payload.data
                state.lang.lang = payload.lang
                state.lang.id = id
            },
            getLanguageListMutation(state, data) {
                state.lang.list = data
            },
            setAuthenticationMutation(state, payload) {
                state.authentication = payload
                state.authentication.id = date.getUTCMilliseconds(new Date())
            },
            changePropsDownloadLangMutation(state, payload) {
                state.props.download.lang.source = payload.source
                state.props.download.lang.target = payload.target
            },
            changePropsUploadLangMutation(state, payload) {
                state.props.upload.lang.source = payload.source
                state.props.upload.lang.target = payload.target
            },
            changeVocabularyLangMutation(state, payload) {
                state.vocabulary.lang.source = payload.source
                state.vocabulary.lang.target = payload.target
            },
            setDefaultVocabularyLangMutation(state, payload) {
                state.vocabulary.lang = {}
                if (!_.isNil() && !_.isEmpty(payload)) {
                    this.deleteLangFromVocabularyLangs(payload)
                }
            },
            deleteLangFromVocabularyLangs(state, payload) {
                const i = this.getters.getVocabularyLangsInx(payload)
                if (i >= 0) {
                    state.vocabulary.langs = [
                        ...state.vocabulary.langs.slice(0, i),
                        ...state.vocabulary.langs.slice(i + 1)
                    ]
                }
            },
            createVocabularyDatabaseMutation(state, payload) {
                state.dictionaries = [payload.dictionary]
                state.cards = [[]]
                state.vocabulary.id = date.getUTCMilliseconds(new Date())
            },
        },
        actions: {
            async updateFrontendAction({commit}, payload) {
                const result = await frontendApi.getFrontend(payload.lang)
                const data = await result.data
                if (result.ok) {
                    commit('updateFrontendMutation', data)
                }
            },

            async setFrontendAction({commit}, data) {
                commit('updateFrontendMutation', data)
            },
            async setPageAttributesAction({commit}, payload) {
                commit('setPageAttributesMutation', payload)
            },

            async getFrontendAction({commit}, lang) {
                await loadLanguageAsync(lang)
                const result = await frontendApi.getFrontend(lang)
                const data = await result.data
                if (result.ok) {
                    return data
                } else {
                    return null
                }
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
            async getAuthenticationAction({commit, getters}) {
                const result = await signApi.getUsers(getters.getUsersTokens)
                const data = await result.data
                lock.acquire('authentication', () => {
                    if (result.ok) {
                        commit('setAuthenticationMutation', data)
                        commit('authenticationSyncLocalWithStateMutation')
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },

            // cards
            async uploadCardsByExcelFilesAction({commit}, payload) {
                const formData = payload.formData
                formData.append('pair',
                    new Blob([JSON.stringify(payload.pair)], {type: "application/json"}))
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
                formData.append('pair',
                    new Blob([JSON.stringify(payload.pair)], {type: "application/json"}))
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
                formData.append('pair',
                    new Blob([JSON.stringify(payload.pair)], {type: "application/json"}))
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
                formData.append('pair',
                    new Blob([JSON.stringify(payload.pair)], {type: "application/json"}))
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
                    commit('setActionMutation', {id: date.getUTCMilliseconds(new Date()), errors: []})
                }
            },
            async deleteCardsInDictionaryAction({commit}, payload) {
                const result = await cardApi.deleteByIdIn({ids: payload.cards.map(c => c.id)})
                if (result.ok) {
                    commit('deleteCardsInDictionaryMutation', {
                        cards: payload.cards,
                        dictionaryId: payload.dictionaryId
                    })
                    commit('setActionMutation', {id: date.getUTCMilliseconds(new Date()), errors: []})
                }
            },
            async deleteCardsByDictionaryAction({commit}, payload) {
                const result = await cardApi.deleteByDictionary({id: payload.dictionaryId})
                if (result.ok) {
                    commit('deleteCardsByDictionaryMutation', {dictionaryId: payload.dictionaryId})
                    commit('setActionMutation', {id: date.getUTCMilliseconds(new Date()), errors: []})
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
                            commit('setActionMutation', {id: date.getUTCMilliseconds(new Date()), errors: []})
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
                            commit('setActionMutation', {id: date.getUTCMilliseconds(new Date()), errors: []})
                        }
                    }
                }
            },
            async findDictionariesAndCardsAction({commit, dispatch}, payload) {
                const result = await dictionaryApi.findDictionariesAndCards(payload)
                const data = await result.data
                if (result.ok) {
                    if (_.isEmpty(data.dictionaries)) {
                        commit('setDefaultVocabularyLangMutation', payload)
                    } else {
                        commit('setDictionariesAndCardsMutation', {dictionaries: data.dictionaries, cards: data.cards})
                    }
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
            async addDictionaryAction({commit}, payload) {
                const result = await dictionaryApi.saveUnrepeated(payload.dictionary)
                const data = await result.data
                if (result.ok) {
                    commit('addDictionaryMutation', {dictionary: data.saved})
                    commit('setActionMutation', {id: payload.actionId, errors: data.errors})
                }
            },
            async addDictionaryWithPictureAction({commit}, payload) {
                let result = null
                if (payload.formData) {
                    payload.formData.append('dictionary',
                        new Blob([JSON.stringify(payload.dictionary)], {type: "application/json"}))
                    result = await dictionaryApi.saveUnrepeatedWithPicture(payload.formData)
                } else {
                    result = await dictionaryApi.saveUnrepeated(payload.dictionary)
                }
                const data = await result.data
                if (result.ok) {
                    commit('addDictionaryMutation', {dictionary: data.saved})
                    commit('setActionMutation', {id: payload.actionId, errors: data.errors})
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
                    commit('addCardMutation', {card: data.saved})
                    commit('setActionMutation', {id: payload.actionId, errors: data.errors})
                }
            },

            async deleteDictionaryByIdAction({commit, state}, payload) {
                const result = await dictionaryApi.remove(payload.id)
                if (result.ok) {
                    commit('deleteDictionaryByIdMutation', {id: payload.id})
                    commit('setActionMutation', {id: payload.actionId, errors: []})
                }
            },

            async deleteDictionariesByUnrepeatedAndCascadeCardsAction({commit}, payload) {
                const result = await dictionaryApi.deleteByUnrepeated({unrepeated: payload.unrepeated})
                if (result.ok) {
                    commit('deleteDictionariesByUnrepeatedAndCascadeCardsMutation', payload)
                    commit('setActionMutation', {id: payload.actionId, errors: []})
                }
            },


            //dragdrop
            dragdropStartAction({commit}, payload) {
                commit('setDragdropStartMutation', payload)
            },
            dragdropEndAndExecuteAction({commit, state, dispatch}, payload) {
                // console.info("dragend: " + date.getUTCMilliseconds(new Date()))
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
                    const pair = storeMethods.getCapitalizeLangPair(payload.dictionary)
                    link.download = payload.dictionary.name + pair + '.xlsx'
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
                    const pair = storeMethods.getCapitalizeLangPair(payload.dictionary)
                    link.download = payload.dictionary.name + pair + '.xml'
                    link.click();
                }
            },
            async changePropsDownloadLangAction({commit}, payload) {
                commit('changePropsDownloadLangMutation', payload)
            },
            async changePropsUploadLangAction({commit}, payload) {
                commit('changePropsUploadLangMutation', payload)
            },
            async changeVocabularyDatabaseAction({commit, dispatch}, payload) {
                commit('changeVocabularyLangMutation', payload)
                dispatch('findDictionariesAndCardsAction', payload)
            },
            async createVocabularyDatabaseAction({commit, dispatch}, payload) {
                commit('changeVocabularyLangMutation', payload)
                let result = await dictionaryApi.saveNewUnrepeated(payload)
                const data = await result.data
                if (result.ok) {
                    commit('createVocabularyDatabaseMutation', {dictionary: data})
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
import Vue from "vue";
import Vuex from "vuex";
import frontendApi from "../api/frontend";
import pictureMediaApi from "../api/pictureMedia";
import languageApi from "../api/language";
import signApi from "../api/sign";
import cardApi from "../api/card";
import dictionaryApi from "../api/dictionary";
import VuexPersistence from "vuex-persist";
import vlf from "../util/vlf";
import date from "../util/date";
import compare from "../util/compare";
import {loadLanguageAsync} from "../setup/i18n-setup"



Vue.use(Vuex)

let AsyncLock = require('async-lock');
let lock = new AsyncLock();

const persist = new VuexPersistence(
    {
        storage: window.localStorage,
        reducer: (state) => (
            {
                authentication: state.authentication,
                frontend: state.frontend,
                pictureMedia: state.pictureMedia,
                lang: state.lang,
                cards: state.cards,
                dictionaries: state.dictionaries,
                action: state.action,
                pictures: state.pictures,
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
                user: {},
            },
            frontend: {},
            pictureMedia: {},

            lang: {
                id: 0,
                lang: {lang: "en", country: "US", locale: "en_US"},
                langs: [],
                map: {}
            },
        },
        getters: {
            getActionId: state => () => state.action.id,
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
            getDictionaryById: (state, getters) => (id) => {
                return state.dictionaries[getters.getDictionaryInx(id)]
            },
            getCountCardsInDictionaryById: (state, getters) => (id) => state.cards[getters.getDictionaryInx(id)].length,
            getUniqueDictionaries: (state) => () => state.dictionaries.filter(d => d.unique === true),
            getNonUniqueDictionaries: (state) => () => state.dictionaries.filter(d => d.unique === false),
            getDictionariesInxsByUnique: (state) => (unique) => {
                const inxs = []
                state.dictionaries.forEach((d, i) => {
                    if (d.unique === unique) inxs.push(i)
                })
                return inxs;
            },
            getNonUniqueDictionariesInxs: (state) => () => {
                const inxs = []
                state.dictionaries.forEach((d, i) => {
                    if (d.unique === false) inxs.push(i)
                })
                return inxs;
            },
            getUniqueDictionariesUniquePropertyValues: (state, getters) => (property) => {
                return [...new Set(getters.getUniqueDictionaries.map(d => d[property]))]
            },
            getUniqueDictionariesPropertyValues: (state, getters) => (property) => {
                return getters.getUniqueDictionaries.map(d => d[property])
            },
            getNonUniqueDictionariesUniquePropertyValues: (state, getters) => (property) => {
                return [...new Set(getters.getNonUniqueDictionaries().map(d => d[property]))]
            },
            getNonUniqueDictionariesPropertyValues: (state, getters) => (property) => {
                return getters.getNonUniqueDictionaries().map(d => d[property])
            },
            isNeedCheckDictionaryUnique: (state, getters) => (sourceId, destId) => {
                return !getters.getDictionaryById(sourceId).unique && getters.getDictionaryById(destId).unique
            },
            sortArrayByStringProperty: (state) => (dictionaries, property) => dictionaries.sort((a, b) => compare.compareStringNaturalByProperty(a, b, property)),
            getUrl: state => part => decodeURI(encodeURI(state.frontend.config.url)).concat(part),
            getLangId: state => () => state.lang.id,
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
            }
        },
        mutations: {
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
            setPicturesMutation(state, pictures) {
                state.pictures = pictures
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
            deleteDictionariesByUniqueAndCascadeCardsMutation(state, payload) {
                const inxs = this.getters.getDictionariesInxsByUnique(payload.unique)
                for (let i = state.dictionaries.length - 1; i >= 0; i--) {
                    if (inxs.indexOf(i) >= 0) state.cards.splice(i, 1)
                }
                state.dictionaries = state.dictionaries.filter(d => d.unique !== payload.unique)
            },

            setDictionariesMutation(state, payload) {
                state.dictionaries = payload.dictionaries
            },

            updateFrontendMutation(state, data) {
                state.frontend = data
                state.lang.langs = data.langLangs
                state.lang.map = data.langMap
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
            getAuthenticationMutation(state, data) {
                state.authentication = data
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
                        commit('getAuthenticationMutation', data)
                        commit('authenticationSyncLocalWithStateMutation')
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },

            // cards
            async uploadCardsByExcelFilesAction({commit}, payload) {
                const formData = payload.formData
                const result = await cardApi.uploadExcelFiles(formData)
                const data = await result.data
                lock.acquire('uploadCards', () => {
                    if (result.ok) {
                        commit('uploadNewDictionariesAndCardsMutation', data)
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            async uploadCardsByXmlFilesAction({commit}, payload) {
                const formData = payload.formData
                const result = await cardApi.uploadXmlFiles(formData)
                const data = await result.data
                lock.acquire('uploadCards', () => {
                    if (result.ok) {
                        commit('uploadNewDictionariesAndCardsMutation', data)
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            async uploadCardsByExcelFileAction({commit}, payload) {
                const formData = payload.formData
                const result = await cardApi.uploadExcelFile(formData)
                const data = await result.data
                lock.acquire('uploadCards', () => {
                    if (result.ok) {
                        commit('uploadNewDictionariesAndCardsMutation', data)
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            async uploadCardsByXmlFileAction({commit}, payload) {
                const formData = payload.formData
                const result = await cardApi.uploadXmlFile(formData)
                const data = await result.data
                lock.acquire('uploadCards', () => {
                    if (result.ok) {
                        commit('uploadNewDictionariesAndCardsMutation', data)
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
            async findDictionariesAndCards({commit}) {
                const result = await dictionaryApi.findDictionariesAndCards()
                const data = await result.data
                if (result.ok) {
                    commit('setDictionariesMutation', {dictionaries: data.dictionaries})
                    commit('setCardsMutation', {cards: data.cards})
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
                const result = await dictionaryApi.saveUnique(payload.dictionary)
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
                    result = await dictionaryApi.saveUniqueWithPicture(payload.formData)
                } else {
                    result = await dictionaryApi.saveUnique(payload.dictionary)
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

            async deleteDictionariesByUniqueAndCascadeCardsAction({commit}, payload) {
                const result = await dictionaryApi.deleteByUnique({unique: payload.unique})
                if (result.ok) {
                    commit('deleteDictionariesByUniqueAndCascadeCardsMutation', payload)
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
            async enterUserAction({commit}, payload) {
                let result = await signApi.in(payload)
                const data = await result.data
                console.info(data)
                if (result.ok) {
                    console.info("errors: " + data.errors)
                    if(data.user){
                        console.info("user: "+ data.user)
                    }
                    return data.errors
                }
            },
        },
    }
)
import Vue from "vue";
import Vuex from "vuex";
import frontendApi from "api/frontend";
import messagesApi from "../api/messages";
import pictureMediaApi from "../api/pictureMedia";
import languageApi from "api/language";
import categoryHierarchyApi from "api/categoryHierarchy";
import categoryApi from "api/category";
import productApi from "api/product";
import authenticationApi from "api/authentication";
import cardApi from "../api/card";
import dictionaryApi from "../api/dictionary";
import VuexPersistence from "vuex-persist";
import storeMethods from "store/storeMethods";
import vlf from "../util/vlf";
import date from "../util/date";

Vue.use(Vuex)

var AsyncLock = require('async-lock');
var lock = new AsyncLock();

let defaultBasket = {
    id: 0,
    products: [],
    amount: 0,
    sort: null,
    groupByCategory: false,
}
let defaultFavorite = {
    id: 0,
    products: [],
    sort: null,
    groupByCategory: false,
}
let defaultRecent = {
    maxCount: 100,
    id: 0,
    products: [],
    sort: null,
    groupByCategory: false,
}

const persist = new VuexPersistence(
    {
        storage: window.localStorage,
        reducer: (state) => (
            {
                messages: state.messages,
                authentication: state.authentication,
                frontend: state.frontend,
                pictureMedia: state.pictureMedia,
                lang: state.lang,
                categoryChildMapIds: state.categoryChildMapIds,
                categoryParentMapIds: state.categoryParentMapIds,
                categoryRootIds: state.categoryRootIds,
                categoryMap: state.categoryMap,
                propertyChoices: state.propertyChoices,
                page: state.page,
                category: state.category,
                categoryChoices: state.categoryChoices,
                product: state.product,
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
            messages: [],

            authentication: {
                id: 0,
                users: [],
                user: {},
            },
            frontend: {},
            pictureMedia: {},

            categoryChildMapIds: [],
            categoryParentMapIds: [],
            categoryRootIds: [],
            categoryMap: [],

            propertyChoices: [],

            page: null,
            category: null,
            categoryChoices: [],

            basketChange: {id: 0, sum: 0, count: 0},
            favoriteChange: {id: 0, mark: null},

            basket: defaultBasket,
            favorite: defaultFavorite,
            recent: defaultRecent,

            product: null,
            sortOrder: [
                {id: 0, property: "UTCMillis", direction: "desc", name: "selectedRecently", typeMark: "number"},
                {id: 1, property: "UTCMillis", direction: "asc", name: "selectedAgo", typeMark: "number"},
                {id: 2, property: "popular", direction: "desc", name: "popular", typeMark: "number"},
                {id: 3, property: "price", direction: "asc", name: "cheap", typeMark: "number"},
                {id: 4, property: "price", direction: "desc", name: "expensive", typeMark: "number"},
                {id: 5, property: "creationLDT", direction: "desc", name: "new", typeMark: "ldt"},
                {
                    id: 6,
                    property: "vat",
                    direction: "desc",
                    name: "withDiscount",
                    typeMark: "number",
                    htmlBefore: '<i class="fas fa-fire text-danger"></i>&nbsp;'
                }
            ],

            scrollDuration: 500,
            scrollOptions: {
                container: 'body',
                easing: 'ease-in',
                offset: -100,
                force: true,
                cancelable: true,
                onStart: function (element) {
                    // scrolling started
                },
                onDone: function (element) {
                    // scrolling is done
                },
                onCancel: function () {
                    // scrolling has been interrupted
                },
                x: false,
                y: true
            },

            lang: {
                id: 0,
                current: {abbr: "RU", name: "Русский"},
                list: {},
                map: {}
            },

            notifications: [{
                "mark": "registrationActivation",
                "creationLDT": "2020.12.12:10.10",
                "description": "registrationActivationDescription",
                "path": "/",
                "canRemove": "false"
            }],
        },
        getters: {
            getActionId: state => () => state.action.id,
            getCardsByDictionaryInx: state => i => {
                if (state.dictionaries.length <= i) return []
                const id = state.dictionaries[i].id
                return state.cards.filter((x) => x.dictionary.id === id)
            },
            getCardsByDictionaryId: state => id => {
                return state.cards.filter((x) => x.dictionary.id === id)
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
            getCountCardsInDictionaryById: (state, getters) => (id) => state.cards.filter(c => c.dictionary.id === id).length,
            getUniqueDictionaries: (state) => () => state.dictionaries.filter(d => d.unique === true),
            getNonUniqueDictionaries: (state) => () => state.dictionaries.filter(d => d.unique === false),
            getUniqueDictionariesUniquePropertyValues: (state, getters) => (property) =>
                [...new Set(getters.getUniqueDictionaries.map(d => d[property]))],
            getNonUniqueDictionariesUniquePropertyValues: (state, getters) => (property) =>
                [...new Set(getters.getNonUniqueDictionaries().map(d => d[property]))],
            isNeedCheckDictionaryUnique: (state, getters) => (sourceId, destId) => {
                return !getters.getDictionaryById(sourceId).unique && getters.getDictionaryById(destId).unique
            },
            sortedMessages: state => state.messages.sort((a, b) => -(a.id - b.id)),
            getUrl: state => part => decodeURI(encodeURI(state.frontend.config.url)).concat(part),
            getLangId: state => () => state.lang.id,
            getBasketChangeId: state => () => state.basketChange.id,
            getBasketId: state => () => state.basket.id,
            getFavoriteId: state => () => state.favorite.id,
            getRecentId: state => () => state.recent.id,
            getFavoriteChangeId: state => () => state.favoriteChange.id,
            getCategoryChildsById: state => categoryId => {
                let childs = state.categoryParentMapIds[categoryId].map((x) => {
                    return state.categoryMap[x]
                })
                return JSON.parse(JSON.stringify(childs))
            },
            getCategoryFullPathById: (state, getters) => categoryId => {
                let childs = getters.getCategoryChildsById(categoryId)
                let path = ''
                for (let i = 0; i < childs.length; i++) {
                    path = path + '/' + childs[i].path
                }
                return path
            },
            isFavoriteProduct: state => id => {
                let ids = state.favorite.products.map((x) => {
                    return x.id
                })
                return ids.indexOf(id) !== -1
            },
            getSortOrderByIndex: state => index => {
                return JSON.parse(JSON.stringify(state.sortOrder[index]))
            },
            getMessage: state => key => {
                return state.lang.map[key]
            },
            getMessages: (state, getters) => keys => {
                let messages = {}
                for (let i = 0; i < keys.length; i++) {
                    let key = keys[i]
                    messages[key] = getters.getMessage(key)
                }
                return messages;
            },
            getLangByAbbr: state => abbr => {
                return state.lang.list[abbr]
            },
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
                state.cardsNotSaved=[
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
                if (payload.card === null) return
                state.cards = [
                    ...state.cards,
                    payload.card
                ]
            },

            updateCardMutation(state, payload) {
                if (payload.card === null) return
                const i = state.cards.findIndex(c => c.id === payload.card.id)
                state.cards = [
                    ...state.cards.slice(0, i),
                    payload.card,
                    ...state.cards.slice(i + 1)
                ]
            },

            removeCardMutation(state, card) {
                const i = state.cards.findIndex(c => c.id === card.id)
                if (i > -1) {
                    state.cards = [
                        ...state.cards.slice(0, i),
                        ...state.cards.slice(i + 1)
                    ]
                }
            },
            setCardsMutation(state, payload) {
                state.cards = payload.cards
            },

            defaultCardsMutation(state) {
                state.cards = []
            },

            removeCardsMutation(state, payload) {
                const ids = payload.cards.map((c) => c.id)
                state.cards = state.cards.filter((c) => {
                    return ids.indexOf(c.id) < 0;
                })
            },
            addCardsMutation(state, payload) {
                state.cards = [
                    ...state.cards,
                    ...payload.cards,
                ]
            },
            updateCardsMutation(state, payload) {
                if (payload.cards.length > 0) {
                    const ids = state.cards.map((c) => c.id)
                    payload.cards.forEach((c, i) => {
                        let iIds = ids.indexOf(c.id)
                        if (iIds >= 0) {
                            state.cards.splice(iIds, 1, c)
                        }
                    })
                }
            },

            // upload dictionaries and cards
            uploadNewDictionariesAndCardsMutation(state, payload) {
                this.commit('addDictionariesMutation', payload)
                this.commit('addCardsMutation', payload)
            },

            // dictionary
            addDictionaryMutation(state, payload) {
                state.dictionaries = [
                    ...state.dictionaries,
                    payload.dictionary
                ]
            },
            addDictionariesMutation(state, payload) {
                state.dictionaries = [
                    ...state.dictionaries,
                    ...payload.dictionaries
                ]
            },
            updateDictionaryMutation(state, dictionary) {
                const i = state.dictionaries.findIndex(d => d.id === dictionary.id)
                state.dictionaries = [
                    ...state.dictionaries.slice(0, i),
                    dictionary,
                    ...state.dictionaries.slice(i + 1)
                ]
            },
            deleteDictionaryByIdMutation(state, payload) {
                const i = state.dictionaries.findIndex(d => d.id === payload.id)
                if (i > -1) {
                    state.dictionaries = [
                        ...state.dictionaries.slice(0, i),
                        ...state.dictionaries.slice(i + 1)
                    ]
                    state.cards = state.cards.filter(c => c.dictionary.id !== payload.id)
                }
            },
            deleteDictionariesByUniqueAndCascadeCardsMutation(state, payload) {
                state.dictionaries = state.dictionaries.filter(d => d.unique !== payload.unique)
                state.cards = state.cards.filter(c => c.dictionary.unique !== payload.unique)

            },
            setDictionariesMutation(state, payload) {
                state.dictionaries = payload.dictionaries
            },

            // message
            addMessageMutation(state, message) {
                state.messages = [
                    ...state.messages,
                    message
                ]
            },
            updateMessageMutation(state, message) {
                const updateIndex = state.messages.findIndex(item => item.id === message.id)
                state.messages = [
                    ...state.messages.slice(0, updateIndex),
                    message,
                    ...state.messages.slice(updateIndex + 1)
                ]
            },
            removeMessageMutation(state, message) {
                const i = state.messages.findIndex(m => m.id === message.id)
                if (i > -1) {
                    state.messages = [
                        ...state.messages.slice(0, i),
                        ...state.messages.slice(i + 1)
                    ]
                }
            },
            getMessagesMutation(state, data) {
                state.messages = data
            },
            updateFrontendMutation(state, data) {
                state.frontend = data
            },
            getPictureMediaMutation(state, data) {
                state.pictureMedia = data
            },
            getCategoryHierarchyMutation(state, data) {
                state.categoryChildMapIds = data.childMapIds
                state.categoryParentMapIds = data.parentMapIds
                state.categoryRootIds = data.rootIds
                state.categoryMap = data.map
                state.categoryVersion = data.version
            },

            getCategoryPageMutation(state, data) {
                state.page = data['page']
                state.category = data['category']
                if (data['isNeedPropertyChoices']) {
                    state.propertyChoices = data['propertyChoices']
                }
            },
            getProductPageMutation(state, data) {
                state.category = data.category
                state.product = data
            },

            addBasketProductMutation(state, data) {
                let millis = date.getUTCMilliseconds(new Date())
                let priceWithDiscount = data.product.priceWithDiscount
                let count = data.count
                state.basketChange.sum = storeMethods.multiply(priceWithDiscount, count, 2)
                state.basketChange.count = count
                let amount = state.basket.amount + state.basketChange.sum
                state.basket.amount = storeMethods.round(amount, 2)
                state.basketChange.id = millis
                data.product["basketUTCMillis"] = millis
                let ids = state.basket.products.map((x) => {
                    return x.id
                })
                let index = ids.indexOf(data.product.id)
                if (index === -1) {
                    data.product["count"] = count
                    state.basket.products.push(data.product)
                } else {
                    state.basket.products[index]["count"] += count
                }
            },

            updateBasketCountOfProductMutation(state, data) {
                let ids = state.basket.products.map((x) => {
                    return x.id
                })
                let index = ids.indexOf(data.product.id)
                if (index === -1) return
                let deltaCount = data.count - state.basket.products[index].count
                let priceWithDiscount = state.basket.products[index].priceWithDiscount
                let deltaSum = storeMethods.multiply(priceWithDiscount, deltaCount, 2)
                state.basket.amount = storeMethods.round(state.basket.amount + deltaSum, 2)
                state.basket.products[index].count = data.count
            },

            removeBasketMutation(state) {
                state.basket = defaultBasket
            },

            removeBasketProductByIdMutation(state, deleteIndex) {
                let ids = state.basket.products.map((x) => {
                    return x.id
                })
                let index = ids.indexOf(deleteIndex)
                if (index === -1) return
                let price = state.basket.products[index].priceWithDiscount
                let count = state.basket.products[index].count
                let sum = storeMethods.multiply(price, count, 2)
                state.basket.products = [
                    ...state.basket.products.slice(0, index),
                    ...state.basket.products.slice(index + 1)
                ]
                let amount = state.basket.amount - sum
                amount = storeMethods.round(amount, 2)
                state.basket.amount = amount
            },

            updateBasketAmountMutation(state) {
                let amount = 0
                for (let i = 0; i < state.basket.products.length; i++) {
                    let price = state.basket.products[i].priceWithDiscount
                    let count = state.basket.products[i].count
                    amount += storeMethods.multiply(price, count, 2)
                }
                amount = storeMethods.round(amount, 2)
                state.basket.amount = amount
            },


            addFavoriteProductMutation(state, product) {
                let millis = date.getUTCMilliseconds(new Date())
                state.favoriteChange.id = millis
                state.favoriteChange.mark = '+1'
                product["favoriteUTCMillis"] = millis
                let ids = state.favorite.products.map((x) => {
                    return x.id
                })
                let index = ids.indexOf(product.id)
                if (index === -1) {
                    state.favorite.products.push(product)
                }
            },

            addRecentProductMutation(state, product) {
                let millis = date.getUTCMilliseconds(new Date())
                let ids = state.recent.products.map((x) => {
                    return x.id
                })
                let index = ids.indexOf(product.id)
                if (index !== -1) {
                    this.commit('removeProductFromRecentMutation', product)
                }
                if (state.recent.products.length >= state.recent.maxCount) {
                    this.commit('removeOlderProductFromRecentMutation', product)
                }
                product["recentUTCMillis"] = millis
                state.recent.products.push(product)
            },

            removeProductFromRecentMutation(state, product) {
                let ids = state.recent.products.map((x) => {
                    return x.id
                })
                let index = ids.indexOf(product.id)
                if (index === -1) return
                state.recent.products = [
                    ...state.recent.products.slice(0, index),
                    ...state.recent.products.slice(index + 1)
                ]
            },
            removeOlderProductFromRecentMutation(state, product) {
                let ldts = state.recent.products.map((x) => {
                    return parseInt(x["recentUTCMillis"], 10)
                })
                let min = Math.min(ldts);
                let index = ldts.indexOf(min)
                if (index === -1) return
                state.recent.products = [
                    ...state.recent.products.slice(0, index),
                    ...state.recent.products.slice(index + 1)
                ]
            },

            removeProductFromFavoritesMutation(state, product) {
                let ids = state.favorite.products.map((x) => {
                    return x.id
                })
                let index = ids.indexOf(product.id)
                if (index === -1) return
                state.favoriteChange.mark = '-1'
                state.favoriteChange.id = date.getUTCMilliseconds(new Date())
                state.favorite.products = [
                    ...state.favorite.products.slice(0, index),
                    ...state.favorite.products.slice(index + 1)
                ]
            },

            removeFavoriteProductsMutation(state) {
                state.favorite = defaultFavorite
            },
            removeRecentProductsMutation(state) {
                state.recent = defaultRecent
            },
            favoriteSetSortMutation(state, sort) {
                state.favorite.sort = sort
            },
            recentSetSortMutation(state, sort) {
                state.recent.sort = sort
            },
            basketSetSortMutation(state, sort) {
                state.basket.sort = sort
            },
            favoriteUpdateMutation(state, products) {
                state.favorite.products = products
            },
            basketUpdateMutation(state, products) {
                state.basket.products = products
            },
            recentUpdateMutation(state, products) {
                state.recent.products = products
            },

            syncBasketStateWithLocalMutation(state, basketLocal) {
                lock.acquire('basket', () => {
                    if (typeof basketLocal === 'undefined' || basketLocal === null) {
                        //
                    } else {
                        if (basketLocal.id > state.basket.id) {
                            state.basket = basketLocal
                        }
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            basketSyncLocalWithStateMutation(state) {
                let id = date.getUTCMilliseconds(new Date())
                let basketCopy = Object.assign({}, state.basket)
                basketCopy.id = id
                vlf.setItem('basket', basketCopy).then(() => {
                    state.basket.id = id
                })
            },
            syncFavoriteStateWithLocalMutation(state, favoriteLocal) {
                lock.acquire('favorite', () => {
                    if (typeof favoriteLocal === 'undefined' || favoriteLocal === null) {
                        //
                    } else {
                        if (favoriteLocal.id > state.favorite.id) {
                            state.favorite = favoriteLocal
                        }
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            syncRecentStateWithLocalMutation(state, recentLocal) {
                lock.acquire('recent', () => {
                    if (typeof recentLocal === 'undefined' || recentLocal === null) {
                        //
                    } else {
                        if (recentLocal.id > state.recent.id) {
                            state.recent = recentLocal
                        }
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
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

            favoriteSyncLocalWithStateMutation(state) {
                let id = date.getUTCMilliseconds(new Date())
                let favoriteCopy = Object.assign({}, state.favorite)
                favoriteCopy.id = id
                vlf.setItem('favorite', favoriteCopy).then(() => {
                    state.favorite.id = id
                })
            },
            recentSyncLocalWithStateMutation(state) {
                let id = date.getUTCMilliseconds(new Date())
                let recentCopy = Object.assign({}, state.recent)
                recentCopy.id = id
                vlf.setItem('recent', recentCopy).then(() => {
                    state.recent.id = id
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
            getLanguageMapMutation(state, props) {
                let id = date.getUTCMilliseconds(new Date())
                state.lang.map = props.data
                state.lang.current = props.lang
                state.lang.id = id
            },
            getLanguageListMutation(state, data) {
                state.lang.list = data
            },
            getAuthenticationMutation(state, data) {
                state.authentication = data
            },
            getActivationMutation(state, data) {
                if (state.authentication.user.id === data.id) {
                    state.authentication.user.active = true
                }
            },
            removeNotificationMutation(state, index) {
                state.notifications = [
                    ...state.notifications.slice(0, index),
                    ...state.notifications.slice(index + 1)
                ]
            }
        },
        actions: {
            async addMessageAction({commit, state}, message) {
                const result = await messagesApi.add(message)
                const data = await result.json()
                const index = state.messages.findIndex(item => item.id === data.id)
                if (index > -1) {
                    commit('updateMessageMutation', data)
                } else {
                    commit('addMessageMutation', data)
                }
            },
            async updateMessageAction({commit}, message) {
                const result = await messagesApi.update(message)
                const data = await result.json()
                commit('updateMessageMutation', data)
            },
            async removeMessageAction({commit}, message) {
                const result = await messagesApi.remove(message.id)
                if (result.ok) {
                    commit('removeMessageMutation', message)
                }
            },
            async getMessagesAction({commit}) {
                const result = await messagesApi.get()
                const data = await result.data
                if (result.ok) {
                    commit('getMessagesMutation', data)
                }
            },

            async updateFrontendAction({commit}) {
                const result = await frontendApi.getFrontend()
                const data = await result.data
                if (result.ok) {
                    commit('updateFrontendMutation', data)
                }
            },

            async setFrontendAction({commit}, data) {
                commit('updateFrontendMutation', data)
            },

            async getFrontendAction({commit}) {
                const result = await frontendApi.getFrontend()
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

            async getCategoryHierarchyAction({commit}) {
                const result = await categoryHierarchyApi.getHierarchy()
                const data = await result.data
                if (result.ok) {
                    commit('getCategoryHierarchyMutation', data)
                }
            },

            async getCategoryPageAction({commit}, categoryChainUrl) {
                const result = await categoryApi.getCategoryPage(categoryChainUrl)
                const data = await result.data
                if (result.ok) {
                    commit('getCategoryPageMutation', data)
                }
            },
            async getProductPageAction({commit}, id) {
                const result = await productApi.findById(id)
                const data = await result.data
                if (result.ok) {
                    commit('getProductPageMutation', data)
                }
            },
            async addBasketProductAction({commit}, data) {
                lock.acquire('basket', () => {
                    commit('addBasketProductMutation', data)
                    commit('basketSyncLocalWithStateMutation')
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },

            async removeBasketAction({commit}) {
                lock.acquire('basket', () => {
                    commit('removeBasketMutation')
                    commit('basketSyncLocalWithStateMutation')
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },

            async removeBasketProductByIdAction({commit}, removeIndex) {
                lock.acquire('basket', () => {
                    commit('removeBasketProductByIdMutation', removeIndex)
                    commit('basketSyncLocalWithStateMutation')
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },

            async updateBasketCountOfProductAction({commit}, data) {
                lock.acquire('basket', () => {
                    commit('updateBasketCountOfProductMutation', data)
                    commit('basketSyncLocalWithStateMutation')
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },

            async addFavoriteProductAction({commit}, product) {
                lock.acquire('favorite', () => {
                    commit('addFavoriteProductMutation', product)
                    commit('favoriteSyncLocalWithStateMutation')
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            async removeFavoriteProductAction({commit}, product) {
                lock.acquire('favorite', () => {
                    commit('removeProductFromFavoritesMutation', product)
                    commit('favoriteSyncLocalWithStateMutation')
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },

            async removeFavoriteAction({commit}) {
                lock.acquire('favorite', () => {
                    commit('removeFavoriteProductsMutation')
                    commit('favoriteSyncLocalWithStateMutation')
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            async favoriteSetSortAction({commit}, sort) {
                commit('favoriteSetSortMutation', sort)
            },
            async recentSetSortAction({commit}, sort) {
                commit('recentSetSortMutation', sort)
            },
            async basketSetSortAction({commit}, sort) {
                commit('basketSetSortMutation', sort)
            },
            async favoriteUpdateAction({commit}, products) {
                lock.acquire('favorite', () => {
                    commit('favoriteUpdateMutation', products)
                    commit('favoriteSyncLocalWithStateMutation')
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            async basketUpdateAction({commit}, products) {
                lock.acquire('basket', () => {
                    commit('basketUpdateMutation', products)
                    commit('basketSyncLocalWithStateMutation')
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            async recentUpdateAction({commit}, products) {
                lock.acquire('recent', () => {
                    commit('recentUpdateMutation', products)
                    commit('recentSyncLocalWithStateMutation')
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },

            async removeCookiesAndStateAction({commit, dispatch}) {
                await dispatch('removeBasketAction')
                await dispatch('removeFavoriteAction')
            },

            async syncBasketStateWithLocalAction({commit}) {
                let basketLocal = await vlf.getItem('basket')
                commit('syncBasketStateWithLocalMutation', basketLocal)
            },

            async syncFavoriteStateWithLocalAction({commit}) {
                let favoriteLocal = await vlf.getItem('favorite')
                commit('syncFavoriteStateWithLocalMutation', favoriteLocal)
            },

            async syncRecentStateWithLocalAction({commit}) {
                let recentLocal = await vlf.getItem('recent')
                commit('syncRecentStateWithLocalMutation', recentLocal)
            },

            async syncAuthenticationStateWithLocalAction({commit}) {
                let authenticationLocal = await vlf.getItem('authentication')
                commit('syncAuthenticationStateWithLocalMutation', authenticationLocal)
            },

            async removeRecentAction({commit}) {
                lock.acquire('recent', () => {
                    commit('removeRecentProductsMutation')
                    commit('recentSyncLocalWithStateMutation')
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            async addRecentProductAction({commit}, product) {
                lock.acquire('recent', () => {
                    commit('addRecentProductMutation', product)
                    commit('recentSyncLocalWithStateMutation')
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            async getLanguageMapAction({commit}, lang) {
                const result = await languageApi.getMap(lang.abbr)
                const data = await result.data
                if (result.ok) {
                    let props = {lang: lang, data: data}
                    commit('getLanguageMapMutation', props)
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
                const result = await authenticationApi.getAuthentication(getters.getUsersTokens)
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
            async removeNotificationAction({commit}, index) {
                commit('removeNotificationMutation', index)
            },
            async getActivationAction({commit}, id) {
                const result = await authenticationApi.getActivation(id)
                const data = await result.data
                lock.acquire('authentication', () => {
                    if (result.ok) {
                        commit('getActivationMutation', data)
                        commit('authenticationSyncLocalWithStateMutation')
                        return data
                    } else {
                        return null
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
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
            async removeCardsAction({commit}, payload) {
                if (payload.cards.length === 1) {
                    const result = await cardApi.remove(payload.cards[0].id)
                    const data = await result.data
                    if (result.ok) {
                        commit('removeCardMutation', payload.cards[0])
                    }
                } else {
                    const result = await cardApi.deleteByIdIn({ids: payload.cards.map(c => c.id)})
                    if (result.ok) {
                        commit('removeCardsMutation', {cards: payload.cards})
                    }
                }
            },

            async updateCardsAction({commit}, payload) {
                if (payload.cards.length === 1) {
                    const result = await cardApi.updateUnique(payload.cards[0])
                    const data = await result.data
                    if (result.ok) {
                        commit('updateCardMutation', {card: data.saved})
                        commit('addCardNotSavedMutation', {card: data.notSaved})
                    }
                } else {
                    const result = await cardApi.updateAllUnique(payload.cards)
                    const data = await result.data
                    if (result.ok) {
                        commit('updateCardsMutation', {cards: data.saved})
                        commit('addCardsNotSavedMutation', {cards: data.notSaved})
                    }
                }
            },
            async cardsChangeDictionariesAction({commit, state, getters, dispatch}, payload) {
                if (payload.cards && payload.cards.length > 0) {
                    const isNeedCheckUnique = getters.isNeedCheckDictionaryUnique(payload.sourceId, payload.destId)
                    console.info("start: " + date.getUTCMilliseconds(new Date()))
                    if(payload.cards.length === 1){
                        const result = await cardApi.changeDictionary(payload.cards[0].id, payload.destId, isNeedCheckUnique)
                        const data = await result.data
                        if (result.ok) {
                            console.info("end: " + date.getUTCMilliseconds(new Date()))
                            commit('updateCardMutation', {card: data.saved})
                            commit('addCardNotSavedMutation', {card: data.notSaved})
                        }
                    }else {
                        const result = await cardApi.changeDictionaries(payload.cards.map(c => c.id), payload.destId, isNeedCheckUnique)
                        const data = await result.data
                        if (result.ok) {
                            console.info("end: " + date.getUTCMilliseconds(new Date()))
                            commit('updateCardsMutation', {cards: data.saved})
                            commit('addCardsNotSavedMutation', {cards: data.notSaved})
                        }
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

            async deleteDictionaryByIdAction({commit, state}, payload) {
                const result = await dictionaryApi.remove(payload.id)
                if (result.ok) {
                    commit('deleteDictionaryByIdMutation', {id: payload.id})
                }
            },

            async deleteDictionariesByUniqueAndCascadeCardsAction({commit}, payload) {
                const result = await dictionaryApi.deleteByUnique({unique: payload.unique})
                if (result.ok) {
                    commit('deleteDictionariesByUniqueAndCascadeCardsMutation', payload)
                }
            },


            //dragdrop
            dragdropStartAction({commit}, payload) {
                commit('setDragdropStartMutation', payload)
            },
            dragdropEndAndExecuteAction({commit, state, dispatch}, payload) {
                console.info("dragend: " + date.getUTCMilliseconds(new Date()))
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
        },
    }
)
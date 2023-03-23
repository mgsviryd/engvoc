import Vue from "vue";
import Vuex from "vuex";
import frontendApi from "api/frontend";
import messagesApi from "../api/messages";
import pictureMediaApi from "api/pictureMedia";
import languageApi from "api/language";
import categoryHierarchyApi from "api/categoryHierarchy";
import categoryApi from "api/category";
import productApi from "api/product";
import authenticationApi from "api/authentication";
import cardApi from "../api/card";
import VuexPersistence from "vuex-persist";
import storeMethods from "store/storeMethods";
import vlf from "../util/vlf";
import date from "../util/date";
import string from "../util/string";

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
            drapDropPayload: {},
            cards: {
                db: {
                    dictionaries: [],
                    cards: [],
                },
                upload: {
                    filenames: [],
                    dictionaries: [],
                    cards: [],
                }
            },
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
            getCardsDBByDictionaryInx: state => i => {
                return state.cards.db.cards[i]
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
            dragMutation(state, payload){
                state.drapDropPayload = payload
            },
            dropMutation(state, payload){
                payload = {
                    // status: "drop",
                    type: "card",
                    pull: "replace", // copy
                    push: "add", // replace
                    item: card,
                    id: i,
                    markSource: this.markSource,
                    sourceInx: this.dictionaryInx
                }
                if (payload.type === "card"){
                    if(payload.markSource === "upload"){

                    }
                    if(payload.markSource === "db"){

                    }
                }
            },
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
                const deleteIndex = state.messages.findIndex(item => item.id === message.id)
                if (deleteIndex > -1) {
                    state.messages = [
                        ...state.messages.slice(0, deleteIndex),
                        ...state.messages.slice(deleteIndex + 1)
                    ]
                }
            },
            getMessageMutation(state, data) {
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

            getCardsUploadFileMutation(state, payload) {
                const data = payload.data
                const d = new Date
                const millis = date.getUTCMilliseconds(d)
                const dictionaries = []
                const lookup = {};
                for (let i = 0; i < data.length; i++) {
                    let dictionary = data[i].dictionary
                    dictionary.id = millis + i
                    let name = dictionary.name
                    if (!(name in lookup)) {
                        lookup[name] = 1;
                        dictionaries.push(dictionary)
                    }
                }
                const filenames = []
                for (let i = 0; i < dictionaries.length; i++) {
                    filenames.push({name: payload.name, date: d})
                }
                const cards = []
                for (let i = 0; i < dictionaries.length; i++) {
                    cards.push(data.filter(item => string.isEqual(item.dictionary.name, dictionaries[i].name)))
                }
                state.cards.upload.dictionaries.push(...dictionaries)
                state.cards.upload.filenames.push(...filenames)
                state.cards.upload.cards.push(...cards)
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
            async getMessageAction({commit}) {
                const result = await messagesApi.get()
                const data = await result.data
                if (result.ok) {
                    commit('getMessageMutation', data)
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
            async getUploadExcelFilesAction({commit}, payload) {
                const formData = payload.formData
                const result = await cardApi.uploadExcelFiles(formData)
                const data = await result.data
                lock.acquire('cardUpload', () => {
                    if (result.ok) {
                        commit('getCardsUploadFileMutation', {data: data, name: payload.name})
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            async getUploadXmlFilesAction({commit}, payload) {
                const formData = payload.formData
                const result = await cardApi.uploadXmlFiles(formData)
                const data = await result.data
                lock.acquire('cardUpload', () => {
                    if (result.ok) {
                        commit('getCardsUploadFileMutation', {data: data, name: payload.name})
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            async getUploadExcelFileAction({commit}, payload) {
                const formData = payload.formData
                const result = await cardApi.uploadExcelFile(formData)
                const data = await result.data
                lock.acquire('cardUpload', () => {
                    if (result.ok) {
                        commit('getCardsUploadFileMutation', {data: data, name: payload.name})
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
            async getUploadXmlFileAction({commit}, payload) {
                const formData = payload.formData
                const result = await cardApi.uploadXmlFile(formData)
                const data = await result.data
                lock.acquire('cardUpload', () => {
                    if (result.ok) {
                        commit('getCardsUploadFileMutation', {data: data, name: payload.name})
                    }
                }).catch((err) => {
                    console.log(err) // output: error
                })
            },
        },
    }
)
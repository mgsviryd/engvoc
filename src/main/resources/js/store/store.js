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
            dragdropActions: {
                db: {
                    removeAllAction: "cardDbRemoveAllAction",
                    addAllAction: "cardDbAddAllAction",
                    updateAllAction: "cardDbUpdateAllAction",
                    addUpdateAllAction: "cardDbAddUpdateAllAction",
                    updateDictionaryAllAction: "cardDbUpdateDictionaryAllAction"
                },
                upload: {
                    removeAllAction: "cardUploadRemoveAllAction",
                    addAllAction: "cardUploadAddAllAction",
                    updateAllAction: "cardUploadUpdateAllAction",
                    addUpdateAllAction: "cardUploadAddUpdateAllAction",
                },
            },
            pictures: [],
            action: {
                id: 0,
                errors: {},
            },
            cards: {
                db: {
                    sourceMark: "db",
                    dictionaries: [],
                    cards: [],
                },
                upload: {
                    sourceMark: "upload",
                    dictionaryId: 0,
                    cardId: 0,
                    dictionaries: [],
                    cards: [],
                },
                notSaved: [],
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
            incrementAndGetCardUploadId: state => () => {
                state.cards.upload.cardId = state.cards.upload.cardId + 1;
                return state.cards.upload.sourceMark + state.cards.upload.cardId
            },
            incrementAndGetDictionaryUploadId: state => () => {
                state.cards.upload.dictionaryId = state.cards.upload.dictionaryId + 1;
                return state.cards.upload.sourceMark + state.cards.upload.dictionaryId
            },
            getActionId: state => () => state.action.id,
            getCardsDBByDictionaryInx: state => i => {
                if (state.cards.db.dictionaries.length <= i) return []
                const dictionaryId = state.cards.db.dictionaries[i].id
                return state.cards.db.cards.filter((x) => x.dictionary.id === dictionaryId)
            },
            getCardsByDictionaryDbId: state => id => {
                return state.cards.db.cards.filter((x) => x.dictionary.id === id)
            },
            getCardsByDictionaryUploadId: state => id => {
                const inx = state.cards.upload.dictionaries.findIndex(item => item.id === id)
                return state.cards.upload.cards[inx]
            },
            isDbSource: state => (source) => {
                return source === state.cards.db.sourceMark
            },
            isUploadSource: state => (source) => {
                return source === state.cards.upload.sourceMark
            },
            getCardsUploadCreationLDTs: state => [...new Set(state.cards.upload.dictionaries.map(item => item.creationLDT))],
            isDbDictionaryExists: state => (id) => {
                return state.cards.db.dictionaries.findIndex(item => item.id === id) >= 0
            },
            isUploadDictionaryExists: state => (id) => {
                return state.cards.upload.dictionaries.findIndex(item => item.id === id) >= 0
            },
            getDbDictionaryInx: state => (id) => {
                return state.cards.db.dictionaries.findIndex(item => item.id === id)
            },
            getUploadDictionaryInx: state => (id) => {
                return state.cards.upload.dictionaries.findIndex(item => item.id === id)
            },
            getDbDictionaryById: (state, getters) => (id) => {
                return state.cards.db.dictionaries[getters.getDbDictionaryInx(id)]
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

            // card
            addCardsNotSavedMutation(state, payload) {
                if (payload.cards === null || payload.cards.length === 0) return
                state.cards.notSaved.push(...payload.cards)
            },
            addCardNotSavedMutation(state, payload) {
                if (payload.card === null) return
                state.cards.notSaved.push(payload.card)
            },
            defaultCardsNotSavedMutation(state) {
                state.cards.notSaved = []
            },
            deleteCardsNotSavedMutation(state, payload) {
                if (payload.cards === null || payload.cards.length === 0) return
                const ids = payload.cards.map(item => item.id)
                state.cards.notSaved = state.cards.notSaved.filter(item => ids.findIndex(item.id) < 0)
            },

            addCardDbMutation(state, payload) {
                if (payload.card === null) return
                state.cards.db.cards = [
                    ...state.cards.db.cards,
                    payload.card
                ]
            },

            updateCardDbMutation(state, card) {
                if (card === null) return
                const updateIndex = state.cards.db.cards.findIndex(item => item.id === card.id)
                state.cards.db.cards = [
                    ...state.cards.db.cards.slice(0, updateIndex),
                    card,
                    ...state.cards.db.cards.slice(updateIndex + 1)
                ]
            },
            addUpdateCardDbMutation(state, card) {
                if (card === null) return
                const cards = []
                cards.push(card)
                const ids = state.cards.db.cards.map((item) => item.id)
                const newCards = []
                cards.forEach((item, i) => {
                    let iIds = ids.indexOf((item.id))
                    if (iIds >= 0) {
                        state.cards.db.cards[iIds] = item
                    } else {
                        newCards.push(item)
                    }
                })
                state.cards.db.cards.push(...newCards)
            },
            removeCardDbMutation(state, card) {
                const deleteIndex = state.cards.db.cards.findIndex(item => item.id === card.id)
                if (deleteIndex > -1) {
                    state.cards.db.cards = [
                        ...state.cards.db.cards.slice(0, deleteIndex),
                        ...state.cards.db.cards.slice(deleteIndex + 1)
                    ]
                }
            },
            setCardsDbMutation(state, data) {
                state.cards.db.cards = data
            },

            deleteCardsUploadMutation(state) {
                state.cards.upload.cardId = 0
                state.cards.upload.dictionaryId = 0
                state.cards.upload.cards = []
                state.cards.upload.dictionaries = []
            },
            deleteCardsDbMutation(state) {
                state.cards.db.cards = []
                state.cards.db.dictionaries = []
            },

            getCardsUploadFileMutation(state, payload) {
                const data = payload.data
                const d = new Date()
                let cardId = 0
                data.forEach((card, i) => {
                    card.id = cardId++
                })
                const dictionaries = []
                const lookup = {};
                let dictionaryId = 0
                for (let i = 0; i < data.length; i++) {
                    let dictionary = data[i].dictionary
                    dictionary.creationLDT = d
                    dictionary.source = payload.name
                    dictionary.id = dictionaryId++
                    let name = dictionary.name
                    if (!(name in lookup)) {
                        lookup[name] = 1;
                        dictionaries.push(dictionary)
                    }
                }
                const cards = []
                for (let i = 0; i < dictionaries.length; i++) {
                    const dicCards = data.filter(item => string.isEqual(item.dictionary.name, dictionaries[i].name))
                    cards.push(dicCards)
                }
                state.cards.upload.dictionaries.push(...dictionaries)
                state.cards.upload.cards.push(...cards)
            },


            cardUploadRemoveAllMutation(state, payload) {
                const ids = payload.cards.map((item) => item.id)
                state.cards.upload.cards.splice(payload.inx, 1, state.cards.upload.cards[payload.inx].filter((item) => {
                    return ids.indexOf(item.id) < 0;
                }))
            },
            cardUploadAddAllMutation(state, payload) {
                payload.cards.forEach((item, i) => item.id = this.getters.incrementAndGetCardUploadId())
                state.cards.upload.cards[payload.inx].push(...payload.cards)
            },
            cardUploadUpdateAllMutation(state, payload) {
                const ids = payload.cards.map((item) => item.id)
                state.cards.upload.cards[payload.inx].forEach((item, i) => {
                    let iIds = ids.indexOf(item.id)
                    if (iIds > 0) {
                        state.cards.upload.cards[payload.inx].splice(i, 1, payload.cards[iIds])
                    }
                });
            },
            cardUploadAddUpdateAllMutation(state, payload) {
                const ids = state.cards.upload.cards[payload.inx].map((item) => item.id)
                const newCards = []
                payload.cards.forEach((item, i) => {
                    let iIds = ids.indexOf((item.id))
                    if (iIds >= 0) {
                        state.cards.upload.cards[iIds] = item
                    } else {
                        item.id = this.getters.incrementAndGetCardUploadId()
                        newCards.push(item)
                    }
                })
                state.cards.upload.cards.push(...newCards)
            },
            cardDbRemoveAllMutation(state, payload) {
                const ids = payload.cards.map((item) => item.id)
                state.cards.db.cards = state.cards.db.cards.filter((item) => {
                    return ids.indexOf(item.id) < 0;
                })
            },
            cardDbAddAllMutation(state, payload) {
                state.cards.db.cards.push(...payload.cards)
            },
            cardDbUpdateAllMutation(state, payload) {
                if (payload.cards.length > 0) {
                    const ids = state.cards.db.cards.map((card) => card.id)
                    payload.cards.forEach((card, i) => {
                        let iIds = ids.indexOf(card.id)
                        if (iIds >= 0) {
                            state.cards.db.cards.splice(iIds, 1, card)
                        }
                    })
                }
            },
            cardDbAddUpdateAllMutation(state, payload) {
                const ids = state.cards.db.cards.map((card) => card.id)
                const newCards = []
                payload.cards.forEach((card, i) => {
                    let iIds = ids.indexOf(card.id)
                    if (iIds >= 0) {
                        state.cards.db.cards[iIds] = card
                    } else {
                        newCards.push(card)
                    }
                })
                state.cards.db.cards.push(...newCards)
            },

            // dictionary
            addDictionaryDbMutation(state, payload) {
                if (payload.dictionary === null) return
                state.cards.db.dictionaries = [
                    ...state.cards.db.dictionaries,
                    payload.dictionary
                ]
            },
            updateDictionaryDbMutation(state, dictionary) {
                const updateIndex = state.cards.db.dictionaries.findIndex(item => item.id === dictionary.id)
                state.cards.db.dictionaries = [
                    ...state.cards.db.dictionaries.slice(0, updateIndex),
                    dictionary,
                    ...state.cards.db.dictionaries.slice(updateIndex + 1)
                ]
            },
            deleteDictionaryDbMutation(state, id) {
                const deleteIndex = state.cards.db.dictionaries.findIndex(item => item.id === id)
                if (deleteIndex > -1) {
                    state.cards.db.dictionaries = [
                        ...state.cards.db.dictionaries.slice(0, deleteIndex),
                        ...state.cards.db.dictionaries.slice(deleteIndex + 1)
                    ]
                    state.cards.db.cards = state.cards.db.cards.filter(item => item.dictionary.id !== id)
                }
            },
            setDictionariesDbMutation(state, data) {
                state.cards.db.dictionaries = data
            },

            addDictionaryUploadMutation(state, payload) {
                state.cards.upload.dictionaries = [
                    ...state.cards.upload.dictionaries,
                    payload.dictionary
                ]
                state.cards.upload.cards = [
                    ...state.cards.upload.cards,
                    []
                ]

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
                const deleteIndex = state.messages.findIndex(item => item.id === message.id)
                if (deleteIndex > -1) {
                    state.messages = [
                        ...state.messages.slice(0, deleteIndex),
                        ...state.messages.slice(deleteIndex + 1)
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

            // card
            async cardUploadRemoveAllAction({commit, getters}, payload) {
                payload.inx = getters.getUploadDictionaryInx(payload.id)
                commit('cardUploadRemoveAllMutation', payload)
            },
            async cardUploadAddAllAction({commit, getters}, payload) {
                payload.inx = getters.getUploadDictionaryInx(payload.id)
                commit('cardUploadAddAllMutation', payload)
            },
            async cardUploadUpdateAllAction({commit, getters}, payload) {
                payload.inx = getters.getUploadDictionaryInx(payload.id)
                commit('cardUploadUpdateAllMutation', payload)
            },
            async cardUploadAddUpdateAllAction({commit, getters}, payload) {
                payload.inx = getters.getUploadDictionaryInx(payload.id)
                commit('cardUploadAddUpdateAllMutation', payload)
            },
            async cardDbRemoveAllAction({commit}, payload) {
                if (payload.cards.length === 1) {
                    const result = await cardApi.remove(payload.cards[0].id)
                    const data = await result.data
                    if (result.ok) {
                        commit('removeCardDbMutation', payload.cards[0])
                    }
                } else {
                    const result = await cardApi.deleteByIdIn({ids: payload.cards.map(x => x.id)})
                    if (result.ok) {
                        commit('cardDbRemoveAllMutation', {cards: payload.cards})
                    }
                }
            },
            async cardDbAddAllAction({commit, state, getters}, payload) {
                payload.cards.forEach(item => item.id = null)
                const inx = getters.getDbDictionaryInx(payload.id)
                payload.cards.forEach((x) => x.dictionary = state.cards.db.dictionaries[inx])
                if (payload.cards.length === 1) {
                    const result = await cardApi.saveUnique(payload.cards[0])
                    const data = await result.data
                    if (result.ok) {
                        commit('addCardDbMutation', {card: data.saved})
                        commit('addCardNotSavedMutation', {card: data.notSaved})
                    }
                } else {
                    const result = await cardApi.saveAllUnique(payload.cards)
                    const data = await result.data
                    if (result.ok) {
                        commit('cardDbAddAllMutation', {cards: data.saved})
                        commit('addCardsNotSavedMutation', {cards: data.notSaved})
                    }
                }
            },
            async cardDbUpdateAllAction({commit}, payload) {
                payload.cards.forEach(item => item.id = null)
                if (payload.cards.length === 1) {
                    const result = await cardApi.updateUnique(payload.cards[0])
                    const data = await result.data
                    if (result.ok) {
                        commit('updateCardDbMutation', data.saved)
                        commit('addCardNotSavedMutation', {card: data.notSaved})
                    }
                } else {
                    const result = await cardApi.updateAllUnique(payload.cards)
                    const data = await result.data
                    if (result.ok) {
                        commit('cardDbUpdateAllMutation', {cards: data.saved})
                        commit('addCardsNotSavedMutation', {cards: data.notSaved})
                    }
                }
            },
            async cardDbAddUpdateAllAction({commit}, payload) {
                payload.cards.forEach(item => item.id = null)
                if (payload.cards.length === 1) {
                    const result = await cardApi.addUpdateUnique(payload.cards[0])
                    const data = await result.data
                    if (result.ok) {
                        commit('addUpdateCardDbMutation', data.saved)
                        commit('addCardNotSavedMutation', {card: data.notSaved})
                    }
                } else {
                    const result = await cardApi.addUpdateAllUnique(payload.cards)
                    const data = await result.data
                    if (result.ok) {
                        commit('cardDbAddUpdateAllMutation', {cards: data.saved})
                        commit('addCardsNotSavedMutation', {cards: data.notSaved})
                    }
                }
            },
            async cardDbUpdateDictionaryAllAction({commit, state, getters, dispatch}, payload) {
                if (payload.cards.length === 1) {
                    const result = await cardApi.updateDictionary(payload.cards[0].id, payload.id)
                    const data = await result.data
                    if (result.ok) {
                        commit('updateCardDbMutation', data.saved)
                        commit('addCardNotSavedMutation', {cards: data.notSaved})
                    }
                } else if (payload.cards.length > 1) {
                    const result = await cardApi.updateDictionaryAll(payload.cards.map(card => card.id), payload.id)
                    const data = await result.data
                    if (result.ok) {
                        commit('cardDbUpdateAllMutation', {cards: data.saved})
                        commit('addCardsNotSavedMutation', {cards: data.notSaved})
                    }
                }
            },
            async findDictionariesDb({commit}) {
                const result = await dictionaryApi.findAll()
                const data = await result.data
                if (result.ok) {
                    commit('setDictionariesDbMutation', data)
                }
            },
            async findCardsDb({commit}) {
                const result = await cardApi.findAll()
                const data = await result.data
                if (result.ok) {
                    commit('setCardsDbMutation', data)
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

            // dictionaryAction
            async addDictionaryDbAction({commit}, payload) {
                const result = await dictionaryApi.saveUnique(payload.dictionary)
                const data = await result.data
                if (result.ok) {
                    commit('addDictionaryDbMutation', {dictionary: data.saved})
                    commit('setActionMutation', {id: payload.actionId, errors: data.errors})
                }
            },
            async addDictionaryDbWithPictureAction({commit}, payload) {
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
                    commit('addDictionaryDbMutation', {dictionary: data.saved})
                    commit('setActionMutation', {id: payload.actionId, errors: data.errors})
                }
            },
            async addDictionaryUploadAction({commit, getters, dispatch}, payload) {
                const picture = await dispatch('addPictureAction', payload)
                payload.dictionary.id = getters.incrementAndGetDictionaryUploadId()
                payload.dictionary.picture = picture
                commit('addDictionaryUploadMutation', payload)
                commit('setActionMutation', {id: payload.actionId, errors: {}})
            },
            async deleteCardsUploadAction({commit}) {
                commit('deleteCardsUploadMutation')
            },
            async deleteCardsDbAction({commit, state}) {
                const result = await dictionaryApi.deleteByIdIn({ids: state.cards.db.dictionaries.map(item => item.id)})
                if (result.ok) {
                    commit('deleteCardsDbMutation')
                }
            },
            async deleteDictionaryDbAction({commit, state}, payload) {
                const result = await dictionaryApi.remove(payload.id)
                if (result.ok) {
                    commit('deleteDictionaryDbMutation', payload.id)
                }
            },

            //dragdrop
            dragdropStartAction({commit}, payload) {
                commit('setDragdropStartMutation', payload)
            },
            dragdropEndAndExecuteAction({commit, state, dispatch}, payload) {
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
                if (state.dragdrop.start.groups.indexOf("card") >= 0) {
                    dispatch("dragdropExecuteCardAction")
                }
            },
            dragdropExecuteCardAction({commit, state, dispatch}) {
                const actions = state.dragdropActions
                let actionsStart = actions[state.dragdrop.start.data.sourceMark];
                let actionsEnd = actions[state.dragdrop.end.data.sourceMark];

                if (state.dragdrop.start.data.sourceMark === state.dragdrop.end.data.sourceMark
                    && state.dragdrop.start.data.sourceMark === state.cards.db.sourceMark
                ) {
                    dispatch(actionsStart.updateDictionaryAllAction,
                        {
                            cards: state.dragdrop.start.data.items,
                            id: state.dragdrop.end.data.sourceId,
                        })
                    return
                }
                // console.info("passReturn")
                if (state.dragdrop.end.options.pull === "delete") {
                    dispatch(actionsStart.removeAllAction,
                        {
                            cards: state.dragdrop.start.data.items,
                            id: state.dragdrop.start.data.sourceId,
                        })
                }
                if (state.dragdrop.end.options.push === "delete") {
                    dispatch(actionsEnd.removeAllAction,
                        {
                            cards: state.dragdrop.end.data.items,
                            id: state.dragdrop.end.data.sourceId,
                        })
                }
                if (state.dragdrop.end.options.operation === "add") {
                    dispatch(actionsEnd.addAllAction,
                        {
                            cards: state.dragdrop.start.data.items,
                            id: state.dragdrop.end.data.sourceId,
                        })
                }
                if (state.dragdrop.end.options.operation === "update") {
                    dispatch(actionsEnd.updateAllAction,
                        {
                            cards: state.dragdrop.start.data.items,
                            id: state.dragdrop.end.data.sourceId,
                        })
                }
                if (state.dragdrop.end.options.operation === "addUpdate") {
                    dispatch(actionsEnd.addUpdateAllAction,
                        {
                            cards: state.dragdrop.start.data.items,
                            id: state.dragdrop.end.data.sourceId,
                        })
                }
            }
        },
    }
)
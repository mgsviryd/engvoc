import * as _ from "lodash"
export default {
    multiply(price, count, decimals) {
        if (!(price || count)) {
            return 0
        } else {
            return this.round(price * count, decimals)
        }
    },
    round(value, decimals){
        return Number(Math.round(value + 'e' + decimals) + 'e-' + decimals);
    },
    sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    },
    getCapitalizeLangPair(dictionary){
        return _.capitalize(dictionary.pair.source.lang) + _.capitalize(dictionary.pair.target.lang)
    },
}
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
    }
}
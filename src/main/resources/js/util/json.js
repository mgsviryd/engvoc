export default {
    getUniqueByProperties(array, ...property) {
        let arr = array
        for (let i = 0; i < property.length; i++) {
            arr = this.getUniqueByProperty(array, property[i])
        }
        return arr
    },
    getUniqueByProperty(array, property) {
        const lookup = {}
        let arr = []
        for (let i = 0; i < array.length; i++) {
            let prop = array[i][property]
            if (!(prop in lookup)) {
                lookup[prop] = 1;
                arr.push(array[i])
            }
        }
        return arr
    },
    isEqualByProperties(json1, json2, ...property){
        for (let i = 0; i < property.length; i++) {
            if (json1[property[i]] !== json2[property[i]]){
                return false;
            }
        }
        return true;
    }
}
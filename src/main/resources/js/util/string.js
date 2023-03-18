export default {
    getWithFirstCapital(str){
        if(typeof str === 'undefined' || str.length === 0) return ""
        return str.charAt(0).toUpperCase() + str.slice(1)
    },
    getUpperCase(str){
        if(typeof str === 'undefined' || str.length === 0) return ""
        return str.toUpperCase()
    },
    isEqual(str1, str2){
        return str1.normalize() === str2.normalize()
    }
}
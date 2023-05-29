export default {
    convertLowerToCamelCase(arrayStr){
        if (arrayStr.length === 0) return ""
        let str = arrayStr[0]
        for (let i = 1; i < arrayStr.length; i++) {
            str = str.concat(this.getWithFirstCapital(arrayStr[i]))
        }
        return str
    },
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
    },
    breakNewLinesAndGetAsHtml(str){
        const lines = str.split('\n')
        lines.forEach((l,i)=> lines[i]=lines[i].concat("<br>"))
        return lines.join('')
    },
}
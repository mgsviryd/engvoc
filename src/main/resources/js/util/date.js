export default {
    getUTCMilliseconds(date){
        if (typeof date === 'undefined' || date === null) return 0;
        return date.getTime() - (date.getTimezoneOffset() * 60000);
    },
    convertDateToUTC(date){
        return Date.UTC(date.getUTCFullYear, date.getUTCMonth, date.getUTCDate,
            date.getUTCHours, date.getUTCMinutes, date.getUTCSeconds, date.getUTCMilliseconds);
    },
    compare(a, b) {
        //  -1 : if a < b
        //   0 : if a = b
        //   1 : if a > b
        return (a > b) - (a < b)
    },
    isAfter(expected, date){
        return this.compare(expected, date) === 1
    },
    isIsoAfter(expectedIso, dateIso){
        let expected = this.parseISOString(expectedIso)
        let date = this.parseISOString(dateIso)
        return this.compare(expected, date) === 1
    },
    parseISOString(s) {
        return new Date(s);
    },
}
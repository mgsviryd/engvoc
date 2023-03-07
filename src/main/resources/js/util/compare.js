export default {
    compareBoolean(a,b){
        return (a === b) ? 0 : x ? -1 : 1;
    },
    compareDateNatural(a, b) {
        //  -1 : if a < b
        //   0 : if a = b
        //   1 : if a > b
        return (a > b) - (a < b)
    },
    compareDateReverse(a, b) {
        //  -1 : if a < b
        //   0 : if a = b
        //   1 : if a > b
        return (b > a) - (b < a)
    },
    compareDateISOStringNatural(a, b){
        let aDate = new Date(a)
        let bDate = new Date(b)
        return this.compareDateNatural(aDate, bDate)
    },
    compareDateISOStringReverse(a, b){
        let aDate = new Date(a)
        let bDate = new Date(b)
        return this.compareDateReverse(aDate, bDate)
    },
    compareStringNatural(a, b){
        return a.localeCompare(b)
    },
    compareStringReverse(a, b){
        return b.localeCompare(a)
    },
    compareNumberNatural(a, b){
        let aFloat = parseFloat(a)
        let bFloat = parseFloat(b)
        if (aFloat < bFloat) {
            return -1;
        }
        if (aFloat > bFloat) {
            return 1;
        }
        return 0;
    },
    compareNumberReverse(a, b){
        return this.compareNumberNatural(b, a)
    },
    compareDateISOStringNaturalByProperty(a, b, property){
        let aDate = new Date(a[property])
        let bDate = new Date(b[property])
        return this.compareDateNatural(aDate, bDate)
    },
    compareDateISOStringReverseByProperty(a, b, property){
        let aDate = new Date(a[property])
        let bDate = new Date(b[property])
        return this.compareDateReverse(aDate, bDate)
    },
    compareStringNaturalByProperty(a, b, property){
        return a[property].localeCompare(b[property])
    },
    compareStringReverseByProperty(a, b, property){
        return b[property].localeCompare(a[property])
    },
    compareNumberNaturalByProperty(a, b, property){
        let aFloat = parseFloat(a[property])
        let bFloat = parseFloat(b[property])
        if (aFloat < bFloat) {
            return -1;
        }
        if (aFloat > bFloat) {
            return 1;
        }
        return 0;
    },
    compareNumberReverseByProperty(a, b, property){
        return this.compareNumberNaturalByProperty(b, a, property)
    },
    compareLDTNatural(a, b){
        let x = this.compareNumberNatural(parseInt(a["date"]["year"], 10), parseInt(b["date"]["year"], 10))
        if (x != 0) return x
        x = this.compareNumberNatural(parseInt(a["date"]["month"], 10), parseInt(b["date"]["month"], 10))
        if (x != 0) return x
        x = this.compareNumberNatural(parseInt(a["date"]["day"], 10), parseInt(b["date"]["day"], 10))
        if (x != 0) return x
        x = this.compareNumberNatural(parseInt(a["time"]["hour"], 10), parseInt(b["time"]["hour"], 10))
        if (x != 0) return x
        x = this.compareNumberNatural(parseInt(a["time"]["minute"], 10), parseInt(b["time"]["minute"], 10))
        if (x != 0) return x
        x = this.compareNumberNatural(parseInt(a["time"]["second"], 10), parseInt(b["time"]["second"], 10))
        if (x != 0) return x
        x = this.compareNumberNatural(parseInt(a["time"]["nano"], 10), parseInt(b["time"]["nano"], 10))
        return x
    },
    compareLDTReverse(a, b){
        return this.compareLDTNatural(b, a)
    },
    compareLDTNaturalByProperty(a, b, property){
        let x = this.compareNumberNatural(parseInt(a[property]["date"]["year"], 10), parseInt(b[property]["date"]["year"]))
        if (x != 0)return x
        x = this.compareNumberNatural(parseInt(a[property]["date"]["month"], 10), parseInt(b[property]["date"]["month"], 10))
        if (x != 0)return x
        x = this.compareNumberNatural(parseInt(a[property]["date"]["day"], 10), parseInt(b[property]["date"]["day"], 10))
        if (x != 0)return x
        x = this.compareNumberNatural(parseInt(a[property]["time"]["hour"], 10), parseInt(b[property]["time"]["hour"], 10))
        if (x != 0)return x
        x = this.compareNumberNatural(parseInt(a[property]["time"]["minute"], 10), parseInt(b[property]["time"]["minute"], 10))
        if (x != 0)return x
        x = this.compareNumberNatural(parseInt(a[property]["time"]["second"], 10), parseInt(b[property]["time"]["second"], 10))
        if (x != 0)return x
        x = this.compareNumberNatural(parseInt(a[property]["time"]["nano"], 10), parseInt(b[property]["time"]["nano"], 10))
        return x
    },
    compareLDTReverseByProperty(a, b, property){
        return this.compareLDTNaturalByProperty(b, a, property)
    }
}
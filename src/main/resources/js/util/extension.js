import * as _ from "lodash"

const excel = "xlsx"
const xml = "xml"
export default {
    getExtension(filename) {
        return filename.substr((filename.lastIndexOf('.') + 1));
    },

    isExcel(filename) {
        return _.isEqual(_.normalize(this.getExtension(filename)), excel)
    },
    isXml(filename) {
        return _.isEqual(_.normalize(this.getExtension(filename)), xml)
    },

    isExcelExt(ext) {
        return _.isEqual(_.normalize(ext), excel)
    },
    isXmlExt(ext) {
        return _.isEqual(_normalize(ext), xml)
    }
}
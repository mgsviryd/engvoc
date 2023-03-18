import string from './string'

const excel = "xlsx"
const xml = "xml"
export default {
    getExtension(filename) {
        return filename.substr((filename.lastIndexOf('.') + 1));
    },

    isExcel(filename) {
        return string.isEqual(this.getExtension(filename), excel)
    },
    isXml(filename) {
        return string.isEqual(this.getExtension(filename), xml)
    },

    isExcelExt(ext) {
        return string.isEqual(ext, excel)
    },
    isXmlExt(ext) {
        return string.isEqual(ext, xml)
    }
}
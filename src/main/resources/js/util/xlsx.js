import * as XLSX from "xlsx"
const extension = '.xlsx'

export default {
    file_to_wb(file, callback) {
        let reader = new FileReader()
        reader.onload = function (e) {
            /* e.target.result is an ArrayBuffer */
            callback(XLSX.read(e.target.result))
        }
        reader.readAsArrayBuffer(file)
    },
    wb_to_file(wb, filename) {
        /* impute bookType from file extension */
        const ext = filename.slice(filename.lastIndexOf(".") + 1)
        /* write workbook to Uint8Array */
        const u8 = XLSX.write(wb, {bookType: ext, type: "buffer"})
        /* create array of parts */
        const parts = [u8]; // `File` constructor expects this
        /* create File */
        const file = new File(parts, filename, {type: "application/vnd.ms-excel"})
        return file
    },
    ws_to_file(wb, sheetName){
        const newWb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(newWb, wb.Sheets[sheetName], sheetName)
        const file = this.wb_to_file(newWb, sheetName + extension)
        return file
    },

}
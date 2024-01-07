export default {
    move(array, from, to) {
        if (!array) return
        if (array.length < 2) return

        const item = array[from]
        array.splice(from, 1)
        array.splice(to, 0, item)
    },
    subArray(array, from, to) {
        return {
            get: function (i) {
                return array[i + from]
            },
            length: to - from
        }
    },
}
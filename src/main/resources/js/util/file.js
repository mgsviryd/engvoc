export default {
    blobToFile(blob, type, filename) {
        return new File([blob], filename, {type: type})
    },
}
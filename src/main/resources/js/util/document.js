export default {
    getPageAttributes(htmlId, htmlAttr) {
        return JSON.parse(document.getElementById(htmlId).getAttribute(htmlAttr))
    },
}
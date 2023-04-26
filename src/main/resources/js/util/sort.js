import compare from "./compare";
export default {
    sort(array, property, propertyType, order){
        if (order === null) return
        if (propertyType === "string"){
            if(order === "natural"){
                array.sort((x,y) => compare.compareStringNaturalByProperty(x,y, property))
            }
            if(order === "reverse"){
                array.sort((x,y) => compare.compareStringReverseByProperty(x,y, property))
            }
        }
    },
    sortUsingJsonSorts(array, jsonSortsOrigin){
        const jsonSorts = [...jsonSortsOrigin]
        jsonSorts.sort((x,y) => compare.compareNumberNaturalByProperty(x,y, "priority"))
        const funcs = []
        for (let i = 0; i <jsonSorts.length; i++) {
            const jsonSort = jsonSorts[i]
            if (jsonSort.order === null) continue
            if (jsonSort.propertyType === "string"){
                if(jsonSort.order === "natural"){
                    const f = (x,y) => compare.compareStringNaturalByProperty(x,y, jsonSort.property)
                    funcs.push(f)
                }
                if(jsonSort.order === "reverse"){
                    const f = (x,y)=> compare.compareStringReverseByProperty(x,y, jsonSort.property)
                    funcs.push(f)
                }
            }
        }
        if (funcs.length === 0) return
        const funcMain = (x,y)=>{
            let result = null
            for (let i = 0; i < funcs.length; i++) {
                const func = funcs[i]
                result = func(x,y)
                if (result !== 0){
                    return result
                }
            }
            return result
        }
        array.sort(funcMain)
    }
}
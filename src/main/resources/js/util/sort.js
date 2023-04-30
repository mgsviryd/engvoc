import compare from "./compare";
export default {
    sort(array, propertyType, order){
        if (order === null) return
        if (propertyType === "string"){
            if(order === "natural"){
                array.sort((x,y) => compare.compareStringNatural(x,y))
            }
            if(order === "reverse"){
                array.sort((x,y) => compare.compareStringReverse(x,y))
            }
        }
        if (propertyType === "dateISOString"){
            if(order === "natural"){
                array.sort((x,y) => compare.compareDateISOStringNaturalByProperty(x,y))
            }
            if(order === "reverse"){
                array.sort((x,y)=> compare.compareDateISOStringReverseByProperty(x,y))
            }
        }
        if (propertyType === "number"){
            if(order === "natural"){
                array.sort((x,y) => compare.compareNumberNatural(x,y))
            }
            if(order === "reverse"){
                array.sort((x,y) => compare.compareNumberReverse(x,y))
            }
        }
        if (propertyType === "boolean"){
            if(order === "natural"){
                array.sort((x,y) => compare.compareBooleanNatural(x,y))
            }
            if(order === "reverse"){
                array.sort((x,y) => compare.compareBooleanReverse(x,y))
            }
        }
    },
    sortUsingProperty(array, property, propertyType, order){
        if (order === null) return
        if (propertyType === "string"){
            if(order === "natural"){
                array.sort((x,y) => compare.compareStringNaturalByProperty(x,y, property))
            }
            if(order === "reverse"){
                array.sort((x,y) => compare.compareStringReverseByProperty(x,y, property))
            }
        }
        if (propertyType === "dateISOString"){
            if(order === "natural"){
                array.sort((x,y) => compare.compareDateISOStringNaturalByProperty(x,y, property))
            }
            if(order === "reverse"){
                array.sort((x,y)=> compare.compareDateISOStringReverseByProperty(x,y, property))
            }
        }
        if (propertyType === "number"){
            if(order === "natural"){
                array.sort((x,y) => compare.compareNumberNaturalByProperty(x,y, property))
            }
            if(order === "reverse"){
                array.sort((x,y) => compare.compareNumberReverseByProperty(x,y, property))
            }
        }
        if (propertyType === "boolean"){
            if(order === "natural"){
                array.sort((x,y) => compare.compareBooleanNaturalByProperty(x,y, property))
            }
            if(order === "reverse"){
                array.sort((x,y) => compare.compareBooleanReverseByProperty(x,y, property))
            }
        }
    },
    sortUsingPropertySettings(array, propertySettingsOrigin){
        const propertySettings = [...propertySettingsOrigin].filter(ps => ps.sortable && ps.priorityOrder !== 0)
        propertySettings.sort((x,y) => compare.compareNumberNaturalByProperty(x,y, "priorityOrder"))
        const funcs = []
        for (let i = 0; i <propertySettings.length; i++) {
            const setting = propertySettings[i]
            if (setting.propertyType === "string"){
                if(setting.order === "natural"){
                    const f = (x,y) => compare.compareStringNaturalByProperty(x,y, setting.property)
                    funcs.push(f)
                }
                if(setting.order === "reverse"){
                    const f = (x,y)=> compare.compareStringReverseByProperty(x,y, setting.property)
                    funcs.push(f)
                }
            }
            if (setting.propertyType === "dateISOString"){
                if(setting.order === "natural"){
                    const f = (x,y) => compare.compareDateISOStringNaturalByProperty(x,y, setting.property)
                    funcs.push(f)
                }
                if(setting.order === "reverse"){
                    const f = (x,y)=> compare.compareDateISOStringReverseByProperty(x,y, setting.property)
                    funcs.push(f)
                }
            }
            if (setting.propertyType === "number"){
                if(order === "natural"){
                    const f = (x,y) => compare.compareNumberNaturalByProperty(x,y, setting.property)
                    funcs.push(f)
                }
                if(order === "reverse"){
                    const f = (x,y) => compare.compareNumberReverseByProperty(x,y, setting.property)
                    funcs.push(f)
                }
            }
            if (setting.propertyType === "boolean"){
                if(order === "natural"){
                    const f = (x,y) => compare.compareBooleanNaturalByProperty(x,y, setting.property)
                    funcs.push(f)
                }
                if(order === "reverse"){
                    const f = (x,y) => compare.compareBooleanReverseByProperty(x,y, setting.property)
                    funcs.push(f)
                }
            }
        }
        if (funcs.length === 0) return
        array.sort(compare.compareAllFunction(funcs))
    }
}
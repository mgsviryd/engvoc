const email = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/
const registrationPassword = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$/
const hasUppercaseLatinLetter = /[A-Z]/
const hasLowercaseLatinLetter = /[a-z]/
const hasLatinLetter = /[a-zA-Z]/
const hasNumber =  /\d/
const hasSpecialCharacter = /[`!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/
export default {
    isEmail(str) {
        if (!str) return false
        return email.test(str)
    },
    isSignUpPassword(str) {
        if (!str) return false
        return registrationPassword.test(str)
    },
    hasLatinLetter(str){
        if (!str) return false
        return hasLatinLetter.test(str)
    },
    hasUppercaseLatinLetter(str){
        if (!str) return false
        return hasUppercaseLatinLetter.test(str)
    },
    hasLowercaseLatinLetter(str){
        if(!str) return false
        return hasLowercaseLatinLetter.test(str)
    },
    hasNumber(str){
        if (!str)return false
        return hasNumber.test(str)
    },
    hasSpecialCharacter(str){
        if (!str) return false
        return hasSpecialCharacter.test(str)
    },

}
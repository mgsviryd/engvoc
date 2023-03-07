<template>
    <div class="registration-registration">
        <form id="registration_form" @submit="" :action="absolutePath" method="post">
            <div class="form-group row">
                <label class="col-sm-2 col-form-label"> Email:</label>
                <span class="input-group-text"> <i class="fas fa-at"></i> </span>
                <div class="col-sm-6">
                    <input type="email"
                           id="email"
                           v-model="email"
                           name="email"
                           class="form-control"
                           :class="hasEmailErrors ? is-invalid : ''"
                           placeholder="some@some.com"/>
                    <div v-if="frontendData.emailError" class="invalid-feedback">
                        {{frontendData.emailError}}
                    </div>

                    <div v-if="frontendData.UserAlreadyExistsException" class="invalid-feedback">
                        {{frontendData.UserAlreadyExistsException}}
                    </div>

                    <div v-if="frontendData.UserEmailValidationException" class="invalid-feedback">
                        {{frontendData.UserEmailValidationException}}
                    </div>
                </div>
            </div>

            <div class="form-group row">
                <label class="col-sm-2 col-form-label"> Пароль:</label>
                <span class="input-group-text"> <i class="fas fa-user-lock"></i> </span>
                <div class="col-sm-6">
                    <input type="password"
                           id="password"
                           v-model="password"
                           name="password"
                           class="form-control"
                           :class="hasPasswordErrors ? is-invalid : ''"
                           placeholder="Пароль"/>
                    <div v-if="frontendData.passwordError" class="invalid-feedback">
                        {{frontendData.passwordError}}
                    </div>

                    <div v-if="${frontendData.UserPasswordValidationException}" class="invalid-feedback">
                        {{frontendData.UserPasswordValidationException}}
                    </div>
                </div>
            </div>

            <div class="form-group row">
                <label class="col-sm-2 col-form-label"> Пароль (повтор):</label>
                <span class="input-group-text"> <i class="fas fa-user-lock"></i> </span>
                <div class="col-sm-6">
                    <input type="password"
                           id="passwordConfirm"
                           v-model="passwordConfirm"
                           name="passwordConfirm"
                           class="form-control"
                           :class="hasConfirmPasswordErrors ? is-invalid : ''"
                           placeholder="Повторите пароль"/>
                </div>
            </div>

            <div>
                <div class="col-sm-6">
                    <div class="g-recaptcha" data-sitekey="6LdkuusUAAAAAE5xeWs2E1g5GFi_XAHPnrtoC2Uh"></div>
                    <div v-if="frontendData.captchaError" class="alert alert-danger" role="alert">
                        {{frontendData.captchaError}}
                    </div>
                </div>
            </div>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <button type="submit" class="btn btn-primary">ЗАРЕГИСТРИРОВАТЬ</button>
        </form>
    </div>
</template>

<script>
    export default{
        props: ['absolutePath'],
        data(){
            return {
                frontendData: frontendData
            }
        },
        methods:{
            hasEmailErrors(){
                return !!(this.frontendData.emailError
                || this.frontendData.UserAlreadyExistsException
                || this.UserEmailValidationException)
            },
            hasPasswordErrors(){
                return !!(this.frontendData.passwordError
                || this.frontendData.UserPasswordValidationException)
            },
            hasConfirmPasswordErrors(){
                return !!(this.frontendData.passwordError)
            }
        }
    }

</script>

<style>

</style>
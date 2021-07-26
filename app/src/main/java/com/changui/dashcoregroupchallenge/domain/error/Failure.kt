package com.changui.dashcoregroupchallenge.domain.error

/**
 * list of errors we might have when making an http call
 * this list is non-exhaustive, so we decided to explicitly handle a few of them; should the server return
 * an error not known and/or handled on the client side, the UnknownError is returned
 */
sealed class Failure {
    object NetworkError : Failure()
    object ServerError : Failure()
    object BadRequestError : Failure()
    object GatewayError : Failure()
    object UnknownError : Failure()
}

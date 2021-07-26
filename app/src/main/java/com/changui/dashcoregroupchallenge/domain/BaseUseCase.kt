package com.changui.dashcoregroupchallenge.domain


interface UseCaseWithoutParam<out T : ResultState> {
    suspend fun execute(): T
}

interface UseCaseWithParams<out T : ResultState, in Params> {
    suspend fun execute(params: Params): T
}
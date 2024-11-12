package com.hw.fetch.data.models

class UiStatesModel(val restCallStatus: Pair<REST_CALL_Status, String?>) {
    enum class REST_CALL_Status{ LOADING, SUCCESS, ERROR, UNINITAIATED}
}
package com.example.myapplicationtg


sealed class TelegramState {

    data class CurrentList(val currentList: List<CurrentFileDataClass>) : TelegramState()

    object Empty : TelegramState()
}
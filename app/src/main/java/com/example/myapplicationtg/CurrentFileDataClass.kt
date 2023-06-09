package com.example.myapplicationtg

//класс обертка над полученными каталогами
data class CurrentFileDataClass(
    val fileName: String,
    val size: Long,
    val uriString: String,
    val date: Long,
    var selected: Boolean = false
)

package com.example.myapplicationtg

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TelegramViewModel() : ViewModel() {

    val telegramContentStateFlow = MutableStateFlow<TelegramState>(TelegramState.Empty)

    fun getDirectories(context: Context, uriString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val findTelegramFilesUseCase =  FindTelegramFilesUseCase()
            telegramContentStateFlow.emit(TelegramState.CurrentList(findTelegramFilesUseCase.getTelegramContent(context, uriString)))
        }
    }

    fun deleteSelectedFiles(context: Context, pathList: List<String>, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.Default) {
            pathList.forEach { string ->
                try {
                    val directory =
                        DocumentFile.fromTreeUri(
                            context,
                            Uri.parse(string)
                        )
                    val currentDirectory = directory?.listFiles()?.find { dir -> dir.uri.toString() == string }
                    currentDirectory?.listFiles()?.forEach {
                        it.delete()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        callback.invoke()
    }
}
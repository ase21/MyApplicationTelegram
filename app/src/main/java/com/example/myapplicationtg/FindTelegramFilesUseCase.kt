package com.example.myapplicationtg

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

class FindTelegramFilesUseCase {

    fun getTelegramContent(context: Context, uriString: String): List<CurrentFileDataClass> {
        val result = mutableListOf<CurrentFileDataClass>()
        try {
            val uri = Uri.parse(uriString)

            val telegramRootDirectory = DocumentFile.fromTreeUri(context, uri)

            telegramRootDirectory?.let {
                //получаем содержимое каталога telegramRootDirectory
                val files = telegramRootDirectory.listFiles()
                //проваливаемся в каждый из них для получения количества данных
                files.forEach { currentFile ->
                    var subFilesSize = 0L
                    //тут мы считаем размер в цикле - оооочень долгая штука
//                    currentFile.listFiles().forEach {
//                        if (it.isFile)
//                            subFilesSize += it.length()
//                    }
                    result.add(
                        CurrentFileDataClass(
                            fileName = currentFile.name ?: "",
                            size = subFilesSize,
                            uriString = currentFile.uri.toString(),
                            date = currentFile.lastModified()
                        )
                    )
                }
            }
        } catch (exception: Exception){
            exception.printStackTrace()
        }
        return result
    }
}
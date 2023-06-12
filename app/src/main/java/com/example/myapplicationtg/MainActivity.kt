package com.example.myapplicationtg

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var logTextView: TextView
    private lateinit var progressBar: ProgressBar

    private val viewModel: TelegramViewModel by viewModels()

    private val settingsActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let { resultUri -> //resultUri - если я правильно понял, это uri, на который мы получили разрешение
                logTextView.text = resultUri.toString()
                progressBar.isVisible = true
                viewModel.getDirectories(applicationContext, resultUri.toString())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logTextView = findViewById(R.id.logTextView)
        progressBar = findViewById(R.id.progressBar)
        setButtonActions()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.telegramContentStateFlow.collectLatest {state ->
                    if (state is TelegramState.CurrentList){
                        progressBar.isVisible = false
                        state.currentList.forEach {
                            logTextView.text = "${logTextView.text}\n\n${it.fileName}"
                        }
                        val pathList = state.currentList.map { it.uriString }
                        val deleteButton = findViewById<Button>(R.id.deleteButton)
                        deleteButton.setOnClickListener {
                            viewModel.deleteSelectedFiles(applicationContext, pathList){
                                logTextView.text = "Deleting is finished"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setButtonActions() {
        val permissionRequestButton = findViewById<Button>(R.id.permissionRequestButton)
        permissionRequestButton.setOnClickListener {
            sendIntent()
        }
    }

    private fun sendIntent() {

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        //тут я пытаюсь сформировать uri максимально точно, чтоб пользователю не пришлось никуда переходить
        val uri: Uri = DocumentsContract.buildDocumentUri(
            "com.android.externalstorage.documents",
            "primary:Android/data/org.telegram.messenger/files/Telegram"
        )
        intent.putExtra("android.provider.extra.INITIAL_URI", uri)

        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        settingsActivityResult.launch(intent)
    }
}
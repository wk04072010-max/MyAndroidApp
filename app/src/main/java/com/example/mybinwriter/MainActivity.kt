package com.example.mybinwriter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_SD = 1001
    private var folderUri: Uri? = null

    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSelectFolder = findViewById<Button>(R.id.btnSelectFolder)
        val btnWriteBin = findViewById<Button>(R.id.btnWriteBin)
        tvStatus = findViewById(R.id.tvStatus)

        btnSelectFolder.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            startActivityForResult(intent, REQUEST_CODE_SD)
        }

        btnWriteBin.setOnClickListener {
            if (folderUri != null) {
                writeBinToFolder(folderUri!!)
            } else {
                tvStatus.text = "まずフォルダを選択してください"
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SD && resultCode == Activity.RESULT_OK) {
            folderUri = data?.data
            tvStatus.text = "フォルダ選択完了: $folderUri"
            // 永続権限
            folderUri?.let {
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
        }
    }

    private fun writeBinToFolder(uri: Uri) {
        try {
            val docUri = DocumentsContract.createDocument(
                contentResolver,
                uri,
                "application/octet-stream",
                "sample.bin"
            )
            val outputStream: OutputStream? = docUri?.let { contentResolver.openOutputStream(it) }
            val binData = ByteArray(256) { it.toByte() } // 例: 0..255のBINデータ
            outputStream?.write(binData)
            outputStream?.close()
            tvStatus.text = "BIN書き込み完了: sample.bin"
        } catch (e: Exception) {
            tvStatus.text = "書き込み失敗: ${e.message}"
            e.printStackTrace()
        }
    }
}

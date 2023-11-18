package at.interactivecuriosity.imagedownload

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var downloadButton: Button
    private lateinit var deleteButton: Button
    private val imageUrl = "https://www.markusmaurer.at/fhj/eyecatcher.jpg"
    private val fileName = "downloadedImage.jpg"

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "imageDownloadComplete") {
                val filePath = intent.getStringExtra("filePath")
                displayImage(filePath)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        downloadButton = findViewById(R.id.downloadButton)
        deleteButton = findViewById(R.id.deleteButton)

        registerReceiver()

        downloadButton.setOnClickListener {
            downloadImage(imageUrl, fileName)
        }

        deleteButton.setOnClickListener {
            deleteImage(fileName)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver()
    }

    private fun registerReceiver() {
        val filter = IntentFilter("imageDownloadComplete")
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
    }

    private fun unregisterReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    private fun downloadImage(urlString: String, fileName: String) {
        val intent = Intent(this, ImageDownloadService::class.java)
        intent.putExtra("imageUrl", urlString)
        intent.putExtra("fileName", fileName)
        startService(intent)
        Toast.makeText(this, "Download gestartet", Toast.LENGTH_SHORT).show()
    }

    private fun deleteImage(fileName: String) {
        val file = File(getExternalFilesDir(null), fileName)
        if (file.exists()) {
            file.delete()
            imageView.setImageBitmap(null)
            Toast.makeText(this, "Bild gelöscht", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayImage(filePath: String?) {
        if (filePath != null) {
            val bitmap = BitmapFactory.decodeFile(filePath)
            imageView.setImageBitmap(bitmap)
            Toast.makeText(this, "Bild heruntergeladen", Toast.LENGTH_SHORT).show()
        }
    }
}

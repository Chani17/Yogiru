package inu.withus.restructversion

import android.content.Intent
import android.graphics.Camera
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import inu.withus.restructversion.databinding.ActivityCameraintegrationBinding

class CameraIntegrationActivity : AppCompatActivity() {

    private lateinit var viewModel: CameraViewModel
    private lateinit var binding: ActivityCameraintegrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraintegrationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory(application))
            .get(CameraViewModel::class.java)

        val bottomNavigation = binding.bottomNavigation
        setContentView(binding.root)

        bottomNavigation.selectedItemId = R.id.action_ocr
        startActivity(Intent(this, LivePreviewActivity::class.java))
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_object_detect -> {
                    startActivity(Intent(this, DetectorActivity::class.java))
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                R.id.action_ocr -> {
                    startActivity(Intent(this, LivePreviewActivity::class.java))
                }
            }
            true
        }
    }
}

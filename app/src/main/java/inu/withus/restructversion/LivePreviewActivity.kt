package inu.withus.restructversion;

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.annotation.KeepName
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import inu.withus.restructversion.databinding.ActivityVisionLivePreviewBinding
import inu.withus.restructversion.textdetector.TextRecognitionProcessor
import java.io.IOException

/** Live preview demo for ML Kit APIs. */
@KeepName
class LivePreviewActivity :
    AppCompatActivity(), OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: ActivityVisionLivePreviewBinding
    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var selectedModel = TEXT_RECOGNITION_KOREAN
    var expireDateCount = HashMap<String, Int>()
    var foodName = HashMap<String, Float>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        binding = ActivityVisionLivePreviewBinding.inflate(layoutInflater)

        val bottomNavigation = binding.bottomNavigation
        setContentView(binding.root)

        bottomNavigation.selectedItemId = R.id.action_ocr

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_object_detect -> {
//                    bottomNavigation.selectedItemId = R.id.action_object_detect
                    val intent = Intent(this, DetectorActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
                R.id.action_ocr -> {
//                    bottomNavigation.selectedItemId = R.id.action_ocr
                    val intent = Intent(this, LivePreviewActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
//        setContentView(R.layout.activity_vision_live_preview)

        preview = findViewById(R.id.preview_view)
        if (preview == null) {
            Log.d(TAG, "Preview is null")
        }

        graphicOverlay = findViewById(R.id.graphic_overlay)
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null")
        }

        if (allPermissionsGranted()) {
            startCameraSource()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

//        val options: MutableList<String> = ArrayList()
//        options.add(TEXT_RECOGNITION_KOREAN)

//        // Creating adapter for spinner
//        val dataAdapter = ArrayAdapter(this, R.layout.spinner_style, options)
//
//        // Drop down layout style - list view with radio button
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        // attaching data adapter to spinner
//        spinner.adapter = dataAdapter
//        spinner.onItemSelectedListener = this
//
//        val facingSwitch = findViewById<ToggleButton>(R.id.facing_switch)
//        facingSwitch.setOnCheckedChangeListener(this)
//
//        val settingsButton = findViewById<ImageView>(R.id.settings_button)
//        settingsButton.setOnClickListener {
//            val intent = Intent(applicationContext, SettingsActivity::class.java)
//            intent.putExtra(SettingsActivity.EXTRA_LAUNCH_SOURCE, SettingsActivity.LaunchSource.LIVE_PREVIEW)
//            startActivity(intent)
//        }

        Log.d(TAG, "Before createCameraSource :  $selectedModel")
        createCameraSource(selectedModel)
    }


    @Synchronized
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        selectedModel = parent?.getItemAtPosition(pos).toString()
        Log.d(TAG, "Selected model: $selectedModel")
        preview?.stop()
        createCameraSource(selectedModel)
        startCameraSource()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing.
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        Log.d(TAG, "Set facing")
        if (cameraSource != null) {
            if (isChecked) {
                cameraSource?.setFacing(CameraSource.CAMERA_FACING_FRONT)
            } else {
                cameraSource?.setFacing(CameraSource.CAMERA_FACING_BACK)
            }
        }
        preview?.stop()
        startCameraSource()
    }

    private fun createCameraSource(model: String) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = CameraSource(this, graphicOverlay)
        }
        try {
            when (model) {
                TEXT_RECOGNITION_KOREAN -> {
                    Log.i(TAG, "Using on-device Text recognition Processor for Latin and Korean")
                    cameraSource!!.setMachineLearningFrameProcessor(
                        TextRecognitionProcessor(
                            this,
                            KoreanTextRecognizerOptions.Builder().build(),
                            expireDateCount,
                            foodName
                        )
                    )
                }
                else -> Log.e(TAG, "Unknown model: $model")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Can not create image processor: $model", e)
            Toast.makeText(
                applicationContext,
                "Can not create image processor: " + e.message,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private fun startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null")
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null")
                }
                preview!!.start(cameraSource, graphicOverlay)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                cameraSource!!.release()
                cameraSource = null
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        createCameraSource(selectedModel)
        startCameraSource()
    }

    /** Stops the camera. */
    override fun onPause() {
        super.onPause()
        preview?.stop()
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (cameraSource != null) {
            cameraSource?.release()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val TEXT_RECOGNITION_KOREAN = "Text Recognition Korean"
        private const val TAG = "LivePreviewActivity"
        private val REQUIRED_PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}
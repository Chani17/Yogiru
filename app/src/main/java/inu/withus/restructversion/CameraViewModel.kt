package inu.withus.restructversion

import android.view.MenuItem
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {

    val onItemSelectedEvent: MutableLiveData<VisionType> = MutableLiveData()

    fun onBottomMenuClicked(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_object_detect -> postVisionType(VisionType.Object)
            R.id.action_ocr -> postVisionType(VisionType.OCR)
        }
        item.isChecked = true
        return false
    }

    private fun postVisionType(type: VisionType) {
        onItemSelectedEvent.postValue(type)
    }
}
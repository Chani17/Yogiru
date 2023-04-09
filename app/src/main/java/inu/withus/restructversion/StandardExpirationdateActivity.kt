package inu.withus.restructversion

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText

class StandardExpirationdateActivity (context: Context) {

    private val standardDialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener

    init{
        instance = this
    }

    companion object{
        private var instance:StandardExpirationdateActivity? = null
        fun getInstance(): StandardExpirationdateActivity? {
            return instance
        }
    }


    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }

    fun showDialog() {
        standardDialog.setContentView(R.layout.activity_standard_expirationdate)
        standardDialog.setCanceledOnTouchOutside(true)
        standardDialog.setCancelable(true)
        standardDialog.show()
        standardDialog.window!!.setLayout(800, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    interface OnDialogClickListener
    {
        fun onClicked(name: String, memo: String) //버튼을 누르면 name, memo setting
    }

}
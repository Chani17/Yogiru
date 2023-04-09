package inu.withus.restructversion

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import inu.withus.restructversion.databinding.EditDialogBinding

class EditDialog(context: Context) {

    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener

    fun setOnClickListener(listener: OnDialogClickListener)
    {
        onClickListener = listener
    }

    fun showDialog(name: String, memo: String)
    {

        dialog.setContentView(R.layout.edit_dialog)
        //dialog 바깥을 누르면 취소
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()
        //dialog 크기 조절
        dialog.window!!.setLayout(800, WindowManager.LayoutParams.WRAP_CONTENT)

        //이 부분을 수정해야 할 듯! binding으로 가져온 해당 위치 리스트의 아이템
        val edit_name = dialog.findViewById<EditText>(R.id.mod_name)
        edit_name.setText(name)

        val edit_memo = dialog.findViewById<EditText>(R.id.mod_memo)
        edit_memo.setText(memo)

        //mod_bt를 누르면 내가 EditText에 입력한 정보들이 각각 setting된 후 dialog 닫힘
        dialog.findViewById<Button>(R.id.mod_bt).setOnClickListener {
            onClickListener.onClicked(edit_name.text.toString(), edit_memo.text.toString())
            dialog.dismiss()
        }

    }

    interface OnDialogClickListener
    {
        fun onClicked(name: String, memo: String) //버튼을 누르면 name, memo setting
    }

}
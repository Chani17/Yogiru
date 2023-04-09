package inu.withus.restructversion

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import inu.withus.restructversion.databinding.ActivityRecipeRecommendationBinding
import inu.withus.restructversion.databinding.RecipeDialogBinding

class RecipeDialog (context: Context) {

    /* 레시피 코드는 이미지 버튼 클릭 시 전달됨! 각 세 가지 매개변수만 레시피 코드를 이용하여 값 넣어 주기*/
    private var recipeCode = 1 //레시피 코드
    private var recipeName : String ?= "김치참치찌개" //레시피 이름
    private var recipeIngredient : String ?= "김치 1컵, 참치 1캔, 양파 1/2개, 파 한줌, 쌀뜨물 400ml, 청양고추 1~2개, 설탕 2스푼, 참기름 1스푼, 국간장 1스푼, 새우젓 1스푼, 다진마늘 0.5~1스푼" //전체 재료
    private var recipeProcess : String ?= "1. 재료를 준비해 주세요.\n2. 김치 1/4 포기에 설탕 2스푼을 뿌리고 잘 섞은 다음 10분 간 재워 주세요.\n" +
            "3. 참기름 한 스푼을 냄비에 둘러주세요.\n4. 그 위에 설탕을 뿌려 준 김치를 넣어 달달 볶아주세요.\n"+
            "5. 김치가 익으면서 김치 색이 투명해지면 양파를 넣어 주시고 달달 볶아주세요\n6. 김치랑 양파가 어느정도 익으면 김치가 잠길 정도로 쌀뜨물을 넣어주세요\n" +
       "7. 국간장 1스푼을 넣어주세요. \n8. 다음 새우젓 1스푼 듬뿍 넣어주세요.\n9. 마늘 반스푼~한스푼 사이 넣어주세요.\n" +
            "10. 참치는 꼭 기름을 꾹 눌러 다 빼주신 다음 참치 알맹이만 넣어주세요.\n11. 청양고추, 대파를 넣어주세요. \n12. 대파를 넣고 푹 5-10분 정도 보글보글 끓여주세요.\n" //조리 과정


    private val dialog = Dialog(context)

    fun showDialog(code: Int)
    {
        recipeCode = code
        dialog.setContentView(R.layout.recipe_dialog)
        //dialog 바깥을 누르면 취소
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()
        //dialog 크기 조절
        dialog.window!!.setLayout(700, WindowManager.LayoutParams.WRAP_CONTENT)
        //스크롤뷰 상단에서부터 시작
        dialog.findViewById<ScrollView>(R.id.recipeScrollView).fullScroll(ScrollView.FOCUS_UP);

        //각 항목에 해당하는 정보로 setting해 주기
        dialog.findViewById<TextView>(R.id.recipe_name).setText(recipeName)
        dialog.findViewById<TextView>(R.id.recipe_ingredient).setText(recipeIngredient)
        dialog.findViewById<TextView>(R.id.recipe_process).setText(recipeProcess)
    }
}
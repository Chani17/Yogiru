package inu.withus.restructversion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import inu.withus.restructversion.databinding.ActivityRecipeBinding

class RecipeActivity : AppCompatActivity() {

    private var mBinding : ActivityRecipeBinding? = null
    private val binding get() = mBinding!!
    var recipe : String ?= " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        mBinding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //검색 버튼을 눌렀을 때
        binding.recipeSearch.setOnClickListener {
            if (binding.recipeNameInput.text.toString().isNotBlank()){ //빈 문자열이 아니라면
                recipe = binding.recipeNameInput.text.toString()
                binding.recipeNameOutput.text = recipe
                //binding.recipeInfo.visibility = View.VISIBLE

                //네이버 버튼 클릭 가능 및 uri 전달
                binding.naver.apply{
                    isEnabled = true
                    setOnClickListener {
                        var uri = "https://search.naver.com/search.naver?query="+recipe+"+레시피"
                        var intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        startActivity(intent)
                    }
                }

                //유튜브 버튼 클릭 가능 및 uri 전달
                binding.youtube.apply{
                    isEnabled = true
                    setOnClickListener {
                        var uri = "https://www.youtube.com/results?search_query="+recipe+"+레시피"
                        var intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        startActivity(intent)
                    }
                }

                //만개의 레시피 버튼 클릭 가능 및 uri 전달
                binding.tenthousand.apply{
                    isEnabled = true
                    setOnClickListener {
                        var uri = "https://www.10000recipe.com/recipe/list.html?q="+recipe
                        var intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        startActivity(intent)
                    }
                }
            }
        }

    }
}
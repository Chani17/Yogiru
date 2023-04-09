package inu.withus.restructversion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import inu.withus.restructversion.databinding.ActivityStoreBinding

class StoreActivity : AppCompatActivity() {

    private var mBinding : ActivityStoreBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        mBinding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.marketkurly.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.kurly.com/"))
            startActivity(intent)
        }

        binding.emart.setOnClickListener{
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://emart.ssg.com/"))
            startActivity(intent)
        }

        binding.coupang.setOnClickListener{
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coupang.com/"))
            startActivity(intent)
        }

        binding.homeplus.setOnClickListener{
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://front.homeplus.co.kr/"))
            startActivity(intent)
        }

    }
}
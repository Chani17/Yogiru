package inu.withus.restructversion

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import inu.withus.restructversion.databinding.ActivityItemBinding
import inu.withus.restructversion.dto.FoodInfoDTO
import inu.withus.restructversion.dto.RecyclerFoodInfoDTO
import java.text.SimpleDateFormat
import java.util.*

class RecyclerAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private lateinit var binding: ActivityItemBinding
    var datas = mutableListOf<RecyclerFoodInfoDTO>()


    //onCreateViewHolder : 어떤 목록 레이아웃을 만들 것인지 반환(어떤 뷰를 생성할 것인가)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_item,parent,false)
        return ViewHolder(view)
    }

    //onBindViewHolder : 생성된 뷰(껍데기)에 무슨 데이터를 넣을 것인가
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    //getItemCount : 몇 개의 목록을 만들지를 반환
    override fun getItemCount(): Int = datas.size


    fun replaceList(newList: MutableList<RecyclerFoodInfoDTO>) {
        datas = newList.toMutableList()
        //어댑터의 데이터가 변했다는 notify를 날린다
        notifyDataSetChanged()
    }

    fun removeFood(position: Int) {
        datas.removeAt(position)
        notifyDataSetChanged()
    }



    //ViewHolder : 목록의 개별 항목 레이아웃을 포함하는 View 래퍼로, 각 목록 레이아웃에 필요한 기능들을 구현하는 공간
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val foodName: TextView = itemView.findViewById(R.id.item_foodName)
        private val expireDate: TextView = itemView.findViewById(R.id.item_expireDate)
        private val quantity: TextView = itemView.findViewById(R.id.item_quantity)
        private val foodItemBackground : LinearLayout = itemView.findViewById(R.id.foodItemBackground)

        fun bind(item: RecyclerFoodInfoDTO) {
            foodName.text = item.foodName
            var dday = dDayCount(item.expireDate!!.get(0))
            if(dday<0) {
                dday = -dday
                expireDate.text = "+${dday.toString()}"
            }
            else
                expireDate.text = "-${dday.toString()}"
            quantity.text = item.count?.get(0).toString()

            if(dday.toInt()<5){
                foodItemBackground.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.item_pink_background))
            }

            itemView.setOnClickListener {
                Intent(context, FoodDetailActivity::class.java).apply {
                    putExtra("data", item)
                    Log.d(TAG,"Food information - detail : + $item")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { context.startActivity(this) }




            }


        }
    }

    private fun dDayCount(expireDate : String) : Long{
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        val endDate = dateFormat.parse(expireDate).time
        Log.d(ContentValues.TAG, "end :::::::::::::$endDate")
//        val calendar = Calendar.getInstance()
//        calendar.setTime(date)

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time
//        val dDay = (today - endDate)/ (60 * 60 * 24 * 1000)
        val dDay = (endDate - today)/ (60 * 60 * 24 * 1000)
//        if(dDay<0)
//            binding2.dayBar.text = "+"
        Log.d(ContentValues.TAG, "D-day :::::::::::::$dDay")
//        return dDay.toString()
        return dDay


    }


}

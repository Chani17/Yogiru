package inu.withus.restructversion

import android.icu.util.Calendar.getInstance
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import inu.withus.restructversion.dto.FoodDTO
import kotlinx.android.synthetic.main.activity_food_detail_item.view.*
import java.text.SimpleDateFormat
import java.util.*


class FoodDetailRecyclerAdapter: RecyclerView.Adapter<FoodDetailRecyclerAdapter.FoodDetailViewHolder>(){

    private var foodDetailData = mutableListOf<FoodDTO>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodDetailRecyclerAdapter.FoodDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_food_detail_item,parent,false)
        return FoodDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodDetailViewHolder, position: Int) {
        val foodData = foodDetailData[position]
        holder.bind(foodData, position)

//        holder.itemView.deleteFoodButton.setOnClickListener{
//            onClickDeleteIcon.invoke(foodData)
//        }
    }

    override fun getItemCount(): Int =  foodDetailData.size

    inner class FoodDetailViewHolder(view : View) : RecyclerView.ViewHolder(view){

        private val foodDetailActivity = FoodDetailActivity.getInstance()
        var mMember: FoodDTO? = null
        var mPosition: Int? = null

        private val expireDate: TextView = view.findViewById(R.id.expireDate)
        private val dDay: TextView = view.findViewById(R.id.dday)
        private val count: TextView = view.findViewById(R.id.count)
        private val deleteButton : ImageButton = view.findViewById(R.id.deleteFoodButton)
        private val reduceButton : ImageButton = view.findViewById(R.id.reduceFoodButton)

        init{
            deleteButton.setOnClickListener {
                Log.d("TAG", "전체 삭제 버튼 눌림")
                foodDetailActivity!!.deleteFoodDataAll(mMember!!)

            }

            reduceButton.setOnClickListener {
                Log.d("TAG", "감소 버튼 눌림")
                foodDetailActivity?.reduceFoodData(mMember!!)

            }
        }

        fun bind(item: FoodDTO, position : Int) {
            this.mMember = item
            this.mPosition = position

            Log.d("TAG", "${item.foodName} 데이터 : ")

            count.text = item.count!!.toString()
            Log.d("TAG", "count : ${item.count!!}")

            expireDate.text = item.expireDate!!
            Log.d("TAG", "expireDate : ${item.expireDate!!}")

            var dday = dDayCount(item.expireDate!!)
            if(dday<0) {
                dday = -dday
                dDay.text = "+${dday.toString()}"
            }
            else
                dDay.text = "-${dday.toString()}"
        }
    }

    fun replaceList(newList: MutableList<FoodDTO>) {
        foodDetailData = newList.toMutableList()
        notifyDataSetChanged()
    }


    //년-월-일 날짜 형식 -> d-day로 변환
    private fun dDayCount(expireDate : String) : Long{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        val endDate = dateFormat.parse(expireDate).time

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time

        val dDay = (endDate - today)/ (60 * 60 * 24 * 1000)
        return dDay


    }

}
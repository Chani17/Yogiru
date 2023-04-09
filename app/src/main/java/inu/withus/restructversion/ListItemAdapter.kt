package inu.withus.restructversion

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import inu.withus.restructversion.databinding.ListItemBinding

class ListItemAdapter(val dataSet: ArrayList<ListItem>, val onClickDeleteIcon: (listitem: ListItem) -> Unit, val onClickEditIcon: (listitem: ListItem) -> Unit) :
//삭제2. delete button이 눌렸을때 onclickDeleteIcon을 실행하라는뜻, 0->Unit이기때문에 함수자체에 return없다는뜻
    RecyclerView.Adapter<ListItemAdapter.CustomViewHolder>(){

    private var mBinding : ListItemBinding? = null
    private val binding get() = mBinding!!

    var viewbinderhelper = ViewBinderHelper()
    init { //스와이프 한 번에 하나씩만 열리도록 초기화
        viewbinderhelper.setOpenOnlyOne(true)
    }

    //뷰홀더가 처음 생성될 때
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListItemAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return CustomViewHolder(ListItemBinding.bind(view))
    }

    //재활용해 주는 곳 및 값을 넣어 주는 곳
    override fun onBindViewHolder(holder: ListItemAdapter.CustomViewHolder, position: Int) {
        val listposition = dataSet[position]

        //뷰 홀더를 데이터 개체와 바인딩(viewbinderhelper로 스와이프 레이아웃과 데이터 개체를 고유하게 정의하는 문자열로 묶어 주기)
        //Help to save and restore open/close state of the swipeLayout.
        viewbinderhelper.bind(holder.swipelayout, listposition.name)

        holder.name.text = listposition.name
        holder.memo.text = listposition.memo

        holder.binding.itemDelete.setOnClickListener {
            onClickDeleteIcon.invoke(listposition) //삭제1. item_delete가 눌렸을때 listposition를 전달하면서 onClickDeleteIcon함수를 실행한다.
        }

        holder.binding.itemEdit.setOnClickListener{
            onClickEditIcon.invoke(listposition)
            viewbinderhelper.closeLayout(listposition.name) //데이터 개체를 고유하게 정의하는 문자열을 id로 넘겨 주기
        }

    }

    //리스트의 개수를 적어 준다
    override fun getItemCount(): Int {
        return dataSet.size
    }

    //뷰홀더 클래스 (음료수처럼 잡아 주는 홀더)
    //이곳에서 파인드뷰아이디로 리스트 아이템에 있는 뷰들을 참조한다.
    inner class CustomViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = itemView.findViewById<TextView>(R.id.item_name)
        val memo = itemView.findViewById<TextView>(R.id.item_memo)
        val swipelayout : SwipeRevealLayout = //스와이프
            itemView.findViewById(R.id.swipeLayout)
    }

}
package inu.withus.restructversion

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import inu.withus.restructversion.dao.SearchItemNameDAO
import inu.withus.restructversion.databinding.ActivityShoppingListBinding
import inu.withus.restructversion.dto.ShoppingListDTO


//정확히는 *2가 아니라 기존에 있는 리스트에 계속 firebase가 자동 업데이트 해 놔서 누적합(기존 리스트+수정한 리스트+수정한 리스트2...)로 처리되는 거였음
//clear 위치 바꿨더니 추가, 삭제는 ok 수정도 ok but, 수정은 됐다가 안 됐다가 함 (getList 자동 호출할 때마다 인덱스 매핑이 달라져서인 듯)

//onCreate()가 끝난 이후에야 getList()의 firestore.collection("장보기리스트").addSnapshotListener~가 실행되는 게 문제
//액티비티는 생성이 됐는데 아직 불러온 데이터가 없으니 안 뜨는 게 당연

class ShoppingListActivity : AppCompatActivity() {

    private var mBinding: ActivityShoppingListBinding? = null
    private val binding get() = mBinding!!
    private val itemList = arrayListOf<ListItem>()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)

        mBinding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //레이아웃 매니저를 이용해 어댑터의 방향을 설정
        binding.recyclerViewList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // 현재 DB에 저장되어 있는 장보기 리스트 항목들 가져와서 보여주기
        Log.d(TAG, "getList() 들어갈거임!")

        getList()


        //어댑터에 리스트 항목을 삽입, 삭제, 수정
        binding.recyclerViewList.adapter = ListItemAdapter(itemList,
            onClickDeleteIcon = { //삭제3. onBindViewHolder에서 listposition을 전달받고 이 함수를 실행하게 된다.
                deleteItem(it)      //deleteItem함수가 포지션값인 it을 받고 지운다.
            },
            onClickEditIcon = {
                editItem(it)
            })
        binding.recyclerViewList.setHasFixedSize(true) //어댑터의 성능을 위한 것

        // 검색 버튼 누를 시
        binding.itemSearch.setOnClickListener {
            val itemName: TextView = findViewById(R.id.item_name)
            searchItem(itemName.text.toString())
            //binding.shoppingListInfo.visibility = View.VISIBLE
        }

        //추가 시작
        binding.itemAdd.setOnClickListener {
            addItem() //addItem 함수 실행
        }

        Log.d(TAG, "onCreate 함수 호출 끝!")
    }

    // DB에 저장되어 있는 장보기 리스트들 불러오기
    private fun getList() {
        Log.d(TAG, "getList() 들어왔음!")

        /*
        //이 위치에 둘 땐 추가 올바르게 작동, 수정, 삭제 기능은 하지만 *2로 출력
        if (itemList.isNotEmpty()){
            itemList.clear()
        }
*/
        //이거 error에 대응되는 게 뭔지 물어보기! 대응되는 게 없어서 그런가_
        firestore.collection("장보기리스트").addSnapshotListener { value, error ->
            if (itemList.isNotEmpty()) {
                itemList.clear()
            }
            for (snapshot in value!!.documents) {
                val item = ListItem(snapshot["foodName"].toString(), snapshot["memo"].toString())
                itemList.add(item)
                Log.d(TAG, "item = $item" + " (getList() 내부 for문 도는 중) ")
                binding.recyclerViewList.adapter?.notifyDataSetChanged()
            }
            Log.d(
                TAG,
                "getList() 내부 for문 끝난 후의 itemList의 크기는 " + binding.recyclerViewList.adapter?.getItemCount()
                    .toString()
            )
            Log.d(TAG, "itemList에 담긴 원소는 " + itemList.toString())
        }
        //*2의 원인인 for문 순환 시엔 여기까지 오지도 않음 (엥 왜 이게 firestore.collection~보다 빨리 실행되지? 초기에 크기 null이라고 뜸)
        Log.d(
            TAG,
            "getList() 호출 끝난 후의 itemList의 크기는 " + binding.recyclerViewList.adapter?.getItemCount()
                .toString()
        )
    }


    // 식품명 검색
    private fun searchItem(food: String) {
        var check = false
        var searchItemNameDAO = SearchItemNameDAO()
        var count = ArrayList<Int>()
        var sum = 0

        Log.d(TAG, "food = $food")
        firestore.collection("냉장")
            .addSnapshotListener { value, error ->
                Log.d(TAG, "냉장으로 들어옴")
                for (snapshot in value!!.documents) {
                    if (snapshot.getString("foodName")!!.contains(food)) {
                        count = snapshot["count"] as ArrayList<Int>
                        searchItemNameDAO.foodName = food

                        for (i in count.indices) {
                            Log.d(TAG, "i = ${count[i]}")
                            sum += count[i]
                        }
                        searchItemNameDAO.foodCount = sum
                    }
                }
                binding.shoppingListName.text = searchItemNameDAO.foodName
                binding.shoppingListCount.text = searchItemNameDAO.foodCount.toString()
                check = true
                binding.shoppingListInfo.visibility = View.VISIBLE
                return@addSnapshotListener
            }

        if (!check) {
            firestore.collection("실온")
                .addSnapshotListener { value, error ->
                    Log.d(TAG, "실온으로 들어옴")
                    for (snapshot in value!!.documents) {
                        if (snapshot.getString("foodName")!!.contains(food)) {
                            count = snapshot["count"] as ArrayList<Int>
                            searchItemNameDAO.foodName = food

                            for (i in count.indices) {
                                Log.d(TAG, "i = ${count[i]}")
                                sum += count[i]
                            }
                            searchItemNameDAO.foodCount = sum
                        }
                    }
                    binding.shoppingListName.text = searchItemNameDAO.foodName
                    binding.shoppingListCount.text = searchItemNameDAO.foodCount.toString()
                    check = true
                    binding.shoppingListInfo.visibility = View.VISIBLE
                    return@addSnapshotListener
                }
        }

        if (!check) {
            firestore.collection("냉동")
                .addSnapshotListener { value, error ->
                    Log.d(TAG, "냉동으로 들어옴")
                    for (snapshot in value!!.documents) {
                        if (snapshot.getString("foodName")!!.contains(food)) {
                            count = snapshot["count"] as ArrayList<Int>
                            searchItemNameDAO.foodName = food

                            for (i in count.indices) {
                                Log.d(TAG, "i = ${count[i]}")
                                sum += count[i]
                            }
                            searchItemNameDAO.foodCount = sum
                        }
                    }
                    binding.shoppingListName.text = searchItemNameDAO.foodName
                    binding.shoppingListCount.text = searchItemNameDAO.foodCount.toString()
                    check = true
                    binding.shoppingListInfo.visibility = View.VISIBLE
                    return@addSnapshotListener
                }
        }

        if (!check) {
            // 사용자가 식품명을 잘못입력하고 검색을 했다면
            Log.d(TAG, "사용자가 잘못 입력함")
            searchItemNameDAO.foodName = food
            searchItemNameDAO.foodCount = 0
        }
    }

    fun addItem() {
        //EditText에서 쓴 내용들을 가져오기
        val item = ListItem(
            binding.itemName.text.toString(),
            binding.itemMemo.text.toString()
        ) //ListItem는 데이터 클래스

        //가져온 내용을 arraylist에 추가
        itemList.add(item)

        /*
        //itemList 내용 확인용
        for (i in itemList){
            Log.d(TAG, "itemList에 담긴 원소는 "+i.toString())
        }
        Log.d(TAG, "itemList에 담긴 원소는 "+itemList.toString())
        */

        itemList.clear() //이 코드 없으면 *2 출력됨
        Log.d(
            TAG,
            "아이템 추가 후의 itemList의 크기는 " + binding.recyclerViewList.adapter?.getItemCount().toString()
        )

        // DB에 저장 (이 단계에서 *2)
        val shoppingListDTO = ShoppingListDTO()
        shoppingListDTO.foodName = binding.itemName.text.toString()
        shoppingListDTO.memo = binding.itemMemo.text.toString()
        //여기서부터가 문제
        firestore?.collection("장보기리스트")!!.document(shoppingListDTO.foodName!!).set(shoppingListDTO)
            .addOnSuccessListener {
                Log.d(TAG, "장보기 리스트 DB에 저장!")
                Log.d(
                    TAG,
                    "최종 itemList의 크기는 " + binding.recyclerViewList.adapter?.getItemCount()
                        .toString()
                )
            }

        //EditText 비우기
        binding.itemName.setText(null)
        binding.itemMemo.setText(null)

        //binding.recyclerViewList.adapter?.notifyItemRangeRemoved(0, itemList.size)
        //getList()

        //아이템이 추가되고 UI가 바뀐걸 업데이트해 주는 코드
        binding.recyclerViewList.adapter?.notifyDataSetChanged()
    }

    fun deleteItem(item: ListItem) {
        // DB에서 삭제
        Log.d(TAG, "item.name = ${item.name}")

        firestore?.collection("장보기리스트")!!.document(item.name)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "장보기리스트 삭제 성공!")
                Log.d(
                    TAG,
                    "itemList의 크기는 " + binding.recyclerViewList.adapter?.getItemCount().toString()
                )
            }
            .addOnFailureListener { e -> Log.w(TAG, "Delete Error", e) }

        itemList.remove(item)
        //itemList.clear()

        //아이템이 삭제되고 UI가 바뀐걸 업데이트해 주는 코드
        binding.recyclerViewList.adapter?.notifyDataSetChanged()
    }


    fun editItem(item: ListItem) { //다이얼로그 출력 -> 입력 -> 결과 반영해서 수정 (시연할 땐 이름 말고 메모만 바꾸기 ㅜ)(한 번만)

        val dialog = EditDialog(this)
        val original_name = item.name

        dialog.showDialog(item.name, item.memo)
        dialog.setOnClickListener(object : EditDialog.OnDialogClickListener {
            override fun onClicked(name: String, memo: String) {
                val shoppingListDTO = ShoppingListDTO()
                shoppingListDTO.foodName = binding.itemName.text.toString()
                shoppingListDTO.memo = binding.itemMemo.text.toString()

                //아이템 내용 업데이트
                item.name = name
                item.memo = memo

                Log.d(TAG, "item은 수정 완료!")

                // DB에 수정된 내용 반영
                firestore?.collection("장보기리스트")!!.document(original_name)
                    .update("foodName", name, "memo", memo)
                    .addOnSuccessListener {
                        Log.d(TAG, "장보기리스트 수정 완료!")
                        Log.d(
                            TAG,
                            "itemList의 크기는 " + binding.recyclerViewList.adapter?.getItemCount()
                                .toString()
                        )
                    }

                //아이템이 수정되고 UI가 바뀐걸 업데이트해 주는 코드
                binding.recyclerViewList.adapter?.notifyDataSetChanged()
            }
        })
    }

}
package inu.withus.restructversion

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import inu.withus.restructversion.dto.SettingDateDTO
import java.util.*

class PreferenceActivity : PreferenceFragmentCompat() {

    lateinit var prefs: SharedPreferences

    var timePreference: Preference? = null
    var pushNotification: Preference? = null
    var setDatePreference: Preference? = null
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    // onCreate() 중에 호출되어 Fragment에 preference를 제공하는 메서드
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // preference xml을 inflate하는 메서드
        setPreferencesFromResource(R.xml.preference, rootKey)

        // rootkey가 null이라면
        if (rootKey == null) {
            // Preference 객체들 초기화
            timePreference = findPreference("setTime")
            pushNotification = findPreference("pushNotification")
            setDatePreference = findPreference("setDay")



//            setDatePreference!!.setOnPreferenceClickListener {
//                var date : String = preferenceManager.getSharedPreferences()!!.getString("setDay", "-1")!!
//                Log.d("TAG", "preference 값 확인 in PreferenceFragmentCompat : $date")
//                true
//            }

            setDatePreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener{ preference, newValue ->

                var d : String = newValue.toString()
                setDatePreference!!.summary = d
                var date : Int = d[0].digitToInt()
                Log.d("TAG", "d : $d")
                Log.d("TAG", "date : $date")
                firestore.collection("Settings").document("test").update("date", date)
                true
            }


            var pushCheck =
                preferenceManager.sharedPreferences!!.getBoolean("pushNotification", true)
            Log.d("TAG", "pushCheck : $pushCheck")

            pushNotification!!.setOnPreferenceClickListener {
                pushCheck = !pushCheck
                true
            }

            Log.d("TAG", "pushCheck : $pushCheck")
            timePreference!!.setOnPreferenceClickListener {
                firestore.collection("Settings").document("test").addSnapshotListener { value, error ->
                    val result = value!!["hour"].toString() + "시 " + value!!["minute"].toString() + "분"
                    timePreference!!.summary = result
                }
                var checkState: Boolean =
                    preferenceManager.sharedPreferences!!.getBoolean("setTime", true)
                Log.d("TAG", "checkState1 : $checkState")
                if(checkState) {
                    val timepickerIntent = Intent(activity, TimepickerActivity::class.java)
                    timepickerIntent.putExtra("pushCheck", pushCheck)
                    startActivity(timepickerIntent)
                }
                Log.d("TAG", "checkState2 : $checkState")
                true
            }

        }
    }

    override fun onStop() {
        super.onStop()
        Intent(activity, SettingActivity::class.java)
    }
}

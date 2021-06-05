package com.example.jsonparsetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.jsonparsetest.classes.SeniorMeal
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    // currently testing with one service class only; will implement other classes later

    lateinit var listViewDetails: ListView
    var arrayListDetails: ArrayList<SeniorMeal> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listViewDetails = findViewById(R.id.listView) as ListView
        jsonParseClient("https://radiant-dawn-48071.herokuapp.com/service/SeniorMeals")
        //TODO: Implement different URLs
    }

    fun jsonParseClient (url: String) {
        println("Attempting to parse JSON!")
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object: Callback {

            override fun onResponse(call: Call, response: Response) {
                val strResponse = response.body?.string()
                val jsonContact = JSONObject(strResponse)
                val jsonObj: JSONObject = jsonContact.getJSONObject("SeniorMeals")
                var size: Int = jsonObj.length()
                arrayListDetails = ArrayList()
                for (i in 0 until size) {
                    var jsonObjDetail: JSONObject = jsonObj.getJSONObject(jsonObj.toString())
                    var seniorMeal: SeniorMeal = SeniorMeal()
                    seniorMeal.name = jsonObjDetail.getString("name")
                    seniorMeal.location = jsonObjDetail.getString("location")
                    seniorMeal.days = jsonObjDetail.getString("days")
                    seniorMeal.phone = jsonObjDetail.getString("phone")
                    seniorMeal.email = jsonObjDetail.getString("email")
                    seniorMeal.website = jsonObjDetail.getString("website")
                    seniorMeal.additionalnotes = jsonObjDetail.getString("additional notes")
                    arrayListDetails.add(seniorMeal)
                }
                runOnUiThread {
                    val objAdapter =
                        CustomAdapter(applicationContext, arrayListDetails)
                    listViewDetails.adapter = objAdapter
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                println("JSON parse failed!")
            }
        })
    }

}
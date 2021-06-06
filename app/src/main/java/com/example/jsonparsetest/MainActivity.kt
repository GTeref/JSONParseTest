package com.example.jsonparsetest

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import com.example.jsonparsetest.classes.SeniorMeal
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.jar.Manifest
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    // currently testing with one service class only; will implement other classes later
    val PERMISSION_ID = 42
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var listViewDetails: ListView
    var arrayListDetails: ArrayList<SeniorMeal> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listViewDetails = findViewById(R.id.listView) as ListView
        jsonParseClient("https://radiant-dawn-48071.herokuapp.com/services")
        //TODO: Implement different URLs
    }

    private fun getLastLocation () {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {

                    }
                }
            }
        }
    }

    private fun requestNewLocationData () {
        var locationRequest = LocationRequest()
        

    }

    private fun requestPermissions () {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun isLocationEnabled () : Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
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
                arrayListDetails = ArrayList()
                val jsonKeys: Iterator<String> = jsonObj.keys()
                while (jsonKeys.hasNext()) {
                    var jsonObjDetail: JSONObject = jsonObj.getJSONObject(jsonKeys.next())
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
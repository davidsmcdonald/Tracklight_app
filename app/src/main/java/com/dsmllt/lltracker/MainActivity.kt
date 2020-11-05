package com.dsmllt.lltracker
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import api.LLtrackerService
import api.LocSender
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Response
import java.lang.System.currentTimeMillis
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private lateinit var retService : LLtrackerService
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var token: String

    private var running = false
    private var dur = 1
    private var ms_min = 60000

    var myCountDownTimerObject: CountDownTimer = object :
        CountDownTimer(dur.toLong() * ms_min, dur.toLong() * ms_min) {
        override fun onTick(millisUntilFinished: Long) {
        }
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onFinish() {
            postLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start_button.setOnClickListener { startSession() }
        end_button.setOnClickListener { cancelSession() }
        sign_out_button.setOnClickListener { signOut() }

        val intent = intent
        val message = intent.getStringExtra("com.dsmllt.lltracker.USER")
        user_name.text = message

        token = intent.getStringExtra("com.dsmllt.lltracker.TOKEN").toString()
        Log.i("action3", token)

        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }

        retService = LocSender
            .getRetrofitInstance()
            .create(LLtrackerService::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun startSession() {
        if (running) {
            recent_data.append("\n Session already running")
        } else {
            recent_data.append("\n Session started")
            running = true
            dur = interval_spinner.selectedItem.toString().toInt()
            myCountDownTimerObject.start()
        }
    }

    private fun cancelSession() {
        if (running) {
            recent_data.append("\n\n Session cancelled")
            running = false
            myCountDownTimerObject.cancel()
        } else {
            recent_data.append("\n\n Nothing to cancel")
        }
    }

    private fun signOut() {
        token = ""
        if (running) {
            running = false
            myCountDownTimerObject.cancel()
        }
        finish()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    private fun postLocation() {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
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

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val user = user_name.text.toString()
                    val logtime = currentTimeMillis()
                    val lat = location.latitude
                    val lon = location.longitude
                    val lca = LocListItem(user, lat, lon, logtime)

                    Log.i("action", token)
                    Log.i("action", user)

                    val postResponse: LiveData<Response<LocListItem>> = liveData {
                        val response = retService.updateLocation(lca, token)
                        emit(response)
                    }
                    val current = LocalDateTime.now()

                    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss a")
                    val formatted = current.format(formatter)

                    postResponse.observe(this, Observer {
                        recent_data.append("\n\n Updated: $formatted\nLocation: $lat, $lon" )
                    })
                }
            }
        myCountDownTimerObject.start()
    }

}
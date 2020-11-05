package com.dsmllt.lltracker
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import api.LLtrackerService
import api.LocSender
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private lateinit var retService: LLtrackerService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener { logIn() }
        new_user_button.setOnClickListener { addUser() }

        retService = LocSender
            .getRetrofitInstance()
            .create(LLtrackerService::class.java)
    }

    private fun logIn() {
        val username = li_user_name.text.toString().trim()
        val password = li_password.text.toString().trim()
        val user = UserItem(username, password)

        val postResponse: LiveData<Response<WebTokItem>> = liveData {
            val response = retService.signIn(user)
            if (response.isSuccessful) {
                emit(response)
            } else if ( response.code() > 401){
                error_box.text = "Network could be restarting. Please try again in a moment"
            } else {
                val gson = Gson()
                val errorMessageItem: WebErrItem = gson.fromJson(
                    response.errorBody()!!.charStream(),
                    WebErrItem::class.java
                )
                error_box.text = errorMessageItem.message
            }
        }

        postResponse.observe(this, Observer {
            val responseItem = it.body()

            val token = responseItem?.token.toString()
            val intent = Intent(this, MainActivity::class.java)
                .apply {
                    putExtra("com.dsmllt.lltracker.USER", username)
                    putExtra("com.dsmllt.lltracker.TOKEN", token)
                }
            error_box.text = ""
            startActivity(intent)
        })
    }


    private fun addUser() {
        val username = li_user_name.text.toString()
        val password = li_password.text.toString()
        val user = UserItem(username, password)
        val postResponse: LiveData<Response<WebTokItem>> = liveData {
            val response = retService.newUser(user)
            if (response.isSuccessful) {
                emit(response)
            } else {
                val gson = Gson()
                val errorMessageItem: WebErrItem = gson.fromJson(
                    response.errorBody()!!.charStream(),
                    WebErrItem::class.java
                )
                error_box.text = errorMessageItem.message
            }
        }

        postResponse.observe(this, Observer {
            val responseItem = it.body()

            val token = responseItem?.token.toString()
            val intent = Intent(this, MainActivity::class.java)
                .apply {
                    putExtra("com.dsmllt.lltracker.USER", username)
                    putExtra("com.dsmllt.lltracker.TOKEN", token)
                }
            startActivity(intent)

        })
    }
}
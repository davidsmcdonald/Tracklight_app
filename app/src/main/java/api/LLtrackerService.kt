package api

import com.dsmllt.lltracker.LocList
import com.dsmllt.lltracker.LocListItem
import com.dsmllt.lltracker.UserItem
import com.dsmllt.lltracker.WebTokItem
import retrofit2.Response
import retrofit2.http.*


interface LLtrackerService {

    @GET("/finduserapp")
    suspend fun getlocations(@Query("username") username: String) : Response<LocList>

    @POST("/addLocation")
    suspend fun updateLocation(@Body location: LocListItem, @Header( "token") token: String?) : Response<LocListItem>

    @POST("/adduser")
    suspend fun newUser(@Body user: UserItem) : Response<WebTokItem>

    @POST("/signin")
    suspend fun signIn(@Body user: UserItem) : Response<WebTokItem>

//  @POST("/destroy")

}
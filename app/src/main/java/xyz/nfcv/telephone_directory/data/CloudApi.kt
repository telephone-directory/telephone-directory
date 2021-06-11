package xyz.nfcv.telephone_directory.data

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import xyz.nfcv.telephone_directory.model.Person
import xyz.nfcv.telephone_directory.model.User

interface CloudApi {
    companion object {
//        private const val SERVER = "https://phone.huhaorui.com"
        private const val SERVER = "http://nfcv.xyz:8802"
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        data class Result<T>(val data: T?, val message: String, val code: Int)

        inline fun <reified T> defaultCallback(crossinline success: (Int, T?) -> Unit): Callback<Result<T>> {
            return object : Callback<Result<T>> {
                override fun onResponse(call: Call<Result<T>>, response: Response<Result<T>>) {
                    success(response.body()?.code ?: response.code(), response.body()?.data)
                }

                override fun onFailure(call: Call<Result<T>>, t: Throwable) {
                    t.printStackTrace()
                }
            }
        }
    }

    @POST("/user/login")
    @FormUrlEncoded
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<Result<User>>

    @POST("/user/add")
    @FormUrlEncoded
    fun register(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<Result<String>>

    @POST("/user/validate")
    @FormUrlEncoded
    fun validate(
        @Field("userId") userId: String,
        @Field("token") token: String
    ): Call<Result<Any>>

    @POST("/person/sync")
    fun sync(
        @Header("UserId") userId: String,
        @Header("Token") token: String,
        @Body local: List<Person>
    ): Call<Result<List<Cloud.Record>>>
}
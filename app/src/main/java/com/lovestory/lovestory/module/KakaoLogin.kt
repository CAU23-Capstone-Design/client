package com.lovestory.lovestory.module

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.lovestory.lovestory.model.LoginPayload
import com.lovestory.lovestory.network.sendTokenToServer
import com.lovestory.lovestory.ui.screens.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

lateinit var navHostControllerForAuth : NavHostController
lateinit var context: Context

/** callback function for kakao account login*/
val callback : (OAuthToken?, Throwable?) -> Unit = { token, error ->
    if(error != null){
        Log.e("KAKAO-AUTH-ACCOUNT", "Fail get KAKAO token : $error")
    }else if(token != null){
        Log.d("KAKAO-AUTH-ACCOUNT", "Success get KAKAO token")

        CoroutineScope(Dispatchers.Main).launch {
            Log.d("Access Token", "${token.accessToken}")
            val response = sendTokenToServer(token.accessToken)
            if(response.isSuccessful){
                response.body()?.token?.let {
//                    tokenForSaved = it
//                    val context = LocalContext.current as Activity
                    saveToken(context = context, it)
                    val chunks: List<String> = it.split(".")
                    val decoder: Base64.Decoder = Base64.getUrlDecoder()

                    val header: String = String(decoder.decode(chunks[0]))
                    val payload: String = String(decoder.decode(chunks[1]))

                    val payloadJSON : JsonObject = JsonParser.parseString(payload).asJsonObject
                    Log.d("erihfvkdhgiksdighkdfrhjkgjkhdfg", "$it")

                    val data = Gson().fromJson(payloadJSON, LoginPayload::class.java)

                    Log.d("after get token and parse token", "${data.couple}")
                    Log.d("after get token and parse token", "${data.user.code}")
                    if(data.couple != null){
                        navHostControllerForAuth.navigate(route = Screen.DashBoard.route){
                            popUpTo(Screen.Login.route)
                        }
                    }else{
                        Log.e("No couple","커플 생성해야함//////!!!!!!!!!!!")
                        navHostControllerForAuth.navigate(route = Screen.CoupleSync.route+"/${data.user.code}&${data.user.name}"){
                            popUpTo(Screen.Login.route)
                        }
                    }
                }
            }else{
                Log.e("login error","${response.errorBody()}")
            }
        }
    }
}

/** kakao Login for LoveStory */
fun kakaoLogin(currentContext: Context, navHostController: NavHostController){
    navHostControllerForAuth = navHostController
    context = currentContext
    KakaoSdk.init(currentContext, "e2d337c73844599d05e5e1b56e5cbed9")

    /** kakao app installed */
//    UserApiClient.instance.loginWithKakaoAccount(context = currentContext, callback = callback)

    if(UserApiClient.instance.isKakaoTalkLoginAvailable(context = currentContext)){
        //kakao talk login
        UserApiClient.instance.loginWithKakaoTalk(context = currentContext){ token, error->
            if (error != null){
                // fail kakao talk login
                Log.e("KAKAO-AUTH-APP", "Fail login with KAKAO app : $error")
                if(error is ClientError && error.reason == ClientErrorCause.Cancelled){
                    return@loginWithKakaoTalk
                }

                UserApiClient.instance.loginWithKakaoAccount(context = currentContext, callback = callback)

            }else if(token != null){
                Log.e("KAKAO-AUTH-APP", "Success login with KAKAO app : $token ")

                CoroutineScope(Dispatchers.Main).launch {
                    Log.d("Access Token", "${token.accessToken}")
                    val response = sendTokenToServer(token.accessToken)
                    if(response.isSuccessful){
                        response.body()?.token?.let {
//                    tokenForSaved = it
//                    val context = LocalContext.current as Activity
                            saveToken(context = context, it)
                            val chunks: List<String> = it.split(".")
                            val decoder: Base64.Decoder = Base64.getUrlDecoder()

                            val header: String = String(decoder.decode(chunks[0]))
                            val payload: String = String(decoder.decode(chunks[1]))

                            val payloadJSON : JsonObject = JsonParser.parseString(payload).asJsonObject

                            val data = Gson().fromJson(payloadJSON, LoginPayload::class.java)

                            Log.d("after get token and parse token", "${data.couple}")
                            Log.d("after get token and parse token", "${data.user.code}")
                            if(data.couple != null){
                                navHostControllerForAuth.navigate(route = Screen.DashBoard.route){
                                    popUpTo(Screen.Login.route)
                                }
                            }else{
                                Log.e("No couple","커플 생성해야함//////!!!!!!!!!!!")
                                navHostControllerForAuth.navigate(route = Screen.CoupleSync.route+"/${data.user.code}&${data.user.name}"){
                                    popUpTo(Screen.Login.route)
                                }
                            }
                        }
                    }else{
                        Log.e("login error","${response.errorBody()}")
                    }
                }
//                CoroutineScope(Dispatchers.Main).launch {
//                    val response = sendTokenToServer(token.accessToken)
//                    if(response.isSuccessful){
//                        response.body()?.token?.let {
//                            val jwt = it
//
//                            Log.d("JWT TAG", "Raw JWT token: $jwt")
//
//                            // JWT 토큰 처리
//                            val decodedJWT = JWT(jwt)
//
//                            // 디코딩된 JWT 토큰의 헤더와 페이로드 출력
//                            Log.d("JWT TAG", "Decoded JWT header: ${decodedJWT.header}")
//                            Log.d("JWT TAG", "Decoded JWT payload: $decodedJWT")
//
//                            val hasCouple= decodedJWT.getClaim("couple")?.asString()
//
//                            val user  = decodedJWT.getClaim("user")
//
//                            if (hasCouple!=null) {
//                                // Navigate to DashBoardScreen
//                                Log.d("has couple","커플 회우너!!!!!!!!!!!")
//                                navHostControllerForAuth.navigate(route = Screen.DashBoard.route){
//                                    popUpTo(Screen.Login.route)
//                                }
//                            } else {
//                                // Navigate to CoupleSyncScreen
//                                Log.e("No couple","커플 생성해야함//////!!!!!!!!!!!")
//                            }
//                        }
//                    }else{
//
//                    }
//                }
            }
        }
    }
    /** kakao app not installed */
    else{
        Log.d("KAKAO-AUTH", "Not installed Kakao App")
        UserApiClient.instance.loginWithKakaoAccount(context = currentContext, callback = callback)
    }
}

fun sendToToken(accessToeken : String){

}


fun kakaoLogout(currentContext: Context){
    KakaoSdk.init(currentContext, "e2d337c73844599d05e5e1b56e5cbed9")
    UserApiClient.instance.logout { error ->
        if (error != null) {
            Log.e("KAKAO-AUTH_LOGOUT", "회원 로그아웃 실패 : $error")

        } else {
            Log.d("KAKAO-AUTH-LOGOUT", "회원 로그아웃")
        }
    }
}

fun kakaoWithdrawal(currentContext: Context){
    KakaoSdk.init(currentContext, "e2d337c73844599d05e5e1b56e5cbed9")
    UserApiClient.instance.unlink { error->
        if (error != null) {
            Log.e("KAKAO-AUTH_UNLINK", "회원 탈퇴 실패 : $error")

        } else {
            Log.d("KAKAO-AUTH-UNLINK", "회원 탈퇴 성공")
        }
    }
}
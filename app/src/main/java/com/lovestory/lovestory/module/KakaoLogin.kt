package com.lovestory.lovestory.module

import android.content.Context
import android.util.Log
import androidx.navigation.NavHostController
import com.auth0.android.jwt.JWT
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.lovestory.lovestory.network.sendTokenToServer
import com.lovestory.lovestory.ui.screens.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

lateinit var navHostControllerForAuth : NavHostController

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
                    Log.d("tagaeghsdjkgbjdfbfjsbdjvb - header", "$it")

                    val chunks: List<String> = it.split(".")
                    val decoder: Base64.Decoder = Base64.getUrlDecoder()

                    val header: String = String(decoder.decode(chunks[0]))
                    val payload: String = String(decoder.decode(chunks[1]))

                    Log.d("tagaeghsdjkgbjdfbfjsbdjvb - header", "$header")
                    Log.d("tagaeghsdjkgbjdfbfjsbdjvb - pagyl;od", "$payload")

//                    val jwtParser = Jwts.parserBuilder()
//                        .deserializeJsonWith(JacksonDeserializer())
//                        .build()
//                    val claims = jwtParser.parseClaimsJwt(it).body
//
//                    val userClaim = claims["user"]
//                    if (userClaim != null) {
//                        val userJsonObject = JSONObject(userClaim.toString())
//
//                        val userId = userJsonObject.getString("_id")
//                        val userName = userJsonObject.getString("name")
//                        val userCode = userJsonObject.getString("code")
//
//                        // 추출한 값들을 사용하세요.
//                        Log.d("JWT", "User ID: $userId")
//                        Log.d("JWT", "User Name: $userName")
//                        Log.d("JWT", "User Code: $userCode")
//                    } else {
//                        Log.d("JWT", "User claim is null")
//                    }

//                    if (hasCouple!=null) {
//                        // Navigate to DashBoardScreen
//                        Log.d("has couple","커플 회우너!!!!!!!!!!!")
//                        navHostControllerForAuth.navigate(route = Screen.DashBoard.route){
//                            popUpTo(Screen.Login.route)
//                        }
//                    } else {
//                        // Navigate to CoupleSyncScreen
//                        Log.e("No couple","커플 생성해야함//////!!!!!!!!!!!")
//                    }
                }
            }else{
                Log.e("login error","${response.errorBody()}")
            }
        }


//        UserApiClient.instance.me { user, error ->
//            if (error != null) {
//                Log.e("KAKAO-AUTH-GET_INFO", "Fail get user information : $error")
//            }
//            else if (user != null) {
//                Log.d("KAKAO-AUTH-GET_INFO", "Succes get user information" +
//                        "\n회원번호: ${user.id}" +
//                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
//                        "\n성별: ${user.kakaoAccount?.gender}"+
//                        "\n생일: ${user.kakaoAccount?.birthday}"
//                )
//
//                CoroutineScope(Dispatchers.Main).launch {
//                    val response = verifiedUserID(id = user.id.toString())
//                    if (response.isSuccessful) {
//                        Log.d("verifiedUser", "$response")
//                        // Update UI with response data
//                        navHostControllerForAuth.navigate(route = Screen.CoupleSync.route+"/${response.body()?.id}&${response.body()?.code}&${response.body()?.name}"){
//                            popUpTo(Screen.Login.route)
//                        }
//                    } else {
//                        // Handle error
//                        Log.e("verifiedUser", "$response")
//
//                        navHostControllerForAuth.navigate(
//                            Screen.SignUp.route+"/${user.id}&${user.kakaoAccount?.profile?.nickname}&${user.kakaoAccount?.gender}&${user.kakaoAccount?.birthday}"){
//                            popUpTo(Screen.Login.route)
//                        }
//                    }
//                }
//            }
//        }
    }
}

/** kakao Login for LoveStory */
fun kakaoLogin(currentContext: Context, navHostController: NavHostController){
    navHostControllerForAuth = navHostController
    KakaoSdk.init(currentContext, "e2d337c73844599d05e5e1b56e5cbed9")

    /** kakao app installed */
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
                    val response = sendTokenToServer(token.accessToken)
                    if(response.isSuccessful){
                        response.body()?.token?.let {
                            val jwt = it

                            Log.d("JWT TAG", "Raw JWT token: $jwt")

                            // JWT 토큰 처리
                            val decodedJWT = JWT(jwt)

                            // 디코딩된 JWT 토큰의 헤더와 페이로드 출력
                            Log.d("JWT TAG", "Decoded JWT header: ${decodedJWT.header}")
                            Log.d("JWT TAG", "Decoded JWT payload: $decodedJWT")

                            // JWT 토큰 처리
//                            val decodedJWT = JWT(jwt)
                            val hasCouple= decodedJWT.getClaim("couple")?.asString()

                            val user  = decodedJWT.getClaim("user")
//                            print(user)

                            if (hasCouple!=null) {
                                // Navigate to DashBoardScreen
                                Log.d("has couple","커플 회우너!!!!!!!!!!!")
                                navHostControllerForAuth.navigate(route = Screen.DashBoard.route){
                                    popUpTo(Screen.Login.route)
                                }
                            } else {
                                // Navigate to CoupleSyncScreen
                                Log.e("No couple","커플 생성해야함//////!!!!!!!!!!!")
                            }
                        }
                    }else{

                    }
                }


//                UserApiClient.instance.me { user, error ->
//                    if (error != null) {
//                        Log.e("KAKAO-AUTH-GET_INFO", "Fail get user information : $error")
//                    }
//                    else if (user != null) {
//                        Log.e("KAKAO-AUTH-GET_INFO", "Succes get user information" +
//                                "\n회원번호: ${user.id}" +
//                                "\n이메일: ${user.kakaoAccount?.email}" +
//                                "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
//                                "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")
//
////                        sendToToken()
////
////                        CoroutineScope(Dispatchers.Main).launch {
////                            val response = verifiedUserID(id = user.id.toString())
////                            if (response.isSuccessful) {
////                                // Update UI with response data
////                                navHostControllerForAuth.navigate(route = Screen.CoupleSync.route+"/${response.body()?.id}&${response.body()?.code}"){
////                                    popUpTo(Screen.Login.route)
////                                }
////                            } else {
////                                // Handle error
////                                navHostControllerForAuth.navigate(
////                                    Screen.SignUp.route+"/${user.id}&${user.kakaoAccount?.profile?.nickname}&${user.kakaoAccount?.gender}&${user.kakaoAccount?.birthday}"){
////                                    popUpTo(Screen.Login.route)
////                                }
////                            }
////                        }
//
//
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
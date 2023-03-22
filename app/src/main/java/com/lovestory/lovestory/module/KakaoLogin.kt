package com.lovestory.lovestory.module

import android.content.Context
import android.util.Log
import androidx.navigation.NavHostController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.lovestory.lovestory.ui.screens.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

lateinit var navHostControllerForAuth : NavHostController

/** callback function for kakao account login*/
val callback : (OAuthToken?, Throwable?) -> Unit = { token, error ->
    if(error != null){
        Log.e("KAKAO-AUTH-ACCOUNT", "Fail get KAKAO token : $error")
    }else if(token != null){
        Log.d("KAKAO-AUTH-ACCOUNT", "Success get KAKAO token")

        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("KAKAO-AUTH-GET_INFO", "Fail get user information : $error")
            }
            else if (user != null) {
                Log.d("KAKAO-AUTH-GET_INFO", "Succes get user information" +
                        "\n회원번호: ${user.id}" +
                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                        "\n성별: ${user.kakaoAccount?.gender}"+
                        "\n생일: ${user.kakaoAccount?.birthday}"
                )

                CoroutineScope(Dispatchers.Main).launch {
                    val response = verifiedUserID(id = user.id.toString())
                    if (response.isSuccessful) {
                        Log.d("verifiedUser", "$response")
                        // Update UI with response data
                        navHostControllerForAuth.navigate(route = Screen.CoupleSync.route+"/${response.body()?.id}&${response.body()?.code}"){
                            popUpTo(Screen.Login.route)
                        }
                    } else {
                        // Handle error
                        Log.e("verifiedUser", "$response")

                        navHostControllerForAuth.navigate(
                            Screen.SignUp.route+"/${user.id}&${user.kakaoAccount?.profile?.nickname}&${user.kakaoAccount?.gender}&${user.kakaoAccount?.birthday}"){
                            popUpTo(Screen.Login.route)
                        }
                    }
                }
            }
        }
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

                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e("KAKAO-AUTH-GET_INFO", "Fail get user information : $error")
                    }
                    else if (user != null) {
                        Log.e("KAKAO-AUTH-GET_INFO", "Succes get user information" +
                                "\n회원번호: ${user.id}" +
                                "\n이메일: ${user.kakaoAccount?.email}" +
                                "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")

                        CoroutineScope(Dispatchers.Main).launch {
                            val response = verifiedUserID(id = user.id.toString())
                            if (response.isSuccessful) {
                                // Update UI with response data
                                navHostControllerForAuth.navigate(route = Screen.CoupleSync.route+"/${response.body()?.id}&${response.body()?.code}"){
                                    popUpTo(Screen.Login.route)
                                }
                            } else {
                                // Handle error
                                navHostControllerForAuth.navigate(
                                    Screen.SignUp.route+"/${user.id}&${user.kakaoAccount?.profile?.nickname}&${user.kakaoAccount?.gender}&${user.kakaoAccount?.birthday}"){
                                    popUpTo(Screen.Login.route)
                                }
                            }
                        }



                    }
                }
            }
        }
    }
    /** kakao app not installed */
    else{
        Log.d("KAKAO-AUTH", "Not installed Kakao App")
        UserApiClient.instance.loginWithKakaoAccount(context = currentContext, callback = callback)
    }
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
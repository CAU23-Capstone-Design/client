package com.lovestory.lovestory.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.RenderMode
import com.airbnb.lottie.compose.*

@Composable
fun AnimateHeart(

){
    val lottieAnimatable = rememberLottieAnimatable()
    val heartAnimation by rememberLottieComposition(
        spec = LottieCompositionSpec.Asset("heart.json"))

    LaunchedEffect(heartAnimation){
        lottieAnimatable.animate(
            composition = heartAnimation,
            reverseOnRepeat = true,
            iterations = LottieConstants.IterateForever,
            iteration = 4,
            continueFromPreviousAnimate = true
        )
    }

    LottieAnimation(
        composition = heartAnimation,
        progress = lottieAnimatable.progress,
        contentScale = ContentScale.FillHeight,
        renderMode = RenderMode.AUTOMATIC,
    )
}

@Composable
fun AnimateCharacter(

){
    val lottieAnimatable = rememberLottieAnimatable()
    val characterAnimation by rememberLottieComposition(
        spec = LottieCompositionSpec.Asset("love-animation.json"))

    LaunchedEffect(characterAnimation) {
        lottieAnimatable.animate(
            composition = characterAnimation,
            reverseOnRepeat = true,
            iterations = LottieConstants.IterateForever,
            iteration = 4,
            continueFromPreviousAnimate = true
        )
    }

    Box(modifier = Modifier
        .height(250.dp)
        .width(250.dp)) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LottieAnimation(
                composition = characterAnimation,
                progress = lottieAnimatable.progress,
                contentScale = ContentScale.FillHeight,
                renderMode = RenderMode.AUTOMATIC,
            )
        }
    }

}

@Composable
fun AnimateFlyHeart(
){
    val flyingHeartAnimation by rememberLottieComposition(
        spec = LottieCompositionSpec.Asset("flying-heart.json"))

    val lottieAnimatable = rememberLottieAnimatable()

    LaunchedEffect(flyingHeartAnimation){
        lottieAnimatable.animate(
            composition = flyingHeartAnimation,
            iterations = LottieConstants.IterateForever,
            iteration = 4,
            continueFromPreviousAnimate = true
        )
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.height(250.dp)) {
            LottieAnimation(
                composition = flyingHeartAnimation,
                progress = lottieAnimatable.progress,
                contentScale = ContentScale.FillHeight,
                renderMode = RenderMode.AUTOMATIC,
            )
        }

    }
}
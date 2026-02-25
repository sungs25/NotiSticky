package com.example.notisticky.util

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {
    private var interstitialAd: InterstitialAd? = null
    // 구글 전면 광고 테스트 ID
    private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    // 몇 번 저장할 때마다 띄울 것인가
    private const val SHOW_FREQUENCY = 4
    private var saveCount = 0

    // 앱 시작 시 미리 광고를 불러오기
    fun loadAd(context: Context) {
        if (interstitialAd != null) return

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, AD_UNIT_ID, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad // 로드 성공
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                interstitialAd = null // 실패하면 넘김
            }
        })
    }

    // 저장 완료 후 호출할 함수
    fun showAdIfReady(activity: Activity, onAdDismissed: () -> Unit) {
        saveCount++

        // 4번째 저장이고, 광고가 있다면
        if (saveCount % SHOW_FREQUENCY == 0 && interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // 유저가 X버튼 눌러서 광고를 닫았을 때
                    interstitialAd = null
                    loadAd(activity) // 다음 광고 장전
                    onAdDismissed() // 홈 화면으로 이동 등 원래 할 일 실행
                }

                override fun onAdFailedToShowFullScreenContent(e: AdError) {
                    interstitialAd = null
                    onAdDismissed() // 에러 나면 방해하지 않고 바로 넘김
                }
            }
            interstitialAd?.show(activity)
        } else {
            // 빈도가 안 맞거나 로드 실패했으면 넘어감
            onAdDismissed()
        }
    }
}
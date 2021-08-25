/*
 * Copyright (c) 2020 Hemanth Savarla.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package com.shaigerbi.retromusic.activities

import android.os.Bundle
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.shaigerbi.appthemehelper.util.ATHUtil
import com.shaigerbi.appthemehelper.util.ToolbarContentTintHelper
import com.shaigerbi.retromusic.R
import com.shaigerbi.retromusic.activities.base.AbsBaseActivity
import kotlinx.android.synthetic.main.activity_donation.*

const val AD_UNIT_ID = "ca-app-pub-1173887754649201/4970804580"

class SupportDevelopmentActivity : AbsBaseActivity(){

    private var mInterstitialAd: InterstitialAd? = null
    private var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation)

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}


        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
                this, AD_UNIT_ID, adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                        showInterstitial()
                    }
                }
        )



        setStatusbarColorAuto()
        setNavigationbarColorAuto()
        setTaskDescriptionColorAuto()
        setLightNavigationBar(true)

        setupToolbar()
    }


    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    mInterstitialAd = null
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                }
            }
            mInterstitialAd?.show(this)
        }

    }


    private fun setupToolbar() {
        val toolbarColor = ATHUtil.resolveColor(this, R.attr.colorSurface)
        toolbar.setBackgroundColor(toolbarColor)
        ToolbarContentTintHelper.colorBackButton(toolbar)
        setSupportActionBar(toolbar)
    }
}

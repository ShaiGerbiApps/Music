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
package com.shaigerbi.retromusic.fragments.about

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.shaigerbi.retromusic.App
import com.shaigerbi.retromusic.Constants
import com.shaigerbi.retromusic.R
import com.shaigerbi.retromusic.adapter.ContributorAdapter
import com.shaigerbi.retromusic.fragments.LibraryViewModel
import com.shaigerbi.retromusic.util.NavigationUtil
import kotlinx.android.synthetic.main.card_credit.*
import kotlinx.android.synthetic.main.card_other.*
import kotlinx.android.synthetic.main.card_retro_info.*
import kotlinx.android.synthetic.main.card_social.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AboutFragment : Fragment(R.layout.fragment_about), View.OnClickListener {
    private val libraryViewModel by sharedViewModel<LibraryViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        version.setSummary(getAppVersion())
        setUpView()
        loadContributors()
    }

    private fun openUrl(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    private fun setUpView() {
        appGithub.setOnClickListener(this)
        faqLink.setOnClickListener(this)
        appRate.setOnClickListener(this)
        appShare.setOnClickListener(this)
        donateLink.setOnClickListener(this)
        openSource.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.faqLink -> openUrl(Constants.FAQ_LINK)
            R.id.appGithub -> openUrl(Constants.GITHUB_PROJECT)
            R.id.appRate -> openUrl(Constants.RATE_ON_GOOGLE_PLAY)
            R.id.appShare -> shareApp()
            R.id.donateLink -> NavigationUtil.goToSupportDevelopment(requireActivity())
            R.id.openSource -> NavigationUtil.goToOpenSource(requireActivity())
        }
    }

    private fun getAppVersion(): String {
        return try {
            val isPro = if (App.isProVersion()) "Pro" else "Free"
            val packageInfo =
                requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            "${packageInfo.versionName} $isPro"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "0.0.0"
        }
    }

    private fun shareApp() {
        ShareCompat.IntentBuilder.from(requireActivity()).setType("text/plain")
            .setChooserTitle(R.string.share_app)
            .setText(String.format(getString(R.string.app_share), requireActivity().packageName))
            .startChooser()
    }

    private fun loadContributors() {
        val contributorAdapter = ContributorAdapter(emptyList())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
            adapter = contributorAdapter
        }
        libraryViewModel.fetchContributors().observe(viewLifecycleOwner, { contributors ->
            contributorAdapter.swapData(contributors)
        })
    }
}

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
package com.shaigerbi.retromusic.fragments.genres

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shaigerbi.retromusic.R
import com.shaigerbi.retromusic.adapter.song.SongAdapter
import com.shaigerbi.retromusic.extensions.dipToPix
import com.shaigerbi.retromusic.fragments.base.AbsMainActivityFragment
import com.shaigerbi.retromusic.helper.menu.GenreMenuHelper
import com.shaigerbi.retromusic.model.Genre
import com.shaigerbi.retromusic.model.Song
import java.util.*
import kotlinx.android.synthetic.main.fragment_playlist_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GenreDetailsFragment : AbsMainActivityFragment(R.layout.fragment_playlist_detail) {
    private val arguments by navArgs<GenreDetailsFragmentArgs>()
    private val detailsViewModel: GenreDetailsViewModel by viewModel {
        parametersOf(arguments.extraGenre)
    }
    private lateinit var genre: Genre
    private lateinit var songAdapter: SongAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        mainActivity.setBottomBarVisibility(View.GONE)
        mainActivity.addMusicServiceEventListener(detailsViewModel)
        mainActivity.setSupportActionBar(toolbar)
        progressIndicator.hide()
        setupRecyclerView()
        detailsViewModel.getSongs().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            songs(it)
        })
        detailsViewModel.getGenre().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            genre = it
            toolbar?.title = it.name
        })
    }

    private fun setupRecyclerView() {
        songAdapter = SongAdapter(requireActivity(), ArrayList(), R.layout.item_list, null)
        recyclerView.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songAdapter
        }
        songAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkIsEmpty()
            }
        })
    }

    fun songs(songs: List<Song>) {
        songAdapter.swapDataSet(songs)
    }

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    private fun checkIsEmpty() {
        checkForPadding()
        emptyEmoji.text = getEmojiByUnicode(0x1F631)
        empty?.visibility = if (songAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun checkForPadding() {
        val height = dipToPix(52f).toInt()
        recyclerView.setPadding(0, 0, 0, height)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_genre_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return GenreMenuHelper.handleMenuClick(requireActivity(), genre, item)
    }
}

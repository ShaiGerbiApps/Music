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

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.NavigationUI
import com.shaigerbi.retromusic.ADAPTIVE_COLOR_APP
import com.shaigerbi.retromusic.ALBUM_COVER_STYLE
import com.shaigerbi.retromusic.ALBUM_COVER_TRANSFORM
import com.shaigerbi.retromusic.BANNER_IMAGE_PATH
import com.shaigerbi.retromusic.BLACK_THEME
import com.shaigerbi.retromusic.CAROUSEL_EFFECT
import com.shaigerbi.retromusic.CIRCULAR_ALBUM_ART
import com.shaigerbi.retromusic.DESATURATED_COLOR
import com.shaigerbi.retromusic.EXTRA_SONG_INFO
import com.shaigerbi.retromusic.GENERAL_THEME
import com.shaigerbi.retromusic.HOME_ARTIST_GRID_STYLE
import com.shaigerbi.retromusic.KEEP_SCREEN_ON
import com.shaigerbi.retromusic.LANGUAGE_NAME
import com.shaigerbi.retromusic.LIBRARY_CATEGORIES
import com.shaigerbi.retromusic.NOW_PLAYING_SCREEN_ID
import com.shaigerbi.retromusic.PROFILE_IMAGE_PATH
import com.shaigerbi.retromusic.R
import com.shaigerbi.retromusic.ROUND_CORNERS
import com.shaigerbi.retromusic.TAB_TEXT_MODE
import com.shaigerbi.retromusic.TOGGLE_ADD_CONTROLS
import com.shaigerbi.retromusic.TOGGLE_FULL_SCREEN
import com.shaigerbi.retromusic.TOGGLE_GENRE
import com.shaigerbi.retromusic.TOGGLE_HOME_BANNER
import com.shaigerbi.retromusic.TOGGLE_SEPARATE_LINE
import com.shaigerbi.retromusic.TOGGLE_VOLUME
import com.shaigerbi.retromusic.USER_NAME
import com.shaigerbi.retromusic.activities.base.AbsSlidingMusicPanelActivity
import com.shaigerbi.retromusic.extensions.findNavController
import com.shaigerbi.retromusic.helper.MusicPlayerRemote
import com.shaigerbi.retromusic.helper.SearchQueryHelper.getSongs
import com.shaigerbi.retromusic.model.CategoryInfo
import com.shaigerbi.retromusic.model.Song
import com.shaigerbi.retromusic.repository.PlaylistSongsLoader
import com.shaigerbi.retromusic.service.MusicService
import com.shaigerbi.retromusic.util.AppRater
import com.shaigerbi.retromusic.util.PreferenceUtil
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class MainActivity : AbsSlidingMusicPanelActivity(), OnSharedPreferenceChangeListener {
    companion object {
        const val TAG = "MainActivity"
        const val EXPAND_PANEL = "expand_panel"
        const val APP_UPDATE_REQUEST_CODE = 9002
    }

    override fun createContentView(): View {
        return wrapSlidingMusicPanel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setDrawUnderStatusBar()
        super.onCreate(savedInstanceState)

        setStatusbarColorAuto()
        setNavigationbarColorAuto()
        setLightNavigationBar(true)
        setTaskDescriptionColorAuto()
        hideStatusBar()
        AppRater.appLaunched(this)
        updateTabs()

        // NavigationUI.setupWithNavController(getBottomNavigationView(), findNavController(R.id.fragment_container))
        setupNavigationController()
        if (!hasPermissions()) {
            findNavController(R.id.fragment_container).navigate(R.id.permissionFragment)
        }
    }

    private fun setupNavigationController() {
        val navController = findNavController(R.id.fragment_container)
        val navInflater = navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.main_graph)

        val categoryInfo: CategoryInfo = PreferenceUtil.libraryCategory.first { it.visible }
        if (categoryInfo.visible) {
            navGraph.startDestination = categoryInfo.category.id
        }
        navController.graph = navGraph
        NavigationUI.setupWithNavController(getBottomNavigationView(), navController)
        navController.addOnDestinationChangedListener { _, _, _ ->
            // appBarLayout.setExpanded(true, true)
        }
    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.fragment_container).navigateUp()

    override fun onResume() {
        super.onResume()
        PreferenceUtil.registerOnSharedPreferenceChangedListener(this)
        if (intent.hasExtra(EXPAND_PANEL) &&
            intent.getBooleanExtra(EXPAND_PANEL, false) &&
            PreferenceUtil.isExpandPanel
        ) {
            expandPanel()
            intent.removeExtra(EXPAND_PANEL)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == GENERAL_THEME || key == BLACK_THEME || key == ADAPTIVE_COLOR_APP || key == USER_NAME || key == TOGGLE_FULL_SCREEN || key == TOGGLE_VOLUME || key == ROUND_CORNERS || key == CAROUSEL_EFFECT || key == NOW_PLAYING_SCREEN_ID || key == TOGGLE_GENRE || key == BANNER_IMAGE_PATH || key == PROFILE_IMAGE_PATH || key == CIRCULAR_ALBUM_ART || key == KEEP_SCREEN_ON || key == TOGGLE_SEPARATE_LINE || key == TOGGLE_HOME_BANNER || key == TOGGLE_ADD_CONTROLS || key == ALBUM_COVER_STYLE || key == HOME_ARTIST_GRID_STYLE || key == ALBUM_COVER_TRANSFORM || key == DESATURATED_COLOR || key == EXTRA_SONG_INFO || key == TAB_TEXT_MODE || key == LANGUAGE_NAME || key == LIBRARY_CATEGORIES
        ) {
            postRecreate()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        if (intent == null) {
            return
        }
        handlePlaybackIntent(intent)
    }

    private fun handlePlaybackIntent(intent: Intent) {
        lifecycleScope.launch(IO) {
            val uri: Uri? = intent.data
            val mimeType: String? = intent.type
            var handled = false
            if (intent.action != null &&
                intent.action == MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
            ) {
                val songs: List<Song> = getSongs(intent.extras!!)
                if (MusicPlayerRemote.shuffleMode == MusicService.SHUFFLE_MODE_SHUFFLE) {
                    MusicPlayerRemote.openAndShuffleQueue(songs, true)
                } else {
                    MusicPlayerRemote.openQueue(songs, 0, true)
                }
                handled = true
            }
            if (uri != null && uri.toString().isNotEmpty()) {
                MusicPlayerRemote.playFromUri(uri)
                handled = true
            } else if (MediaStore.Audio.Playlists.CONTENT_TYPE == mimeType) {
                val id = parseLongFromIntent(intent, "playlistId", "playlist")
                if (id >= 0L) {
                    val position: Int = intent.getIntExtra("position", 0)
                    val songs: List<Song> = PlaylistSongsLoader.getPlaylistSongList(get(), id)
                    MusicPlayerRemote.openQueue(songs, position, true)
                    handled = true
                }
            } else if (MediaStore.Audio.Albums.CONTENT_TYPE == mimeType) {
                val id = parseLongFromIntent(intent, "albumId", "album")
                if (id >= 0L) {
                    val position: Int = intent.getIntExtra("position", 0)
                    val songs = libraryViewModel.albumById(id).songs
                    MusicPlayerRemote.openQueue(
                        songs,
                        position,
                        true
                    )
                    handled = true
                }
            } else if (MediaStore.Audio.Artists.CONTENT_TYPE == mimeType) {
                val id = parseLongFromIntent(intent, "artistId", "artist")
                if (id >= 0L) {
                    val position: Int = intent.getIntExtra("position", 0)
                    val songs: List<Song> = libraryViewModel.artistById(id).songs
                    MusicPlayerRemote.openQueue(
                        songs,
                        position,
                        true
                    )
                    handled = true
                }
            }
            if (handled) {
                setIntent(Intent())
            }
        }
    }

    private fun parseLongFromIntent(
        intent: Intent,
        longKey: String,
        stringKey: String
    ): Long {
        var id = intent.getLongExtra(longKey, -1)
        if (id < 0) {
            val idString = intent.getStringExtra(stringKey)
            if (idString != null) {
                try {
                    id = idString.toLong()
                } catch (e: NumberFormatException) {
                    println(e.message)
                }
            }
        }
        return id
    }
}

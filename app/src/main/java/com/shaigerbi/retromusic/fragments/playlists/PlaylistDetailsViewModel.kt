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
package com.shaigerbi.retromusic.fragments.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shaigerbi.retromusic.db.PlaylistWithSongs
import com.shaigerbi.retromusic.db.SongEntity
import com.shaigerbi.retromusic.interfaces.IMusicServiceEventListener
import com.shaigerbi.retromusic.model.Song
import com.shaigerbi.retromusic.repository.RealRepository

class PlaylistDetailsViewModel(
    private val realRepository: RealRepository,
    private var playlist: PlaylistWithSongs
) : ViewModel(), IMusicServiceEventListener {

    private val playListSongs = MutableLiveData<List<Song>>()

    fun getSongs(): LiveData<List<SongEntity>> =
        realRepository.playlistSongs(playlist.playlistEntity.playListId)

    override fun onMediaStoreChanged() {}
    override fun onServiceConnected() {}
    override fun onServiceDisconnected() {}
    override fun onQueueChanged() {}
    override fun onPlayingMetaChanged() {}
    override fun onPlayStateChanged() {}
    override fun onRepeatModeChanged() {}
    override fun onShuffleModeChanged() {}
}

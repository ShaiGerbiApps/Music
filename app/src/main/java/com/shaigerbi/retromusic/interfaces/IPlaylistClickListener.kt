package com.shaigerbi.retromusic.interfaces

import android.view.View
import com.shaigerbi.retromusic.db.PlaylistWithSongs

interface IPlaylistClickListener {
    fun onPlaylistClick(playlistWithSongs: PlaylistWithSongs, view: View)
}
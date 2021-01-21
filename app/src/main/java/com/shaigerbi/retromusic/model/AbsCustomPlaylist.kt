package com.shaigerbi.retromusic.model

import com.shaigerbi.retromusic.repository.LastAddedRepository
import com.shaigerbi.retromusic.repository.SongRepository
import com.shaigerbi.retromusic.repository.TopPlayedRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class AbsCustomPlaylist(
    id: Long,
    name: String
) : Playlist(id, name), KoinComponent {

    abstract fun songs(): List<Song>

    protected val songRepository by inject<SongRepository>()

    protected val topPlayedRepository by inject<TopPlayedRepository>()

    protected val lastAddedRepository by inject<LastAddedRepository>()
}
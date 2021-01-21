package com.shaigerbi.retromusic.model.smartplaylist

import com.shaigerbi.retromusic.App
import com.shaigerbi.retromusic.R
import com.shaigerbi.retromusic.model.Song
import kotlinx.android.parcel.Parcelize
import org.koin.core.KoinComponent

@Parcelize
class HistoryPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.history),
    iconRes = R.drawable.ic_history
), KoinComponent {

    override fun songs(): List<Song> {
        return topPlayedRepository.recentlyPlayedTracks()
    }
}
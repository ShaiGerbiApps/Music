package com.shaigerbi.retromusic.model.smartplaylist

import com.shaigerbi.retromusic.App
import com.shaigerbi.retromusic.R
import com.shaigerbi.retromusic.model.Song
import kotlinx.android.parcel.Parcelize

@Parcelize
class TopTracksPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.my_top_tracks),
    iconRes = R.drawable.ic_trending_up
) {
    override fun songs(): List<Song> {
        return topPlayedRepository.topTracks()
    }
}
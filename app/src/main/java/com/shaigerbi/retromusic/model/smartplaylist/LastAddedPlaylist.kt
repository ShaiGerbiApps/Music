package com.shaigerbi.retromusic.model.smartplaylist

import com.shaigerbi.retromusic.App
import com.shaigerbi.retromusic.R
import com.shaigerbi.retromusic.model.Song
import kotlinx.android.parcel.Parcelize

@Parcelize
class LastAddedPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.last_added),
    iconRes = R.drawable.ic_library_add
) {
    override fun songs(): List<Song> {
        return lastAddedRepository.recentSongs()
    }
}
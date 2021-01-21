package com.shaigerbi.retromusic.model.smartplaylist

import com.shaigerbi.retromusic.App
import com.shaigerbi.retromusic.R
import com.shaigerbi.retromusic.model.Song
import kotlinx.android.parcel.Parcelize

@Parcelize
class ShuffleAllPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.action_shuffle_all),
    iconRes = R.drawable.ic_shuffle
) {
    override fun songs(): List<Song> {
        return songRepository.songs()
    }
}
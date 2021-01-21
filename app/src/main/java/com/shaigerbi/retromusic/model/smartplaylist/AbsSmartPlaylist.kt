package com.shaigerbi.retromusic.model.smartplaylist

import androidx.annotation.DrawableRes
import com.shaigerbi.retromusic.R
import com.shaigerbi.retromusic.model.AbsCustomPlaylist

abstract class AbsSmartPlaylist(
    name: String,
    @DrawableRes val iconRes: Int = R.drawable.ic_queue_music
) : AbsCustomPlaylist(
    id = PlaylistIdGenerator(name, iconRes),
    name = name
)
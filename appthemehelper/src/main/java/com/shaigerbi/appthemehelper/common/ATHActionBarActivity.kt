package com.shaigerbi.appthemehelper.common

import androidx.appcompat.widget.Toolbar

import com.shaigerbi.appthemehelper.util.ToolbarContentTintHelper

class ATHActionBarActivity : ATHToolbarActivity() {

    override fun getATHToolbar(): Toolbar? {
        return ToolbarContentTintHelper.getSupportActionBarView(supportActionBar)
    }
}

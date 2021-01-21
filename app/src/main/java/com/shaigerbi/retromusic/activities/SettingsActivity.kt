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

import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.NavController
import com.shaigerbi.appthemehelper.ThemeStore
import com.shaigerbi.appthemehelper.util.VersionUtils
import com.shaigerbi.retromusic.R
import com.shaigerbi.retromusic.activities.base.AbsBaseActivity
import com.shaigerbi.retromusic.appshortcuts.DynamicShortcutManager
import com.shaigerbi.retromusic.extensions.applyToolbar
import com.shaigerbi.retromusic.extensions.findNavController
import com.afollestad.materialdialogs.color.ColorChooserDialog
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AbsBaseActivity(), ColorChooserDialog.ColorCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        setDrawUnderStatusBar()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setStatusbarColorAuto()
        setNavigationbarColorAuto()
        setLightNavigationBar(true)
        setupToolbar()
    }

    private fun setupToolbar() {
        setTitle(R.string.action_settings)
        applyToolbar(toolbar)
        val navController: NavController = findNavController(R.id.contentFrame)
        navController.addOnDestinationChangedListener { _, _, _ ->
            toolbar.title = navController.currentDestination?.label
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.contentFrame).navigateUp() || super.onSupportNavigateUp()
    }

    override fun onColorSelection(dialog: ColorChooserDialog, selectedColor: Int) {
        when (dialog.title) {
            R.string.accent_color -> {
                ThemeStore.editTheme(this).accentColor(selectedColor).commit()
                if (VersionUtils.hasNougatMR())
                    DynamicShortcutManager(this).updateDynamicShortcuts()
            }
        }
        recreate()
    }

    override fun onColorChooserDismissed(dialog: ColorChooserDialog) {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}

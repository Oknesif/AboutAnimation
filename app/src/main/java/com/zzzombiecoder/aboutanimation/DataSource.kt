package com.zzzombiecoder.aboutanimation

import android.content.Context
import android.net.Uri
import com.zzzombiecoder.aboutanimation.collapsing.layout.InfoItem

class DataSource(context: Context) {

    private val resources = context.resources
    private val packageName = context.packageName

    fun getMainInfoItem(): InfoItem {
        return InfoItem(
                text = resources.getString(R.string.main_title),
                additionalText = resources.getString(R.string.main_subtitle),
                imageUri = "cake".toUri())
    }

    fun getInfoItems(): List<InfoItem> {
        return listOf(
                InfoItem(
                        imageUri = "cake_about".toUri(),
                        text = resources.getString(R.string.about_title),
                        additionalText = resources.getString(R.string.about_content)),
                InfoItem(
                        imageUri = "cake_menu".toUri(),
                        text = resources.getString(R.string.menu_title),
                        additionalText = resources.getString(R.string.menu_content)),
                InfoItem(
                        imageUri = "cake_catalog".toUri(),
                        text = resources.getString(R.string.catalog_title),
                        additionalText = resources.getString(R.string.catalog_content))
        )

    }

    private fun String.toUri(): Uri {
        return Uri.parse("android.resource://$packageName/drawable/$this")
    }
}
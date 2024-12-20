package com.example.playlistmaker.data.settings.sharing.imp


import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.settings.sharing.repository.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun shareApp() {
        val linkUrl = context.getString(R.string.appLinkUrl)
        val intentShare = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, linkUrl)
            type = "text/plain"
        }
        context.startActivity(
            Intent.createChooser(intentShare, "Share App").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun openSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.emailUrl)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.emailSubtext))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.emailTitleText))
        }

        context.startActivity(
            Intent.createChooser(
                emailIntent,
                context.getString(R.string.emailUrl)
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun openTermsOfUse() {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.termOfUseRl))).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )
        context.startActivity(browserIntent)
    }
}
package com.example.aaa_magnumbread

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * Membuat sebagian teks (linkText) di dalam TextView berwarna biru dan bisa diklik.
 * Contoh: "Belum punya akun? Daftar" -> hanya kata "Daftar" yang biru & bisa diklik.
 */
fun TextView.setClickableLink(linkText: String, action: () -> Unit) {
    val fullText = text.toString()
    val start = fullText.indexOf(linkText)
    if (start < 0) {
        // Kata link tidak ditemukan -> seluruh teks dijadikan tombol
        setOnClickListener { action() }
        return
    }

    val spannable = SpannableString(fullText)
    spannable.setSpan(
        object : ClickableSpan() {
            override fun onClick(widget: View) {
                action()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(context, R.color.primary_blue)
                ds.isUnderlineText = false
            }
        },
        start,
        start + linkText.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    text = spannable
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT
}
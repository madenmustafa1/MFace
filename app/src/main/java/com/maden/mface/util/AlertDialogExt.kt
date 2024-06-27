package com.maden.mface.util

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText


fun Context.showInputDialog(input: (input: String) -> Unit) {
    val editText = EditText(this)

    val dialog = AlertDialog.Builder(this)
        .setTitle("Enter name")
        .setView(editText)
        .setPositiveButton("OK") { _, _ ->
            input(editText.text.toString())
        }
        .setNegativeButton("Cancel", null)
        .create()

    dialog.show()
}
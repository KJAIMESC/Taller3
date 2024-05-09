package com.example.taller3.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.example.taller3.R

class Alerts constructor(private val context: Context) {
    private var TAG = Alerts::class.java.name

    fun shortToast(message: String?) {
        Log.i(TAG, "shortToast: $message")
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun longToast(message: String?) {
        Log.i(TAG, "longToast: $message")
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun shortSimpleSnackbar(parentView: View, message: String) {
        Log.i(TAG, "shortSimpleSnackbar: $message")
        Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT).show()
    }

    fun longSimpleSnackbar(parentView: View, message: String) {
        Log.i(TAG, "longSimpleSnackbar: $message")
        Snackbar.make(parentView, message, Snackbar.LENGTH_LONG).show()
    }

    fun indefiniteSnackbar(parentView: View, message: String) {
        Log.i(TAG, "indefiniteSnackbar: $message")
        val snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(R.string.cerrar) { snackbar.dismiss() }
        snackbar.show()
    }

    fun showErrorDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.cerrar)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
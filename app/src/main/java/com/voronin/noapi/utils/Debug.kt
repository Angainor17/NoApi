package com.voronin.noapi.utils

import android.util.Log

const val DEBUG_TAG = "noApi"

fun debug(text: String){
    Log.w(DEBUG_TAG, text)
}
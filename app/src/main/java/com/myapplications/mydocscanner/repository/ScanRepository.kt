package com.myapplications.mydocscanner.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.myapplications.mydocscanner.model.ScanItem

class ScanRepository(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("ScanData", Context.MODE_PRIVATE)
    private val gson = Gson()

    private companion object {
        const val IN_LIST_KEY = "in_list_key"
        const val OUT_LIST_KEY = "out_list_key"
    }

    fun saveLists(inList: List<ScanItem>, outList: List<ScanItem>) {
        val inListJson = gson.toJson(inList)
        val outListJson = gson.toJson(outList)
        sharedPreferences.edit()
            .putString(IN_LIST_KEY, inListJson)
            .putString(OUT_LIST_KEY, outListJson)
            .apply()
    }

    fun loadInList(): MutableList<ScanItem> {
        val json = sharedPreferences.getString(IN_LIST_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<ScanItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    fun loadOutList(): MutableList<ScanItem> {
        val json = sharedPreferences.getString(OUT_LIST_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<ScanItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }
}

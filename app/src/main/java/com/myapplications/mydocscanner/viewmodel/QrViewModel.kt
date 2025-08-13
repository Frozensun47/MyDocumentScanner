package com.myapplications.mydocscanner.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class QrViewModel : ViewModel() {

    // Using mutableStateListOf to make it observable by Compose UI
    val inList = mutableStateListOf<String>()
    val outList = mutableStateListOf<String>()

    fun addItem(item: String, status: Status) {
        // Avoid adding duplicates to the same list
        when (status) {
            Status.IN -> if (!inList.contains(item)) inList.add(item)
            Status.OUT -> if (!outList.contains(item)) outList.add(item)
        }
    }

    fun switchItemStatus(item: String, from: Status) {
        when (from) {
            Status.IN -> {
                if (inList.remove(item)) {
                    outList.add(item)
                }
            }
            Status.OUT -> {
                if (outList.remove(item)) {
                    inList.add(item)
                }
            }
        }
    }
}

enum class Status {
    IN, OUT
}

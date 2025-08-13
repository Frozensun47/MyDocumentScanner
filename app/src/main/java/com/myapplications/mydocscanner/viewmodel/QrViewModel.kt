package com.myapplications.mydocscanner.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.myapplications.mydocscanner.model.ScanItem
import com.myapplications.mydocscanner.model.Status
import com.myapplications.mydocscanner.model.StatusLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class QrViewModel : ViewModel() {

    private val _inList = mutableStateListOf<ScanItem>()
    private val _outList = mutableStateListOf<ScanItem>()

    val inList: List<ScanItem> = _inList
    val outList: List<ScanItem> = _outList

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun addItem(item: ScanItem): Boolean {
        val content = item.content
        val exists = _inList.any { it.content == content } || _outList.any { it.content == content }
        if (exists) {
            return false
        }
        // Add the initial status log
        item.logs.add(StatusLog(item.status))

        when (item.status) {
            Status.IN -> _inList.add(0, item)
            Status.OUT -> _outList.add(0, item)
        }
        return true
    }

    fun switchItemStatus(item: ScanItem) {
        if (item.status == Status.IN) {
            _inList.remove(item)
            item.status = Status.OUT
            _outList.add(0, item)
        } else {
            _outList.remove(item)
            item.status = Status.IN
            _inList.add(0, item)
        }
        // Add a new log entry for the status change
        item.logs.add(StatusLog(item.status))
    }

    fun updateItem(item: ScanItem, newContent: String, newNotes: String) {
        item.content = newContent
        item.notes = newNotes
        // Force recomposition by creating a copy to trigger state update
        when (item.status) {
            Status.IN -> {
                val index = _inList.indexOfFirst { it.id == item.id }
                if (index != -1) _inList[index] = item.copy()
            }
            Status.OUT -> {
                val index = _outList.indexOfFirst { it.id == item.id }
                if (index != -1) _outList[index] = item.copy()
            }
        }
    }

    fun deleteAllData() {
        _inList.clear()
        _outList.clear()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}

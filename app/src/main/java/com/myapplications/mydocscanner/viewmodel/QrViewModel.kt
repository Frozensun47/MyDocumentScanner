package com.myapplications.mydocscanner.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.myapplications.mydocscanner.model.ScanItem
import com.myapplications.mydocscanner.model.Status
import com.myapplications.mydocscanner.model.StatusLog
import com.myapplications.mydocscanner.repository.ScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class QrViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScanRepository(application)

    private val _inList = mutableStateListOf<ScanItem>()
    private val _outList = mutableStateListOf<ScanItem>()

    val inList: List<ScanItem> = _inList
    val outList: List<ScanItem> = _outList

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _inList.addAll(repository.loadInList())
        _outList.addAll(repository.loadOutList())
    }

    private fun saveData() {
        repository.saveLists(_inList, _outList)
    }

    fun addItem(item: ScanItem): Boolean {
        val content = item.content
        val exists = _inList.any { it.content == content } || _outList.any { it.content == content }
        if (exists) {
            return false
        }
        item.logs.add(StatusLog(item.status))

        when (item.status) {
            Status.IN -> _inList.add(0, item)
            Status.OUT -> _outList.add(0, item)
        }
        saveData()
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
        item.logs.add(StatusLog(item.status))
        saveData()
    }

    fun updateItem(item: ScanItem, newContent: String, newNotes: String) {
        item.content = newContent
        item.notes = newNotes
        when (item.status) {
            Status.IN -> {
                val index = _inList.indexOfFirst { it.id == item.id }
                if (index != -1) _inList[index] = item.copy(logs = item.logs)
            }
            Status.OUT -> {
                val index = _outList.indexOfFirst { it.id == item.id }
                if (index != -1) _outList[index] = item.copy(logs = item.logs)
            }
        }
        saveData()
    }

    fun deleteItem(item: ScanItem) {
        // Remove the item from whichever list it's in.
        _inList.remove(item)
        _outList.remove(item)
        saveData() // Save the updated lists.
    }

    fun deleteAllData() {
        _inList.clear()
        _outList.clear()
        saveData()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}

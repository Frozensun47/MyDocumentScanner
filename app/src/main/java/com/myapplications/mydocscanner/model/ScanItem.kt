package com.myapplications.mydocscanner.model

import java.util.Date
import java.util.UUID

// Represents a single scanned item with its details.
data class ScanItem(
    val id: String = UUID.randomUUID().toString(), // Unique ID for each item
    var content: String,
    var notes: String = "",
    val logs: MutableList<StatusLog> = mutableListOf(),
    var status: Status
) {
    val lastUpdated: Date
        get() = logs.lastOrNull()?.timestamp ?: Date()
}


// Represents a single log entry for a status change.
data class StatusLog(
    val status: Status,
    val timestamp: Date = Date()
)

// Enum to represent the status of the scanned item.
enum class Status {
    IN, OUT
}

package com.codinginflow.todolist.data

import androidx.room.TypeConverter
import com.codinginflow.todolist.data.models.Priority

class Converter {

    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(priority: String): Priority {
        return Priority.valueOf(priority)
    }

}

// Should learn this
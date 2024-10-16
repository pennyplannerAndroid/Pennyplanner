package com.penny.planner.helpers

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import java.sql.Date

class FirebaseTimestampConverter {

    @TypeConverter
    fun fromTimestamp(timestamp: Timestamp): Long {
        return timestamp.toDate().time
    }

    @TypeConverter
    fun toTimeStamp(time: Long): Timestamp {
        return Timestamp(Date(time))
    }

}
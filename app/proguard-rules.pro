# OSMDroid
-keep class org.osmdroid.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep enum names for Room TypeConverters
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep data classes used as Room entities
-keep class com.opsec.fieldintelligence.data.model.** { *; }

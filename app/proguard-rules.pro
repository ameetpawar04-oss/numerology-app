# Keep Kotlin classes
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# Keep Jetpack Compose
-keep class androidx.compose.** { *; }

# Keep Room database
-keep class androidx.room.** { *; }
-keepclasseswithmembernames class * {
    @androidx.room.* <methods>;
}

# Keep DataStore
-keep class androidx.datastore.** { *; }

# Keep our application classes
-keep class com.amietppawar.numerology.** { *; }
-keep class com.amietppawar.numerology.core.** { *; }

# Preserve line numbers for crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

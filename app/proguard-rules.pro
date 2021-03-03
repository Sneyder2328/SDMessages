# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class groupName to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file groupName.
#-renamesourcefileattribute SourceFile

# It's here solving "Execution failed for task ':app:transformClassesAndResourcesWithProguardForRelease'." error
-ignorewarnings
-keep class * {
    public private *;
}

# These rules were provided directly by the libraries:
# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}


# These other ones were put on my own after some troubles using proguard and some "can't find reference to":
# Amazon
-keep class com.amazon.device.ads.** { *; }
-dontwarn com.amazon.device.ads.**

# Dagger
-keep class dagger.android.** { *; }
-dontwarn dagger.android.**

# Okio
-keep class okio.** { *; }
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Retrofit2
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
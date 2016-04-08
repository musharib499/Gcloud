# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/ankit/Setups/android-sdk-linux/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#-keepclassmembers class com.gcloud.gaadi.ui {
#  public *;
#}
#}

##---------------Begin: proguard configuration common for all Android apps ----------
-optimizationpasses 5

# When not preverifying in a case-sensitive filing system,such as Windows.
# Because this tool unpacks your processed jars,you should then use :
-dontusemixedcaseclassnames

#specifies not to ignore non-public library classes.As of version 4.5 this is default setting
-dontskipnonpubliclibraryclasses


# Specifies not to ignore package visible library class members(fileds and methods).
# By default proguard skips these class members while parsing library classes,
# as program classes generally not refers to them.
# It can be useful to actually read the class members,in order to make sure the proceed code remains consistent.
-dontskipnonpubliclibraryclassmembers

# Preverification is irrevelant for the dexcompiler and the Dalvik VM.
#So we can switch off with -dontpreverify option.
-dontpreverify

# Specifies to write out some more information duringprocessing.If the program terminate with exception,
#this option will print the entire stacktrace,instead of just exception message.
-verbose

#dump class_files
-dump class_files.txt

#print all files
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt

# To understand or change this check to specifies the optimization to be enabled or disabled at a more fine-grained level.
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# Specifies that the access modifier of classes and class members may be broadend during processing.
#This can improve result of optimisation steps.
-allowaccessmodification

#Uncomment if using annotaion
-keepattributes *Annotation*

-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# To repackage classes on a single package
-repackageclasses ''


-keepattributes Signature

# keep classes that are referenced on the Android Manifest
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.
-keepclassmembers class **.R$* {
  public static <fields>;
}

# Preserve the special static methods that are required in all enumeration classes.
#Maintain enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


-keep public class * {
    public protected *;
}

# To keep parcelable classes(to serialize or deserialize objects to sent through intents)
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

##---------------End: proguard configuration common for all Android apps ----------


 ##---------------Begin: proguard configuration for Gson  ----------
 # Gson uses generic type information stored in a class file when working with fields. Proguard
 # removes such information by default, so configure it to keep all of it.
-dontwarn com.gallerylib.**

-dontwarn rx.**

-dontwarn okio.**

-dontwarn com.squareup.okhttp.*

-dontwarn retrofit.appengine.UrlFetchClient

-keepattributes *Annotation*

-keep class retrofit.** { *; }

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
#-keep class retrofit.** { *; }
-keep class package.with.model.classes.** { *; }
#-keepclassmembernames interface * {
#    @retrofit.http.* <methods>;
#}

-keepattributes Signature

# Exclude those 2 dependency classes, cause otherwise it wont compile
-dontwarn com.arellomobile.android.push.**
-dontwarn com.amazon.**
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

-keep class com.gcloud.gaadi.chat.**{*;}
-keep class com.gcloud.gaadi.model.**{*;}

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

##---------------End: proguard configuration for Gson  ----------


##---------------BEGIN: proguard configuration for Joda  ----------
-dontwarn org.joda.convert.FromString
-dontwarn org.joda.convert.ToString
##---------------End: proguard configuration for Joda  ----------

-dontwarn com.google.android.gms.**
-dontwarn com.pushwoosh.support.v4.app.**

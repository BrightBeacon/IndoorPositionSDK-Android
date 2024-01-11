# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontshrink
-dontusemixedcaseclassnames
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

#继承activity,application,service,broadcastReceiver,contentprovider....不进行混淆
-keep public class * extends android.app.** {*;}
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

## 保留support下的所有类及其内部类
-keep class android.support.** {*;}

#接入Google原生的一些服务时使用。
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

#表示不混淆任何包含native方法的类的类名以及native方法名
-keepclasseswithmembernames class * {
    native <methods>;
}

-keep class com.mapbox.** {*;}
-keep class com.google.auto.** {*;}
-keep class com.brtbeacon.** {*;}
-keep class android.app.** {*;}
-keep class org.xutils.** {*;}
-keep class com.google.gson.** {*;}
-keep class sun.misc.Unsafe { *; }
-keep class com.vividsolutions.** {*;}
-keep class java.awt.** {*;}
-keep class android.arch.** {*;}
-keep class okio.** {*;}
-keep class org.apache.commons.** {*;}

-dontwarn java.awt.**
-dontwarn com.google.auto.**

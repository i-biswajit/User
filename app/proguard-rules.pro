# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/bowshulsheikrahaman/Library/Android/sdk/tools/proguard/proguard-android.txt
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
-keep class android.support.v7.widget.** { *; }

-ignorewarnings

#-keep class * {
#    public private *;
#}

#-keep public class

# Butterknife
-dontwarn butterknife.internal.**
-keep class butterknife.** { *; }
-keep class **$$ViewInjector { *; }
-keep class android.support.graphics.** { *; }
-keep class android.support.animation.** { *; }
-keep class android.animation.** { *; }
-keep class android.graphics.** { *; }
-keep class android.view.animation.** { *; }

-keepclasseswithmembernames class * {
    @butterknife.BindView <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.OnClick <methods>;
    @butterknife.OnEditorAction <methods>;
    @butterknife.OnItemClick <methods>;
    @butterknife.OnItemLongClick <methods>;
    @butterknife.OnLongClick <methods>;
}

#For Stripe payment (card payment)
-keep class com.stripe.android.** { *; }


#For retrofit
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-keepattributes *Annotation*,SourceFile,LineNumberTable

# Support Design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
-keep class android.support.v7.widget.AppCompatImageView { *; }
-dontwarn com.github.mikephil.**
-keep public class com.cab.user.common.helper.** {
     public protected *;
}

-keep class com.cab.user.taxi.views.firebaseChat.FirebaseChatModelClass{ *; }

-keep class com.cab.user.taxi.views.main.MainActivity{ *; }
-keep class com.cab.user.common.datamodels.**{ *; }
-keep class com.cab.user.taxi.datamodels.**{ *; }
-keep class com.cab.user.common.map.**{ *; }

-keep class com.cab.user.taxi.views.voip.CabmeSinchService{ *; }
#}



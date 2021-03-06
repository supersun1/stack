package me.tylerbwong.stack.data.auth.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.tylerbwong.stack.data.SiteStore
import me.tylerbwong.stack.ui.settings.Experimental
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SiteSharedPreferences

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthSharedPreferences

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalSharedPreferences

@Module
@InstallIn(SingletonComponent::class)
class SharedPreferencesModule {

    @Provides
    @SiteSharedPreferences
    fun provideSiteSharedPreferences(
        context: Context
    ): SharedPreferences = context.getSharedPreferences(
        SiteStore.SITE_PREFERENCES,
        Context.MODE_PRIVATE
    )

    @Provides
    @ExperimentalSharedPreferences
    fun provideExperimentalSharedPreferences(
        context: Context
    ): SharedPreferences = context.getSharedPreferences(
        Experimental.EXPERIMENTAL_SHARED_PREFS,
        Context.MODE_PRIVATE
    )

    @Provides
    @AuthSharedPreferences
    fun provideAuthSharedPreferences(
        context: Context
    ): SharedPreferences = EncryptedSharedPreferences.create(
        context,
        AUTH_PREFERENCES,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        internal const val AUTH_PREFERENCES = "auth_preferences"
    }
}

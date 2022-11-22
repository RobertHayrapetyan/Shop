package com.tyche.tyche.di

import android.content.Context
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.objectbox.BoxStore
import io.objectbox.android.Admin
import com.roberthayrapetyan.shop.BuildConfig
import com.roberthayrapetyan.shop.data.entity.MyObjectBox
import dagger.hilt.android.qualifiers.ApplicationContext
import io.objectbox.exception.DbException
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun getContext(@ApplicationContext appContext: Context):Context{
        return appContext
    }

    @Provides
    @Singleton
    fun provideStorage(ctx: Context): BoxStore {
        try {
            return MyObjectBox.builder()
                .androidContext(ctx)
                .maxReaders(256)
                .build()
                .apply {
                    if (BuildConfig.DEBUG) {
                        val started = Admin(this).start(ctx)
                        Log.i("ObjectBoxAdmin", "Started: $started")
                    }
                }

        }catch (e: DbException){
            e.message
            throw e
        }
    }
}
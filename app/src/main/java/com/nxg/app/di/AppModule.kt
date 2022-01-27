package com.nxg.app.di

import android.content.Context
import androidx.room.Room
import com.nxg.app.audiorecord.AudioRecordHandlerLifecycleObserver
import com.nxg.app.data.source.IAudioRecordDocDataSource
import com.nxg.app.data.source.AudioRecordFileRepository
import com.nxg.app.data.source.IAudioRecordFileDataSource
import com.nxg.app.data.source.IAudioRecordFileRepository
import com.nxg.app.data.source.local.AudioRecordFileDataBase
import com.nxg.app.data.source.local.AudioRecordDocLocalDataSource
import com.nxg.app.data.source.local.AudioRecordFileLocalDataSource
import com.nxg.app.data.source.remote.AudioRecordFileRemoteDataSource
import com.nxg.audiorecord.AudioRecordHandler
import com.nxg.audiorecord.AudioTrackHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}

@InstallIn(SingletonComponent::class)
@Module
object CoroutinesScopesModule {

    @Singleton
    @Provides
    fun providesCoroutineScope(
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + defaultDispatcher)

}

@InstallIn(SingletonComponent::class)
@Module
object CoroutinesDispatchersModule {

    @DefaultDispatcher
    @Provides
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @MainImmediateDispatcher
    @Provides
    fun providesMainImmediateDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate
}

@Module
@InstallIn(SingletonComponent::class)
object AudioRecordModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class QualifierAudioRecordLocalDataSource

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class QualifierAudioRecordRemoteDataSource

    @Singleton
    @QualifierAudioRecordLocalDataSource
    @Provides
    fun provideAudioRecordLocalDataSource(
        database: AudioRecordFileDataBase,
        ioDispatcher: CoroutineDispatcher
    ): IAudioRecordFileDataSource {
        return AudioRecordFileLocalDataSource(database.audioRecordFileDao(), ioDispatcher)
    }


    @Singleton
    @QualifierAudioRecordRemoteDataSource
    @Provides
    fun provideAudioRecordRemoteDataSource(
        @ApplicationContext context: Context
    ): IAudioRecordFileDataSource {
        return AudioRecordFileRemoteDataSource(context)
    }

    @Singleton
    @Provides
    fun provideAudioRecordDocLocalDataSource(
        @ApplicationContext context: Context
    ): IAudioRecordDocDataSource {
        return AudioRecordDocLocalDataSource(context)
    }

    @Singleton
    @Provides
    fun provideAudioRecordRepository(
        @AudioRecordModule.QualifierAudioRecordLocalDataSource audioRecordLocalDataSource: IAudioRecordFileDataSource,
        @AudioRecordModule.QualifierAudioRecordRemoteDataSource audioRecordRemoteDataSource: IAudioRecordFileDataSource,
        ioDispatcher: CoroutineDispatcher
    ): IAudioRecordFileRepository {
        return AudioRecordFileRepository(
            audioRecordLocalDataSource,
            audioRecordRemoteDataSource,
            ioDispatcher
        )
    }

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AudioRecordFileDataBase {
        return Room.databaseBuilder(
            context.applicationContext,
            AudioRecordFileDataBase::class.java,
            "AudioRecordFile.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideAudioRecordHandler(
        @ApplicationContext context: Context
        // Potential dependencies of this type
    ): AudioRecordHandler {
        return AudioRecordHandler.Builder().build(context)
    }

    @Singleton
    @Provides
    fun provideAudioTrackHandler(
        @ApplicationContext context: Context
        // Potential dependencies of this type
    ): AudioTrackHandler {
        return AudioTrackHandler.Builder().build()
    }

    /*@Provides
    fun provideAudioRecordHandlerLifecycleObserver(
        @ApplicationContext context: Context
        // Potential dependencies of this type
    ): AudioRecordHandlerLifecycleObserver {
        return AudioRecordHandlerLifecycleObserver()
    }*/
}
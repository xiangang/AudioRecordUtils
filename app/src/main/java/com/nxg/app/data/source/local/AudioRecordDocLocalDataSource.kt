package com.nxg.app.data.source.local

import android.content.Context
import androidx.annotation.NonNull
import androidx.appsearch.app.*
import androidx.appsearch.exceptions.AppSearchException
import androidx.appsearch.localstorage.LocalStorage
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.nxg.app.data.AudioRecordDoc
import com.nxg.app.data.source.IAudioRecordDocDataSource
import com.nxg.audiorecord.LogUtil
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * AppSearch数据源
 */
class AudioRecordDocLocalDataSource @Inject constructor(context: Context) :
    IAudioRecordDocDataSource {

    companion object {
        const val TAG = "AudioRecordLocalDataSource"
        const val DATABASE_NAME = "audio_record_master_app"
        const val NAME_SPACE = "AudioRecordNP"
    }

    /**
     * 使用无界队列（如 LinkedBlockingQueue）作为等待队列，当所有的核心线程都在处理任务时，
     * 新提交的任务都会进入队列等待。因此，不会有大于 corePoolSize 的线程会被创建（maximumPoolSize 也将失去作用）。
     * 这种策略适合每个任务都完全独立于其他任务的情况；例如网站服务器。
     * 这种类型的等待队列可以使瞬间爆发的高频请求变得平滑。
     * 当任务持续到达的平均速度超过可处理速度时，可能导致等待队列无限增长。
     */
    private val mExecutor = ThreadPoolExecutor(
        4, 4, 30L, TimeUnit.SECONDS, LinkedBlockingQueue()
    )

    /**
     * Open a database
     */
    private val sessionFuture = LocalStorage.createSearchSession(
        LocalStorage.SearchContext.Builder(context, /*databaseName=*/DATABASE_NAME)
            .build()
    )

    /**
     * Set a schema
     */
    fun setSchema() {
        val setSchemaRequest =
            SetSchemaRequest.Builder().addDocumentClasses(AudioRecordDoc::class.java)
                .build()

        val setSchemaFuture = Futures.transformAsync(
            sessionFuture,
            { session ->
                session?.setSchema(setSchemaRequest)
            }, mExecutor
        )
    }

    override suspend fun createAudioRecordDoc(a: AudioRecordDoc) {
        putAudioRecord(a)
    }

    override suspend fun updateAudioRecordDoc(a: AudioRecordDoc) {
        putAudioRecord(a)
    }

    override suspend fun readAudioRecordDoc(
        queryExpression: String,
        futureCallback: FutureCallback<SearchResults>
    ) {
        queryAudioRecord(queryExpression, futureCallback)
    }

    override suspend fun deleteAudioRecordDoc(ids: Collection<String>) {
        removeAudioRecord(ids)
    }

    /**
     * Put a document in the database
     */
    private fun putAudioRecord(audioRecordDoc: AudioRecordDoc) {
        /*val audioRecordDoc = AudioRecordSchema(
            namespace = NAME_SPACE,
            id = audioRecordFile.name,
            createTime = audioRecordFile.lastModified(),
            duration = getWAVFileDuration(audioRecordFile.absolutePath),
            filePath = audioRecordFile.absolutePath
        )*/

        val putRequest = PutDocumentsRequest.Builder().addDocuments(audioRecordDoc).build()
        val putFuture = Futures.transformAsync(
            sessionFuture,
            { session ->
                session?.put(putRequest)
            }, mExecutor
        )

        Futures.addCallback(
            putFuture,
            object : FutureCallback<AppSearchBatchResult<String, Void>?> {
                override fun onSuccess(result: AppSearchBatchResult<String, Void>?) {
                    // Gets map of successful results from Id to Void
                    val successfulResults = result?.successes
                    LogUtil.i(TAG, "successfulResults $successfulResults")
                    // Gets map of failed results from Id to AppSearchResult
                    val failedResults = result?.failures
                    LogUtil.i(TAG, "failedResults $failedResults")
                }

                override fun onFailure(t: Throwable) {
                    LogUtil.e(TAG, "Failed to put documents.", t)
                }
            },
            mExecutor
        )
    }

    /**
     * 持久化到磁盘
     */
    fun persist2Disk() {
        val requestFlushFuture = Futures.transformAsync(
            sessionFuture,
            { session -> session?.requestFlush() }, mExecutor
        )

        Futures.addCallback(requestFlushFuture, object : FutureCallback<Void?> {
            override fun onSuccess(result: Void?) {
                // Success! Database updates have been persisted to disk.
                LogUtil.e(TAG, "persist2Disk onSuccess $result")
            }

            override fun onFailure(t: Throwable) {
                LogUtil.e(TAG, "Failed to flush database updates.", t)
            }
        }, mExecutor)
    }

    /**
     * remove a document in the database
     */
    private fun removeAudioRecord(ids: Collection<String>) {
        val removeRequest = RemoveByDocumentIdRequest.Builder(NAME_SPACE)
            .addIds(ids)
            .build()

        val removeFuture = Futures.transformAsync(
            sessionFuture, { session ->
                session?.remove(removeRequest)
            },
            mExecutor
        )
    }

    /**
     * 关闭会话
     */
    fun close() {
        val closeFuture = Futures.transform<AppSearchSession, Unit>(
            sessionFuture,
            { session ->
                session?.close()
                Unit
            }, mExecutor
        )
    }

    /**
     * 根据关键字查询
     */
    @JvmOverloads
    fun queryAudioRecord(
        @NonNull queryExpression: String,
        futureCallback: FutureCallback<SearchResults>
    ) {
        val searchSpec = SearchSpec.Builder()
            .addFilterNamespaces(NAME_SPACE)
            .build();

        val searchFuture = Futures.transform(
            sessionFuture,
            { session ->
                session?.search(queryExpression, searchSpec)
            },
            mExecutor
        )
        Futures.addCallback(
            searchFuture,
            futureCallback,
            mExecutor
        )
    }

    private val mFutureCallback = object : FutureCallback<SearchResults> {
        override fun onSuccess(searchResults: SearchResults?) {
            iterateSearchResults(searchResults)
        }

        override fun onFailure(t: Throwable?) {
            LogUtil.e(TAG, "Failed to search audio record doc in AppSearch.", t)
        }
    }

    /**
     * 遍历结果
     */
    fun iterateSearchResults(searchResults: SearchResults?) {
        Futures.transform(
            searchResults?.nextPage,
            { page: List<SearchResult>? ->
                // Gets GenericDocument from SearchResult.
                val genericDocument: GenericDocument = page!![0].genericDocument
                val schemaType = genericDocument.schemaType
                val audioRecordDoc: AudioRecordDoc? = try {
                    if (schemaType == AudioRecordDoc::class.java.name) {
                        // Converts GenericDocument object to AudioRecordDoc object.
                        genericDocument.toDocumentClass(AudioRecordDoc::class.java)
                    } else null
                } catch (e: AppSearchException) {
                    LogUtil.e(
                        TAG,
                        "Failed to convert GenericDocument to Note",
                        e
                    )
                    null
                }
                audioRecordDoc
            },
            mExecutor
        )
    }


}
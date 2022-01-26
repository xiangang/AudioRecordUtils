package com.nxg.app.data

import androidx.appsearch.annotation.Document
import androidx.appsearch.app.AppSearchSchema

/**
 * AppSearch的录音文件Doc，支持Platform Storage
 */
@Document
data class AudioRecordDoc(

    // Required field for a document class. All documents MUST have a namespace.
    @Document.Namespace
    val namespace: String,

    // Required field for a document class. All documents MUST have an Id.
    @Document.Id
    val id: String,

    @Document.LongProperty
    val createTime: Long,

    @Document.StringProperty
    val duration: String,

    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES)
    val fileName: String,

    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES)
    val filePath: String
)

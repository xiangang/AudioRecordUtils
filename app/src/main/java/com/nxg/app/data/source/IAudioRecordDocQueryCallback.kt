package com.nxg.app.data.source

import org.checkerframework.checker.nullness.compatqual.NullableDecl

interface IAudioRecordDocQueryCallback<T> {

    fun onSuccess(@NullableDecl result: T)

    fun onFailure(t: Throwable)
}
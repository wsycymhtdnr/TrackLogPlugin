package com.xiaoan

import com.android.build.api.transform.QualifiedContent
import com.android.build.gradle.internal.pipeline.TransformManager
import com.xiaoan.plugins.IncrementalTransform
import org.gradle.api.Project
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * @Author: liyunfei
 * @Description: 日志transform
 * @Date: 2022-04-11 01:43
 */
class TrackLogTransform(project: Project) : IncrementalTransform(project) {
    override fun getName(): String {
        return "TrackLogTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return false
    }

    override fun doJarAction(inputStream: InputStream?, outputStream: OutputStream?): Boolean {
        TODO("Not yet implemented")
    }

    override fun doDirectoryAction(inputJar: File?, outputJar: File?): Boolean {
        TODO("Not yet implemented")
    }
}
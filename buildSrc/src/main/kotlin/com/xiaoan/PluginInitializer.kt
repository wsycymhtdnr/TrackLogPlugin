package com.xiaoan

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.GradleException
import org.gradle.api.Project

/**
 * 创建Extensions
 */
object PluginInitializer {
    lateinit var project: Project

    fun initial(project: Project, clazz: Class<*>) {
        val hasAppPlugin = project.plugins.hasPlugin(AppPlugin::class.java)
        val hasLibPlugin = project.plugins.hasPlugin(LibraryPlugin::class.java)
        if (!hasAppPlugin && !hasLibPlugin) {
            throw  GradleException("The 'com.android.application' or 'com.android.library' plugin is required.")
        }
        this.project = project
        // 创建extensions
        project.extensions.create("TrackLogExt", clazz)
    }
}

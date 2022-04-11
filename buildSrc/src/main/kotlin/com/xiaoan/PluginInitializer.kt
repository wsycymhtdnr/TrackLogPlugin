package com.xiaoan

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.GradleException
import org.gradle.api.Project

object PluginInitializer {
    //gradle 拓展名称
    private const val TRACK_LOG_EXT = "TrackLogExt"
    lateinit var project: Project

    fun initial(project: Project) {
        val hasAppPlugin = project.plugins.hasPlugin(AppPlugin::class.java)
        val hasLibPlugin = project.plugins.hasPlugin(LibraryPlugin::class.java)
        if (!hasAppPlugin && !hasLibPlugin) {
            throw  GradleException("Component: The 'com.android.application' or 'com.android.library' plugin is required.")
        }
        this.project = project
        //  创建extensions
        project.extensions.create(TRACK_LOG_EXT, TrackLogExt::class.java)
    }

    private fun getComponentConfig(): TrackLogExt = project.extensions.getByType(TrackLogExt::class.java)
    fun getEventSize(): Int = getComponentConfig().eventSize
}
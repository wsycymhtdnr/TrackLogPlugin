package com.xiaoan.plugins

import com.xiaoan.PluginInitializer
import com.xiaoan.TrackLogExt
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 埋点插件
 */
class TrackLogPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("--------------------TrackLogPlugin starting------------------------")
        PluginInitializer.initial(project, TrackLogExt::class.java)
    }

}
package com.xiaoan.plugins

import com.android.build.gradle.AppPlugin
import com.xiaoan.PluginInitializer
import com.xiaoan.transform.TrackLogTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension




/**
 * 埋点插件
 */
class TrackLogPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("--------------------TrackLogPlugin starting------------------------")
        PluginInitializer.initial(project)
        // 注册埋点transform
        if (project.plugins.hasPlugin(AppPlugin::class.java)) {
            val extension = project.extensions.getByType(AppExtension::class.java)
            extension.registerTransform(TrackLogTransform())
        }
    }

}
package com.xiaoan.utils

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.xiaoan.extension.TrackLogConfigExt
import com.xiaoan.extension.TrackLogConfigExt.Companion.TRACK_LOG_EXT
import com.xiaoan.transform.TrackLogTransform
import org.gradle.api.GradleException
import org.gradle.api.Project

/**
 * @Author liyunfei
 * @Description 初始化插件
 * @Date 2022/4/9 17:58
 */
object PluginInitializer {
    lateinit var project: Project

    fun initial(project: Project) {
        val hasAppPlugin = project.plugins.hasPlugin(AppPlugin::class.java)
        val hasLibPlugin = project.plugins.hasPlugin(LibraryPlugin::class.java)
        if (!hasAppPlugin && !hasLibPlugin) {
            throw  GradleException("Component: The 'com.android.application' or 'com.android.library' plugin is required.")
        }
        PluginInitializer.project = project
        // 创建插件extensions
        project.extensions.create(TRACK_LOG_EXT, TrackLogConfigExt::class.java)
        // 注册插件transform
        val extension = project.extensions.getByType(AppExtension::class.java)
        val configExt = PluginInitializer.project.extensions.getByType(TrackLogConfigExt::class.java)
        extension.registerTransform(TrackLogTransform(configExt))
    }
}
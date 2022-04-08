package com.xiaoan.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class TrackLogPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        System.out.println("--------------------TrackLogPlugin------------------------")
    }
}
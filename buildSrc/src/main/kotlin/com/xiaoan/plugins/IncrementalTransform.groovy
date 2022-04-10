package com.xiaoan.plugins

import com.android.build.api.transform.Transform
import com.google.common.collect.FluentIterable
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * @Author: liyunfei
 * @Description: Transform模板方法
 * @Date: 2022-04-11 01:43
 */
abstract class IncrementalTransform extends Transform {

    private Project project

    IncrementalTransform(Project project) {
        this.project = project
    }

    @Override
    void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        doTransform(transformInvocation)
    }

    private void doTransform(TransformInvocation invocation) {
        TransformOutputProvider outputProvider = invocation.outputProvider

        if (!invocation.isIncremental()) {
            outputProvider.deleteAll()
        }

        invocation.inputs.each { TransformInput transformInput ->
            // JAR
            transformInput.jarInputs.each { JarInput jarInput ->
                new Thread(new Runnable() {
                    @Override
                    void run() {
                        handleJar(jarInput, outputProvider, invocation)
                    }
                }).start()
            }

            // DIR
            transformInput.directoryInputs.each { DirectoryInput directoryInput ->
                new Thread(new Runnable() {
                    @Override
                    void run() {
                        handleDirectory(directoryInput, outputProvider, invocation)
                    }
                }).start()
            }
        }
    }

    private void handleJar(
            JarInput jarInput,
            TransformOutputProvider outputProvider,
            TransformInvocation invocation) {

        //得到上一个Transform输入文件
        File inputJar = jarInput.file
        // 得到当前Transform输出Jar文件
        File outputJar =
                outputProvider.getContentLocation(
                        jarInput.name, jarInput.contentTypes,
                        jarInput.scopes, Format.JAR)

        if (invocation.isIncremental()) {// 增量处理
            if (jarInput.status == Status.NOTCHANGED) {//文件没有改变
                println("IncrementalTransform >>> File NOTCHANGED")
            } else if (jarInput.status == Status.ADDED) {//有新增文件

                dispatchAction(inputJar, outputJar, true)

            } else if (jarInput.status == Status.CHANGED) {//有修改文件

                FileUtils.deleteIfExists(outputJar)// 先把上次生成的文件删除
                dispatchAction(inputJar, outputJar, true)

            } else if (jarInput.status == Status.REMOVED) {//文件被移除

                //把上次当前Transform输出文件删除
                FileUtils.delete(outputJar)
            }
        } else {// 全量处理
            dispatchAction(inputJar, outputJar, true)
        }
    }

    private void handleDirectory(
            DirectoryInput directoryInput, TransformOutputProvider outputProvider,
            TransformInvocation invocation) {
        //得到上一个Transform输入文件目录
        File inputDir = directoryInput.file

        // 得到当前Transform输出文件目录
        File outputDir =
                outputProvider.getContentLocation(
                        directoryInput.name, directoryInput.contentTypes,
                        directoryInput.scopes, Format.DIRECTORY)

        if (invocation.isIncremental()) {
            directoryInput.changedFiles.entrySet().each { Map.Entry<File, Status> entry ->
                File inputFile = entry.key

                if (entry.value == Status.NOTCHANGED) {//文件没有改变
                    println("IncrementalTransform >>> File NOTCHANGED")
                } else if (entry.value == Status.ADDED) {//有增加文件

                    File outputFile = FileUtil.toOutputFile(outputDir, inputDir, inputFile)
                    dispatchAction(inputFile, outputFile, false)

                } else if (entry.value == Status.CHANGED) {//文件有修改

                    File outputFile = FileUtil.toOutputFile(outputDir, inputDir, inputFile)
                    FileUtils.deleteIfExists(outputFile)// 先把上次生成的文件删除
                    dispatchAction(inputFile, outputFile, false)

                } else if (entry.value == Status.REMOVED) {//文件被移除

                    //把上次输出的目录删除
                    File outputFile = FileUtil.toOutputFile(outputDir, inputDir, inputFile)
                    FileUtils.deleteIfExists(outputFile)
                }
            }
        } else {
            // 上一个Transform的输出目录下的所有文件
            FluentIterable<File> dirChildFiles = FileUtils.getAllFiles(inputDir)

            dirChildFiles.each { File inputFile ->
                // 当前Transform输出文件
                File outputFile = FileUtil.toOutputFile(outputDir, inputDir, inputFile)
                dispatchAction(inputFile, outputFile, false)
            }
        }
    }


    protected void dispatchAction(
            File inputFile, File outputFile, boolean handleJar) {
        if (handleJar) {//JAR
            // 输出目标jar文件
            FileOutputStream fos = new FileOutputStream(outputFile)
            JarOutputStream outputJarOs = new JarOutputStream(fos)

            //处理输入Jar文件
            JarFile inputJarFile = new JarFile(inputFile)
            Enumeration<JarEntry> entries = inputJarFile.entries()

            while (entries.hasMoreElements()) {
                JarEntry inputJarEntry = entries.nextElement()
                String inputJarEntryName = inputJarEntry.getName()

                // 拿到jar包里面的输入流
                InputStream inputJarEntryInputStream =
                        inputJarFile.getInputStream(inputJarEntry)

                // 构造目标jar文件里的文件实体  即保持和上一个JarEntry名称一致
                outputJarOs.putNextEntry(new ZipEntry(inputJarEntryName))

                //如果不做是否处理，那么仅仅只是将该文件复制到当前Transform的目标输出文件即可
                boolean isHandle =
                        doJarAction(inputJarEntryInputStream, outputJarOs)
                if (!isHandle) {
                    //将修改过的字节码copy到dest
                    outputJarOs.write(
                            IOUtils.toByteArray(inputJarEntryInputStream)
                    )
                }
                inputJarEntryInputStream.close()
            }

            outputJarOs.closeEntry()
            outputJarOs.close()
            inputJarFile.close()
            return
        }
        //DIR

        //如果不做是否处理，那么仅仅只是将该文件复制到当前Transform的目标输出文件即可
        boolean isHandle = doDirectoryAction(inputFile, outputFile)
        if (!isHandle) {
            //将修改过的字节码copy到dest
            FileUtil.copyFileAndMkdirsAsNeed(inputFile, outputFile)
        }
    }

/**
 *  处理Jar文件的资源
 *
 *  这些都是在工作线程中执行的
 *
 * @param inputStream 上一个Transform的输入流
 * @param outputStream 当前Transform的输出流
 * @return isHandle 是否已经处理了该文件，如果已经处理了文件返回 true
 *
 */
    protected abstract boolean doJarAction(InputStream inputStream, OutputStream outputStream)


/**
 * 处理目录的资源文件
 *
 *
 * 这些都是在 工作线程中执行的
 *
 * @param inputJar 上一个Transform的输入文件
 * @param outputJar 当前Transform的输出文件
 * @return 是否已经处理了该文件，如果已经处理了文件返回 true
 */
    protected abstract boolean doDirectoryAction(File inputJar, File outputJar)
}
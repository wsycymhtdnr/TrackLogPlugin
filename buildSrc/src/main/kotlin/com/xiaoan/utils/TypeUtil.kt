package com.xiaoan.utils

/**
 * 类型判断工具类，用来区分是否是某个特定的类型
 *
 * @author liyunfei
 */
object TypeUtil {

    fun isMatchCondition(name: String): Boolean {
        return name.endsWith(".class")
                && !name.contains("R$")
                && !name.contains("R.class")
                && !name.contains("BuildConfig.class")
    }
}

/*
fun weaveJarTask(input: File, output: File) {
    //input: build\intermediates\runtime_library_classes\debug\classes.jar
    //output: build\intermediates\transforms\ScannerComponentTransformKt\debug\0.jar
    println("input: ${input.absolutePath}  output: ${output.absolutePath}")
    var zipOutputStream: ZipOutputStream? = null
    var zipFile: ZipFile? = null
    try {
        zipOutputStream =
            ZipOutputStream(BufferedOutputStream(Files.newOutputStream(output.toPath())))
        zipFile = ZipFile(input)
        val enumeration = zipFile.entries()
        while (enumeration.hasMoreElements()) {
            val zipEntry = enumeration.nextElement()
            val zipEntryName = zipEntry.name
            //jar文件里面就是class文件的了
            // com/github/plugin/usercenter/UserComponent.class
            // com/github/plugin/common/BuildConfig.class
            println("zipEntryName:$zipEntryName")
            if (isMatchCondition(zipEntryName)) {
                val data =
                    WeaveSingleClass.weaveSingleClassToByteArray(BufferedInputStream(zipFile.getInputStream(
                        zipEntry)))
                val byteArrayInputStream = ByteArrayInputStream(data)
                val newZipEntry = ZipEntry(zipEntryName)
                ZipFileUtils.addZipEntry(zipOutputStream, newZipEntry, byteArrayInputStream)
            } else {
                val inputStream = zipFile.getInputStream(zipEntry)
                val newZipEntry = ZipEntry(zipEntryName)
                ZipFileUtils.addZipEntry(zipOutputStream, newZipEntry, inputStream)
            }
        }
    } catch (e: Exception) {
    } finally {
        try {
            if (zipOutputStream != null) {
                zipOutputStream.finish()
                zipOutputStream.flush()
                zipOutputStream.close()
            }
            zipFile?.close()
        } catch (e: Exception) {
            println("close stream err!")
        }
    }
}*/

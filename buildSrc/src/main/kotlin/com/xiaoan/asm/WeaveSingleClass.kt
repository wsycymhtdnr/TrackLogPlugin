package com.xiaoan.asm

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.InputStream

object WeaveSingleClass {

    fun weaveTrackLog(inputStream: InputStream): ByteArray {
        //1、解析字节码
        val classReader = ClassReader(inputStream)
        //2、修改字节码
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val classVisitor = TrackLogVisitor(classWriter)
        //3、开始解析字节码
        classReader.accept(classVisitor, ClassReader.SKIP_FRAMES)
        return classWriter.toByteArray()
    }
}
package com.xiaoan

import com.xiaoan.plugins.TrackLogClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.InputStream

/**
 * @Author: liyunfei
 * @Description: 插入埋点代码
 * @Date: 2022-04-11 01:43
 */
object WeaveTrackLogClass {
    fun weaveSingleClassToByteArray(inputStream: InputStream): ByteArray {
        //1、解析字节码
        val classReader = ClassReader(inputStream)
        //2、修改字节码
        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
        val classVisitor = TrackLogClassVisitor(classWriter)
        //3、开始解析字节码
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }
}
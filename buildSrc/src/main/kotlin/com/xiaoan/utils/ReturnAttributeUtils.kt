package com.xiaoan.utils

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.commons.LocalVariablesSorter

/**
 * @Author: liyunfei
 * @Description:
 * @Date: 2022-04-20 22:56
 */
object ReturnAttributeUtils {
    fun printBoolean(mv: MethodVisitor) {
        mv.visitFieldInsn(AdviceAdapter.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitInsn(AdviceAdapter.SWAP)
        mv.visitMethodInsn(AdviceAdapter.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false)
    }

    fun printChar(mv: MethodVisitor) {
        mv.visitFieldInsn(AdviceAdapter.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitInsn(AdviceAdapter.SWAP)
        mv.visitMethodInsn(AdviceAdapter.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(C)V", false)
    }

    fun printInt(mv: MethodVisitor) {
        mv.visitFieldInsn(AdviceAdapter.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitInsn(AdviceAdapter.SWAP)
        mv.visitMethodInsn(AdviceAdapter.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false)
    }

    fun printFloat(mv: MethodVisitor) {
        mv.visitFieldInsn(AdviceAdapter.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitInsn(AdviceAdapter.SWAP)
        mv.visitMethodInsn(AdviceAdapter.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V", false)
    }

    fun printLong(mv: MethodVisitor) {
        mv.visitFieldInsn(AdviceAdapter.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitInsn(AdviceAdapter.DUP_X2)
        mv.visitInsn(AdviceAdapter.POP)
        mv.visitMethodInsn(AdviceAdapter.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false)
    }

    fun printDouble(mv: MethodVisitor) {
        mv.visitFieldInsn(AdviceAdapter.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitInsn(AdviceAdapter.DUP_X2)
        mv.visitInsn(AdviceAdapter.POP)
        mv.visitMethodInsn(AdviceAdapter.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(D)V", false)
    }

    fun printString(mv: MethodVisitor) {
        mv.visitFieldInsn(AdviceAdapter.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitInsn(AdviceAdapter.SWAP)
        mv.visitMethodInsn(AdviceAdapter.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
    }

    fun printObject(mv: LocalVariablesSorter, returnKey: String, attributesIndex: Int) {

        mv.visitFieldInsn(AdviceAdapter.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitInsn(AdviceAdapter.DUP)
        val temp = mv.newLocal(Type.INT_TYPE)
        mv.visitVarInsn(AdviceAdapter.ASTORE, temp)
        mv.visitLdcInsn(returnKey)
        mv.visitVarInsn(AdviceAdapter.ASTORE, temp+1)
        mv.visitVarInsn(AdviceAdapter.ALOAD, attributesIndex)
        mv.visitVarInsn(AdviceAdapter.ALOAD, temp+1)
        mv.visitVarInsn(AdviceAdapter.ALOAD, temp)
        mv.visitMethodInsn(AdviceAdapter.INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true)
        mv.visitInsn(AdviceAdapter.POP)

//        mv.visitFieldInsn(AdviceAdapter.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitVarInsn(AdviceAdapter.ALOAD, attributesIndex)
        mv.visitMethodInsn(AdviceAdapter.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
        mv.visitInsn(AdviceAdapter.POP)
    }
}
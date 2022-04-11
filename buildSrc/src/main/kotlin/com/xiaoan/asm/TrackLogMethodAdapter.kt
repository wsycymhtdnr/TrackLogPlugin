package com.xiaoan.asm

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @Author liyunfei
 * @Description 用于获取@TrackLog标注的方法的信息
 * @Date 2022/4/11 15:52
 */
class TrackLogMethodAdapter(methodVisitor: MethodVisitor?, access: Int, name: String?, descriptor: String?) :
    AdviceAdapter(Opcodes.ASM7, methodVisitor, access, name, descriptor) {

}
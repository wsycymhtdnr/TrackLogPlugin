package com.xiaoan.plugins

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

/**
 * @Author: liyunfei
 * @Description:
 * @Date: 2022-04-11 04:57
 */
class TrackLogClassVisitor(classVisitor: ClassVisitor): ClassVisitor(Opcodes.ASM7, classVisitor){
}
package com.example.tracklog;

//import org.checkerframework.checker.interning.qual.UnknownInterned;
//import org.checkerframework.framework.qual.DefaultFor;
//import org.checkerframework.framework.qual.LiteralKind;
//import org.checkerframework.framework.qual.QualifierForLiterals;
//import org.checkerframework.framework.qual.SubtypeOf;
//import org.checkerframework.framework.qual.TypeKind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
//@SubtypeOf(UnknownInterned.class)
//@QualifierForLiterals({LiteralKind.PRIMITIVE, LiteralKind.STRING}) // everything but NULL
//@DefaultFor(
//        typeKinds = {
//                TypeKind.BOOLEAN,
//                TypeKind.BYTE,
//                TypeKind.CHAR,
//                TypeKind.DOUBLE,
//                TypeKind.FLOAT,
//                TypeKind.INT,
//                TypeKind.LONG,
//                TypeKind.SHORT
//        })
public @interface LocalVariableJava {
    String key();
}

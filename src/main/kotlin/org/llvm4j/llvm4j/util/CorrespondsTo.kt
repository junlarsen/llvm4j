package org.llvm4j.llvm4j.util

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
public annotation class CorrespondsTo(vararg val names: String)

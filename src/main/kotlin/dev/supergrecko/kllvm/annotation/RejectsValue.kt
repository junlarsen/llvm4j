package dev.supergrecko.kllvm.annotation

import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind

/**
 * Informs that this method rejects some kind [kinds]
 */
@Target(AnnotationTarget.FUNCTION)
public annotation class RejectsValue(public vararg val kinds: LLVMTypeKind)

package org.llvm4j.llvm4j.util

/**
 * Suggests that this class matches/corresponds to a class in the LLVM C++ API
 *
 * @property names C++ type names user corresponds to
 *
 * @author Mats Larsen
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
public annotation class CorrespondsTo(vararg val names: String)

/**
 * Suggests that this function uses custom LLVM APIs
 *
 * Custom APIs are built and compiled at the JavaCPP repository.
 *
 * See https://github.com/bytedeco/javacpp-presets/tree/master/llvm/src/main/resources/org/bytedeco/llvm/include
 *
 * @author Mats Larsen
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
public annotation class CustomApi

/**
 * Suggests that this interface exists only to share code between multiple modules
 *
 * This means that consumers should never expect this type as a parameter or return type
 *
 * @author Mats Larsen
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
public annotation class InternalApi

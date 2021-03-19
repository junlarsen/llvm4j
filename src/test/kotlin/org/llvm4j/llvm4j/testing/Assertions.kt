package org.llvm4j.llvm4j.testing

import org.llvm4j.optional.Option
import org.llvm4j.optional.Result
import kotlin.test.assertTrue

internal fun <T> assertIsNone(opt: Option<T>) = assertTrue(opt.isNone(), "Expected $opt to be None")
internal fun <T> assertIsSome(opt: Option<T>) = assertTrue(opt.isSome(), "Expected $opt to be Some")
internal fun <T, E> assertIsOk(res: Result<T, E>) = assertTrue(res.isOk(), "Expected $res to be Ok")
internal fun <T, E> assertIsErr(res: Result<T, E>) = assertTrue(res.isErr(), "Expected $res to be Err")

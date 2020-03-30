# Code Style

KLLVM follows the Kotlin Coding Conventions (https://kotlinlang.org/docs/reference/coding-conventions.html)

Because KLLVM is a library, we should also use these recommended rules to ensure API stability:

- Always explicitly specify member visibility (to avoid accidentally exposing declarations as public API)
- Always explicitly specify function return types and property types (to avoid accidentally changing the return type when the implementation changes)
- Provide KDoc comments for all public members, with the exception of overrides that do not require any new documentation (to support generating documentation for the library)

package io.polywrap.wasm.runtime

import io.github.kawamuray.wasmtime.*

/**
 * A class for wrapping imports for a WebAssembly module with the JVM implementation.
 */
class WrapImportsFactoryJvm {

    companion object {
        /**
         * Factory method for creating a collection of WasmTime [Extern] objects for the given imports.
         *
         * @property store The WebAssembly module state.
         * @property memory The memory to be used by the WebAssembly module.
         * @property requiredImports The list of imports required by the WebAssembly module.
         * @return A collection of WasmTime [Extern] objects.
         */
        fun get(store: Store<WasmModuleState>, memory: Memory, requiredImports: List<String>): Collection<Extern> {
            val wrapImports = WrapImportsJvm(store, memory)
            val result = mutableListOf<Extern>()
            for (import in requiredImports) {
                val extern = allImports[import]?.invoke(store, wrapImports)
                if (extern != null) {
                    result.add(extern)
                }
                if (import == "memory") {
                    val memExtern = Extern.fromMemory(memory)
                    result.add(memExtern)
                }
            }
            return result
        }

        private val allImports = mapOf(
            "__wrap_subinvoke" to ::__wrap_subinvoke,
            "__wrap_subinvoke_result_len" to ::__wrap_subinvoke_result_len,
            "__wrap_subinvoke_result" to ::__wrap_subinvoke_result,
            "__wrap_subinvoke_error_len" to ::__wrap_subinvoke_error_len,
            "__wrap_subinvoke_error" to ::__wrap_subinvoke_error,
            "__wrap_invoke_args" to ::__wrap_invoke_args,
            "__wrap_invoke_result" to ::__wrap_invoke_result,
            "__wrap_invoke_error" to ::__wrap_invoke_error,
            "__wrap_getImplementations" to ::__wrap_getImplementations,
            "__wrap_getImplementations_result_len" to ::__wrap_getImplementations_result_len,
            "__wrap_getImplementations_result" to ::__wrap_getImplementations_result,
            "__wrap_abort" to ::__wrap_abort,
            "__wrap_debug_log" to ::__wrap_debug_log,
            "__wrap_load_env" to ::__wrap_load_env
        )

        private fun __wrap_subinvoke(store: Store<WasmModuleState>, wrapImports: WrapImportsJvm): Extern {
            val func = WasmFunctions.wrap(
                store,
                WasmValType.I32,
                WasmValType.I32,
                WasmValType.I32,
                WasmValType.I32,
                WasmValType.I32,
                WasmValType.I32,
                WasmValType.I32,
                wrapImports::__wrap_subinvoke
            )
            return Extern.fromFunc(func)
        }

        private fun __wrap_subinvoke_result_len(store: Store<WasmModuleState>, wrapImports: WrapImportsJvm): Extern {
            val func = WasmFunctions.wrap(store, WasmValType.I32, wrapImports::__wrap_subinvoke_result_len)
            return Extern.fromFunc(func)
        }

        private fun __wrap_subinvoke_result(store: Store<WasmModuleState>, wrapImports: WrapImportsJvm): Extern {
            val func = WasmFunctions.wrap(store, WasmValType.I32, wrapImports::__wrap_subinvoke_result)
            return Extern.fromFunc(func)
        }

        private fun __wrap_subinvoke_error_len(store: Store<WasmModuleState>, wrapImports: WrapImportsJvm): Extern {
            val func = WasmFunctions.wrap(store, WasmValType.I32, wrapImports::__wrap_subinvoke_error_len)
            return Extern.fromFunc(func)
        }

        private fun __wrap_subinvoke_error(store: Store<WasmModuleState>, wrapImports: WrapImportsJvm): Extern {
            val func = WasmFunctions.wrap(store, WasmValType.I32, wrapImports::__wrap_subinvoke_error)
            return Extern.fromFunc(func)
        }

        private fun __wrap_invoke_args(store: Store<WasmModuleState>, wrapImports: WrapImportsJvm): Extern {
            val func = WasmFunctions.wrap(store, WasmValType.I32, WasmValType.I32, wrapImports::__wrap_invoke_args)
            return Extern.fromFunc(func)
        }

        private fun __wrap_invoke_result(store: Store<WasmModuleState>, wrapImports: WrapImportsJvm): Extern {
            val func = WasmFunctions.wrap(store, WasmValType.I32, WasmValType.I32, wrapImports::__wrap_invoke_result)
            return Extern.fromFunc(func)
        }

        private fun __wrap_invoke_error(store: Store<WasmModuleState>, wrapImports: WrapImportsJvm): Extern {
            val func = WasmFunctions.wrap(store, WasmValType.I32, WasmValType.I32, wrapImports::__wrap_invoke_error)
            return Extern.fromFunc(func)
        }

        private fun __wrap_getImplementations(store: Store<WasmModuleState>, wrapImports: WrapImportsJvm): Extern {
            val func = WasmFunctions.wrap(
                store,
                WasmValType.I32,
                WasmValType.I32,
                WasmValType.I32,
                wrapImports::__wrap_getImplementations
            )
            return Extern.fromFunc(func)
        }

        private fun __wrap_getImplementations_result_len(store: Store<WasmModuleState>, wrapImports: WrapImportsJvm): Extern {
            val func = WasmFunctions.wrap(store, WasmValType.I32, wrapImports::__wrap_getImplementations_result_len)
            return Extern.fromFunc(func)
        }

        private fun __wrap_getImplementations_result(store: Store<WasmModuleState>, wrapImports: WrapImportsJvm): Extern {
            val func = WasmFunctions.wrap(store, WasmValType.I32, wrapImports::__wrap_getImplementations_result)
            return Extern.fromFunc(func)
        }

        private fun __wrap_abort(store: Store<WasmModuleState>, wrapImports: WrapImportsJvm): Extern {
            val func = WasmFunctions.wrap(
                store,
                WasmValType.I32,
                WasmValType.I32,
                WasmValType.I32,
                WasmValType.I32,
                WasmValType.I32,
                WasmValType.I32,
                wrapImports::__wrap_abort
            )
            return Extern.fromFunc(func)
        }

        private fun __wrap_debug_log(store: Store<WasmModuleState>, wrapImports: WrapImportsJvm): Extern {
            val func = WasmFunctions.wrap(store, WasmValType.I32, WasmValType.I32, wrapImports::__wrap_debug_log)
            return Extern.fromFunc(func)
        }

        private fun __wrap_load_env(store: Store<WasmModuleState>, wrapImports: WrapImportsJvm): Extern {
            val func = WasmFunctions.wrap(store, WasmValType.I32, wrapImports::__wrap_load_env)
            return Extern.fromFunc(func)
        }
    }
}

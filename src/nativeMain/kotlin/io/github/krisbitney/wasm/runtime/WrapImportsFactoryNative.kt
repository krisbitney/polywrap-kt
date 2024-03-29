package io.github.krisbitney.wasm.runtime

import io.github.krisbitney.wasmtime.*
import io.github.krisbitney.wasmtime.util.FuncFactory
import io.github.krisbitney.wasmtime.wasm.ValType

/**
 * A class for wrapping imports for a WebAssembly module with the JVM implementation.
 */
class WrapImportsFactoryNative {

    companion object {
        /**
         * Factory method for setting imports in the linker.
         *
         * @param store The WebAssembly module state.
         * @param memory The memory to be used by the WebAssembly module.
         * @param linker The linker to be used by the WebAssembly module.
         * @return A collection of WasmTime [Extern] objects.
         */
        fun define(store: Store<WasmModuleState>, memory: Memory, linker: Linker) {
            val wrapImports = WrapImportsNative(store, memory)
            allImports.forEach { (name, import) ->
                val func = import.invoke(store, wrapImports)
                linker.define(store, "wrap", name, func)
            }
            linker.define(store, "env", "memory", memory)
        }

        private val allImports = arrayOf(
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

        private fun __wrap_subinvoke(store: Store<WasmModuleState>, wrapImports: WrapImportsNative): Extern {
            return FuncFactory.wrap(
                store,
                ValType.I32(),
                ValType.I32(),
                ValType.I32(),
                ValType.I32(),
                ValType.I32(),
                ValType.I32(),
                ValType.I32(),
                wrapImports::__wrap_subinvoke
            )
        }

        private fun __wrap_subinvoke_result_len(store: Store<WasmModuleState>, wrapImports: WrapImportsNative): Extern {
            return FuncFactory.wrap(store, ValType.I32(), wrapImports::__wrap_subinvoke_result_len)
        }

        private fun __wrap_subinvoke_result(store: Store<WasmModuleState>, wrapImports: WrapImportsNative): Extern {
            return FuncFactory.wrap(store, ValType.I32(), wrapImports::__wrap_subinvoke_result)
        }

        private fun __wrap_subinvoke_error_len(store: Store<WasmModuleState>, wrapImports: WrapImportsNative): Extern {
            return FuncFactory.wrap(store, ValType.I32(), wrapImports::__wrap_subinvoke_error_len)
        }

        private fun __wrap_subinvoke_error(store: Store<WasmModuleState>, wrapImports: WrapImportsNative): Extern {
            return FuncFactory.wrap(store, ValType.I32(), wrapImports::__wrap_subinvoke_error)
        }

        private fun __wrap_invoke_args(store: Store<WasmModuleState>, wrapImports: WrapImportsNative): Extern {
            return FuncFactory.wrap(store, ValType.I32(), ValType.I32(), wrapImports::__wrap_invoke_args)
        }

        private fun __wrap_invoke_result(store: Store<WasmModuleState>, wrapImports: WrapImportsNative): Extern {
            return FuncFactory.wrap(store, ValType.I32(), ValType.I32(), wrapImports::__wrap_invoke_result)
        }

        private fun __wrap_invoke_error(store: Store<WasmModuleState>, wrapImports: WrapImportsNative): Extern {
            return FuncFactory.wrap(store, ValType.I32(), ValType.I32(), wrapImports::__wrap_invoke_error)
        }

        private fun __wrap_getImplementations(store: Store<WasmModuleState>, wrapImports: WrapImportsNative): Extern {
            return FuncFactory.wrap(
                store,
                ValType.I32(),
                ValType.I32(),
                ValType.I32(),
                wrapImports::__wrap_getImplementations
            )
        }

        private fun __wrap_getImplementations_result_len(store: Store<WasmModuleState>, wrapImports: WrapImportsNative): Extern {
            return FuncFactory.wrap(store, ValType.I32(), wrapImports::__wrap_getImplementations_result_len)
        }

        private fun __wrap_getImplementations_result(store: Store<WasmModuleState>, wrapImports: WrapImportsNative): Extern {
            return FuncFactory.wrap(store, ValType.I32(), wrapImports::__wrap_getImplementations_result)
        }

        private fun __wrap_abort(store: Store<WasmModuleState>, wrapImports: WrapImportsNative): Extern {
            return FuncFactory.wrap(
                store,
                ValType.I32(),
                ValType.I32(),
                ValType.I32(),
                ValType.I32(),
                ValType.I32(),
                ValType.I32(),
                wrapImports::__wrap_abort
            )
        }

        private fun __wrap_debug_log(store: Store<WasmModuleState>, wrapImports: WrapImportsNative): Extern {
            return FuncFactory.wrap(store, ValType.I32(), ValType.I32(), wrapImports::__wrap_debug_log)
        }

        private fun __wrap_load_env(store: Store<WasmModuleState>, wrapImports: WrapImportsNative): Extern {
            return FuncFactory.wrap(store, ValType.I32(), wrapImports::__wrap_load_env)
        }
    }
}

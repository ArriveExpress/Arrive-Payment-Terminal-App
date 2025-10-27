package com.arrive.terminal.core.model;


/**
 * Extension property to check if the result state is an error state.
 */
val <T> ResultState<T>?.isErrorState get() = this is ResultState.Error

/**
 * Extension property to check if the result state is a success state.
 */
val <T> ResultState<T>?.isSuccessState get() = this is ResultState.Success

/**
 * Extension property to check if the result state is a loading state.
 */
val <T> ResultState<T>?.isLoadingState get() = this is ResultState.Loading

/**
 * Extension property to check if the result state is an empty success state for a list of type T.
 */
val <T> ResultState<List<T>>.isEmptySuccessState get() = this is ResultState.Success && this.data.isEmpty()

/**
 * Extension property to retrieve the value if the result state is a success, otherwise returns null.
 */
val <T> ResultState<T>.dataOrNull get() = if (this is ResultState.Success) data else null

/**
 * Extension property to retrieve the error if the result state is a error, otherwise returns null.
 */
val <T> ResultState<T>.errorOrNull get() = if (this is ResultState.Error) error else null

/**
 * Extension function to map the success result data using the provided transform function.
 *
 * @param transform The function to transform the success result data.
 * @return The new success result with transformed data.
 */
fun <T, R> ResultState.Success<T>.map(transform: (T) -> R): ResultState.Success<R> {
    return ResultState.Success(transform(data))
}

fun <T, R> ResultState<List<T>>.mapList(transform: (T) -> R): ResultState<List<R>> {
    return map { list -> list.map(transform) }
}

fun <T, R> ResultState<T>.map(transform: (T) -> R): ResultState<R> {
    return when (this) {
        is ResultState.Success -> ResultState.Success(transform(this.data))
        is ResultState.Error -> ResultState.Error(
            error = this.error,
        )
        ResultState.Loading -> ResultState.Loading
    }
}

/**
 * Extension function to perform an action when the result state is in the loading state.
 *
 * @param block The action to be performed when in the loading state.
 * @return The original result state.
 */
fun <T> ResultState<T>.onLoading(block: () -> Unit): ResultState<T> {
    if (this is ResultState.Loading) block.invoke()
    return this
}

/**
 * Extension function to perform an action when the result state is in the success state.
 *
 * @param block The action to be performed when in the success state.
 * @return The original result state.
 */
fun <T> ResultState<T>.onSuccess(block: (T) -> Unit): ResultState<T> {
    if (this is ResultState.Success) block.invoke(data)
    return this
}

/**
 * Extension function to perform an action when the result state is in the error state.
 *
 * @param block The action to be performed when in the error state.
 * @return The original result state.
 */
fun <T> ResultState<T>.onError(block: (ResultState.Error) -> Unit): ResultState<T> {
    if (this is ResultState.Error) block.invoke(this)
    return this
}

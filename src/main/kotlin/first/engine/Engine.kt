package first.engine

fun <CTX> send(
    ctx: CTX,
    state: (CTX) -> Unit
) {

}

fun <CTX, R> send2(
    ctx: CTX,
    state: (CTX) -> Result<R>
) {

}

fun <CTX> move(
    ctx: CTX,
    state: (CTX) -> Unit
) {

}

fun <CTX> send(
    ctx: CTX,
    state: (CTX) -> Unit,
    receiver: (Unit) -> Unit
) {

}

fun <CTX, R> receive(
    state: (CTX) -> Result<R>
): Result<R>? {
    return null;
}

fun receive2(
    state: Function<Unit>
): Result2? {
    return null;
}

sealed class Result<R>()

class Ok<CTX>(val ctx: CTX) : Result<CTX>()
class Error<ERR>(val err: ERR) : Result<ERR>()

sealed class Result2()

class Success<CTX>(val ctx: CTX) : Result2()
class Error2<ERR>(val err: ERR) : Result2()

fun <R> complete(
    ctx: R
) {

}

fun <R> complete(
    ctx: R,
    state: Function<Unit>
) {

}

annotation class Starting
annotation class Terminal
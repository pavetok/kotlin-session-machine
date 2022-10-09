package first.engine;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

public class Engine {
    public static <IN extends Object, OUT extends Object> void crab(Function<IN, OUT> s) {

    }

    public static <CTX extends Object> void send(CTX ctx, Consumer<CTX> s) {

    }

    public static <CTX extends Object, R extends Object> Future<R> send2(CTX ctx, Consumer<CTX> s) {
        return null;
    }

    public static <CTX extends Object, R extends Object> void send3(CTX ctx, Consumer<CTX> s) {
    }

    public static <CTX extends Object> void spawn(CTX ctx, Consumer<CTX> s) {

    }

    public static <CTX extends Object, R extends Object> void receive(Consumer<CTX> s, Future<R> p) {

    }

    public static <CTX extends Object> void ok(CTX ctx) {

    }
}

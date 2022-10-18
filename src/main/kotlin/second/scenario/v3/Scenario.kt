package second.scenario.v3

import java.util.function.Function

class Scenario(
    private val a1: A1,
    private val a2: Function<C1, C2>,
    private val a3: A3,
    private val a4: A4,
    private val a5: A5
) {
    fun build(): Scenario {
        return scenario("queue") {
            waiting(a1) {
                choice("enq") {
                    receiving<String>(a2) {
                        waiting(a1)
                    }
                }
                choice("deq") {
                    deciding(a3) {
                        choice("none") {
                            terminating()
                        }
                        choice("some") {
                            sending<String>(a5) {
                                waiting(a1)
                            }
                        }
                    }
                }
            }
        }
    }
}

class C1
class C2
class C3
class C4
class C5

@Activity("s1")
class A1

@Activity("s2")
class A2 : Function<C1, C2> {
    override fun apply(p0: C1): C2 {
        TODO()
    }
}

@Activity("s3")
class A3

@Activity("s4")
class A4

@Activity("s5")
class A5

/**
 * Как организуем методы билдинга?
 * 1. метод в классе
 * 2. метод вовне
 * 3. фабричная функция в классе
 * 4. фабричная функция вовне
 */

/**
 * Как представляем состояние/активити?
 * 1. Метод в классе
 * 2. Функция отдельно
 * 3. Отдельный класс
 * 4. Отдельный инстанс
 * 5. Анонимная лямбда
 * 6. Все или почти все из перечисленного
 */

/**
 * Как подаем состояние/активити?
 * 1. Generic-параметром
 * 2. Function-параметром
 * 3. Назначением в поле
 */

/**
 * Как маркируем прием/отправку?
 * 1. Методами билдера
 * 2. Аннотациями состояния/активити
 * 3. Полями состояния/активити
 * 4. Интерфейсом состояния/активити
 */

annotation class Activity(val name: String)

fun scenario(
    name: String,
    configure: Scenario.() -> Unit
): Scenario {
    val scenario = Scenario(A1(), A2(), A3(), A4(), A5())
    scenario.configure()
    return scenario
}

fun Scenario.deciding(
    activity: Any,
    configure: Scenario.() -> Unit
): Scenario {
    this.configure()
    return this
}

fun Scenario.waiting(
    activity: Any,
    configure: Scenario.() -> Scenario
): Scenario {
    this.configure()
    return this
}

fun Scenario.waiting(
    activity: Any
): Scenario {
    return this
}

fun Scenario.choice(
    name: String,
    configure: Scenario.() -> Unit
): Scenario {
    this.configure()
    return this
}

inline fun <reified T : Any> Scenario.receiving(
    activity: Function<*, *>,
    configure: Scenario.() -> Unit
): Scenario {
    this.configure()
    return this
}

inline fun <reified T : Any> Scenario.sending(
    activity: Any,
    configure: Scenario.() -> Unit
): Scenario {
    this.configure()
    return this
}

fun Scenario.terminating(
    activity: Any
) {
}

fun Scenario.terminating(
) {
}

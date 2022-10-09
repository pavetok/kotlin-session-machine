package second.scenario.v2

class Scenario {
    fun build(): Scenario {
        return scenario("queue") {
            waiting<A1> {
                choice("enq") {
                    receiving<A2> {
                        waiting<A1>()
                    }
                }
                choice("deq") {
                    deciding<A3> {
                        choice("none") {
                            terminating<A4>()
                        }
                        choice("some") {
                            sending<A5> {
                                waiting<A1>()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Activity("s1")
class A1 {
}

@Activity("s2")
class A2

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
 * Как
 */

annotation class Activity(val name: String)

fun scenario(
    name: String,
    configure: Scenario.() -> Unit
): Scenario {
    val scenario = Scenario()
    scenario.configure()
    return scenario
}

inline fun <reified T : Any> Scenario.deciding(
    configure: Scenario.() -> Unit
): Scenario {
    this.configure()
    return this
}

inline fun <reified T : Any> Scenario.waiting(
    configure: Scenario.() -> Unit
): Scenario {
    this.configure()
    return this
}

inline fun <reified T : Any> Scenario.waiting(
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
    configure: Scenario.() -> Unit
): Scenario {
    this.configure()
    return this
}

fun Scenario.sending(
    action: Function<Unit>,
    configure: Scenario.() -> Unit
): Scenario {
    this.configure()
    return this
}

inline fun <reified T : Any> Scenario.sending(
    configure: Scenario.() -> Unit
): Scenario {
    this.configure()
    return this
}

inline fun <reified T : Any> Scenario.terminating(
) {
}

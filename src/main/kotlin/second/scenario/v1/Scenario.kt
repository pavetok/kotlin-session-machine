package second.scenario.v1

class Scenario {
    fun spec(): Scenario {
        return scenario("queue") {
            waiting(this::s1) {
                choice("enq") {
                    receiving(this::s2) {
                        waiting(this::s1)
                    }
                }
                choice("deq") {
                    deciding(this::s3) {
                        choice("none") {
                            terminating("s4")
                        }
                        choice("some") {
                            sending(this::s5) {
                                waiting(this::s1)
                            }
                        }
                    }
                }
            }
        }
    }

    @State("s1")
    fun s1() {
    }

    @State("s2")
    fun s2() {
    }

    @State("s3")
    fun s3() {
    }

    @State("s4")
    fun s4() {
    }

    @State("s5")
    fun s5() {
    }
}

/**
 * Как методы билдинга?
 * 1. метод в классе
 * 2. метод вовне
 * 3. фабричная функция в классе
 * 4. фабричная функция вовне
 */

/**
 * Как состояния?
 * 1. Методы в классе
 * 2. Отдельные классы
 */

fun scenario(
    name: String,
    configure: Scenario.() -> Unit
): Scenario {
    val scenario = Scenario()
    scenario.spec()
    return scenario
}

fun Scenario.deciding(
    name: String,
    configure: Scenario.() -> Unit
): Scenario {
    this.spec()
    return this
}

fun Scenario.deciding(
    action: Function<Unit>,
    configure: Scenario.() -> Unit
): Scenario {
    this.spec()
    return this
}

fun Scenario.waiting(
    name: String,
    configure: Scenario.() -> Unit
): Scenario {
    this.spec()
    return this
}

fun Scenario.waiting(
    action: Function<Unit>,
    configure: Scenario.() -> Unit
): Scenario {
    this.spec()
    return this
}

fun Scenario.waiting(
    name: String
): Scenario {
    return this
}

fun Scenario.waiting(
    action: Function<Unit>
): Scenario {
    return this
}

fun Scenario.choice(
    name: String,
    configure: Scenario.() -> Unit
): Scenario {
    this.spec()
    return this
}

fun Scenario.receiving(
    name: String,
    configure: Scenario.() -> Unit
): Scenario {
    this.spec()
    return this
}

fun Scenario.receiving(
    action: Function<Unit>,
    configure: Scenario.() -> Unit
): Scenario {
    this.spec()
    return this
}

fun Scenario.sending(
    name: String,
    configure: Scenario.() -> Unit
): Scenario {
    this.spec()
    return this
}

fun Scenario.sending(
    action: Function<Unit>,
    configure: Scenario.() -> Unit
): Scenario {
    this.spec()
    return this
}

fun Scenario.terminating(
    name: String
) {
}

annotation class State(val name: String)

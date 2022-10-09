package first.engine

class Scenario1(
    private val scenario2: Scenario2
) {
    @Starting
    fun s1(ctx1: Context1) {
        val ctx2 = Context2()
        send(ctx2, scenario2::s1) {
            when (val result = receive2(scenario2::s4)) {
                is Success<*> -> {
                    val ctx4: Context4 = result.ctx as Context4
                    val ctx5 = Context5()
                    send(ctx5, this::s2)
                }

                is Error2<*> -> send(Context4(), this::s3)
            }
        }
    }

    @Terminal
    fun s2(ctx5: Context5) {

    }

    @Terminal
    fun s3(ctx4: Context4) {

    }
}

class Scenario2 {
    @Starting
    fun s1(ctx2: Context2) {
        val ctx3 = Context3(true)
        if (ctx3.isFoo) {
            move(ctx3, this::s2)
        } else {
            move(ctx3, this::s3)
        }
    }

    private fun s2(ctx3: Context3) {
        val ctx4 = Context4()
        complete(Success(ctx4), this::s4)
    }

    private fun s3(ctx3: Context3) {
        val err = Info()
        complete(Error2(err), this::s4)
    }

    @Terminal
    fun s4() {
    }
}

class Info
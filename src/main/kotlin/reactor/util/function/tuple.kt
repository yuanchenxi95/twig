package reactor.util.function

fun <T1, T2, T3> Tuple3<T1, T2, T3>.convert(): Triple<T1, T2, T3> {
    return Triple(this.t1, this.t2, this.t3)
}

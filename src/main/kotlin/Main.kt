package org.example

import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
    println("Hello World!")
    Zettai().asServer(Jetty(8080)).start()
}
package org.example

import org.http4k.core.HttpHandler
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
    println("Hello World!")
    val items = listOf("write chapter", "insert code", "draw diagrams")
    val toDoList = ToDoList(ListName("book"), items.map(::ToDoItem))
    val lists = mapOf(User("user") to listOf(toDoList))
    val app: HttpHandler = Zettai(lists)
    app.asServer(Jetty(8080)).start()
    println("Server started on http://localhost:8080")
}

fun starTheApplication(
    user: String,
    listName: String,
    items: List<String>
) {
    val toDoList = ToDoList(
        ListName(listName),
        items.map (::ToDoItem)
    )
    val lists = mapOf(User(user) to listOf(toDoList))
    val server = Zettai(lists).asServer(Jetty(8081))
    server.start()
}

private fun parseResponse(html: String): ToDoList {
    val nameRegex = "<h2>.*<".toRegex()
    val listName = ListName(extractListName(nameRegex, html))
    val itemRegex = "<td>.*?<".toRegex()
    val items = itemRegex.findAll(html)
        .map {ToDoItem(extractItemDesc(it))}.toList()
    return ToDoList(listName, items)
}

private fun extractListName(regex: Regex, html: String): String =
    regex.find(html)?.value
        ?.substringAfter("<h2>")
        ?.dropLast(1)
        .orEmpty()

private fun extractItemDesc(matchResult: MatchResult): String =
    matchResult.value.substringAfter("<td>").dropLast(1)
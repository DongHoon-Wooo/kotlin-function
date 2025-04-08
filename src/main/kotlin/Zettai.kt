package org.example

import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

data class ToDoList(val listName: ListName, val items: List<ToDoItem>)
data class ListName(val name: String)
data class ToDoItem(val description: String)
data class User(val name: String)
data class HtmlPage(val content: String)

enum class ToDoStatus {
    ToDo,
    InProgress,
    Blocked,
    Done
}

data class Zettai(val lists: Map<User, List<ToDoList>>): HttpHandler {
    val routes: HttpHandler = routes (
        "/todo/{user}/{list}" bind Method.GET to ::showList
    )

    override fun invoke(req: Request): Response = routes(req)
    private fun showList(request: Request): Response =
        request.let(::extractListData)
            .let(::fetchListContent)
            .let(::renderHtml)
            .let(::createResponse)

    fun extractListData(req: Request): Pair<User, ListName>{
        val user = req.path("user").orEmpty()
        val list = req.path("list").orEmpty()
        return User(user) to ListName(list)
    }

    fun fetchListContent(listId: Pair<User, ListName>): ToDoList =
        lists[listId.first]
            ?.firstOrNull { it.listName == listId.second }
            ?: error("list unknown")

    fun renderHtml(todoList: ToDoList): HtmlPage =
        HtmlPage("""
            <html>
                <head>
                    <title>Sample page</title>
                </head>
                <body>
                    <h1>Zettai</h1>
                    <h2>${todoList.listName.name}</h2> 
                    <table>
                        <tbody>${renderItems(todoList.items)}</tbody>
                </body>
        """.trimIndent())

    fun renderItems(items: List<ToDoItem>) =
        items.map {
            """
            <tr>
                <td>${it.description}</td>
            </tr>
            """.trimIndent()
        }.joinToString("")

    fun createResponse(html: HtmlPage): Response =
        Response(Status.OK).body(html.content)

}
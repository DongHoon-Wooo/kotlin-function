import org.example.*
import org.http4k.client.JettyClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ZettaiTest {
     @Test
    fun listOwnersCanSeeTheirLists() {

        val user = "frank"
        val listName = "shopping"
        val foodToBy = listOf("carrots", "apples", "milk")
        startTheApplication(user, listName, foodToBy)
        val list = getToDoList(user, listName)

        expectThat(list.listName.name).isEqualTo(listName)
    }

    private fun getToDoList(user: String, listName: String): ToDoList {
        val client = JettyClient()
        val request = Request(Method.GET, "http://localhost:8080/todo/$user/$listName")
        val response = client(request)
        return if(response.status == Status.OK)
            parseResponse(response.bodyString())
        else
            error("Error: ${response.status}")
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

    private fun startTheApplication(
        user: String,
        listName: String,
        items: List<String>
    ) {
        val toDoList = ToDoList(ListName(listName), items.map(::ToDoItem))
        val lists = mapOf(User(user) to listOf(toDoList))
        val app: HttpHandler = Zettai(lists)
        app.asServer(Jetty(8080)).start()
    }
 }
import dao.RxMongoDriver
import server.Server

fun main() {
    val server = Server(RxMongoDriver())
    server.run()
}
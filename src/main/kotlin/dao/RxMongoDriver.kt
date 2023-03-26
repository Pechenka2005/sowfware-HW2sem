package dao

import com.mongodb.client.model.Filters
import com.mongodb.rx.client.MongoClient
import com.mongodb.rx.client.MongoClients
import com.mongodb.rx.client.Success
import model.Product
import model.User
import org.bson.Document
import rx.Observable
import java.util.concurrent.TimeUnit

class RxMongoDriver(
    private val mongoClient: MongoClient = MongoClients.create("mongodb://localhost:27017")
) {
    companion object {
        private const val DATABASE = "shop"
        private const val USERS_COLLECTION = "users"
        private const val PRODUCTS_COLLECTION = "products"
        private const val ID_FIELD = "id"
        private const val TIMEOUT = 15
    }

    fun addProduct(product: Product): Success {
        return mongoClient
            .getDatabase(DATABASE)
            .getCollection(PRODUCTS_COLLECTION)
            .insertOne(product.toDocument())
            .timeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .toBlocking()
            .single()
    }

    fun getProducts(id: Int?): Observable<String> {
        return mongoClient
            .getDatabase(DATABASE)
            .getCollection(PRODUCTS_COLLECTION)
            .find()
            .toObservable()
            .map { document: Document? ->
                Product(document!!).toString(
                    findUser(id).getCurrency()
                )
            }
            .reduce { product1: String, product2: String ->
                """
                $product1
                $product2
                """.trimIndent()
            }
    }

    fun addUser(user: User): Success {
        return mongoClient
            .getDatabase(DATABASE)
            .getCollection(USERS_COLLECTION)
            .insertOne(user.toDocument())
            .timeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .toBlocking()
            .single()
    }

    fun findUser(id: Int?): User {
        return mongoClient
            .getDatabase(DATABASE)
            .getCollection(USERS_COLLECTION)
            .find(Filters.eq(ID_FIELD, id))
            .first()
            .map { doc: Document -> User(doc) }
            .timeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .toBlocking()
            .single()
    }

    fun getUsers(): Observable<String> {
        return mongoClient
            .getDatabase(DATABASE)
            .getCollection(USERS_COLLECTION)
            .find()
            .toObservable()
            .map { document: Document? ->
                User(document!!).toString()
            }
            .reduce { user1: String, user2: String ->
                """
                $user1
                $user2
                """.trimIndent()
            }
    }
}
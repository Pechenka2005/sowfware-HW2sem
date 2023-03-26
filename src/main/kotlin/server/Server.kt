package server

import com.mongodb.rx.client.Success
import dao.RxMongoDriver
import io.netty.buffer.ByteBuf
import io.reactivex.netty.protocol.http.server.HttpServer
import io.reactivex.netty.protocol.http.server.HttpServerRequest
import io.reactivex.netty.protocol.http.server.HttpServerResponse
import model.Product
import model.User
import rx.Observable
import java.lang.Enum
import java.util.*
import java.util.stream.Collectors
import kotlin.String


class Server(
    private val mongoDriver: RxMongoDriver
) {
    companion object {
        private const val PORT = 8080

        private const val ID_FIELD = "id"
        private const val UID_FIELD = "user_id"
        private const val NAME_FIELD = "name"
        private const val CURRENCY_FIELD = "currency"
        private const val RUB_FIELD = "rub"
        private const val EUR_FIELD = "eur"
        private const val USD_FIELD = "usd"

        private const val ADD_USER_METHOD = "addUser"
        private const val GET_USERS_METHOD = "getUsers"
        private const val ADD_PRODUCT_METHOD = "addProduct"
        private const val GET_PRODUCTS_METHOD = "getProducts"
        private const val WRONG_METHOD_MESSAGE = ("Wrong method, available methods: "
                + ADD_USER_METHOD + ", "
                + GET_USERS_METHOD + ", "
                + ADD_PRODUCT_METHOD + ", "
                + GET_PRODUCTS_METHOD)
        private const val MISSING_PARAMS_MESSAGE = "Please add missing params: "

        private val ADD_USER_PARAMS = listOf(ID_FIELD, NAME_FIELD, CURRENCY_FIELD)
        private val ADD_PRODUCT_PARAMS = listOf(ID_FIELD, NAME_FIELD, RUB_FIELD, EUR_FIELD, USD_FIELD)
        private val GET_PRODUCTS_PARAMS = listOf(UID_FIELD)
    }

    fun run() {
        HttpServer.newServer(PORT)
            .start { request: HttpServerRequest<ByteBuf?>, response: HttpServerResponse<ByteBuf?> ->
                val method = request.decodedPath.substring(1)
                val params =
                    request.queryParameters
                if (ADD_USER_METHOD == method) {
                    return@start response.writeString(addUser(params))
                }
                if (GET_USERS_METHOD == method) {
                    return@start response.writeString(getUsers(params))
                }
                if (ADD_PRODUCT_METHOD == method) {
                    return@start response.writeString(addProduct(params))
                }
                if (GET_PRODUCTS_METHOD == method) {
                    return@start response.writeString(getProducts(params))
                }
                response.writeString(
                    Observable.just(WRONG_METHOD_MESSAGE)
                )
            }.awaitShutdown()
    }

    fun addUser(params: Map<String, List<String>>): Observable<String> {
        var validation = ""
        val missingParams = ADD_USER_PARAMS
            .stream()
            .filter { param: String? -> !params.containsKey(param) }
            .collect(Collectors.toList())
        if (!missingParams.isEmpty()) {
            validation = MISSING_PARAMS_MESSAGE + java.lang.String.join(", ", missingParams)
        }
        if (validation.isNotEmpty()) return Observable.just(validation)
        val id = params[ID_FIELD]!![0].toInt()
        val name = params[NAME_FIELD]!![0]
        val currency = params[CURRENCY_FIELD]!![0]
        val user = User(id, name, Enum.valueOf(User.Currency::class.java, currency.uppercase(Locale.getDefault())))
        return if (mongoDriver.addUser(user) == Success.SUCCESS) {
            Observable.just("New user:\n$user")
        } else {
            Observable.just("Error")
        }
    }

    fun getUsers(params: Map<String, List<String>>): Observable<String> {
        return mongoDriver.getUsers()
    }

    fun addProduct(params: Map<String, List<String>>): Observable<String> {
        var validation = ""
        val missingParams = ADD_PRODUCT_PARAMS
            .stream()
            .filter { param: String? -> !params.containsKey(param) }
            .collect(Collectors.toList())
        if (missingParams.isNotEmpty()) {
            validation = MISSING_PARAMS_MESSAGE + java.lang.String.join(", ", missingParams)
        }
        if (validation.isNotEmpty()) {
            return Observable.just(validation)
        }
        val id = params[ID_FIELD]!![0].toInt()
        val name = params[NAME_FIELD]!![0]
        val rub = params[RUB_FIELD]!![0]
        val eur = params[EUR_FIELD]!![0]
        val usd = params[USD_FIELD]!![0]
        val product = Product(id, name,
            object : HashMap<User.Currency, String>() {
                init {
                    put(User.Currency.RUB, rub)
                    put(User.Currency.EUR, eur)
                    put(User.Currency.USD, usd)
                }
            })
        return if (mongoDriver.addProduct(product) == Success.SUCCESS) {
            Observable.just("New product:\n$product")
        } else {
            Observable.just("Error")
        }
    }

    fun getProducts(params: Map<String?, List<String>>): Observable<String>? {
        var validation = ""
        val missingParams = GET_PRODUCTS_PARAMS
            .stream()
            .filter { param: String? -> !params.containsKey(param) }
            .collect(Collectors.toList())
        if (!missingParams.isEmpty()) {
            validation = MISSING_PARAMS_MESSAGE + java.lang.String.join(", ", missingParams)
        }
        if (validation.isNotEmpty()) {
            return Observable.just(validation)
        }
        val id = params[UID_FIELD]!![0].toInt()
        return mongoDriver.getProducts(id)
    }
}
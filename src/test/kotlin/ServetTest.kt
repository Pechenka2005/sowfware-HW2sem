import com.mongodb.rx.client.Success
import dao.RxMongoDriver
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import rx.internal.util.ScalarSynchronousObservable
import server.Server

class ServetTest(
    private var mongoDriver: RxMongoDriver,
    private var server: Server
) {
    companion object {
        private const val ID_FIELD = "id"
        private const val NAME_FIELD = "name"
        private const val CURRENCY_FIELD = "currency"
        private const val RUB_FIELD = "rub"
        private const val EUR_FIELD = "eur"
        private const val USD_FIELD = "usd"

        private const val NEW_PRODUCT_RESPONSE = "New product:\n" +
                "Product: {\n" +
                "\tid: 42,\n" +
                "\tname: Test,\n" +
                "\tRUB: 80,\n" +
                "\tEUR: 1,\n" +
                "\tUSD: 2\n" +
                "}"

        private const val NEW_USER_RESPONSE = "New user:\n" +
                "User: {\n" +
                "\tid: 101,\n" +
                "\tname: Test,\n" +
                "\tcurrency: USD\n" +
                "}"
    }

    @Before
    fun before() {
        mongoDriver = Mockito.mock(RxMongoDriver::class.java)
        Mockito.`when`(mongoDriver.addProduct(Mockito.any())).thenReturn(Success.SUCCESS)
        Mockito.`when`(mongoDriver.addUser(Mockito.any())).thenReturn(Success.SUCCESS)
        server = Server(mongoDriver)
    }


    @Test
    fun testAddProduct() {
        val params: Map<String, List<String>> = object : HashMap<String, List<String>>() {
            init {
                put(ID_FIELD, listOf("42"))
                put(NAME_FIELD, listOf("Test"))
                put(RUB_FIELD, listOf("80"))
                put(EUR_FIELD, listOf("1"))
                put(USD_FIELD, listOf("2"))
            }
        }
        val res = server.addProduct(params)
        Assert.assertThat(
            res, IsInstanceOf.instanceOf(
                ScalarSynchronousObservable::class.java
            )
        )
        Assert.assertEquals(
            NEW_PRODUCT_RESPONSE,
            (res as ScalarSynchronousObservable<*>).get().toString()
        )
    }


    @Test
    fun testAddProductMissingParams() {
        val params: MutableMap<String, List<String>> = HashMap()
        params[ID_FIELD] = listOf("1")
        val res = server.addProduct(params)
        val expectedResponse = "Please add missing params: name, rub, eur, usd"
        val actualResponse = (res as ScalarSynchronousObservable<*>).get().toString()
        Assert.assertEquals(expectedResponse, actualResponse)
    }


    @Test
    fun testAddUser() {
        val params: Map<String, List<String>> = object : HashMap<String, List<String>>() {
            init {
                put(ID_FIELD, listOf("101"))
                put(NAME_FIELD, listOf("Test"))
                put(CURRENCY_FIELD, listOf("USD"))
            }
        }
        val res = server.addUser(params)
        Assert.assertEquals(NEW_USER_RESPONSE, (res as ScalarSynchronousObservable<*>?)!!.get().toString())
    }


    @Test
    fun testAddUserMissingParams() {
        val params: MutableMap<String, List<String>> = HashMap()
        params[ID_FIELD] = listOf("1")
        val res = server.addUser(params)
        val expectedResponse = "Please add missing params: name, currency"
        val actualResponse = (res as ScalarSynchronousObservable<*>?)!!.get().toString()
        Assert.assertEquals(expectedResponse, actualResponse)
    }
}
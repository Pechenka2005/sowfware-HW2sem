package model

import org.bson.Document


class Product(
    private val id: Int = 0,
    private val name: String? = null ,
    private val prices: Map<User.Currency, String>? = null
) {

    companion object {
        private const val ID_FIELD = "id"
        private const val NAME_FIELD = "name"
    }


    constructor(doc: Document): this(
        doc.getInteger(ID_FIELD),
        doc.getString(NAME_FIELD),
        object : HashMap<User.Currency, String>() {
            init {
                put(User.Currency.RUB, doc.getString(User.Currency.RUB.toString()))
                put(User.Currency.EUR, doc.getString(User.Currency.EUR.toString()))
                put(User.Currency.USD, doc.getString(User.Currency.USD.toString()))
            }
        }
    )

    fun toDocument(): Document? {
        return Document(ID_FIELD, id)
            .append(NAME_FIELD, name)
            .append(User.Currency.RUB.toString(), prices!![User.Currency.RUB])
            .append(User.Currency.EUR.toString(), prices[User.Currency.EUR])
            .append(User.Currency.USD.toString(), prices[User.Currency.USD])
    }

    fun toString(currency: User.Currency): String {
        return String.format(
            "Product: {\n" +
                    "\tid: %s,\n" +
                    "\tname: %s,\n" +
                    "\t%s: %s\n" +
                    "}", id, name, currency.toString(), prices!![currency]
        )
    }

    override fun toString(): String {
        return String.format(
            ("Product: {\n\tid: %s,\n" +
                    "\tname: %s,\n" +
                    "\t%s: %s,\n" +
                    "\t%s: %s,\n" +
                    "\t%s: %s\n" +
                    "}"), id, name, User.Currency.RUB.toString(),
            prices!![User.Currency.RUB], User.Currency.EUR.toString(),
            prices[User.Currency.EUR], User.Currency.USD.toString(), prices[User.Currency.USD]
        )
    }
}

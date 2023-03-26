package model

import org.bson.Document
import java.util.*
import java.lang.Enum

class User(
    private val id: Int,
    private val name: String,
    private val currency: Currency
) {
    companion object {
        private const val ID_FIELD = "id"
        private const val NAME_FIELD = "name"
        private const val CURRENCY_FIELD = "currency"
    }

    constructor(doc: Document):
            this(
                doc.getInteger(ID_FIELD),
                doc.getString(NAME_FIELD),
                Enum.valueOf(
                    Currency::class.java,
                    doc.getString(CURRENCY_FIELD).uppercase(Locale.getDefault())
                )
            )


    fun getCurrency(): Currency {
        return currency
    }

    fun toDocument(): Document {
        return Document("id", id)
            .append("name", name)
            .append("currency", currency.toString())
    }

    override fun toString(): String {
        return """User: {
	id: $id,
	name: $name,
	currency: $currency
}"""
    }

    enum class Currency {
        RUB, EUR, USD
    }
}
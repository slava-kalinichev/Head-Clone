package ru.tbank.education.school.lesson2.bank

open class Account (
    val id: String,
    var balance: Double,
    val clientId: String
) {
    fun deposit(amount: Double) {
        balance += amount
    }

    open fun withdraw(amount: Double) : Boolean {
        if (balance > amount) {
            balance -= amount
            return true
        }
        return false
    }
}

class CreditAccount(
    id: String,
    balance: Double,
    clientId: String,
    creditLimit: Double
) : Account(
    id,
    balance,
    clientId
) {
    var creditLimit = creditLimit

    override fun withdraw(amount: Double): Boolean {
        if (balance + creditLimit >= amount) {
            balance -= amount
            return true
        }
        return false
    }
}

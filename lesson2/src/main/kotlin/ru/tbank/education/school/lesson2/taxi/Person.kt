package ru.tbank.education.school.lesson2.taxi

abstract class Person (
    val id: String,
    val name: String,
    protected val phoneNumber: String
) {
    constructor(name: String, phoneNumber: String): this(
        id = generateId(),
        name = name,
        phoneNumber = phoneNumber
    )
    companion object {
        private fun generateId(): String = "person_${System.currentTimeMillis()}"
    }
}

class Client(
    id: String,
    name: String,
    phoneNumber: String,
    val paymentMethod: String
) : Person(id, name, phoneNumber) {
    constructor(name: String, phoneNumber: String) : this(
        id = generateClientId(),
        name = name,
        phoneNumber = phoneNumber,
        paymentMethod = "CASH"
    )

    companion object {
        private fun generateClientId(): String = "client_${System.currentTimeMillis()}"
    }
}

class Driver(
    id: String,
    name: String,
    phoneNumber: String,
    val rating: Double,
    var currentVehicle: Vehicle?
) : Person(id, name, phoneNumber) {
    val isAvailable: Boolean
        get() = currentVehicle?.isAvailable == true && rating >= 4.0

    constructor(name: String, phoneNumber: String) : this(
        id = generateDriverId(),
        name = name,
        phoneNumber = phoneNumber,
        rating = 5.0,
        currentVehicle = null
    )
    companion object {
        private fun generateDriverId(): String = "driver_${System.currentTimeMillis()}"
    }
}
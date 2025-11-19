package ru.tbank.education.school.lesson2.taxi

abstract class Vehicle(
    val licensePlate:String,
    val model: String,
    val color: String,
    val year: Int
){
    var currentOrder: Order? = null
    open val isAvailable: Boolean
        get() = currentOrder == null
    constructor(licensePlate: String, model: String): this(
        licensePlate = licensePlate,
        model = model,
        color = "Белый",
        year = 2020)
}

class EconomyCar(
    licensePlate: String,
    model: String,
    color: String,
    year: Int
): Vehicle(licensePlate, model, color, year){
    constructor(licensePlate: String, model: String): this(
        licensePlate = licensePlate,
        model = model,
        color = "Белый",
        year = 2020
    )
}

class ComfortCar(
    licensePlate: String,
    model: String,
    color: String,
    year: Int,
    val hasAirConditioning: Boolean = true
): Vehicle(licensePlate, model, color, year)

class BusinessCar(
    licensePlate: String,
    model: String,
    color: String,
    year: Int,
    val hasWaterAndChargers: Boolean = true
): Vehicle(licensePlate, model, color, year){
    override val isAvailable: Boolean
        get() = super.isAvailable && hasWaterAndChargers
}

class Minivan(
    licensePlate: String,
    model: String,
    color: String,
    year: Int,
    val passengerCapacity: Int = 7
) : Vehicle(licensePlate, model, color, year)
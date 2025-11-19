package ru.tbank.education.school.lesson2.taxi

sealed class OrderStatus {
    object SearchingDriver : OrderStatus()
    data class DriverAssigned(val driver:Driver) : OrderStatus()
    data class InProgress(val startTime: Long) : OrderStatus()
    data class Completed(val endTime: Long, val finalCost: Double) : OrderStatus()
    data class Cancelled(val reason: String) : OrderStatus()
}

data class Route(
    val startPoint: String,
    val destinationPoint: String,
    val distanceKm: Double
) {
    val displayRoute: String
        get() = "$startPoint -> $destinationPoint"
}

class Order(
    val id: String,
    val client: Client,
    val route: Route,
    val tariff: Tariff
){
    var driver: Driver? = null
    var vehicle: Vehicle? = null
    var status: OrderStatus = OrderStatus.SearchingDriver
        private set
    var finalCost: Double? = null
        private set
    constructor(client: Client, route: Route, tariff: Tariff): this (
        id = generateOrderId(),
        client = client,
        route = route,
        tariff = tariff
    )

    fun assignDriver(driver: Driver, vehicle: Vehicle) {
        this.driver = driver
        this.vehicle = vehicle
        this.status = OrderStatus.DriverAssigned(driver)
        vehicle.currentOrder = this
    }

    fun startRide() {
        if (status is OrderStatus.DriverAssigned) {
            this.status = OrderStatus.InProgress(System.currentTimeMillis())
        } else {
            throw IllegalStateException("Нельзя начать поездку без назначенного водителя")
        }
    }

    fun completeRide(timeMinutes: Double) {
        if (status is OrderStatus.InProgress) {
            val cost = tariff.calculateCost(route.distanceKm, timeMinutes)
            this.finalCost = cost
            this.status = OrderStatus.Completed(System.currentTimeMillis(), cost)
            this.vehicle?.currentOrder = null
        } else {
            throw IllegalStateException("Нельзя завершить поездку, которая не начата")
        }
    }

    fun cancelOrder(reason: String) {
        this.status = OrderStatus.Cancelled(reason)
        this.vehicle?.currentOrder = null
    }

    fun getOrderInfo(): String {
        val info = """
        Заказ #$id
        Клиент: ${client.name}
        Маршрут: ${route.displayRoute}
        Расстояние: ${route.distanceKm} км
        Тариф: ${tariff.name}
        Статус: ${getStatusText()}
        ${if (finalCost != null) "Стоимость: ${"%.2f".format(finalCost)} руб." else ""}
    """.trimIndent()
        return info.lines().joinToString("\n    ") { "    $it" }
    }

    private fun getStatusText(): String {
        return when (status) {
            is OrderStatus.SearchingDriver -> "Поиск водителя"
            is OrderStatus.DriverAssigned -> "Водитель назначен: ${(status as OrderStatus.DriverAssigned).driver.name}"
            is OrderStatus.InProgress -> "Поездка начата"
            is OrderStatus.Completed -> "Поездка завершена"
            is OrderStatus.Cancelled -> "Отменен (${(status as OrderStatus.Cancelled).reason})"
        }
    }
    companion object{
        private fun generateOrderId(): String = "order_${System.currentTimeMillis()}"
    }
}
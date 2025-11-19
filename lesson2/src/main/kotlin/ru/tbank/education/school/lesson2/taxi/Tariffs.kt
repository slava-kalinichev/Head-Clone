package ru.tbank.education.school.lesson2.taxi

class DynamicPricing(
    private var surgeMultiplier: Double = 1.0
) {
    fun updateMultiplier(demandLevel: Double) {
        surgeMultiplier = when {
            demandLevel >0.8 -> 1.8
            demandLevel > 0.6 -> 1.5
            demandLevel > 0.4 -> 1.2
            demandLevel > 0.2 -> 1.0  // Дефолтный спрос
            else -> 0.9
        }
    }
    fun getCurrentMultiplier():
            Double = surgeMultiplier
}

abstract class Tariff(
    val name: String,
    protected val basePricePerKm: Double,
    protected val basePricePerMinute: Double
) {
    protected val pricingEngine: DynamicPricing = DynamicPricing()
    abstract fun calculateCost(distance: Double, time: Double): Double

    fun updatePricing(demandLevel: Double){
        pricingEngine.updateMultiplier(demandLevel)
    }
    protected fun getMultiplier():Double = pricingEngine.getCurrentMultiplier()
}

class EconomyTariff : Tariff(
    name = "Эконом",
    basePricePerKm = 10.0,
    basePricePerMinute = 5.0
) {
    override fun calculateCost(distance: Double, time: Double): Double {
        val multiplier = getMultiplier()
        return (distance * basePricePerKm + time * basePricePerMinute) * multiplier
    }
}

class ComfortTariff : Tariff(
    name = "Комфорт",
    basePricePerKm = 15.0,
    basePricePerMinute = 7.0
) {
    override fun calculateCost(distance: Double, time: Double): Double {
        val multiplier = getMultiplier()
        return (distance * basePricePerKm + time * basePricePerMinute) * multiplier
    }
}

class BusinessTariff : Tariff(
    name = "Бизнес",
    basePricePerKm = 25.0,
    basePricePerMinute = 10.0
) {
    private val premiumServiceFee: Double = 100.0
    override fun calculateCost(distance: Double, time: Double): Double {
        val multiplier = getMultiplier()
        val baseCost = (distance*basePricePerKm + time*basePricePerMinute) *multiplier
        return baseCost + premiumServiceFee
    }
}

class MinivanTariff : Tariff(
    name = "Минивэн",
    basePricePerKm = 20.0,
    basePricePerMinute = 8.0
) {
    override fun calculateCost(distance: Double, time: Double): Double{
        val multiplier = getMultiplier()
        return (distance*basePricePerKm + time*basePricePerMinute) *multiplier
    }
    fun calculateCostWithPassengers(distance: Double, time: Double, passengerCount: Int): Double {
        val baseCost = calculateCost(distance, time)
        val extraPassengerFee = if (passengerCount > 4) (passengerCount-4) *50.0 else 0.0
        return baseCost + extraPassengerFee
    }
}
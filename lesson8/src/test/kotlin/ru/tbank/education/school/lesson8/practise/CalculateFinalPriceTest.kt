package ru.tbank.education.school.lesson8.practise

/**
 *
 * Сценарии для тестирования:
 *
 * 1. Позитивные сценарии (happy path):
 *    - Обычный случай: basePrice = 1000, discount = 10%, tax = 20% → проверить корректность формулы.
 *    - Без скидки: discountPercent = 0 → итог = basePrice + налог.
 *    - Без налога: taxPercent = 0 → итог = basePrice минус скидка.
 *    - Без скидки и без налога: итог = basePrice.
 *
 * 2. Негативные сценарии (исключения):
 *    - Отрицательная цена: basePrice < 0 → IllegalArgumentException.
 *    - Скидка вне диапазона: discountPercent < 0 или > 100 → IllegalArgumentException.
 *    - Налог вне диапазона: taxPercent < 0 или > 30 → IllegalArgumentException.
 */

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PriceCalculatorTest {
    // Позитивные сценарии
    @Test
    fun `basic case - correct calculation`() {
        val result = calculateFinalPrice(1000.0, 10, 20)
        // (1000 - 10%) + 20% = (1000 * 0.9) * 1.2 = 1080
        assertEquals(1080.0, result, 0.001)
    }

    @Test
    fun `without discount - price & tax only`() {
        val result = calculateFinalPrice(1000.0, 0, 20)
        assertEquals(1200.0, result, 0.001)
    }

    @Test
    fun `without tax - price and discount only`() {
        val result = calculateFinalPrice(1000.0, 10, 0)
        assertEquals(900.0, result, 0.001)
    }

    @Test
    fun `with no tax and discount - initial Price`() {
        val result = calculateFinalPrice(1000.0, 0, 0)
        assertEquals(1000.0, result, 0.001)
    }

    @ParameterizedTest
    @CsvSource(
        "1000, 10, 20, 1080",
        "500, 5, 10, 522.5",
        "0, 0, 0, 0", // null price
        "1, 0, 0, 1", // min price
        "999999, 50, 30, 649999.35",
    )
    fun `pos sceneries with parameters`(
        basePrice: Double,
        discountPercent: Int,
        taxPercent: Int,
        expected: Double
    ) {
        val result = calculateFinalPrice(basePrice, discountPercent, taxPercent)
        assertEquals(expected, result, 0.001)
    }

   // Негативные сценарии
    @Test
    fun `negative price - throws an exception`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            calculateFinalPrice(-100.0, 10, 20)
        }
        assertEquals("Base price must be >= 0", exception.message)
    }

    @Test
    fun `negative discount - throws an exception`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            calculateFinalPrice(1000.0, -5, 20) }
        assertEquals("Discount must be 0..100", exception.message)
    }

    @Test
    fun `disc more than 100 - throws an exception`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            calculateFinalPrice(1000.0, 150, 20)
        }
        assertEquals("Discount must be 0..100", exception.message)
    }

    @Test
    fun `negative tax - throws an exception`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            calculateFinalPrice(1000.0, 10, -5)
        }
        assertEquals("Tax must be 0..30", exception.message)
    }

    @Test
    fun `tax more than 30 - throws an exception`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            calculateFinalPrice(1000.0, 10, 50)
        }
        assertEquals("Tax must be 0..30", exception.message)
    }

    @ParameterizedTest
    @CsvSource(
        "-100, 10, 20",
        "100, -10, 20",
        "100, 110, 20",
        "100, 10, -20",
        "100, 10, 40"
    )
    fun `neg sceneries with parameters`(
        basePrice: Double,
        discountPercent: Int,
        taxPercent: Int
    ) {
        assertThrows(IllegalArgumentException::class.java) {
            calculateFinalPrice(basePrice, discountPercent, taxPercent)
        }
    }

    // Доптесты для лимитных значений
    @Test
    fun `discount 100 - free price`() {
        val result = calculateFinalPrice(1000.0, 100, 20)
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `max tax 30`() {
        val result = calculateFinalPrice(1000.0, 10, 30)
        assertEquals(1170.0, result, 0.001) // (1000 * 0.9) * 1.3 = 1170
    }

    @Test
    fun `discount 0 - limit value`() {
        val result = calculateFinalPrice(1000.0, 0, 10)
        assertEquals(1100.0, result, 0.001)
    }

    @Test
    fun `discount 100 - limit value`() {
        val result = calculateFinalPrice(1000.0, 100, 10)
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `tax 0 - limit value`() {
        val result = calculateFinalPrice(1000.0, 10, 0)
        assertEquals(900.0, result, 0.001)
    }

    @Test
    fun `tax 30 - limit value`() {
        val result = calculateFinalPrice(1000.0, 10, 30)
        assertEquals(1170.0, result, 0.001)
    }

    @Test
    fun `price 0 - result 0`() {
        val result = calculateFinalPrice(0.0, 50, 30)
        assertEquals(0.0, result, 0.001)
    }
}

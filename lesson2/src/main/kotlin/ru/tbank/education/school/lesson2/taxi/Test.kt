package ru.tbank.education.school.lesson2.taxi

// Тема: Такси
fun main() {
    System.setOut(java.io.PrintStream(System.out, true, "UTF-8"))
    println("Демонстрация работы информационной системы такси с динамическим ценообразованием")
    // Создание клиентов
    val clientMaria = Client("Мария", "+79161234567")
    val clientIvan = Client(
        id = "client_ivan",
        name = "Иван",
        phoneNumber = "+79167654321",
        paymentMethod = "CARD"
    )

    println("- Созданы клиенты:")
    println("   - ${clientMaria.name}, оплата: ${clientMaria.paymentMethod}")
    println("   - ${clientIvan.name}, оплата: ${clientIvan.paymentMethod}")
    // Создание автомоблей
    val economyCar = EconomyCar("А123ВС77", "Kia Rio")
    val comfortCar = ComfortCar(
        licensePlate = "В456ОР77",
        model = "Hyundai Sonata",
        color = "Белый",
        year = 2022,
        hasAirConditioning = true
    )
    val businessCar = BusinessCar(
        licensePlate = "С789ТХ77",
        model = "BMW 5 Series",
        color = "Черный",
        year = 2023,
        hasWaterAndChargers = true
    )
    val minivan = Minivan(
        licensePlate = "Е321КО77",
        model = "Volkswagen Multivan",
        color = "Серый",
        year = 2021,
        passengerCapacity = 7
    )
    println("\n- Созданы автомобили:")
    println("   - ${economyCar.model} (${economyCar.licensePlate}) - Эконом")
    println("   - ${comfortCar.model} (${comfortCar.licensePlate}) - Комфорт")
    println("   - ${businessCar.model} (${businessCar.licensePlate}) - Бизнес")
    println("   - ${minivan.model} (${minivan.licensePlate}) - Минивэн")

    // нанимаем водителей
    val driverIgor = Driver(
        id = "driver_igor",
        name = "Игорь",
        phoneNumber = "+79161112233",
        rating = 4.8,
        currentVehicle = comfortCar
    )
    val driverAnna = Driver(
        id = "driver_anna",
        name = "Анна",
        phoneNumber = "+79162223344",
        rating = 4.9,
        currentVehicle = economyCar
    )
    val driverPetr = Driver(
        id = "driver_petr",
        name = "Петр",
        phoneNumber = "+79163334455",
        rating = 4.7,
        currentVehicle = businessCar
    )
    println("\n- Созданы водители:")
    println("   - ${driverIgor.name}, рейтинг: ${driverIgor.rating}, доступен: ${driverIgor.isAvailable}")
    println("   - ${driverAnna.name}, рейтинг: ${driverAnna.rating}, доступен: ${driverAnna.isAvailable}")
    println("   - ${driverPetr.name}, рейтинг: ${driverPetr.rating}, доступен: ${driverPetr.isAvailable}")

    // объявление тарифов
    val economyTariff = EconomyTariff()
    val comfortTariff = ComfortTariff()
    val businessTariff = BusinessTariff()
    val minivanTariff = MinivanTariff()
    println("\n- Существуют тарифы:")
    println("   - ${economyTariff.name}")
    println("   - ${comfortTariff.name}")
    println("   - ${businessTariff.name}")
    println("   - ${minivanTariff.name}")

    // Динамическое ценообразование
    println("\n- Демонстрация динамического ценообразования:")
    // дефолтный спрос
    comfortTariff.updatePricing(0.3)
    val normalCost = comfortTariff.calculateCost(10.0, 20.0)
    println("   Нормальный спрос (10 км, 20 мин): ${"%.2f".format(normalCost)} руб.")
    // rush hour
    comfortTariff.updatePricing(0.7)
    val highDemandCost = comfortTariff.calculateCost(10.0, 20.0)
    println("   Высокий спрос (10 км, 20 мин): ${"%.2f".format(highDemandCost)} руб.")

    // Создание и обработка заказа
    println("\n- Создание и обработка заказа:")
    // создание маршрута
    val route = Route("Офис на Тверской", "Метро Проспект Мира", 8.5)
    println("   Маршрут: ${route.displayRoute}")
    println("   Расстояние: ${route.distanceKm} км")
    val order = Order(clientMaria, route, comfortTariff)
    println("\n   Создан заказ:")
    println(order.getOrderInfo())
    println("\n   Назначаем водителя...")
    order.assignDriver(driverIgor, comfortCar)
    println(order.getOrderInfo())
    println("\n   Начинаем поездку...")
    order.startRide()
    println(order.getOrderInfo())
    println("\n   Завершаем поездку...")
    order.completeRide(20.0)
    println(order.getOrderInfo())

    // Разные тарифы
    println("\n- Сравнение тарифов для одинакового маршрута (8.5 км, 20 мин):")
    val testRoute = Route("Точка А", "Точка Б", 8.5)
    // сравниваем по дефолтному спросу
    economyTariff.updatePricing(0.3)
    comfortTariff.updatePricing(0.3)
    businessTariff.updatePricing(0.3)
    minivanTariff.updatePricing(0.3)
    println("   Эконом: ${"%.2f".format(economyTariff.calculateCost(8.5, 20.0))} руб.")
    println("   Комфорт: ${"%.2f".format(comfortTariff.calculateCost(8.5, 20.0))} руб.")
    println("   Бизнес: ${"%.2f".format(businessTariff.calculateCost(8.5, 20.0))} руб.")
    println("   Минивэн: ${"%.2f".format(minivanTariff.calculateCost(8.5, 20.0))} руб.")

    println("\n- Демонстрация отмены заказа:")     // Также можно отменить заказ
    val cancelledOrder = Order(clientIvan, Route("Дом", "Аэропорт", 25.0), economyTariff)
    println("   Состояние заказа:")
    println(cancelledOrder.getOrderInfo())
    cancelledOrder.cancelOrder("Передумал ехать")
    println("   После отмены:")
    println(cancelledOrder.getOrderInfo())
}
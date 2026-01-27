import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class Log(val dt: String, val id: Int, val state: String) {
    fun time() = LocalDateTime.parse(dt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
}

class Parser {
    private val fa = """(\d{4}-\d{2}-\d{2} \d{2}:\d{2})\s*\|\s*id:\s*(\d+)\s*\|\s*STATUS:\s*(\w+)""".toRegex(RegexOption.IGNORE_CASE)
    private val fb = """TS\s*=\s*(\d{2}/\d{2}/\d{4})-(\d{2}:\d{2})\s*;\s*status\s*=\s*(\w+)\s*;\s*#(\d+)""".toRegex(RegexOption.IGNORE_CASE)
    private val fc = """\[(\d{2}\.\d{2}\.\d{4})\s+(\d{2}:\d{2})\]\s+(\w+)\s*\(id\s*:\s*(\d+)\)""".toRegex(RegexOption.IGNORE_CASE)

    fun normalize(line: String): Log? {
        val lined = line.trim()
        fa.find(lined)?.let {match ->
            val (dt, idStr, statusStr) = match.destructured
            val status = statusStr.lowercase()
            if (status in listOf("sent", "delivered")) {
                return Log(dt, idStr.toInt(), status)}
        }
        fb.find(lined)?.let{match ->
            val (dateStr, timeStr, statusStr, idStr) = match.destructured
            val status = statusStr.lowercase()
            if (status in listOf("sent", "delivered")) {
                val parts = dateStr.split("/")
                val normed = "${parts[2]}-${parts[1]}-${parts[0]}"
                return Log("$normed $timeStr", idStr.toInt(), status) }
        }
        fc.find(lined)?.let {match ->
            val (dateStr, timeStr, statusStr, idStr) = match.destructured
            val status = statusStr.lowercase()
            if (status in listOf("sent", "delivered")) {
                val parts = dateStr.split(".")
                val normed = "${parts[2]}-${parts[1]}-${parts[0]}"
                return Log("$normed $timeStr", idStr.toInt(), status)}
        }
        return null
    }
}

fun main() {
    val logs = listOf(
        "2026-01-22 09:14 | id:042 | STATUS:sent",
        "TS=22/01/2026-09:27; status=delivered; #042",
        "2026-01-22 09:10 | id:043 | STATUS:sent",
        "2026-01-22 09:18 | id:043 | STATUS:delivered",
        "TS=22/01/2026-09:05; status=sent; #044",
        "[22.01.2026 09:40] delivered (id:044)",
        "2026-01-22 09:20 | id:045 | STATUS:sent",
        "[22.01.2026 09:33] delivered (id:045)",
        "   ts=22/01/2026-09:50; STATUS=Sent; #046   ",
        " [22.01.2026 10:05]   DELIVERED   (id:046) "
    )
    val parser = Parser()
    val normalized = mutableListOf<Log>()
    val broken = mutableListOf<String>()
    logs.forEach {line ->
        val enter = parser.normalize(line)
        if (enter != null) {
            normalized.add(enter)
        }
        else {
            broken.add(line)
        }
    }
    val group = normalized.groupBy { it.id }
    val times = mutableMapOf<Int, Int>()
    val partial = mutableListOf<Int>()
    val timeError = mutableListOf<Int>()
    group.forEach { (id, entries) ->
        val sent = entries.filter {it.state == "sent"}.minByOrNull {it.time()}
        val delivered = entries.filter {it.state == "delivered"}.minByOrNull {it.time()}
        if (sent == null || delivered == null) {
            partial.add(id)
        }
        else {
            val time1 = sent.time()
            val time2 = delivered.time()
            if (time2.isBefore(time1)) {
                timeError.add(id) }
            else {
                val minutes = ChronoUnit.MINUTES.between(time1, time2).toInt()
                times[id] = minutes
            }
        }
    }
    println("Отчет")
    if (broken.isNotEmpty()) {
        println("Битые строки (${broken.size}):")
        broken.forEach { println("  $it") }
    }
    else {
        println("Битые строки: нет")
    }
    if (partial.isNotEmpty()) {
        println("Неполные id (${partial.size}): ${partial.joinToString(", ")}")
    }
    else{
        println("Неполные id: нет")
    }
    if (timeError.isNotEmpty()) {
        println("Ошибки времени (${timeError.size}): ${timeError.joinToString(", ")}")
    }
    else {
        println("Ошибки времени: нет")
    }

    if (times.isNotEmpty()) {
        println("Время доставки по id (desc):")
        println("\tid - минуты")
        times.entries.sortedByDescending {it.value}.forEach {(id, minutes) ->
            println("\t$id - $minutes")
        }
    }
    else {
        println("Нет данных")
    }
    if (times.isNotEmpty()) {
        val longest = times.maxByOrNull {it.value}
        print("Самый долгий заказ: ")
        println("id=${longest!!.key}, время=${longest.value} мин")
    }
    else {
        println("Самый долгий заказ: нет данных")
    }
    val offenders = times.filter {it.value >20}
    if (offenders.isNotEmpty()) {
        println("Нарушители:")
        offenders.forEach {(id, minutes) ->
            println("\tid $id: $minutes минут")
        }
    }
    else {
        println("Нарушители: нет")
    }
    val stats = normalized.filter {it.state == "delivered"}.groupingBy { it.time().hour }.eachCount()
    if (stats.isNotEmpty()) {
        val rushHour = stats.maxByOrNull {it.value}?.key
        println("Сводка по часам:")
        stats.toSortedMap().forEach {(hour, count) ->
            println("\t${"%02d".format(hour)}:00 -> $count доставк.")
        }
        println("\tRush hour: ${"%02d".format(rushHour)}:00")
    }
    else {
        println("Сводка по часам: нет данных")
    }
    val copies = group.filter { (_, entries) ->
        val count1 = entries.count {it.state == "sent"}
        val count2 = entries.count {it.state == "delivered"}
        count1 > 1 || count2 > 1
    }
    if (copies.isNotEmpty()) {
        println("Детектор дублей:")
        copies.forEach { (id, entries) ->
            val count1 = entries.count { it.state == "sent" }
            val count2 = entries.count { it.state == "delivered" }
            println("\tid $id: sent = $count1, delivered = $count2")
        }
    }
    else {
        println("Детектор дублей: не обнаружено")
    }
}
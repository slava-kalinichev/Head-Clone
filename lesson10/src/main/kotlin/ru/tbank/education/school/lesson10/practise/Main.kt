import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun main() {
    task1()
    println()
    task2()
    println()
    task3()
    println()
    task4()
    println()
    task5()
    println()
    task6()
    println()
    task7()
    println()
    task8()
}

/*
1) Строки + регулярные выражения
["Name: Ivan, score=17", ...]
Извлечь имя и score, собрать пары, вывести победителя.
*/
fun task1() {
    val lines = listOf(
        "Name: Ivan, score=17",
        "Name: Olga, score=23",
        "Name: Max, score=5"
    )

    val re = Regex("""^Name:\s*([A-Za-z]+)\s*,\s*score=(\d+)\s*$""")

    val pairs: List<Pair<String, Int>> = lines.mapNotNull { s ->
        val m = re.find(s) ?: return@mapNotNull null
        val name = m.groupValues[1]
        val score = m.groupValues[2].toInt()
        name to score
    }

    println("Task 1 pairs: $pairs")

    val best = pairs.maxByOrNull { it.second }
    if (best != null) {
        println("Task 1 best: ${best.first} (${best.second})")
    } else {
        println("Task 1: no valid lines")
    }
}

/*
2) Даты + коллекции
["2026-01-22", ...]
Преобразовать в даты, отсортировать, посчитать сколько в январе 2026.
*/
fun task2() {
    val dateStrings = listOf(
        "2026-01-22",
        "2026-02-01",
        "2025-12-31",
        "2026-01-05"
    )

    val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    val dates = dateStrings.map { LocalDate.parse(it, fmt) }.sorted()

    println("Task 2 sorted dates: ${dates.joinToString { it.format(fmt) }}")

    val countJan2026 = dates.count { it.year == 2026 && it.month == Month.JANUARY }
    println("Task 2 count in Jan 2026: $countJan2026")
}

/*
3) Коллекции + строки
"apple orange apple banana orange apple"
Частоты слов, вывести слова с частотой > 1 по алфавиту.
*/
fun task3() {
    val text = "apple orange apple banana orange apple"

    val words = text.trim().split(Regex("""\s+""")).filter { it.isNotEmpty() }

    val freq = mutableMapOf<String, Int>()
    for (w in words) {
        freq[w] = (freq[w] ?: 0) + 1
    }

    println("Task 3 freq: $freq")
    val repeated = freq
        .filter { (_, c) -> c > 1 }
        .keys
        .sorted()

    println("Task 3 repeated words: ${repeated.joinToString(", ")}")
}

fun task4() {
    val data = listOf(
        "A-123",
        "B-7",
        "AA-12",
        "C-001",
        "D-99x"
    )
    val regex = Regex("""^[A-Z]-\d{1,3}$""")
    val filtered = data.filter { regex.matches(it) }
    println("Filtered: $filtered")
}

fun task5() {
    val data = listOf(
        "  Hello   world  ",
        "A   B    C",
        "   one"
    )
    val ans = data.map {str ->
        str.trim().replace(Regex("\\s+"), " ")
    }
    println("Normalized: $ans")
}

fun task6() {
    val pairs = listOf(
        Pair("2026-01-01", "2026-01-10"),
        Pair("2025-12-31", "2026-01-01"),
        Pair("2026-02-01", "2026-01-22")
    )
    val dfs = mutableListOf<Long>()
    for ((x, y) in pairs) {
        val fst = LocalDate.parse(x)
        val snd = LocalDate.parse(y)
        val diff = ChronoUnit.DAYS.between(fst, snd)
        dfs.add(diff)
    }
    println("Diff: $dfs")
}

fun task7() {
    val data = listOf(
        "math:Ivan",
        "bio:Olga",
        "math:Max",
        "bio:Ivan",
        "cs:Olga"
    )
    val mp = mutableMapOf<String, MutableList<String>>()
    for (i in data) {
        val parts = i.split(":")
        val subject = parts[0]
        val student = parts[1]
        val studentsList = mp.getOrPut(subject) { mutableListOf()}
        if (!studentsList.contains(student)) {
            studentsList.add(student) }
    }
    println("$mp")
}

fun task8() {
    val data = listOf(
        "Start at 2026/01/22 09:14",
        "No time here",
        "End: 22-01-2026 18:05"
    )
    val mlist = mutableListOf<String>()
    val ptns = listOf(
        Pair(Regex("""(\d{4})/(\d{2})/(\d{2})\s+(\d{2}):(\d{2})"""),
            { match: MatchResult ->
                "${match.groupValues[1]}-${match.groupValues[2]}-${match.groupValues[3]} ${match.groupValues[4]}:${match.groupValues[5]}"
            }),
        Pair(Regex("""(\d{2})-(\d{2})-(\d{4})\s+(\d{2}):(\d{2})"""),
            { match: MatchResult ->
                "${match.groupValues[3]}-${match.groupValues[2]}-${match.groupValues[1]} ${match.groupValues[4]}:${match.groupValues[5]}"
            })
    )
    for (i in data) {
        for ((pattern, formatter) in ptns) {
            val pos = pattern.find(i)
            if (pos != null) {
                mlist.add(formatter(pos))
                break
            }
        }
    }
    println("Found: $mlist")
}
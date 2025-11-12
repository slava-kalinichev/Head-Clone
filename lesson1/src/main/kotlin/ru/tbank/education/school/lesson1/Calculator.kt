package ru.tbank.education.school.lesson1
import kotlin.math.*

fun calculate(a: Double, b: Double? = null, op: Any = OperationType.ADD): Double? {
    return when (op) {
        is OperationType -> when (op) {
            OperationType.ADD -> a +(b ?: return null)
            OperationType.SUBTRACT -> a - (b ?:return null)
            OperationType.MULTIPLY -> a * (b ?: return null)
            OperationType.DIVIDE -> b?.takeIf {it != 0.0} ?.let {a/it}
        }
        is String -> when (op.lowercase()) {
            "+" -> b?.let {a+it}
            "-" -> b?.let {a-it}
            "*" -> b?.let {a*it}
            "/" -> b?.takeIf {it != 0.0} ?.let {a/it}
            "pow", "power", "**", "^" -> b?.let { Math.pow(a, it)}
            "sin" -> sin(a)
            "cos" -> cos(a)
            "tan", "tg" -> tan(a)
            "sqrt" -> a.takeIf {it >=0.0}?.let {sqrt(it)}
            "ln" -> a.takeIf {it > 0.0}?.let {ln(it)}
            "log10" -> a.takeIf {it > 0.0 }?.let {log10(it)}
            else -> null
        } else -> null
    }
}

@Suppress("ReturnCount")
fun String.calculate(): Double? {
    val data = this.trim().split("\\s+".toRegex())
    return when (data.size) {
        2 -> {
            val op = data[0]
            val a = data[1].toDoubleOrNull() ?: return null
            calculate(a, op = op)
        }
        3 -> {
            val a = data[0].toDoubleOrNull() ?:return null
            val op = data[1]
            val b = data[2].toDoubleOrNull() ?: return null
            calculate(a, b, op)
        }
        else -> null
    }
}

// Для более наглядного вывода целых ответов
fun format(ans: Double?): String {
    return when {
        ans == null -> "null"
        ans % 1 == 0.0 -> "${ans.toInt()}"
        else -> "$ans"
    }
}
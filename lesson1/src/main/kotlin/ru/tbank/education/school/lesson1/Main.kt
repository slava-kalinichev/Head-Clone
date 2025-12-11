package ru.tbank.education.school.lesson1
import kotlin.system.exitProcess

fun main() {
    println("Hi")
    print("tBank!")
    print("\n")
    val d = 'o'     // константа
    val a: Int? = 8
    var ans: Boolean? = null
    val g = "hell$d world"
    println(g)
    var h = 1       // переменная
    if (a != null) {
        println(a)
    }
    for (i in 1..3 step 2) {
        println(i)
    }
    println(5/3)
    exitProcess(0)
}

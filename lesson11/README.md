# Семинар: Многопоточность и корутины в Kotlin

---

## Часть 1. Потоки (Thread)

### Задание 1. Создание потоков
Создайте 3 потока с именами "Thread-A", "Thread-B", "Thread-C". Каждый поток должен вывести своё имя 5 раз с задержкой 500мс.

```kotlin
object CreateThreads {
    fun run(): List<Thread> {
        val data = mutableListOf<Thread>()
        for (i in listOf("Thread-A", "Thread-B", "Thread-C")) {
            val t = Thread {
                repeat(5) {
                    println(i)
                    Thread.sleep(500)
                }
            }
            t.name = i
            data.add(t)
            t.start()
        }
        return data
    }
}
```

### Задание 2. Race condition
Создайте переменную `counter = 0`. Запустите 10 потоков, каждый из которых увеличивает counter на 1000. Выведите финальное значение и объясните результат.

```kotlin
object RaceCondition {
    fun run(): Int {
        var cnt = 0
        val ts = mutableListOf<Thread>()
        repeat(10){
            val t = Thread {
                repeat(1000) {
                    cnt++
                }
            }
            ts.add(t)
            t.start()
        }
        ts.forEach { it.join() }
        return cnt
    }
}
```

### Задание 3. Synchronized
Исправьте задание 2 с помощью `@Synchronized` или `synchronized {}` блока, чтобы результат всегда был 10000.

```kotlin
object SynchronizedCounter {
    private val lock = Any()
    fun run(): Int {
        var cnt = 0
        val lock = Any()
        val ts = mutableListOf<Thread>()
        repeat(10) {
            val t = Thread {
                repeat(1000) {
                    synchronized(lock) {
                        cnt++
                    }
                }
            }
            ts.add(t)
            t.start()
        }
        ts.forEach {it.join()}
        return cnt
    }
}
```

### Задание 4. Deadlock
Создайте пример deadlock с двумя ресурсами и двумя потоками. Затем исправьте его.

```kotlin
object Deadlock {
    fun runDeadlock() {
        val r1 = Any()
        val r2 = Any()
        val t1 = Thread {
            synchronized(r1) {
                println("Thread 1 locked r1")
                Thread.sleep(50)
                synchronized(r2) {
                    println("Thread 1 locked r2")
                }
            }
        }
        val t2 = Thread {
            synchronized(r2) {
                println("Thread 2 locked r2")
                Thread.sleep(50)
                synchronized(r1) {
                    println("Thread 2 locked r1")
                }
            }
        }
        t1.start()
        t2.start()
        t1.join()
        t2.join()
    }

    fun runFixed(): Boolean{
        val r1 = Any()
        val r2 = Any()
        val ok = true
        val t1 = Thread {
            synchronized(r1) {
                println("Thread 1 locked r1")
                Thread.sleep(50)
                synchronized(r2) {
                    println("Thread 1 locked r2")
                }
            }
        }
        val t2 = Thread {
            synchronized(r1) {
                println("Thread 2 locked r1")
                Thread.sleep(50)
                synchronized(r2) {
                    println("Thread 2 locked r2")
                }
            }
        }
        t1.start()
        t2.start()
        t1.join()
        t2.join()
        return ok
    }
}
```

---

## Часть 2. Executor Framework

### Задание 5. ExecutorService
Используя `Executors.newFixedThreadPool(4)`, выполните 20 задач. Каждая задача выводит свой номер и имя потока, затем спит 200мс.

```kotlin
object ExecutorServiceExample {
    fun run(): List<String> {
        val ex = Executors.newFixedThreadPool(4)
        val res = mutableListOf<String>()
        for (i in 1..20) {
            ex.submit {
                val out = "Task $i by ${Thread.currentThread().name}"
                synchronized(res) {
                    res.add(out)
                }
                Thread.sleep(200)
            }
        }
        ex.shutdown()
        ex.awaitTermination(1, TimeUnit.MINUTES)
        return res
    }
}
```

### Задание 6. Future
Используя ExecutorService и `Callable`, параллельно вычислите факториалы чисел от 1 до 10. Соберите результаты через `Future.get()`.

```kotlin
object FutureFactorial {
    fun run(): Map<Int, BigInteger> {
        val ex = Executors.newFixedThreadPool(4)
        val fs = mutableListOf<java.util.concurrent.Future<Pair<Int, BigInteger>>>()
        val res = mutableMapOf<Int, BigInteger>()
        for (i in 1..10) {
            fs.add(ex.submit<java.util.concurrent.Callable<Pair<Int, BigInteger>>> {
                var f = BigInteger.ONE
                for (j in 1..i) {
                    f = f.multiply(BigInteger.valueOf(j.toLong()))
                }
                Pair(i, f)
            })
        }
        fs.forEach {f ->
            val (n, fact) = f.get()
            res[n] = fact
        }
        ex.shutdown()
        return res
    }
}
```

---

## Часть 3. Корутины

### Задание 7. Первая корутина
Используя `runBlocking` и `launch`, запустите 3 корутины, каждая из которых выводит своё имя 5 раз с `delay(500)`.

```kotlin
object CoroutineLaunch {
    fun run(): List<String> = runBlocking {
        val res = mutableListOf<String>()
        val data = mutableListOf<Job>()
        for (n in listOf("Coroutine-A", "Coroutine-B", "Coroutine-C")) {
            data.add(launch {
                repeat(5) {
                    synchronized(res) {
                        res.add(n)
                    }
                    delay(500)
                }
            })
        }
        data.joinAll()
        return@runBlocking res
    }
}
```

### Задание 8. async/await
Используя `async`, параллельно вычислите сумму чисел от 1 до 1_000_000, разбив на 4 части. Соберите результаты через `await()`.

```kotlin
object AsyncAwait {
    fun run(): Long = runBlocking {
        val total = 1_000_000L
        val chunk = total / 4
        val defs = (0 until 4).map {idx ->
            async {
                val s = idx*chunk + 1
                val e = if (idx == 3) total else (idx+1) * chunk
                (s..e).sum()
            }
        }

        defs.sumOf { it.await() }
    }
}
```

### Задание 9. Structured concurrency
Создайте корутину, которая запускает 5 дочерних корутин. Если одна из них падает с исключением, все остальные должны отмениться.

```kotlin
object StructuredConcurrency {
    fun run(failingIndex: Int): Int = runBlocking {
        var done = 0
        try {
            coroutineScope {
                (0 until 5).map {idx ->
                    launch {
                        try{
                            delay(100)
                            if (idx == failingIndex) {
                                throw RuntimeException("Coroutine $idx failed")
                            }
                            done++
                        }
                        catch (e: CancellationException) {
                            throw e
                        }
                    }
                }.joinAll()
            }
        }
        catch (e: Exception) {}
        delay(200)
        return@runBlocking done
    }
}
```

### Задание 10. withContext
Используя `withContext(Dispatchers.IO)`, прочитайте содержимое 3 файлов параллельно и объедините результаты.

```kotlin
object WithContextIO {
    fun run(paths: List<String>): Map<String, String> = runBlocking {
        val defs = paths.map { p ->
            async {
                p to withContext(Dispatchers.IO) {
                    File(p).readText()
                }
            }
        }
        defs.associate { it.await() }
    }
}
```

---

## Часть 4. Практическое задание

### Задание 11. Многопоточный загрузчик изображений

Напишите программу, которая параллельно скачивает изображения из интернета.

**Требования:**
1. Использовать корутины с `Dispatchers.IO`
2. Скачать 10 изображений с https://picsum.photos/200/300
3. Сохранить в папку `downloads/`
4. Вывести прогресс: "Downloaded 1/10", "Downloaded 2/10", ...
5. В конце вывести статистику: общее время, количество успешных/неуспешных загрузок

РЕШЕНИЕ В ФАЙЛЕ Main.kt
---

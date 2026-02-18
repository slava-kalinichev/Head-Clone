import java.io.File
import java.net.URL
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

data class Stats(val tm: Long, val ok: Int, val er: Int)

object ImageDownloader {
    fun run(urls: List<String>, dir: String): Stats = runBlocking {
        File(dir).mkdirs()
        var ok = 0
        var er = 0
        val time = measureTimeMillis {
            withContext(Dispatchers.IO) {
                val data = urls.mapIndexed { idx, url ->
                    async {
                        try{
                            val name = "image_${idx+1}.jpg"
                            val file = File(dir, name)
                            URL(url).openStream().use {it.copyTo(file.outputStream())}
                            synchronized(this) {
                                ok++
                                println("Downloaded $ok/${urls.size}")
                            }
                        }
                        catch (e: Exception) {
                            synchronized(this) {
                                er++
                                println("Failed ${er}/${urls.size}")
                            }
                        }
                    }
                }
                data.joinAll()
            }
        }
        Stats(time, ok, er)
    }
}

/*
при выполнении этого задания возникла проблема:
сервер picsum.photos блокирует запросы из java-кода (ошибка 403),даже с правильными заголовками
Это стандартная защита сервера от автоматического скачивания и я не знаю как ее обойти
 */
fun main() {
    val urls = List(10) {"https://picsum.photos/200/300?random=${it}"}
    val dir = "downloads"
    println("Загрузка..")
    val stats = ImageDownloader.run(urls, dir)
    println("Статистика")
    println("Время: ${stats.tm}ms")
    println("Успешно: ${stats.ok}")
    println("Ошибок: ${stats.er}")
    println("Папка: $dir")
}
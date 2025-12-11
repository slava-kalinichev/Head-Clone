class HomeworkRepository {
    private val homeworks = mutableListOf<Pair<String, String>>()
    fun addHomework(subject: String, text: String) {
        homeworks.add(subject to text)
    }
    fun getAllHomeworks(): List<Pair<String, String>> = homeworks.toList()
}

class HomeworkPrinter {
    fun printHomeworks(homeworks: List<Pair<String, String>>) {
        println("Домашка:")
        for ((subject, text) in homeworks) {
            println("$subject: $text")
        }
    }
}

class NotificationService {
    fun sendRemindersToParents(parentsPhones: List<String>) {
        for (phone in parentsPhones) {
            println("Отправляю SMS на $phone: Не забудьте проверить домашку!")
        }
    }
}

class HomeworkFileService {
    fun saveToFile(homeworks: List<Pair<String, String>>, filename: String) {
        // логика сохранения в файл
    }
}

class HomeworkManager(
    private val repository: HomeworkRepository,
    private val printer: HomeworkPrinter,
    private val notificationService: NotificationService,
    private val fileService: HomeworkFileService
) {
    fun addHomework(subject: String, text: String) {
        repository.addHomework(subject, text)
    }
    fun printHomeworks() {
        printer.printHomeworks(repository.getAllHomeworks())
    }
    fun sendRemindersToParents(parentsPhones: List<String>) {
        notificationService.sendRemindersToParents(parentsPhones)
    }

    fun saveToFile(filename: String) {
        fileService.saveToFile(repository.getAllHomeworks(), filename)
    }
}
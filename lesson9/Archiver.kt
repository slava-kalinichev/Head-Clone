import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class Archiver {
    fun zipArchive(
        origin: String,
        path: String,
        filter: String="all"
    ) {
        val exts = when (filter.lowercase()) {
            "txt" -> setOf("txt")
            "log" -> setOf("log")
            "all" -> null
            else -> { println("Unknown filter type '$filter'. Use: 'all', 'txt', or 'log'")
                return
            }
        }
        val directory = File(origin)
        if (!directory.exists() || !directory.isDirectory) {
            println("Folder '$origin' does not exist or is not a directory")
            return
        }
        val files = filesP(directory, exts)
        if (files.isEmpty()) {
            println("No files to archive with filter: ${when(filter) {
                "all" -> "all files"
                "txt" -> "only .txt"
                "log" -> "only .log"
                else -> filter }}")
            return
        }
        println("Files to archive: ${files.size}")
        println("Archiving")
        try {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(path))).use {zipOut ->
                for (i in files) {
                    add(i, directory, zipOut)
                }
            }
            println("\nSuccess!")
            println("Archive saved in: ${File(path).absolutePath}")
            println("Created archive: $path (${File(path).length()} bytes)")
        }
        catch (e: IOException) {
            println("Error: ${e.message}")
            File(path).delete()
        }
    }

    fun filesP(dir: File, ext: Set<String>?): List<File> {
        val files = mutableListOf<File>()
        dir.listFiles()?.forEach { file ->
            when {
                file.isDirectory -> { files.addAll(filesP(file, ext)) }
                ext == null-> { files.add(file)}
                else -> {
                    val fileExt = extR(file.name).lowercase()
                    if (ext.any { it.lowercase() == fileExt}) {
                        files.add(file)
                    }
                }
            }
        }
        return files
    }

    private fun add(file: File, directory: File, zipOut: ZipOutputStream) {
        val rpath = directory.toPath().relativize(file.toPath()).toString()
        val zipEntry = ZipEntry(rpath)
        zipEntry.time = file.lastModified()
        zipEntry.size = file.length()
        println("  file: $rpath (${file.length()} b)")
        try {
            BufferedInputStream(FileInputStream(file)).use {inputStream ->
                zipOut.putNextEntry(zipEntry)
                val buff = ByteArray(8192)
                var bytes: Int
                while (inputStream.read(buff).also {bytes = it} != -1) {
                    zipOut.write(buff, 0, bytes)
                }
                zipOut.closeEntry()
            }
        }
        catch (e: IOException) {
            println("Addin file error ${file.name}: ${e.message}")
            throw e
        }
    }

    private fun extR(name: String): String {
        return if (name.contains(".")) {
            name.substringAfterLast(".", "")
        }
        else { "" }
    }
}

fun main() {
    println("\nZip archiver")
    val sc = Scanner(System.`in`)
    val ar = Archiver()
    println("Enter Directory path below or press Enter to turn current directory")
    val headClonePath = File(".").canonicalPath + File.separator
    println("Cur directory: $headClonePath")
    print("Directory path: ")
    var userPath = sc.nextLine().trim()
    if (userPath.isEmpty()) {
        print("Directory path: $headClonePath")
        userPath = sc.nextLine().trim()
        if (userPath.isEmpty()) {
            userPath = headClonePath
        }
    }
    val file = File(userPath)
    if (!file.exists() || !file.isDirectory) {
        println("Folder '$userPath' doesn't exist or is not a directory")
        println("Current directory: ${File(".").absolutePath}")
        return
    }
    print("Output archive name: ")
    var target = sc.nextLine().trim()
    if (!target.endsWith(".zip")) {
        target += ".zip"
    }
    println("Filter type:")
    println("1) All files")
    println("2) Only .txt files")
    println("3) Only .log files")
    print("Choose (1-3): ")
    val filterChoice = sc.nextLine().trim()
    val filterType = when (filterChoice) {
        "1" -> "all"
        "2" -> "txt"
        "3" -> "log"
        else -> {
            println("Invalid choice. Using 'all files'.")
            "all"
        }
    }
    val allFiles = ar.filesP(file, null)
    println("\nFiles in directory: ${allFiles.size}")
    val exts = when (filterType.lowercase()) {
        "txt" -> setOf("txt")
        "log" -> setOf("log")
        else -> null
    }
    val filesArch = ar.filesP(file, exts)
    if (filesArch.isEmpty()) {
        println("No files to archive with filter: ${when(filterType) {
            "all" -> "all files"
            "txt" -> "only .txt"
            "log" -> "only .log"
            else -> filterType
        }}")
        return
    }
    println("Files to archive after filter: ${filesArch.size}")
    if (filesArch.size <=10) {
        println("Files list:")
        filesArch.forEach { f ->
            val rpath = file.toPath().relativize(f.toPath()).toString()
            println("  $rpath (${f.length()} b)" )
        }
    }

    print("\nConfirm archiving? (yes/no): ")
    val confirm = sc.nextLine().trim().lowercase()
    if (confirm == "yes") {
        ar.zipArchive(userPath, target, filterType)
    }
    else { println("Cancelled.")}
}
package org.xtimms.shirizu.utils.system

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import org.xtimms.shirizu.BuildConfig
import org.xtimms.shirizu.utils.FileSequence
import java.io.File
import java.io.FileFilter
import java.nio.file.attribute.BasicFileAttributes
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.readAttributes

fun File.subdir(name: String) = File(this, name).also {
    if (!it.exists()) it.mkdirs()
}

fun File.getUriCompat(context: Context): Uri {
    return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", this)
}

fun Context.getFileProvider() = "$packageName.provider"

suspend fun File.computeSize(): Long = runInterruptible(Dispatchers.IO) {
    walkCompat(includeDirectories = false).sumOf { it.length() }
}

@OptIn(ExperimentalPathApi::class)
fun File.walkCompat(includeDirectories: Boolean): Sequence<File> {
    val walk = this.walkTopDown()
    return if (includeDirectories) {
        walk
    } else {
        walk.filter { it.isFile }
    }
}

fun File.children() = FileSequence(this)

suspend fun File.deleteAwait() = withContext(Dispatchers.IO) {
    delete() || deleteRecursively()
}

val File.creationTime: Long
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        try {
            toPath().readAttributes<BasicFileAttributes>().creationTime().toMillis()
        } catch (_: Exception) {
            lastModified()
        }
    } else {
        lastModified()
    }

fun ZipFile.readText(entry: ZipEntry) = getInputStream(entry).bufferedReader().use {
    it.readText()
}

fun Sequence<File>.filterWith(filter: FileFilter): Sequence<File> = filter { f -> filter.accept(f) }

fun File.takeIfReadable() = takeIf { it.exists() && it.canRead() }
fun File.takeIfWriteable() = takeIf { it.exists() && it.canWrite() }

fun File.isNotEmpty() = length() != 0L
package io.wollinger.zipmeup

import com.fasterxml.jackson.databind.ObjectMapper
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.progress.ProgressMonitor
import java.io.File
import java.nio.file.Files
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if(args.isEmpty()) println("Supply a valid json file in the arguments!").also { exitProcess(0) }

    val configFile = File(args[0])
    val settings = ObjectMapper().readValue(configFile, Settings::class.java)

    fun getParams(file: File): ZipParameters {
        return ZipParameters().also { params ->
            var rootName = file.path
            if(file.isAbsolute && Utils.isWindows)
                rootName = rootName.replaceFirst(":", "_DRIVE")
            rootName = rootName.replace(file.name, "")
            params.rootFolderNameInZip = rootName
            params.compressionLevel = CompressionLevel.ULTRA
        }
    }

    var log = ""
    val firstOutput = settings.output.removeFirst()
    var path = Utils.formatEnv(firstOutput)
    path = if(settings.formatOutput) Utils.formatTime(path) else path
    ZipFile(path).run {
        isRunInThread = true
        settings.input.forEachIndexed { iIndex, input ->
            File(Utils.formatEnv(input)).also {
                if(it.isDirectory) addFolder(it, getParams(it))
                else addFile(it, getParams(it))

                val pm = progressMonitor
                var lastFilename = ""
                while (!pm.state.equals(ProgressMonitor.State.READY)) {
                    if(pm.fileName != null && lastFilename != pm.fileName) {
                        val cLog = "Task ${iIndex + 1}/${settings.input.size} (${pm.percentDone}/100%) [${pm.currentTask}] ${pm.fileName}"
                        if(settings.log) println(cLog)
                        if(settings.includeLog) log += cLog + "\n"
                        lastFilename = pm.fileName
                    }
                }
            }
        }

        fun lockWaiter(msg: String) {
            while(!progressMonitor.state.equals(ProgressMonitor.State.READY)) {
                "Waiting for state to change... ($msg)".also {
                    println(it)
                    log += it + "\n"
                }
            }
        }

        if(settings.includeConfig) {
            val zippedConfig = File("ZipMeUpConfig.json").also { it.deleteOnExit() }
            Files.copy(configFile.toPath(), zippedConfig.toPath())
            lockWaiter("IncludeConfig")
            addFile(zippedConfig, getParams(zippedConfig))
        }

        if(settings.includeLog) {
            val logFile = File("ZipMeUpLog.log").also { it.deleteOnExit() }
            Files.writeString(logFile.toPath(), log)
            lockWaiter("IncludeLog")
            addFile(logFile, getParams(logFile))
        }
    }

    settings.output.forEach {
        Files.copy(File(firstOutput).toPath(), File(it).toPath())
    }
}

class Settings {
    var includeConfig = false
    var includeLog = false
    var log = false
    var formatOutput = false
    var input = ArrayList<String>()
    var output = ArrayList<String>()
}
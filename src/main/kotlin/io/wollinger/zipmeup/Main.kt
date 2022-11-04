package io.wollinger.zipmeup

import com.fasterxml.jackson.databind.ObjectMapper
import net.lingala.zip4j.ZipFile
import java.io.File
import java.lang.StringBuilder
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import kotlin.system.exitProcess

const val VERSION = "0.0.1"

class Logger(private val settings: Settings) {
    var log = ""
    fun log(msg: String) {
        if(settings.log) println(msg)
        if(settings.includeLog) log += "$msg\n"
    }
}

fun main(args: Array<String>) {
    if(args.isEmpty()) println("Supply a valid json file in the arguments!").also { exitProcess(0) }
    when(args[0]) {
        "-v", "-version" -> {
            println(VERSION)
            exitProcess(0)
        }
    }

    val configFile = File(args[0])
    val settings = ObjectMapper().readValue(configFile, Settings::class.java).also { it.format() }
    val logger = Logger(settings)

    logger.log("Starting ZipMeUp (v$VERSION) on ${LocalDateTime.now()}")
    logger.log("File: ${configFile.absolutePath}")
    logger.log("Settings:")
    logger.log(settings.toString())

    val firstOutput = settings.output.removeFirst()
    ZipFile(firstOutput).run {
        ArrayList<File>().also { allFiles ->
            //Collect files
            settings.input.forEach { path ->
                File(path).walk().filter { !it.isDirectory }.forEach { allFiles.add(it) }
            }

            //Add files to zip file
            allFiles.forEachIndexed { index, file ->
                logger.log("[${index+1}/${allFiles.size}] $file")
                addFile(file, Utils.getParams(file))
            }
        }

        //Add config if requested
        if(settings.includeConfig) {
            val zippedConfig = File("ZipMeUpConfig.json").also { it.deleteOnExit() }
            Files.copy(configFile.toPath(), zippedConfig.toPath())
            addFile(zippedConfig, Utils.getParams(zippedConfig))
            logger.log("Adding config...")
        }

        //Add log if requested
        if(settings.includeLog) {
            val logFile = File("ZipMeUpLog.log").also { it.deleteOnExit() }
            Files.writeString(logFile.toPath(), logger.log)
            addFile(logFile, Utils.getParams(logFile))
            logger.log("Adding log...")
        }
    }

    //Create additional copies if requested
    settings.output.forEach {
        Files.copy(File(firstOutput).toPath(), File(it).toPath(), StandardCopyOption.REPLACE_EXISTING)
    }
}

class Settings {
    var includeConfig = false
    var includeLog = false
    var log = false
    var formatOutput = false
    var input = ArrayList<String>()
    var output = ArrayList<String>()

    fun format() {
        //Format input
        ArrayList<String>().also { it.addAll(input) }.forEachIndexed { i, s ->
            input[i] = Utils.formatEnv(s)
        }
        //Format output
        ArrayList<String>().also { it.addAll(output) }.forEachIndexed{ i, s ->
            output[i] = Utils.formatEnv(s)
            if(formatOutput) output[i] = Utils.formatTime(output[i])
        }
    }

    override fun toString() = StringBuilder().also { sb ->
        sb.append("\tincludeConfig: $includeConfig\n")
        sb.append("\tincludeLog: $includeLog\n")
        sb.append("\tlog: $log\n")
        sb.append("\tinput:\n")
        input.forEach { sb.append("\t+ $it\n") }
        sb.append("\toutput:\n")
        output.forEach { sb.append("\t+ $it\n") }
    }.toString()
}
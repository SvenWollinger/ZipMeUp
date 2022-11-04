package io.wollinger.zipmeup

import com.fasterxml.jackson.databind.ObjectMapper
import net.lingala.zip4j.ZipFile
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if(args.isEmpty()) println("Supply a valid json file in the arguments!").also { exitProcess(0) }

    val configFile = File(args[0])
    val settings = ObjectMapper().readValue(configFile, Settings::class.java)

    val firstOutput = settings.output.removeFirst()
    var path = Utils.formatEnv(firstOutput)
    path = if(settings.formatOutput) Utils.formatTime(path) else path

    ZipFile(path).run {
        var log = ""
        fun log(msg: String) {
            if(settings.log) println(msg)
            if(settings.includeLog) log += "$msg\n"
        }

        ArrayList<File>().also { allFiles ->
            //Collect files
            settings.input.forEach { path ->
                File(path).walk().filter { !it.isDirectory }.forEach { allFiles.add(it) }
            }

            //Add files to zip file
            allFiles.forEachIndexed { index, file ->
                log("[${index+1}/${allFiles.size}] $file")
                addFile(file, Utils.getParams(file))
            }
        }

        //Add config if requested
        if(settings.includeConfig) {
            val zippedConfig = File("ZipMeUpConfig.json").also { it.deleteOnExit() }
            Files.copy(configFile.toPath(), zippedConfig.toPath())
            addFile(zippedConfig, Utils.getParams(zippedConfig))
            log("Adding config...")
        }

        //Add log if requested
        if(settings.includeLog) {
            val logFile = File("ZipMeUpLog.log").also { it.deleteOnExit() }
            Files.writeString(logFile.toPath(), log)
            addFile(logFile, Utils.getParams(logFile))
            log("Adding log...")
        }
    }

    //Create additional copies if requested
    settings.output.forEach {
        Files.copy(File(path).toPath(), File(it).toPath(), StandardCopyOption.REPLACE_EXISTING)
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
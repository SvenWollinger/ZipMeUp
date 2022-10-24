package io.wollinger.zipmeup

import java.time.LocalDateTime

object Utils {
    val isWindows = System.getProperty("os.name").lowercase().contains("windows")

    fun formatEnv(string: String): String {
        var result = string
        result = formatEnvSingle(result, "appdata")
        result = formatEnvSingle(result, "localappdata")
        result = formatEnvSingle(result, "userprofile")
        result = formatEnvSingle(result, "programdata")
        return result
    }

    private fun formatEnvSingle(string: String, varr: String): String {
        val winVer = "%$varr%"
        var result = string
        if(result.lowercase().contains(winVer))
            result = result.replace(winVer, System.getenv(varr).replace("\\\\", "/"))
        return result
    }

    fun formatTime(string: String): String {
        var result = string
        fun fZ(i: Int): String = if(i > 9) i.toString() else "0$i"

        val now = LocalDateTime.now()
        result = result.replace("%dd%", fZ(now.dayOfMonth))
        result = result.replace("%mm%", fZ(now.monthValue))
        result = result.replace("%yyyy%", now.year.toString())

        result = result.replace("%hh%", fZ(now.hour))
        result = result.replace("%mm%", fZ(now.minute))
        result = result.replace("%ss%", fZ(now.second))
        result = result.replace("%sss%", fZ(now.nano))
        return result
    }
}
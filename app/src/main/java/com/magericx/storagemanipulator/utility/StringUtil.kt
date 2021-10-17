package com.magericx.storagemanipulator.utility

object StringUtil {

    fun capitalized(s: String): String{
        if (s.isBlank() || s.isEmpty()){
            return ""
        }
        val first: Char = s[0]
        return if (Character.isUpperCase(first)){
            s
        }
        else{
            Character.toUpperCase(first) + s.substring(1)
        }
    }
}
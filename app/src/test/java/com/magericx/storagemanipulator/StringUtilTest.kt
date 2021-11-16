package com.magericx.storagemanipulator

import com.magericx.storagemanipulator.utility.StringUtil
import org.junit.Test

import org.junit.Assert.*
import java.io.File

class StringUtilTest {

    private companion object{
        private const val file0 = "file_0"
        private const val file1 = "file_1"
        private const val file2 = "file_2"
        private const val file9 = "file_9"
        private const val file10 = "file_10"
        private const val file9999 = "file_9999"
        private const val file10000 = "file_10000"
    }

    @Test
    fun test_getNextFileName_1() {
        val dummyTestFile = File(file1)
        val nextFileName = StringUtil.getNextFileName(dummyTestFile)
        assertEquals(file2, nextFileName)
    }

    @Test
    fun test_getNextFileName_2() {
        val dummyTestFile = File(file9)
        val nextFileName = StringUtil.getNextFileName(dummyTestFile)
        assertEquals(file10, nextFileName)
    }

    @Test
    fun test_getNextFileName_3() {
        val dummyTestFile = File(file0)
        val nextFileName = StringUtil.getNextFileName(dummyTestFile)
        assertEquals(file1, nextFileName)
    }


    @Test
    fun test_getNextFileName_max_allowance() {
        val dummyTestFile = File(file9999)
        val nextFileName = StringUtil.getNextFileName(dummyTestFile)
        assertEquals(file10000, nextFileName)
    }




}
package com.magericx.storagemanipulator

import com.magericx.storagemanipulator.utility.StringUtil
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class StringUtilTest {

    private companion object {
        private const val file0 = "file_0"
        private const val file1 = "file_1"
        private const val file2 = "file_2"
        private const val file9 = "file_9"
        private const val file10 = "file_10"
        private const val file9999 = "file_9999"
        private const val file10000 = "file_10000"

        private val capitalizedTestData by lazy {
            return@lazy mapOf(
                "test_set_1" to TestData(input = "123456", expected = "123456"),
                "test_set_2" to TestData(input = "abcd", expected = "Abcd"),
                "test_set_3" to TestData(input = "", expected = ""),
                "test_set_4" to TestData(input = "a", expected = "A"),
                "test_set_5" to TestData(input = "A", expected = "A")
            )
        }

        private const val length_1 = 1000000L
        private const val randomChars: String = "ABCDEFGHIJKLMNOPQRSTUWXYZ1234567890"
    }

    private fun String.toFile(): File = File(this)

    private fun TestData.toCapitalized() = this.let {
        assertEquals(it.expected,StringUtil.capitalized(it.input))
    }

    @Test
    fun test_getNextFileName_1() {
        val dummyTestFile = file1.toFile()
        val nextFileName = StringUtil.getNextFileName(dummyTestFile)
        assertEquals(file2, nextFileName)
    }

    @Test
    fun test_getNextFileName_2() {
        val dummyTestFile = file9.toFile()
        val nextFileName = StringUtil.getNextFileName(dummyTestFile)
        assertEquals(file10, nextFileName)
    }

    @Test
    fun test_getNextFileName_3() {
        val dummyTestFile = file0.toFile()
        val nextFileName = StringUtil.getNextFileName(dummyTestFile)
        assertEquals(file1, nextFileName)
    }


    @Test
    fun test_getNextFileName_max_allowance() {
        val dummyTestFile = file9999.toFile()
        val nextFileName = StringUtil.getNextFileName(dummyTestFile)
        assertEquals(file10000, nextFileName)
    }

    @Test
    fun test_capitalized_1() {
        val testData = capitalizedTestData["test_set_1"]
        testData?.toCapitalized()
    }

    @Test
    fun test_capitalized_2() {
        val testData = capitalizedTestData["test_set_2"]
        testData?.toCapitalized()
    }

    @Test
    fun test_capitalized_3() {
        val testData = capitalizedTestData["test_set_3"]
        testData?.toCapitalized()
    }

    @Test
    fun test_capitalized_4() {
        val testData = capitalizedTestData["test_set_4"]
        testData?.toCapitalized()
    }

    @Test
    fun test_capitalized_5() {
        val testData = capitalizedTestData["test_set_5"]
        testData?.toCapitalized()
    }

    @Test
    fun test_generateString_length() {
        val testData = StringUtil.generateRandomString()
        assert(testData.length <= length_1)
    }

    @Test
    fun test_generateString_content() {
        val testData = StringUtil.generateRandomString()
        var checker = true
        testData.forEach {
            if (!randomChars.contains(it)) {
                checker = false
                return@forEach
            }
        }
        assertTrue(checker)
    }

    @Test
    fun test_generateString_abnormal_content() {
        mockkObject(StringUtil)
        every { StringUtil.generateRandomString() } returns StringBuilder("1234abcd")
        val testData = StringUtil.generateRandomString()
        var checker = true
        testData.forEach {
            if (!randomChars.contains(it)) {
                checker = false
                return@forEach
            }
        }
        assertFalse(checker)
        unmockkObject(StringUtil)
    }
}

data class TestData(
    val input: String,
    val expected: String
)
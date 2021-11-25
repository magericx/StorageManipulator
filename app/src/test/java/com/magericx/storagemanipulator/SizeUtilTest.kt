package com.magericx.storagemanipulator

import com.magericx.storagemanipulator.ui.internal_storage.UnitStatus
import com.magericx.storagemanipulator.utility.SizeUtil
import org.junit.Assert.*
import org.junit.Test

class SizeUtilTest {

    private companion object {
        private val decimalTestData by lazy {
            return@lazy mapOf(
                "test_set_1" to TestSizeData(input = 123456.0, expected = 123456.0),
                "test_set_2" to TestSizeData(input = 123456.5, expected = 123456.5),
                "test_set_3" to TestSizeData(input = 123456.1, expected = 123456.1),
                "test_set_4" to TestSizeData(input = 123456.15, expected = 123456.2),
                "test_set_5" to TestSizeData(input = 123456.10, expected = 123456.1)
            )
        }

        private val capacityToConversionTestData by lazy {
            return@lazy mapOf(
                "test_set_1" to TestCapacityToConversionData(
                    inputValue = 1073741824,
                    inputUnit = UnitStatus.GB,
                    expected = 1.0
                ),
                "test_set_2" to TestCapacityToConversionData(
                    inputValue = 1073741824,
                    inputUnit = UnitStatus.MB,
                    expected = 1024.0
                ),
                "test_set_3" to TestCapacityToConversionData(
                    inputValue = 1073741824,
                    inputUnit = UnitStatus.KB,
                    expected = 1048576.0
                ),
                "test_set_4" to TestCapacityToConversionData(
                    inputValue = 1073741824,
                    inputUnit = UnitStatus.B,
                    expected = 1073741824.0
                )
            )
        }

        private val getPercentageTestData by lazy {
            return@lazy mapOf(
                "test_set_1" to TestPercentageData(
                    inputFirstValue = 100,
                    inputSecondValue = 10000,
                    expectedPercent = 1.0
                ),
                "test_set_2" to TestPercentageData(
                    inputFirstValue = 1000,
                    inputSecondValue = 10000,
                    expectedPercent = 10.0
                ),
                "test_set_3" to TestPercentageData(
                    inputFirstValue = 10000,
                    inputSecondValue = 10000,
                    expectedPercent = 100.0
                ),
                "test_set_4" to TestPercentageData(
                    inputFirstValue = 10,
                    inputSecondValue = 10000,
                    expectedPercent = 0.1
                )
            )
        }

    }


    private fun Double.toGBUnit() = this.let {
        SizeUtil.roundTo1Decimal(this / 1073741824.toDouble())
    }

    private fun Double.toMBUnit() = this.let {
        SizeUtil.roundTo1Decimal(this / 1048576.toDouble())
    }

    private fun Double.toKBUnit() = this.let {
        SizeUtil.roundTo1Decimal(this / 1024.toDouble())
    }


    private fun TestSizeData.to1Decimal() = this.let {
        assertEquals(it.expected, SizeUtil.roundTo1Decimal(it.input), 0.0)
    }


    @Test
    fun test_get1Decimal_1() {
        val testData = decimalTestData["test_set_1"]
        testData?.to1Decimal()
    }

    @Test
    fun test_get1Decimal_2() {
        val testData = decimalTestData["test_set_2"]
        testData?.to1Decimal()
    }

    @Test
    fun test_get1Decimal_3() {
        val testData = decimalTestData["test_set_3"]
        testData?.to1Decimal()
    }

    @Test
    fun test_get1Decimal_4() {
        val testData = decimalTestData["test_set_4"]
        testData?.to1Decimal()
    }

    @Test
    fun test_get1Decimal_5() {
        val testData = decimalTestData["test_set_5"]
        testData?.to1Decimal()
    }

    @Test
    fun test_getCapacityWithConversionFromBytesToGigaBytes() {
        val testData = capacityToConversionTestData["test_set_1"]
        testData?.let {
            assertEquals(
                it.expected,
                SizeUtil.getCapacityWithConversionToUnit(
                    it.inputValue,
                    testData.inputUnit
                ), 0.0
            )
        }
    }

    @Test
    fun test_getCapacityWithConversionFromBytesToMegabytes() {
        val testData = capacityToConversionTestData["test_set_2"]
        testData?.let {
            assertEquals(
                it.expected,
                SizeUtil.getCapacityWithConversionToUnit(
                    it.inputValue,
                    testData.inputUnit
                ), 0.0
            )
        }
    }

    @Test
    fun test_getCapacityWithConversionFromBytesToKilobytes() {
        val testData = capacityToConversionTestData["test_set_3"]
        testData?.let {
            assertEquals(
                it.expected,
                SizeUtil.getCapacityWithConversionToUnit(
                    it.inputValue,
                    testData.inputUnit
                ), 0.0
            )
        }
    }

    @Test
    fun test_getCapacityWithConversionFromBytesToBytes() {
        val testData = capacityToConversionTestData["test_set_4"]
        testData?.let {
            assertEquals(
                it.expected,
                SizeUtil.getCapacityWithConversionToUnit(
                    it.inputValue,
                    testData.inputUnit
                ), 0.0
            )
        }
    }

    @Test
    fun test_getCapacityToBytesFromGigabytesToBytes() {
        val testData = capacityToConversionTestData["test_set_1"]
        testData?.let {
            assertEquals(
                it.inputValue,
                SizeUtil.getCapacityToBytes(
                    it.expected,
                    testData.inputUnit
                )
            )
        }
    }

    @Test
    fun test_getCapacityToBytesFromMegabytesToBytes() {
        val testData = capacityToConversionTestData["test_set_2"]
        testData?.let {
            assertEquals(
                it.inputValue,
                SizeUtil.getCapacityToBytes(
                    it.expected,
                    testData.inputUnit
                )
            )
        }
    }

    @Test
    fun test_getCapacityToBytesFromKilobytesToBytes() {
        val testData = capacityToConversionTestData["test_set_3"]
        testData?.let {
            assertEquals(
                it.inputValue,
                SizeUtil.getCapacityToBytes(
                    it.expected,
                    testData.inputUnit
                )
            )
        }
    }

    @Test
    fun test_getCapacityToBytesFromBytesToBytes() {
        val testData = capacityToConversionTestData["test_set_4"]
        testData?.let {
            assertEquals(
                it.inputValue,
                SizeUtil.getCapacityToBytes(
                    it.expected,
                    testData.inputUnit
                )
            )
        }
    }

    @Test
    fun test_getPercentage_1() {
        val testData = getPercentageTestData["test_set_1"]
        testData?.let {
            assertEquals(
                it.expectedPercent,
                SizeUtil.getPercentage(
                    firstValue = it.inputFirstValue,
                    secondValue = it.inputSecondValue
                ), 0.0
            )
        }
    }

    @Test
    fun test_getPercentage_2() {
        val testData = getPercentageTestData["test_set_2"]
        testData?.let {
            assertEquals(
                it.expectedPercent,
                SizeUtil.getPercentage(
                    firstValue = it.inputFirstValue,
                    secondValue = it.inputSecondValue
                ), 0.0
            )
        }
    }

    @Test
    fun test_getPercentage_3() {
        val testData = getPercentageTestData["test_set_3"]
        testData?.let {
            assertEquals(
                it.expectedPercent,
                SizeUtil.getPercentage(
                    firstValue = it.inputFirstValue,
                    secondValue = it.inputSecondValue
                ), 0.0
            )
        }
    }

    @Test
    fun test_getPercentage_4() {
        val testData = getPercentageTestData["test_set_4"]
        testData?.let {
            assertEquals(
                it.expectedPercent,
                SizeUtil.getPercentage(
                    firstValue = it.inputFirstValue,
                    secondValue = it.inputSecondValue
                ), 0.0
            )
        }
    }

}

data class TestSizeData(
    val input: Double,
    val expected: Double
)

data class TestCapacityToConversionData(
    val inputValue: Long,
    val inputUnit: UnitStatus,
    val expected: Double
)

data class TestPercentageData(
    val inputFirstValue: Long,
    val inputSecondValue: Long,
    val expectedPercent: Double
)
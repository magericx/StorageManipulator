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
                    inputValue = 1073741824.0,
                    inputUnit = UnitStatus.GB,
                    expected = 1.0
                )
            )
        }
    }


    private fun Double.toGBUnit() = this.let {
        SizeUtil.roundTo1Decimal(this / 1073741824.toDouble())
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
    fun test_getCapacityWithConversionToUnit_1() {
        val testData = capacityToConversionTestData["test_set_1"]
        testData?.inputValue?.toGBUnit()?.let { assertEquals(testData.expected, it, 0.0) }
    }

}

data class TestSizeData(
    val input: Double,
    val expected: Double
)

data class TestCapacityToConversionData(
    val inputValue: Double,
    val inputUnit: UnitStatus,
    val expected: Double
)
package com.magericx.storagemanipulator

import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

class FileIoUtilTest {


    val internalStorage = "storage_manipulator_files"
    lateinit var rootFileDirectory: File

    @Before
    fun setup() {
       // rootFileDirectory = File(StorageManipulatorApplication.instance.filesDir, internalStorage)
    }

    @Test
    fun test_delete_files_1() {
        assertEquals(1,1)
    }
}
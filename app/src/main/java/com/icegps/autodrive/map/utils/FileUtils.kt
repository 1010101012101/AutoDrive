package com.icegps.autodrive.map.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.*

object FileUtils {
    private var baseDir: File? = null
    private var tileArrayDir: File? = null
    private var tileBitmapDir: File? = null

    /**
     * 将方块数组保存到sd卡
     */
    fun saveTile2SdCard(tileArray: Array<ByteArray>, tileName: String) {
        var fos: FileOutputStream? = null
        var oos: ObjectOutputStream? = null
        var file = File(tileArrayDir, tileName)
        try {
            fos = FileOutputStream(file)
            oos = ObjectOutputStream(fos)
            oos!!.writeObject(tileArray) //写入
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (oos != null) oos!!.close()
                if (fos != null) fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * 从sd卡中获取方块数组
     */
    fun getTileBySdCard(tileName: String): Array<ByteArray>? {
        var fileInputStream: FileInputStream? = null
        var objectInputStream: ObjectInputStream? = null
        var file = File(tileArrayDir, tileName)
        if (!file.exists()) return null
        try {
            fileInputStream = FileInputStream(file)
            objectInputStream = ObjectInputStream(fileInputStream)
            val tileArray = objectInputStream.readObject() as Array<ByteArray>
            return tileArray
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: ClassCastException) {
            return null
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close()
                if (objectInputStream != null) objectInputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return null
    }


    /**
     * 把Bitmap保存到sd卡
     */
    fun saveBitmap2SdCard(bitmapName: String, bitmap: Bitmap): Boolean {
        var out: BufferedOutputStream? = null
        var file: File? = null
        try {
            file = File(tileBitmapDir, bitmapName)
            out = BufferedOutputStream(FileOutputStream(file!!))
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            return true
        } catch (e: FileNotFoundException) {
            file!!.delete()
            return false
        } catch (e: IOException) {
            file!!.delete()
            return false
        }

    }

    /**
     * 从内存卡中获取bitmap
     */
    fun getBitmapBySdCard(bitmapName: String): Bitmap? {
        var file: File? = null
        file = File(tileBitmapDir, bitmapName)
        try {
            if (file!!.exists()) {
                return BitmapFactory.decodeFile(file!!.path).copy(Bitmap.Config.ARGB_8888, true)
            }
        } catch (e: Exception) {
            file?.delete()
            return null
        }
        return null
    }

    /**
     * 获取存储根目录
     */
    private fun getSdCradPath(): File {
        val externalStorageDirectory = Environment.getExternalStorageDirectory()
        return externalStorageDirectory
    }

    /**
     * 用测量时间作为存储文件的文件夹主名称
     */
    fun setDir(measuredTime: Long): FileUtils {

        val sdCradPath = getSdCradPath()

        baseDir = File(sdCradPath, "ICE_AUTODRIVE/" + measuredTime.toString())

        tileArrayDir = File(baseDir, "tileArrayDir")

        tileBitmapDir = File(baseDir, "tileBitmapDir")

        if (!tileArrayDir!!.exists()) {
            tileArrayDir!!.mkdirs()
        }

        if (!tileBitmapDir!!.exists()) {
            tileBitmapDir!!.mkdirs()
        }
        return FileUtils
    }

    /**
     * 删除方块数据(图片+原始数据)
     *
     * @param measuredTime
     * @return
     */
    fun delete(): Boolean {
        delete(baseDir!!)
        return if (baseDir!!.listFiles() == null || baseDir!!.listFiles().size == 0) {
            true
        } else false
    }

    /**
     * 递归删除
     *
     * @param dir
     */
    private fun delete(dir: File) {
        if (dir.isDirectory) {
            val files = dir.listFiles()
            for (file in files) {
                delete(file)
                file.delete()
            }
            dir.delete()
        } else if (dir.isFile) {
            dir.delete()
        }
    }

}
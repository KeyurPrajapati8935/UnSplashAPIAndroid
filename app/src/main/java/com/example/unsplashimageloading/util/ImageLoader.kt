package com.example.unsplashimageloading.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.collection.LruCache
import com.jakewharton.disklrucache.DiskLruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.math.BigInteger
import java.net.URL
import java.security.MessageDigest

class ImageLoader(
    private val applicationContext: Context,
) {

    private val memoryCache: LruCache<String, Bitmap> =
        object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 1024).toInt() / 8) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }

    private val diskCache: DiskLruCache? by lazy {
        initializeDiskCache()
    }

    private fun initializeDiskCache(): DiskLruCache? {
        return try {
            val cacheDir = applicationContext.cacheDir
            val diskCacheSize = 10 * 1024 * 1024 // 10 MB
            DiskLruCache.open(cacheDir, 1, 1, diskCacheSize.toLong())
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    suspend fun loadImage(url: String): Bitmap? {
        val cached = memoryCache.get(url)
        if (cached != null) {
            return cached
        }
        val diskBitmap = loadFromDiskCache(url)
        if (diskBitmap != null) {
            memoryCache.put(url, diskBitmap)
            return diskBitmap
        }
        return try {
            val bitmap = withContext(Dispatchers.IO) {
                val inputStream: InputStream = URL(url).openStream()
                BitmapFactory.decodeStream(inputStream)
            }
            if (bitmap != null) {
                memoryCache.put(url, bitmap)
                storeInDiskCache(url, bitmap)
            }
            bitmap
        } catch (e: Exception) {
            println("Error downloading image: $url - ${e.message}")
            null
        }
    }

    private suspend fun loadFromDiskCache(url: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val key = generateKeyFromUrl(url)
            val snapshot = diskCache?.get(key)
            snapshot?.let {
                it.getInputStream(0).use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            }
        }
    }

    private fun generateKeyFromUrl(url: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val bytes = digest.digest(url.toByteArray())
        val bigInt = BigInteger(1, bytes)
        var result = bigInt.toString(16)
        while (result.length < 32) {
            result = "0$result"
        }
        return result
    }

    private fun storeInDiskCache(url: String, bitmap: Bitmap) {
        diskCache?.apply {
            val key = generateKeyFromUrl(url)
            edit(key)?.apply {
                try {
                    newOutputStream(0).use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                    }
                    commit()
                } catch (e: IOException) {
                    abort()
                    e.printStackTrace()
                }
            }
        }
    }
}



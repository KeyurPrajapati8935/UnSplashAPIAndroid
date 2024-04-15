package com.example.unsplashimageloading.ui.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.LruCache
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.unsplashimageloading.R
import com.example.unsplashimageloading.data.model.Image
import com.example.unsplashimageloading.util.ImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class ImageGridAdapter(private val activity: AppCompatActivity) :
    RecyclerView.Adapter<ImageGridAdapter.ImageViewHolder>() {

    private val images = arrayListOf<Image>()

    private val memoryCache: LruCache<String, Bitmap> =
        object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 1024).toInt() / 8) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }

    private val imageLoader by lazy {
        ImageLoader(activity.applicationContext)
    }

    fun setData(list: List<Image>) {
        images.addAll(list)
        notifyDataSetChanged()
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val errorText: TextView? = itemView.findViewById(R.id.error_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        image.url?.regular?.let { url ->
            loadAsyncImage(url, holder)
        }
    }

    private fun loadAsyncImage(url: String, holder: ImageViewHolder) {
        val cached = memoryCache.get(url)
        if (cached != null) {
            holder.imageView.setImageBitmap(cached)
        } else {
            activity.lifecycleScope.launch {
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        imageLoader.loadImage(url)
                    }
                    if (bitmap != null) {
                        memoryCache.put(url, bitmap)
                    }
                    holder.imageView.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    holder.errorText?.visibility = View.VISIBLE
                    e.printStackTrace()
                }
            }
        }

    }

    override fun getItemCount(): Int = images.size
}

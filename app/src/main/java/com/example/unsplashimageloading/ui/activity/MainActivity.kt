package com.example.unsplashimageloading.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unsplashimageloading.R
import com.example.unsplashimageloading.ui.adapter.ImageGridAdapter
import com.example.unsplashimageloading.ui.viewmodel.ImageViewModel
import com.example.unsplashimageloading.ui.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ImageViewModel
    private lateinit var adapter: ImageGridAdapter

    private var currentPage = 1
    private val PER_PAGE = 20

    private lateinit var layoutManager: GridLayoutManager
    private var isLoading = false

    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        setUpAdapter()

        setViewModelFactory()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    loadMoreItems()
                }
            }
        })

        viewModelObservers()

        loadData()
    }

    private fun setViewModelFactory() {
        val viewModelFactory = ViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory)[ImageViewModel::class.java]
    }

    private fun setUpAdapter() {
        adapter = ImageGridAdapter(this)
        layoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun initView() {
        progressBar = findViewById(R.id.progress_bar)
        recyclerView = findViewById(R.id.image_grid)
    }

    private fun viewModelObservers() {
        viewModel.images.observe(this) { list ->
            adapter.setData(list)
            isLoading = false
            progressBar.visibility = View.GONE
        }
    }

    private fun loadMoreItems() {
        currentPage++
        loadData()
    }

    private fun loadData() {
        isLoading = true
        progressBar.visibility = View.VISIBLE
        viewModel.loadImages(currentPage, PER_PAGE)
    }
}






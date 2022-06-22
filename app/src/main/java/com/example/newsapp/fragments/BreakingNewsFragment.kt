package com.example.newsapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.MainActivity
import com.example.newsapp.R
import com.example.newsapp.adaptere.ArticleAdapter
import com.example.newsapp.utils.Resource
import com.example.newsapp.utils.shareNews
import com.example.newsapp.viewModel.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlin.random.Random


class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    lateinit var viewModel : NewsViewModel
    lateinit var newsAdapter: ArticleAdapter
    val  TAG = "BreakingNewsfragment"
    private fun setupRecyclerView() {
        newsAdapter = ArticleAdapter()
        rvbreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        newsAdapter.setOnItemCLickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }
        newsAdapter.onSaveClickListener {
            if (it.id == null){
                it.id = Random.nextInt(0, 1000);
            }
            viewModel.insertArticle(it)
            Snackbar.make(requireView(),"Saved", Snackbar.LENGTH_SHORT).show()
        }
        newsAdapter.onDeleteClickListener {
            viewModel.deleteArticle(it)
            Snackbar.make(requireView(),"Removed", Snackbar.LENGTH_SHORT).show()
        }
        newsAdapter.onShareNewsClick {
            shareNews(context, it)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()
        setViewModelObserver()
    }
    private fun setViewModelObserver() {
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { newsResponse ->
            when (newsResponse) {
                is Resource.Success -> {
                    shimmerFrameLayout.stopShimmerAnimation()
                    shimmerFrameLayout.visibility = View.GONE
                    newsResponse.data?.let { news ->
                        rvbreakingNews.visibility = View.VISIBLE
                        newsAdapter.differ.submitList(news.articles)
                    }
                }
                is Resource.Error -> {
                    shimmerFrameLayout.visibility = View.GONE
                    newsResponse.message?.let { message ->
                        Log.e(TAG,"Error :: $message")
                    }
                }
                is Resource.Loading -> {
                    shimmerFrameLayout.startShimmerAnimation()
                }
            }
        })
    }

}
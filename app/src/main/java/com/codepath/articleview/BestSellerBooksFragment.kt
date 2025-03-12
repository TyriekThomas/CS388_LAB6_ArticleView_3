package com.codepath.articleview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.gson.Gson
import okhttp3.Headers
import org.json.JSONArray
import org.json.JSONObject

private val API_KEY2 = BuildConfig.API_KEY2
private const val TAG = "BestSellerBooksFragment"
private const val API_KEY = "Removed APIKEY from History"

class BestSellerBooksFragment : Fragment(), OnListFragmentInteractionListener {

    private lateinit var bestSellerRecyclerView: RecyclerView
    private lateinit var bestSellerAdapter: BestSellerBooksRecyclerViewAdapter
    private val bestSellers = mutableListOf<BestSellerBook>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_best_seller_books_list, container, false)

        // Set up RecyclerView
        val progressBar = view.findViewById<ContentLoadingProgressBar>(R.id.progress)
        bestSellerRecyclerView = view.findViewById(R.id.list)
        bestSellerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        bestSellerAdapter = BestSellerBooksRecyclerViewAdapter(requireContext(), bestSellers, this)
        bestSellerRecyclerView.adapter = bestSellerAdapter

        // Fetch best sellers
        fetchBestSellers(progressBar)

        return view
    }

    private fun fetchBestSellers(progressBar: ContentLoadingProgressBar) {
        progressBar.show()

        val client = AsyncHttpClient()
        val params = RequestParams()
        params["api-key"] = API_KEY2

        client.get(
            "https://api.nytimes.com/svc/books/v3/lists/current/hardcover-fiction.json?",
            params,
            object : JsonHttpResponseHandler() {
                override fun onSuccess(
                    statusCode: Int,
                    headers: Headers,
                    json: JsonHttpResponseHandler.JSON
                ) {
                    progressBar.hide()

//                    val resultsJSON: JSONArray = json.jsonObject.getJSONArray("results")
                    val resultsJSON: JSONObject = json.jsonObject.getJSONObject("results")
                    val Books = mutableListOf<BestSellerBook>()


                     val booksJSON: String = resultsJSON.get("books").toString()
                     val booksGSON = Gson().fromJson(booksJSON, Array<BestSellerBook>::class.java)
                    bestSellers.clear()
                    bestSellers.addAll(booksGSON)
                    bestSellerAdapter.notifyDataSetChanged()

                    Log.d(TAG, "response successful")
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    errorResponse: String,
                    t: Throwable?
                ) {
                    progressBar.hide()
                    Log.e(TAG, "Failed to fetch best sellers: $errorResponse")
                }
            }
        )
    }

    override fun onItemClick(item: BestSellerBook) {
        Toast.makeText(context, "Clicked Cell: ${item.title}", Toast.LENGTH_LONG).show()
    }

    override fun onListFragmentInteraction(item: BestSellerBook) {
        Toast.makeText(context, "Interacted with: ${item.title}", Toast.LENGTH_LONG).show()
    }
}

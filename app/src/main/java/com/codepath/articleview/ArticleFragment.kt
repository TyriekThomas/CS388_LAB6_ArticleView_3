package com.codepath.articleview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.Headers
import org.json.JSONException

private const val TAG = "ArticleFragment"
private const val ARTICLE_SEARCH_URL =
    "https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=${BuildConfig.API_KEY}"

class ArticleFragment : Fragment() {

    private lateinit var articlesRecyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private val articles = mutableListOf<Article>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the correct layout file
        val view = inflater.inflate(R.layout.fragment_article, container, false)

        // Set up RecyclerView
        articlesRecyclerView = view.findViewById(R.id.articles)
        articleAdapter = ArticleAdapter(requireContext(), articles)
        articlesRecyclerView.adapter = articleAdapter
        articlesRecyclerView.layoutManager = LinearLayoutManager(requireContext()).also {
            val dividerItemDecoration = DividerItemDecoration(requireContext(), it.orientation)
            articlesRecyclerView.addItemDecoration(dividerItemDecoration)
        }

        // Fetch articles
        fetchArticles()

        return view
    }

    private fun fetchArticles() {
        val client = AsyncHttpClient()
        client.get(ARTICLE_SEARCH_URL, object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "Failed to fetch articles: $statusCode")
            }

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG, "Successfully fetched articles: $json")
                try {
                    val parsedJSON = Json.parseToJsonElement(json.jsonObject.toString()).jsonObject
                    val response = parsedJSON["response"]?.jsonObject
                    val docs = response?.get("docs")

                    if (docs is kotlinx.serialization.json.JsonArray) {
                        for (doc in docs) {
                            val articleJson = doc.jsonObject
                            val multimediaElement = articleJson["multimedia"]

                            val smallImageUrl = if (multimediaElement is kotlinx.serialization.json.JsonArray) {
                                multimediaElement.find {
                                    it.jsonObject["subtype"]?.toString()?.removeSurrounding("\"") == "xlarge"
                                }?.jsonObject?.get("url")?.toString()?.removeSurrounding("\"")?.let { url ->
                                    if (url.startsWith("http")) url else "https://www.nytimes.com/$url"
                                }
                            } else if (multimediaElement is kotlinx.serialization.json.JsonObject) {
                                multimediaElement["default"]?.jsonObject?.get("url")?.toString()?.removeSurrounding("\"")?.let { url ->
                                    if (url.startsWith("http")) url else "https://www.nytimes.com/$url"
                                }
                            } else {
                                null
                            }

                            val largeImageUrl = if (multimediaElement is kotlinx.serialization.json.JsonArray) {
                                multimediaElement.find {
                                    it.jsonObject["subtype"]?.toString()?.removeSurrounding("\"") == "xlarge"
                                }?.jsonObject?.get("url")?.toString()?.removeSurrounding("\"")?.let { url ->
                                    if (url.startsWith("http")) url else "https://www.nytimes.com/$url"
                                }
                            } else if (multimediaElement is kotlinx.serialization.json.JsonObject) {
                                multimediaElement["default"]?.jsonObject?.get("url")?.toString()?.removeSurrounding("\"")?.let { url ->
                                    if (url.startsWith("http")) url else "https://www.nytimes.com/$url"
                                }
                            } else {
                                null
                            }

                            val bylineOriginal = articleJson["byline"]?.jsonObject?.get("original")?.toString()?.removeSurrounding("\"")

                            val article = Article(
                                headline = articleJson["headline"]?.jsonObject?.get("main")?.toString()?.removeSurrounding("\""),
                                abstract = articleJson["abstract"]?.toString()?.removeSurrounding("\""),
                                byline = bylineOriginal,
                                pubDate = articleJson["pub_date"]?.toString()?.removeSurrounding("\""),
                                newsDesk = articleJson["news_desk"]?.toString()?.removeSurrounding("\""),
                                sectionName = articleJson["section_name"]?.toString()?.removeSurrounding("\""),
                                snippet = articleJson["snippet"]?.toString()?.removeSurrounding("\""),
                                leadParagraph = articleJson["lead_paragraph"]?.toString()?.removeSurrounding("\""),
                                smallImageUrl = smallImageUrl,
                                largeImageUrl = largeImageUrl
                            )
                            articles.add(article)
                        }
                        articleAdapter.notifyDataSetChanged()
                    } else {
                        Log.e(TAG, "Expected 'docs' to be a JsonArray but found ${docs?.javaClass?.simpleName}")
                    }
                } catch (e: JSONException) {
                    Log.e(TAG, "JSONException: ${e.localizedMessage}")
                } catch (e: Exception) {
                    Log.e(TAG, "Unexpected exception: ${e.localizedMessage}")
                }
            }

        })

    }
}
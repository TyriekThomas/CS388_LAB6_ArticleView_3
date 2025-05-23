package com.codepath.articleview

import com.google.gson.annotations.SerializedName

/**
 * The Model for storing a single book from the NY Times API
 *
 * SerializedName tags MUST match the JSON response for the
 * object to correctly parse with the gson library.
 */
class BestSellerBook {
    @SerializedName("rank")
    var rank = 0

    @JvmField
    @SerializedName("title")
    var title: String? = null

    @JvmField
    @SerializedName("author")
    var author: String? = null

    //TODO bookImageUrl
    @JvmField
    @SerializedName("book_image")
    var bookImageUrl: String? = null

    //TODO description
    @JvmField
    @SerializedName("description")
    var description: String? = null

    //TODO-STRETCH-GOALS amazonUrl
    @JvmField
    @SerializedName("amazon_product_url")
    var amazonProductUrl: String? = null

}
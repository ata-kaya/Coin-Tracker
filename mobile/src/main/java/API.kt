import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by omidzamani on 15.07.2018.
 */

class API private constructor() {
    private val client: OkHttpClient

    init {

        client = OkHttpClient()
    }

    fun run(url: String, callback: Callback) {
        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(callback)
    }

    companion object {


        private var api: API? = null

        internal val instance: API
            get() {
                if (api == null)
                    api = API()
                return api as API
            }
    }


}

import android.net.ConnectivityManager
import android.net.NetworkCapabilities


object NetworkUtils {

    fun isConnected(connectivityManager: ConnectivityManager): Boolean {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasAnyTransport() == true
    }

    private fun NetworkCapabilities.hasAnyTransport(): Boolean {
        return hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}
// ВАЛЕРА НАСТАЛО ТВОЁ ВРЕМЯ, ТАК И ЗНАЛ ОНО ПРИДЁТ !!!
package com.atakan.mainclient.presentation.currency.screen.broadcast

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import com.atakan.mainclient.service.BroadcastService
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.atakan.mainclient.common.Resource
import com.atakan.mainclient.presentation.currency.CurrencyViewModel
import com.atakan.mainclient.presentation.currency.screen.ServiceViewModel


@Composable
fun BroadcastService(context: Context) {
    DisposableEffect(Unit) {
        // Start the foreground service when the Composable is first activated
        val intent = Intent(context, BroadcastService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, intent)
        } else {
            context.startService(intent)
        }
        onDispose {
            // You can stop the service if you do not need to execute background
        }
    }
}

@Composable
fun BroadcastScreen(context: Context, viewModel: CurrencyViewModel = hiltViewModel(), clickViewModel: ServiceViewModel = hiltViewModel()) {
    val currencyState by viewModel.currencyLiveData.observeAsState()
    val buttonState by clickViewModel.isServiceConnected.observeAsState()

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize()) {

        BroadcastService(context = context)

        when (currencyState) {
            is Resource.Success -> {
                val data = (currencyState as Resource.Success).data
                Text(text = data?.chartName ?: "null")
            }
            is Resource.Loading -> {
                CircularProgressIndicator()
            }
            is Resource.Error -> {
                Log.e("Broadcast Screen", "Something bad happened")
                Text(
                    text = "Something bad happened",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }
            else -> {
                Text(
                    text = "??",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Button(onClick = {
            clickViewModel.toggleServiceConnection()
        }) {
            Text(text = if (buttonState!!) "Disconnect" else "Connect")

        }
    }
}

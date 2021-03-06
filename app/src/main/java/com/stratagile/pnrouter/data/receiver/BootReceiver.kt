package com.stratagile.pnrouter.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.stratagile.pnrouter.data.service.MessageRetrievalService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null && Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val messageRetrievalService = Intent(context, MessageRetrievalService::class.java)
            try {
                context.startService(messageRetrievalService)
            }catch (e:Exception)
            {
                e.printStackTrace()
                try {
                    context.startForegroundService(messageRetrievalService);
                }catch (e:Exception)
                {
                    e.printStackTrace()
                }

            }

        }
    }
}

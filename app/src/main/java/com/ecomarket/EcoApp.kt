package com.ecomarket

import android.app.Application
import com.ecomarket.di.Graph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EcoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)

        // Semilla de productos demo
        CoroutineScope(Dispatchers.IO).launch {
            Graph.repository.ensureSeed()
        }
    }
}

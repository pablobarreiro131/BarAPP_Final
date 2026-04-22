package org.pabarreiro.barapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.pabarreiro.barapp.data.local.BarDatabase
import org.pabarreiro.barapp.data.local.DatabaseBuilder
import org.pabarreiro.barapp.di.initKoin

class BarApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        initKoin {
            androidContext(this@BarApp)
            modules(module {
                single<BarDatabase> { 
                    DatabaseBuilder(this@BarApp).getDatabaseBuilder().build() 
                }
            })
        }
    }
}

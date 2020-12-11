package br.com.alura.alurasquare

import android.app.Application
import br.com.alura.alurasquare.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AlurasquareApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@AlurasquareApplication)
            modules(appModules)
        }
    }

}
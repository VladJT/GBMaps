package jt.projects.gbmaps.di

import jt.projects.gbmaps.App
import jt.projects.gbmaps.core.MyGeocoder
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import java.util.Locale

// зависимости, используемые во всём приложении
val appModule = module {

    // контекст приложения
    single<App> { androidApplication().applicationContext as App }

    single { MyGeocoder(context = get(), locale = Locale("ru_RU")) }
}
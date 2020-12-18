package br.com.alura.alurasquare.di

import br.com.alura.alurasquare.repository.PostRepository
import br.com.alura.alurasquare.ui.viewmodel.EstadoAppViewModel
import br.com.alura.alurasquare.ui.viewmodel.FormularioPostViewModel
import br.com.alura.alurasquare.ui.viewmodel.ListaPostsViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModelModule = module {
    viewModel<FormularioPostViewModel> { FormularioPostViewModel(get()) }
    viewModel<ListaPostsViewModel> { ListaPostsViewModel(get()) }
    viewModel<EstadoAppViewModel> { EstadoAppViewModel() }
}

val repositoryModule = module {
    single<PostRepository> { PostRepository(get(), get()) }
}

val firebaseModule = module {
    single<FirebaseFirestore> { Firebase.firestore }
    single<FirebaseStorage> {Firebase.storage}
}

val appModules: List<Module> = listOf(
    viewModelModule,
    repositoryModule,
    firebaseModule
)
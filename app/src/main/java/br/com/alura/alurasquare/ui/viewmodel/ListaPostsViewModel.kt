package br.com.alura.alurasquare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import br.com.alura.alurasquare.repository.PostRepository

class ListaPostsViewModel(private val repository: PostRepository) : ViewModel() {

    fun buscaTodos() = repository.buscaTodos().asLiveData()

}
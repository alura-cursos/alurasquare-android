package br.com.alura.alurasquare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import br.com.alura.alurasquare.model.Post
import br.com.alura.alurasquare.repository.PostRepository
import br.com.alura.alurasquare.repository.Resultado

class FormularioPostViewModel(
    private val repository: PostRepository
) : ViewModel() {

    fun buscaPost(id: String) = repository.buscaPorId(id).asLiveData()

    fun salva(post: Post) =
        liveData<Resultado<Unit>>(viewModelScope.coroutineContext) {
            try {
                repository.salva(post)
                emit(Resultado.Sucesso())
            } catch (e: Exception) {
                emit(Resultado.Erro(e))
            }
        }

    fun remove(postId: String) =
        liveData<Resultado<Unit>>(viewModelScope.coroutineContext) {
            try {
                repository.remove(postId)
                emit(Resultado.Sucesso())
            } catch (e: Exception) {
                emit(Resultado.Erro(e))
            }
        }

    fun edita(post: Post) =
        liveData<Resultado<Unit>>(viewModelScope.coroutineContext) {
            try {
                repository.edita(post)
                emit(Resultado.Sucesso())
            } catch (e: Exception) {
                emit(Resultado.Erro(e))
            }
        }

}
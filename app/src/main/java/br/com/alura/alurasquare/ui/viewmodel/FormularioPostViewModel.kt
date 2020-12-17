package br.com.alura.alurasquare.ui.viewmodel

import android.util.Log
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

    fun salva(post: Post, imagem: ByteArray) =
        liveData<Resultado<Unit>>(viewModelScope.coroutineContext) {
            try {
                val id = repository.salva(post)
                emit(Resultado.Sucesso())
                repository.enviaImagem(id, imagem)
            } catch (e: Exception) {
                Log.e("FormPostVM", "salva: falha ao enviar post", e)
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

    fun edita(post: Post, imagem: ByteArray) =
        liveData<Resultado<Unit>>(viewModelScope.coroutineContext) {
            try {
                repository.edita(post)
                emit(Resultado.Sucesso())
                post.id?.let { postId ->
                    repository.enviaImagem(postId, imagem)
                }
            } catch (e: Exception) {
                emit(Resultado.Erro(e))
            }
        }

}
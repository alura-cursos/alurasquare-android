package br.com.alura.alurasquare.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import br.com.alura.alurasquare.model.Post
import br.com.alura.alurasquare.repository.PostRepository
import br.com.alura.alurasquare.repository.Resultado

class FormularioPostViewModel(
    private val repository: PostRepository
) : ViewModel() {

    private val _imagemCarregada = MutableLiveData<String?>()
    val imagemCarregada: LiveData<String?> = _imagemCarregada

    fun buscaPost(id: String) = repository.buscaPorId(id).asLiveData()

    fun salva(post: Post, imagem: ByteArray) =
        liveData<Resultado<Unit>>(viewModelScope.coroutineContext) {
            try {
                val id = repository.salva(post)
                emit(Resultado.Sucesso())
                tentaEnviarImagem(id, imagem)
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
                repository.removeImagem(postId)
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
                    tentaEnviarImagem(postId, imagem)
                }
            } catch (e: Exception) {
                emit(Resultado.Erro(e))
            }
        }

    private suspend fun tentaEnviarImagem(postId: String, imagem: ByteArray) {
        imagemCarregada.value?.let {
            repository.enviaImagem(postId, imagem)
        }
    }

    fun atualizaImagem(imagem: String) {
        _imagemCarregada.postValue(imagem)
    }

    fun removeImagem() {
        _imagemCarregada.postValue(null)
    }

}
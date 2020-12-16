package br.com.alura.alurasquare.repository

import br.com.alura.alurasquare.model.Post
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val NOME_COLECACAO = "posts"

class PostRepository(
    private val firestore: FirebaseFirestore
) {

    suspend fun salva(post: Post): String {
        val documento = firestore.collection(NOME_COLECACAO)
            .document()
        documento
            .set(DocumentoPost(post))
            .await()
        return documento.id
    }

    suspend fun enviaImagem(postId: String, imagem: ByteArray){
        GlobalScope.launch {
            firestore.collection(NOME_COLECACAO)
                .document(postId)
                .update(mapOf("temImagem" to true))
                .await()

            val storage = Firebase.storage
            val referencia = storage.reference.child("posts/$postId.jpg")
            referencia.putBytes(imagem).await()
            val url = referencia.downloadUrl.await()

            firestore.collection(NOME_COLECACAO)
                .document(postId)
                .update(mapOf("imagem" to url.toString()))
                .await()
        }
    }

    suspend fun edita(post: Post) {
        val postId =
            post.id ?: throw IllegalArgumentException("Id não pode ser nulo ao editar um post")
        firestore.collection(NOME_COLECACAO)
            .document(postId)
            .set(DocumentoPost(post))
            .await()
    }

    fun buscaTodos() = callbackFlow<Resultado<List<Post>>> {
        val listener = firestore.collection(NOME_COLECACAO)
            .addSnapshotListener { query, erro ->
                erro?.let {
                    offer(Resultado.Erro(erro))
                }
                val posts = query?.documents?.mapNotNull { documento ->
                    documento.paraPost()
                } ?: return@addSnapshotListener
                offer(Resultado.Sucesso(posts))
            }
        awaitClose { listener.remove() }
    }

    fun buscaPorId(id: String) = callbackFlow {
        val listener = firestore.collection(NOME_COLECACAO)
            .document(id)
            .addSnapshotListener { documento, _ ->
                offer(documento?.paraPost())
            }
        awaitClose { listener.remove() }
    }

    suspend fun remove(id: String) {
        firestore.collection(NOME_COLECACAO)
            .document(id)
            .delete().await()
    }

    private fun DocumentSnapshot.paraPost(): Post? {
        return this.toObject(DocumentoPost::class.java)?.paraPost(this.id)
    }

}

private class DocumentoPost(
    val local: String = "",
    val mensagem: String = "",
    val avaliacao: Float = 0.0f,
    val imagem: String? = null,
    val temImagem: Boolean = false
) {

    constructor(post: Post) : this(
        local = post.local,
        mensagem = post.mensagem,
        avaliacao = post.avaliacao,
        imagem = post.imagem,
        temImagem = post.temImagem
    )

    fun paraPost(id: String? = null) = Post(
        id = id,
        local = local,
        mensagem = mensagem,
        avaliacao = avaliacao,
        imagem = imagem,
        temImagem = temImagem
    )

}

sealed class Resultado<out R> {
    data class Sucesso<T>(val dado: T? = null) : Resultado<T>()
    data class Erro(val exception: Exception) : Resultado<Nothing>()
}
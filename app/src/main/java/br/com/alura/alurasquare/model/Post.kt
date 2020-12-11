package br.com.alura.alurasquare.model

data class Post(
    val id: String? = null,
    val local: String,
    val mensagem: String,
    val avaliacao: Float = 0.0f
)

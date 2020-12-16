package br.com.alura.alurasquare.ui.recyclerview.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.alura.alurasquare.R
import br.com.alura.alurasquare.databinding.ItemPostBinding
import br.com.alura.alurasquare.model.Post
import coil.load

class ListaPostsAdapter(
    private val context: Context,
    posts: List<Post> = listOf(),
    val quandoClicaNoItem: (id: String) -> Unit = {}
) : RecyclerView.Adapter<ListaPostsAdapter.ViewHolder>() {

    private val posts: MutableList<Post> = posts.toMutableList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(
        ItemPostBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        holder.vincula(post)
    }

    override fun getItemCount(): Int = posts.size

    fun atualiza(posts: List<Post>) {
        this.posts.clear()
        this.posts.addAll(posts)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var post: Post

        init {
            binding.root.setOnClickListener {
                if (::post.isInitialized) {
                    post.id?.let {
                        quandoClicaNoItem(it)
                    }
                }
            }
        }

        fun vincula(post: Post) {
            this.post = post
            binding.itemPostLocal.text = post.local
            binding.itemPostMensagem.text = post.mensagem
            binding.itemPostAvaliacao.rating = post.avaliacao

            val visibilidade = if(post.temImagem){
                binding.itemPostImagem.load(R.drawable.imagem_carregando_placeholder)
                VISIBLE
            } else {
                GONE
            }
            binding.itemPostImagem.visibility = visibilidade
            post.imagem?.let { imagem ->
                binding.itemPostImagem.load(imagem) {
                    placeholder(R.drawable.imagem_carregando_placeholder)
                    crossfade(true)
                }
            }

        }

    }

}

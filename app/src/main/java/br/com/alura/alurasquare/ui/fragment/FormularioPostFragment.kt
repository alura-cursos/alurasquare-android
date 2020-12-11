package br.com.alura.alurasquare.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.alura.alurasquare.R
import br.com.alura.alurasquare.databinding.FormularioPostBinding
import br.com.alura.alurasquare.extensions.snackbar
import br.com.alura.alurasquare.model.Post
import br.com.alura.alurasquare.repository.Resultado
import br.com.alura.alurasquare.ui.viewmodel.Componentes
import br.com.alura.alurasquare.ui.viewmodel.EstadoAppViewModel
import br.com.alura.alurasquare.ui.viewmodel.FormularioPostViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class FormularioPostFragment : Fragment() {

    private var _binding: FormularioPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FormularioPostViewModel by viewModel()
    private val controlador by lazy {
        findNavController()
    }
    private val estadoAppViewModel: EstadoAppViewModel by sharedViewModel()
    private val argumentos by navArgs<FormularioPostFragmentArgs>()
    private val postId: String? by lazy { argumentos.postId }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FormularioPostBinding.inflate(
        inflater,
        container,
        false
    ).let { binding ->
        this._binding = binding
        binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        estadoAppViewModel.setComponentes(
            Componentes(
                appBar = true
            )
        )
        tentaCarregarPost()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_formulario_post, menu)
        if (postId == null) {
            menu.findItem(R.id.menu_formulario_post_remove).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_formulario_post_enviar -> criaPost()
            R.id.menu_formulario_post_remove -> apresentaDialogoDeRemocao()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun criaPost() {
        val local = binding.formularioPostLocal.text.toString()
        val mensagem = binding.formularioPostMensagem.text.toString()
        val avaliacao = binding.formularioPostAvaliacao.rating
        val postNovo = Post(
            id = postId,
            local = local,
            mensagem = mensagem,
            avaliacao = avaliacao
        )
        enviaPost(postNovo)
    }

    private fun enviaPost(post: Post) {
        if (post.id != null) {
            edita(post)
        } else {
            salva(post)
        }
    }

    private fun edita(post: Post) {
        viewModel.edita(post).observe(viewLifecycleOwner) {
            it?.let { resultado ->
                when (resultado) {
                    is Resultado.Sucesso -> controlador.popBackStack()
                    is Resultado.Erro -> binding.formularioPostCoordinator
                        .snackbar(mensagem = "Post não foi editada")
                }
            }
        }
    }

    private fun salva(post: Post) {
        viewModel.salva(post).observe(viewLifecycleOwner) { resultado ->
            when (resultado) {
                is Resultado.Sucesso -> controlador.popBackStack()
                is Resultado.Erro -> binding.formularioPostCoordinator
                    .snackbar(mensagem = "Post não foi enviada")
            }
        }
    }

    private fun tentaCarregarPost() {
        postId?.let { id ->
            requireActivity().title = "Editar post"
            viewModel.buscaPost(id).observe(viewLifecycleOwner) { post ->
                post?.let(::preencheCampos)
            }
        }
    }

    private fun preencheCampos(post: Post) {
        binding.formularioPostLocal.setText(post.local)
        binding.formularioPostMensagem.setText(post.mensagem)
        binding.formularioPostAvaliacao.rating = post.avaliacao
    }

    private fun apresentaDialogoDeRemocao() {
        AlertDialog.Builder(requireContext())
            .setTitle("Removendo Post")
            .setMessage("Você quer remover esse post?")
            .setPositiveButton("Sim") { _, _ ->
                postId?.let(this::remove)
            }
            .setNegativeButton("Não")
            { _, _ -> }
            .show()
    }

    private fun remove(postId: String) {
        viewModel.remove(postId).observe(viewLifecycleOwner) {
            view?.snackbar("Post foi removido!")
            controlador.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
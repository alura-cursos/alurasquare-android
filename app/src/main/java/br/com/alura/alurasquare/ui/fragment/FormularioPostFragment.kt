package br.com.alura.alurasquare.ui.fragment

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.alura.alurasquare.R
import br.com.alura.alurasquare.databinding.FormularioPostBinding
import br.com.alura.alurasquare.databinding.OpcoesImagemPostBinding
import br.com.alura.alurasquare.extensions.snackbar
import br.com.alura.alurasquare.model.Post
import br.com.alura.alurasquare.repository.Resultado
import br.com.alura.alurasquare.ui.viewmodel.Componentes
import br.com.alura.alurasquare.ui.viewmodel.EstadoAppViewModel
import br.com.alura.alurasquare.ui.viewmodel.FormularioPostViewModel
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream

private const val REQUEST_IMAGE_GET = 1

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
        viewModel.imagemCarregada.observe(viewLifecycleOwner) {
            it?.let { imagem ->
                binding.formularioPostImagem.load(imagem)
                return@observe
            }
            binding.formularioPostImagem.load(R.drawable.imagem_insercao_padrao)
        }
        binding.formularioPostImagem.setOnClickListener {

            val dialogo = BottomSheetDialog(requireContext())
            val bindingOpcoesImagem = OpcoesImagemPostBinding.inflate(layoutInflater)

            bindingOpcoesImagem.opcoesImagemPostGaleria.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*"
                }
                startActivityForResult(intent, REQUEST_IMAGE_GET)
                dialogo.behavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
            bindingOpcoesImagem.opcoesImagemPostRemover.setOnClickListener {
                viewModel.removeImagem()
                dialogo.behavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            dialogo.setContentView(bindingOpcoesImagem.root)
            dialogo.show()


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            data?.data?.let { imagem ->
                viewModel.atualizaImagem(imagem.toString())
            }
        }
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
            avaliacao = avaliacao,
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
        val imagem = devolveImagemDoPost()
        viewModel.edita(post, imagem).observe(viewLifecycleOwner) {
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
        val imagem = devolveImagemDoPost()
        viewModel.salva(post, imagem).observe(viewLifecycleOwner) { resultado ->
            when (resultado) {
                is Resultado.Sucesso -> controlador.popBackStack()
                is Resultado.Erro -> binding.formularioPostCoordinator
                    .snackbar(mensagem = "Post não foi enviada")
            }
        }
    }

    private fun devolveImagemDoPost(): ByteArray {
        val imageView = binding.formularioPostImagem
        val bitmap = imageView.drawable.toBitmap()
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imagem = baos.toByteArray()
        return imagem
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
        post.imagem?.let { imagem ->
             viewModel.atualizaImagem(imagem)
        }
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
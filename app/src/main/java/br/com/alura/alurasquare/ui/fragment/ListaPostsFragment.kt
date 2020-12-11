package br.com.alura.alurasquare.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.alura.alurasquare.databinding.ListaPostsBinding
import br.com.alura.alurasquare.extensions.snackbar
import br.com.alura.alurasquare.repository.Resultado
import br.com.alura.alurasquare.ui.recyclerview.adapter.ListaPostsAdapter
import br.com.alura.alurasquare.ui.viewmodel.EstadoAppViewModel
import br.com.alura.alurasquare.ui.viewmodel.ListaPostsViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ListaPostsFragment : Fragment() {

    private var _binding: ListaPostsBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy {
        ListaPostsAdapter(requireContext()) { postId ->
            vaiParaFormularioPost(postId)
        }
    }
    private val controlador by lazy {
        findNavController()
    }
    private val viewModel: ListaPostsViewModel by viewModel()
    private val estadoAppViewModel: EstadoAppViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ListaPostsBinding.inflate(
            inflater,
            container,
            false
        ).let {
            _binding = it
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        estadoAppViewModel.limpaComponentes()
        configuraRecyclerView()
        configuraFab()
    }

    private fun configuraFab() {
        binding.listaPostsFabAdiciona.setOnClickListener {
            vaiParaFormularioPost()
        }
    }

    private fun configuraRecyclerView() {
        binding.listaPostsRecyclerview.adapter = adapter
        viewModel.buscaTodos().observe(viewLifecycleOwner) {
            it?.let { resultado ->
                when (resultado) {
                    is Resultado.Sucesso -> resultado.dado?.let(adapter::atualiza)
                    is Resultado.Erro -> view?.snackbar("Falha ao encontrar novos posts")
                }
            }
            configuraVisibilidadeDosContainers()
        }
    }

    private fun configuraVisibilidadeDosContainers() {
        if (adapter.itemCount > 0) {
            binding.listaPostsContainerAvaliacoesNaoEncontradas.visibility = GONE
            binding.listaPostsRecyclerview.visibility = VISIBLE
            return
        }
        binding.listaPostsContainerAvaliacoesNaoEncontradas.visibility = VISIBLE
        binding.listaPostsRecyclerview.visibility = GONE
    }

    private fun vaiParaFormularioPost(postId: String? = null) {
        ListaPostsFragmentDirections
            .acaoListaPostsParaFormularioPost(postId)
            .let(controlador::navigate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
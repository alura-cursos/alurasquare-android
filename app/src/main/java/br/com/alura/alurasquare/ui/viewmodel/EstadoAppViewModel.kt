package br.com.alura.alurasquare.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EstadoAppViewModel : ViewModel() {

    private val _componentes = MutableLiveData<Componentes>()
    val componentes: LiveData<Componentes> = _componentes

    fun setComponentes(componentes: Componentes) {
        _componentes.postValue(componentes)
    }

    fun limpaComponentes() {
        _componentes.postValue(SemComponentes())
    }

}

private class SemComponentes : Componentes(
    appBar = false
)

open class Componentes(val appBar: Boolean)

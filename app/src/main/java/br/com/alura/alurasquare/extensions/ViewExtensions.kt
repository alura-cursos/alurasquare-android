package br.com.alura.alurasquare.extensions

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.snackbar(
    mensagem: String,
    duracao: Int = Snackbar.LENGTH_SHORT
) = Snackbar.make(
    this,
    mensagem,
    duracao
).show()
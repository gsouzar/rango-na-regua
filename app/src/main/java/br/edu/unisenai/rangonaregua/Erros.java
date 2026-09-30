package br.edu.unisenai.rangonaregua;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

/** Mensagem de erro é interface: traduz as exceções do Firebase para português. */
final class Erros {
    private Erros() { }

    static String auth(Exception e) {
        if (e instanceof FirebaseAuthWeakPasswordException) return "A senha precisa de pelo menos 6 caracteres.";
        if (e instanceof FirebaseAuthUserCollisionException) return "Este e-mail já está cadastrado.";
        if (e instanceof FirebaseAuthInvalidUserException) return "Não existe conta com este e-mail.";
        if (e instanceof FirebaseAuthInvalidCredentialsException) return "E-mail inválido ou senha incorreta.";
        if (e instanceof FirebaseNetworkException) return "Sem conexão com a internet.";
        return "Não foi possível concluir. Tente novamente.";
    }
}

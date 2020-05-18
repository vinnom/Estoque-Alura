package br.com.alura.estoque.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class BaseCallback<T> implements Callback<T> {

	private static final String RESPOSTA_FALHOU = "Resposta falhou!";
	private static final String FALHA_COMUNICACAO = "Não foi possível fazer a comunicação: ";
	private final NotificaDadosCallback<T> notifica;

	public BaseCallback(NotificaDadosCallback<T> callback) {
		this.notifica = callback;
	}

	@Override
	@EverythingIsNonNull
	public void onResponse(Call<T> call, Response<T> response) {
		if(response.isSuccessful()) {
			if(response.body() != null) {
				notifica.quandoSucesso(response.body());
			} else {
				notifica.quandoFalha(RESPOSTA_FALHOU);
			}
		}
	}

	@Override
	@EverythingIsNonNull
	public void onFailure(Call<T> call, Throwable t) {
		notifica.quandoFalha(FALHA_COMUNICACAO + t.getMessage());
	}

	public interface NotificaDadosCallback<T> {
		void quandoSucesso(T resultado);

		void quandoFalha(String erro);
	}
}

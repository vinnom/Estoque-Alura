package br.com.alura.estoque.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class SemRetornoCallback implements Callback<Void> {

	private static final String RESPOSTA_FALHOU = "Resposta falhou!";
	private static final String FALHA_COMUNICACAO = "Não foi possível fazer a comunicação: ";
	private final RespostaVaziaCallback callback;

	public SemRetornoCallback(RespostaVaziaCallback callback) {
		this.callback = callback;
	}

	@Override
	@EverythingIsNonNull
	public void onResponse(Call<Void> call, Response<Void> response) {
		if(response.isSuccessful()) {
			callback.quandoSucesso();
		} else {
			callback.quandoFalha(RESPOSTA_FALHOU);
		}
	}

	@Override
	@EverythingIsNonNull
	public void onFailure(Call<Void> call, Throwable t) {
		callback.quandoFalha(FALHA_COMUNICACAO + t.getMessage());
	}

	public interface RespostaVaziaCallback {
		void quandoSucesso();

		void quandoFalha(String erro);
	}
}

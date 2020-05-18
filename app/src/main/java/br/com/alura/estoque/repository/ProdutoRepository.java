package br.com.alura.estoque.repository;

import android.content.Context;

import java.util.List;

import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.EstoqueDatabase;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.retrofit.EstoqueRetrofit;
import br.com.alura.estoque.callback.BaseCallback;
import br.com.alura.estoque.callback.SemRetornoCallback;
import br.com.alura.estoque.service.ProdutoService;
import retrofit2.Call;

public class ProdutoRepository {

	private final ProdutoDAO produtoDAO;
	private final ProdutoService produtoService;

	public ProdutoRepository(Context context) {
		EstoqueDatabase db = EstoqueDatabase.getInstance(context);
		this.produtoDAO = db.getProdutoDAO();
		produtoService = new EstoqueRetrofit().getProdutoService();
	}

	public void salva(Produto produto, BaseCallback.NotificaDadosCallback<Produto> notificaDadosCallback) {
		Call<Produto> produtoCall = produtoService.salva(produto);
		produtoCall.enqueue(new BaseCallback<>(new BaseCallback.NotificaDadosCallback<Produto>() {
			@Override
			public void quandoSucesso(Produto resultado) {
				salvaProdutoInterno(resultado, notificaDadosCallback);
			}

			@Override
			public void quandoFalha(String erro) {
				notificaDadosCallback.quandoFalha(erro);
			}
		}));
	}

	private void salvaProdutoInterno(Produto produto, BaseCallback.NotificaDadosCallback<Produto> notificaDadosCallback) {
		new BaseAsyncTask<>(() -> {
			long id = produtoDAO.salva(produto);
			return produtoDAO.buscaProduto(id);
		}, notificaDadosCallback::quandoSucesso).execute();
	}

	public void edita(Produto produto, BaseCallback.NotificaDadosCallback<Produto> notificaDadosCallback) {
		Call<Produto> produtoCall = produtoService.edita(produto.getId(), produto);
		produtoCall.enqueue(new BaseCallback<>(new BaseCallback.NotificaDadosCallback<Produto>() {
			@Override
			public void quandoSucesso(Produto resultado) {
				editaProdutoInterno(resultado, notificaDadosCallback);
			}

			@Override
			public void quandoFalha(String erro) {
				notificaDadosCallback.quandoFalha(erro);
			}
		}));
	}

	private void editaProdutoInterno(Produto produto, BaseCallback.NotificaDadosCallback<Produto> notificaDadosCallback) {
		new BaseAsyncTask<>(() -> {
			produtoDAO.atualiza(produto);
			return produto;
		}, notificaDadosCallback::quandoSucesso).execute();
	}

	public void buscaProdutos(BaseCallback.NotificaDadosCallback<List<Produto>> notificaDadosCallback) {
		new BaseAsyncTask<>(produtoDAO::buscaTodos,
			produtos -> {
				notificaDadosCallback.quandoSucesso(produtos);
				buscaProdutosRede(notificaDadosCallback);
			}).execute();
	}

	private void buscaProdutosRede(BaseCallback.NotificaDadosCallback<List<Produto>> notificaDadosCallback) {
		Call<List<Produto>> produtosCall = produtoService.buscaProdutos();
		produtosCall.enqueue(new BaseCallback<>(new BaseCallback.NotificaDadosCallback<List<Produto>>() {
			@Override
			public void quandoSucesso(List<Produto> resultado) {
				new BaseAsyncTask<>(() -> {
					produtoDAO.salva(resultado);
					return produtoDAO.buscaTodos();
				}, notificaDadosCallback::quandoSucesso).execute();
			}

			@Override
			public void quandoFalha(String erro) {
				notificaDadosCallback.quandoFalha(erro);
			}
		}));
	}

	public void remove(Produto produto, BaseCallback.NotificaDadosCallback<Void> notificaDadosCallback) {
		Call<Void> produtoCall = produtoService.remove(produto.getId());
		produtoCall.enqueue(new SemRetornoCallback(new SemRetornoCallback.RespostaVaziaCallback() {
			@Override
			public void quandoSucesso() {
				removeProdutoInterno(produto, notificaDadosCallback);
			}

			@Override
			public void quandoFalha(String erro) {
				notificaDadosCallback.quandoFalha(erro);
			}
		}));
	}

	private void removeProdutoInterno(Produto produto, BaseCallback.NotificaDadosCallback<Void> notificaDadosCallback) {
		new BaseAsyncTask<>(() -> {
			produtoDAO.remove(produto);
			return null;
		}, notificaDadosCallback::quandoSucesso).execute();
	}

}

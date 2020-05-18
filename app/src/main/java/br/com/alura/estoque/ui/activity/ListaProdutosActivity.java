package br.com.alura.estoque.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import br.com.alura.estoque.R;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.callback.BaseCallback;
import br.com.alura.estoque.repository.ProdutoRepository;
import br.com.alura.estoque.ui.dialog.EditaProdutoDialog;
import br.com.alura.estoque.ui.dialog.SalvaProdutoDialog;
import br.com.alura.estoque.ui.recyclerview.adapter.ListaProdutosAdapter;

public class ListaProdutosActivity extends AppCompatActivity {

	private static final String MENSAGEM_ERRO_BUSCA = "Não foi possível atualizar a lista de produtos";
	private static final String MENSAGEM_ERRO_REMOCAO = "Não foi possível remover o produto";
	private static final String MENSAGEM_ERRO_ADICAO = "Não foi possível adicionar o produto";
	private static final String MENSAGEM_ERRO_ATUALIZACAO = "Não foi possível editar esse produto";
	private static final String TITULO_APPBAR = "Lista de produtos";
	private ListaProdutosAdapter adapter;
	private ProdutoRepository produtoRepository;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_produtos);
		setTitle(TITULO_APPBAR);

		configuraListaProdutos();
		configuraFabSalvaProduto();

		produtoRepository = new ProdutoRepository(this);
		produtoRepository.buscaProdutos(exibeProdutos());
	}

	private void configuraListaProdutos() {
		RecyclerView listaProdutos = findViewById(R.id.activity_lista_produtos_lista);
		adapter = new ListaProdutosAdapter(this, this::abreFormularioEditaProduto);
		listaProdutos.setAdapter(adapter);
		adapter.setOnItemClickRemoveContextMenuListener(this::removeProduto);
	}

	private void configuraFabSalvaProduto() {
		FloatingActionButton fabAdicionaProduto = findViewById(R.id.activity_lista_produtos_fab_adiciona_produto);
		fabAdicionaProduto.setOnClickListener(v -> abreFormularioSalvaProduto());
	}

	private void abreFormularioSalvaProduto() {
		new SalvaProdutoDialog(this, this::salvaProduto).mostra();
	}

	private void abreFormularioEditaProduto(int posicao, Produto produto) {
		new EditaProdutoDialog(this, produto,
			produtoEditado -> editaProduto(posicao, produtoEditado)).mostra();
	}

	@NotNull
	private BaseCallback.NotificaDadosCallback<List<Produto>> exibeProdutos() {
		return new BaseCallback.NotificaDadosCallback<List<Produto>>() {
			@Override
			public void quandoSucesso(List<Produto> dados) {
				adapter.atualiza(dados);
			}

			@Override
			public void quandoFalha(String falha) {
				exibeMensagemErro(MENSAGEM_ERRO_BUSCA);
			}
		};
	}

	private void removeProduto(int posicao, Produto produto) {
		produtoRepository.remove(produto, new BaseCallback.NotificaDadosCallback<Void>() {
			@Override
			public void quandoSucesso(Void resultado) {
				adapter.remove(posicao);
			}

			@Override
			public void quandoFalha(String erro) {
				exibeMensagemErro(MENSAGEM_ERRO_REMOCAO);
			}
		});
	}

	private void salvaProduto(Produto produto) {
		produtoRepository.salva(produto, new BaseCallback.NotificaDadosCallback<Produto>() {
			@Override
			public void quandoSucesso(Produto resultado) {
				adapter.adiciona(resultado);
			}

			@Override
			public void quandoFalha(String erro) {
				exibeMensagemErro(MENSAGEM_ERRO_ADICAO);
			}
		});
	}

	private void editaProduto(int posicao, Produto produtoEditado) {
		produtoRepository.edita(produtoEditado, new BaseCallback.NotificaDadosCallback<Produto>() {
			@Override
			public void quandoSucesso(Produto produtoAtualizado) {
				adapter.edita(posicao, produtoAtualizado);
			}

			@Override
			public void quandoFalha(String erro) {
				exibeMensagemErro(MENSAGEM_ERRO_ATUALIZACAO);
			}
		});
	}

	private void exibeMensagemErro(String mensagem) {
		Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
	}

}

package br.com.alura.estoque.retrofit;

import br.com.alura.estoque.service.ProdutoService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.logging.HttpLoggingInterceptor.Level;

public class EstoqueRetrofit {

	private static final String URL_RAIZ = "http://192.168.0.8:8080/";
	private final ProdutoService produtoService;

	public EstoqueRetrofit(){
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(Level.BODY);
		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

		Retrofit retrofit = new Retrofit.Builder()
			.addConverterFactory(GsonConverterFactory.create()).client(client).baseUrl(URL_RAIZ).build();

		produtoService = retrofit.create(ProdutoService.class);
	}

	public ProdutoService getProdutoService(){
		return produtoService;
	}
}

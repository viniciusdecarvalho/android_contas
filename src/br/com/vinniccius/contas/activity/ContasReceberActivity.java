package br.com.vinniccius.contas.activity;

import android.os.Bundle;
import br.com.vinniccius.contas.bean.Conta;
import br.com.vinniccius.contas.bean.Tipo;
import br.com.vinniccius.contas.datasource.ContasDataSource;
import br.com.vinniccius.contas.datasource.DatabaseAdapter;


public class ContasReceberActivity extends ContasActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DatabaseAdapter dbAdapter = new DatabaseAdapter(this);
		setDbAdapter(dbAdapter);
		setDataSource(new ContasDataSource(dbAdapter) {
			
			@Override
			public String getViewPagasName() {
				return Conta.Views.RECEITAS_PAGAS;
			}
			
			@Override
			public String getViewName() {
				return Conta.Views.RECEITAS;
			}
			
			@Override
			public String getViewAbertasName() {
				return Conta.Views.RECEITAS_ABERTAS;
			}
			
			@Override
			public String getTableName() {
				return Conta.Views.RECEITAS;
			}
			
			@Override
			public Tipo getTipoConta() {				
				return Tipo.RECEBER;
			}
		});
		super.onCreate(savedInstanceState);
	}

	@Override
	public Tipo getContaTipo() {
		return Tipo.RECEBER;
	}
}

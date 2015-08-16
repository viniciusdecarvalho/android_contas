package br.com.vinniccius.contas.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import br.com.vinniccius.contas.R;
import br.com.vinniccius.contas.bean.ContaTotal;
import br.com.vinniccius.contas.bean.ItemList;
import br.com.vinniccius.contas.datasource.ContasDataSource;
import br.com.vinniccius.contas.datasource.DatabaseAdapter;

public class ContasTotaisActivity extends ListActivity {

	private DatabaseAdapter dbAdapter;
	private ContasDataSource dataSource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contas_totais);
		
		dbAdapter = new DatabaseAdapter(this);
		dbAdapter.openDatabase();
		dataSource = new ContasDataSource( dbAdapter );
		
		fillData();
	}

	private void fillData() {
		ContaTotal totalizador = dataSource.getTotalizador();	
		
		String[] contas = new String[] {
			"\t\t\t" + getString(R.string.conta_receber),	
			format(R.string.contas_totais_receber_abertas_vencidas, totalizador.getReceitasAbertasVencidas()),
			format(R.string.contas_totais_receber_abertas_nao_vencidas, totalizador.getReceitasAbertasNaoVencidas()),
			format(R.string.contas_totais_receber_abertas, totalizador.getReceitasAbertas()),
			format(R.string.contas_totais_receber_pagas, totalizador.getReceitasPagas()),
			format(R.string.contas_totais_receber_total, totalizador.getReceitas()),
			"\t\t\t" + getString(R.string.conta_pagar),
			format(R.string.contas_totais_pagar_abertas_vencidas, totalizador.getDespesasAbertasVencidas()),
			format(R.string.contas_totais_pagar_abertas_nao_vencidas, totalizador.getDespesasAbertasNaoVencidas()),
			format(R.string.contas_totais_pagar_abertas, totalizador.getDespesasAbertas()),
			format(R.string.contas_totais_pagar_pagas, totalizador.getDespesasPagas()),
			format(R.string.contas_totais_pagar_total, totalizador.getDespesas()),
			format(R.string.contas_totais_lucro, totalizador.getLucro()),
		};
		
		List<ItemList> items = new ArrayList<ItemList>();
		
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
//				android.R.layout.simple_list_item_1,
//				contas);
		
		for (String title : contas) {
			items.add(new ItemList(title));
		}
		
		setListAdapter(new ItemListAdapter(items));
	}
	
	private String format(int resId, Object value) {
		return String.format(getString(resId), value);
	}
	
	@Override
	protected void onResume() {
		super.onResume();		
		dbAdapter.openDatabase();
		fillData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbAdapter.closeDatabase();
	}
}

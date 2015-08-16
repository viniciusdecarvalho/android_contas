package br.com.vinniccius.contas.activity;

import java.util.ArrayList;
import java.util.Locale;

import roboguice.activity.RoboListActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import br.com.vinniccius.contas.R;
import br.com.vinniccius.contas.bean.ItemList;
import br.com.vinniccius.contas.bean.Tipo;

@ContentView(R.layout.contas_opcoes)
public class MainActivity extends RoboListActivity {

	private ArrayList<ItemList> items = new ArrayList<ItemList>();
	
	@InjectResource(R.string.conta_receber)
	private String contaReceber;
	
	@InjectResource(R.string.conta_pagar)
	private String contaPagar;
	
	@InjectResource(R.string.conta_totais)
	private String contaTotais;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ItemListAdapter(items));
		fillData();
	}

	private void fillData() {
		SharedPreferences pref = getSharedPreferences("contas", 0);
		long receber = pref.getLong(getPreferenceName(Tipo.RECEBER), 0);
		long pagar = pref.getLong(getPreferenceName(Tipo.PAGAR), 0);
		
		items.clear();
		
		items.add(new ItemList(R.drawable.contas_receber, contaReceber, receber));
		items.add(new ItemList(R.drawable.contas_pagar, contaPagar, pagar));		
		items.add(new ItemList(R.drawable.contas, contaTotais, receber + pagar));
		
		((ItemListAdapter)getListAdapter()).notifyDataSetChanged();
	}
		
	private String getPreferenceName(Tipo tipo) {
		return tipo.toString().toLowerCase(Locale.getDefault()).concat("_total");
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		switch (position) {
		case 0:
			contasReceber();
			break;
		case 1:
			contasPagar();
			break;
		case 2:
			contasTotal();
			break;
		}
	}

	private void contasTotal() {
		Intent intent = new Intent(this, ContasTotaisActivity.class);
		startActivity(intent);
	}

	private void contasReceber() {
		Intent intent = new Intent(this, ContasReceberActivity.class);
		startActivity(intent);
	}

	private void contasPagar() {
		Intent intent = new Intent(this, ContasPagarActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		fillData();
	}
}

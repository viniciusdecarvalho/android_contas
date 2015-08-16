package br.com.vinniccius.contas.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import br.com.vinniccius.contas.R;
import br.com.vinniccius.contas.bean.Conta;
import br.com.vinniccius.contas.bean.Conta.Columns;
import br.com.vinniccius.contas.bean.ContaParams;
import br.com.vinniccius.contas.bean.ContaParams.Ordem;
import br.com.vinniccius.contas.bean.Tipo;
import br.com.vinniccius.contas.datasource.ContasDataSource;
import br.com.vinniccius.contas.datasource.DatabaseAdapter;
import br.com.vinniccius.contas.util.DateUtils;

public abstract class ContasActivity extends ListActivity {

	private static final int MENU_INSERT = Menu.FIRST;
	private static final int MENU_PARAMS = Menu.FIRST + 1;
	private static final int EDIT_REQUEST = 0;
	
	private DatabaseAdapter dbAdapter;
	private ContasDataSource dataSource;
	private List<Conta> contas = new ArrayList<Conta>();
	private ContasAdapter dataAdapter;
	private RadioGroup opcoes;
	
	private ContaParams parametros = new ContaParams();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contas);			
		
		refreshTitle();
		
		dbAdapter.openDatabase();
		dataAdapter = new ContasAdapter(this);
		setListAdapter(dataAdapter);
		
		opcoes = (RadioGroup)findViewById(R.id.contas_opcoes);
		opcoes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				consultaContas();
			}
		});
		consultaContas();
	}

	private void refreshTitle() {
		TextView view = (TextView)findViewById(R.id.contas_param_datas);
		view.setText(parametros.toString());
		view.setVisibility(parametros.hasValue() ? View.VISIBLE : View.GONE);
			
	}

	private void consultaContas() {				
		switch (opcoes.getCheckedRadioButtonId()) {		
		case R.id.contas_aberto:
			contas = dataSource.recuperarTodosAbertas(parametros);
			break;
		case R.id.contas_paga:
			contas = dataSource.recuperarTodosPagas(parametros);
			break;
		case R.id.contas_todas:
			contas = dataSource.recuperarTodos(parametros);
			break;
		default:
			contas = new ArrayList<Conta>();
			break;
		}
		dataAdapter.notifyDataSetChanged();
	}
	
	public List<Conta> getContas() {
		return contas;
	}
	
	public DatabaseAdapter getDbAdapter() {
		return dbAdapter;
	}
	
	public void setDbAdapter(DatabaseAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}
	
	public ContasDataSource getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(ContasDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_INSERT, 0, R.string.conta_insert);
		menu.add(0, MENU_PARAMS, 0, R.string.conta_param);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_INSERT:
			addConta();
			return true;
		case MENU_PARAMS:
			configParams();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void configParams() {
		Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(R.string.conta_param);
		
		final View view = getLayoutInflater().inflate(R.layout.contas_param, null, false);
		final RadioGroup ordem = (RadioGroup)view.findViewById(R.id.conta_param_ordena);
		final Button limpar = (Button)view.findViewById(R.id.conta_param_limpar);
		final DatePicker inicio = (DatePicker)view.findViewById(R.id.conta_param_data_inicio);
		final DatePicker fim = (DatePicker)view.findViewById(R.id.conta_param_data_fim);
		
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH);
		int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
		inicio.init(year, month, dayOfMonth, new DatePicker.OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Calendar dataInicial = getDateFromDatePicker(inicio);
				Calendar dataFinal = getDateFromDatePicker(fim);
				if (dataInicial.after(dataFinal)) {
					updateDatePicker(inicio, dataFinal);					
					toast(R.string.conta_param_start_invalid);
				}
			}
		});
		
		fim.init(year, month, dayOfMonth, new DatePicker.OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Calendar dataInicial = getDateFromDatePicker(inicio);
				Calendar dataFinal = getDateFromDatePicker(fim);
				if (dataFinal.before(dataInicial)) {
					updateDatePicker(inicio, dataFinal);
					toast(R.string.conta_param_end_invalid);					
				}
			}
		});

		bindParams(parametros, ordem, inicio, fim);
		
		dialogBuilder.setView(view);	
		dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Calendar dataInicial = getDateFromDatePicker(inicio);
				Calendar dataFinal = getDateFromDatePicker(fim);
				try {
					parametros.setInterval(dataInicial, dataFinal, getOrdem());
					refreshTitle();
					consultaContas();
					dialog.cancel();
				} catch (Exception e) {
					Toast.makeText(ContasActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}

			private Ordem getOrdem() {
				switch (ordem.getCheckedRadioButtonId()) {
				case R.id.conta_param_ordena_vencimento:
					return Ordem.VENCIMENTO;
				case R.id.conta_param_ordena_pagamento:
					return Ordem.PAGAMENTO;			
				}
				return Ordem.VENCIMENTO;
			}
		});
		dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		final AlertDialog dialog = dialogBuilder.create();
		limpar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				parametros.clear();
				refreshTitle();
				toast(R.string.conta_all);
				consultaContas();
				dialog.cancel();
			}
		});
		dialog.show();
	}
	
	protected void toast(int id) {
		Toast.makeText(ContasActivity.this, id, Toast.LENGTH_LONG).show();
	}
	
	private Calendar getDateFromDatePicker(DatePicker picker) {
		return new GregorianCalendar(picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
	}
	
	private void updateDatePicker(DatePicker picker, Calendar data) {
		picker.updateDate(data.get(Calendar.YEAR), data.get(Calendar.MONTH), data.get(Calendar.DAY_OF_MONTH));
	}

	private void bindParams(ContaParams parametros, RadioGroup ordem, DatePicker inicio, DatePicker fim) {
		if (parametros.getOrdem() == Ordem.PAGAMENTO) {
			ordem.check(R.id.conta_param_ordena_pagamento);
		} else {
			ordem.check(R.id.conta_param_ordena_vencimento);
		}
		if (parametros.hasValue()) {
			updateDatePicker(inicio, parametros.getStart());
			updateDatePicker(fim, parametros.getEnd());
		}
	}

	private void addConta() {
		Intent intent = new Intent(this, ContaActivity.class);
		startActivityForResult(intent, EDIT_REQUEST);
	}
	
	public void editConta(Conta conta) {
		Intent intent = new Intent(this, ContaActivity.class);
		intent.putExtra(Columns.ID, conta.getId());
		intent.putExtra(Columns.DESCRICAO, conta.getDescricao());
		intent.putExtra(Columns.VENCIMENTO, DateUtils.dateToString(conta.getVencimento(), DateUtils.DATE_VIEW));
		intent.putExtra(Columns.PAGAMENTO, DateUtils.dateToString(conta.getPagamento(), DateUtils.DATE_VIEW));
		intent.putExtra(Columns.VALOR, conta.getValor());
		startActivityForResult(intent, EDIT_REQUEST);
	}

	public void deleteConta(Conta conta) {
		dataSource.delete(conta);
		toast(R.string.conta_delete_success);
		contas.remove(conta);
		dataAdapter.notifyDataSetChanged();
	}

	public void downConta(Conta conta) {
		conta.setPagamento(new Date());
		dataSource.save(conta);
		toast(R.string.conta_down_success);
		opcoes.check(R.id.contas_paga);
	}
	
	public void upConta(Conta conta) {
		conta.setPagamento(null);
		dataSource.save(conta);
		toast(R.string.conta_up_success);
		opcoes.check(R.id.contas_aberto);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		dbAdapter.openDatabase();
		consultaContas();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		dbAdapter.closeDatabase();
		contas.clear();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if ( resultCode == RESULT_OK && requestCode == EDIT_REQUEST ) {
			Conta conta = new Conta(intent.getExtras(), getContaTipo());
			try {
				dataSource.save(conta);
				saveSharedPreferences();
				toast(R.string.conta_save_success);
				consultaContas();
			} catch (Exception e) {
				Toast.makeText(ContasActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	}

	private void saveSharedPreferences() {
		Editor editor = getSharedPreferences("contas", 0).edit();
		editor.putLong(getPreferenceName(getContaTipo()), dataSource.getCount());
		editor.commit();
	}

	private String getPreferenceName(Tipo tipo) {
		return tipo.toString().toLowerCase(Locale.getDefault()).concat("_total");
	}
	
	public abstract Tipo getContaTipo();

}

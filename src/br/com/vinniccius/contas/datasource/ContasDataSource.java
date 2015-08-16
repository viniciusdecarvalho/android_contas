package br.com.vinniccius.contas.datasource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.com.vinniccius.contas.R;
import br.com.vinniccius.contas.bean.Conta;
import br.com.vinniccius.contas.bean.Conta.Columns;
import br.com.vinniccius.contas.bean.ContaParams;
import br.com.vinniccius.contas.bean.ContaParams.Ordem;
import br.com.vinniccius.contas.bean.ContaTotal;
import br.com.vinniccius.contas.bean.Tipo;
import br.com.vinniccius.contas.util.DateUtils;

public class ContasDataSource {

	private DatabaseAdapter dbAdapter;
	
	public String getTableName() {
		return Conta.TABLE_NAME;
	}
	
	public String getViewName() {
		return getTableName();
	}
	
	public String getViewAbertasName() {
		return getTableName();
	}
	
	public String getViewPagasName() {
		return getTableName();
	}
	
	public Tipo getTipoConta() {
		return Tipo.INDEFINIDO;
	}
	
	public ContasDataSource(DatabaseAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}
	
	private String getResourceString(int id) {
		return dbAdapter.getContext().getResources().getString(id);
	}
	
	public SQLiteDatabase getDatabase() {
		if (!dbAdapter.isOpen()) 
			dbAdapter.openDatabase();
		return dbAdapter.getDatabase();
	}
	
	private void insert(Conta conta) {
		long id = getDatabase().insert(getTableName(), null, conta.getValues());
		conta.setId(id);
	}

	private void update(Conta conta) {
		String where = Conta.Columns.ID + "=" + conta.getId();
		getDatabase().update(getTableName(), conta.getValues(), where, null);
	}

	public void delete(Conta conta) {
		String where = Conta.Columns.ID + "=" + conta.getId();
		getDatabase().delete(getTableName(), where, null);
	}
	
	public void save(Conta conta) {
		if (conta.getTipo() == null ||
			conta.getTipo().equals(Tipo.INDEFINIDO)) {
			throw new IllegalStateException(getResourceString(R.string.conta_indefined));
		}
		
		if (exist(conta)) {
			throw new IllegalStateException(getResourceString(R.string.conta_exists));
		}
		
		if (conta.isNew()) {
			insert(conta);
		} else {
			update(conta);
		}
	}

	public boolean exist(Conta conta) {
		String where = String.format("lower(%s) = ? and date(%s) = date(?) and %s != ?", Columns.DESCRICAO, Columns.VENCIMENTO, Columns.ID);
		String[] whereArgs = new String[] {
			conta.getDescricao().toLowerCase(Locale.getDefault()), 
			DateUtils.dateToString(conta.getVencimento(), DateUtils.DATE_DB),
			conta.isNew() ? "NULL" : conta.getId().toString()
		};		
		String[] columns = new String[]{ Conta.Columns.ID };
		Cursor cursor = getDatabase().query(getViewName(), columns, where, whereArgs, null, null, null);
		boolean existe = false;
		
		existe = cursor.moveToFirst();
		
		cursor.close();
		cursor = null;
		return existe;
	}
	
	public Conta recuperar(Long id) {
		String where = Conta.Columns.ID + "=" + id;
		Cursor cursor = getDatabase().query(getTableName(), Conta.TABLE_COLUMNS, where, null, null, null, null);
		Conta conta = null;
		
		if (cursor.moveToFirst())
			conta = new Conta(cursor, getTipoConta());
		
		cursor.close();
		cursor = null;
		return conta;
	}

	public List<Conta> recuperarTodos(ContaParams params) {
		return getContasParameterizadas(getViewName(), params);
	}

	public List<Conta> recuperarTodosAbertas(ContaParams params) {
		return getContasParameterizadas(getViewAbertasName(), params);
	}
	
	public List<Conta> recuperarTodosPagas(ContaParams params) {
		return getContasParameterizadas(getViewPagasName(), params);
	}
	
	private List<Conta> getContasParameterizadas(String tableOrViewName, ContaParams params) {
		if (params.hasValue())
			return getContasPorVencimentoBetween(params.getStart(), params.getEnd(), tableOrViewName, params.getOrdem());
		return getContas(tableOrViewName, null, null, Columns.VENCIMENTO);
	}
	
	private List<Conta> getContasPorVencimentoBetween(Calendar dataInicial, Calendar dataFinal, String tableOrViewName, Ordem ordem) {
		String where = String.format("date(%s) between date(?) and date(?)", Conta.Columns.VENCIMENTO);
		String[] whereArgs = new String[]{
			DateUtils.dateToString(dataInicial.getTime(), DateUtils.DATE_DB),
			DateUtils.dateToString(dataFinal.getTime(), DateUtils.DATE_DB)
		};
		String orderBy = (ordem == null) ? Ordem.VENCIMENTO.getColumnName() : ordem.getColumnName(); 
		return getContas(tableOrViewName, where, whereArgs, orderBy);
	}
	
	private List<Conta> getContas(String tableOrView, String where, String[] args, String orderBy) {
		List<Conta> contas = new ArrayList<Conta>();
		Cursor cursor = getDatabase().query(tableOrView, Conta.TABLE_COLUMNS, where, args, null, null, orderBy);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			contas.add(new Conta(cursor, getTipoConta()));
			cursor.moveToNext();
		}
		cursor.close();
		cursor = null;
		return contas;
	}
	
	public ContaTotal getTotalizador() {	
		Cursor cursor = getDatabase().query(ContaTotal.TABLE_NAME, ContaTotal.COLUMNS, null, null, null, null, null);
		
		cursor.moveToFirst();
		ContaTotal totais = new ContaTotal(
			cursor.getDouble(cursor.getColumnIndex(ContaTotal.COLUMN_RECEITAS_ABERTAS_VENCIDAS)),
			cursor.getDouble(cursor.getColumnIndex(ContaTotal.COLUMN_RECEITAS_ABERTAS_NAO_VENCIDAS)),
			cursor.getDouble(cursor.getColumnIndex(ContaTotal.COLUMN_RECEITAS_PAGAS)),
			cursor.getDouble(cursor.getColumnIndex(ContaTotal.COLUMN_DESPESAS_ABERTAS_VENCIDAS)),
			cursor.getDouble(cursor.getColumnIndex(ContaTotal.COLUMN_DESPESAS_ABERTAS_NAO_VENCIDAS)),
			cursor.getDouble(cursor.getColumnIndex(ContaTotal.COLUMN_DESPESAS_PAGAS))
		);
		
		cursor.close();
		cursor = null;
		return totais;
	}

	public long getCount() {
		Cursor cursor = getDatabase().rawQuery("select count(id) from " + getViewName(), null);
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		cursor = null;
		return count;
	}

}

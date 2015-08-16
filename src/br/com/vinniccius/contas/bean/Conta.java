package br.com.vinniccius.contas.bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import br.com.vinniccius.contas.util.DateUtils;

public class Conta implements Serializable {

	private static final long serialVersionUID = -8887338988110687620L;
	public static final String TABLE_NAME = "contas";
	
	public static class Columns {
		public final static String ID = "id";
		public final static String DESCRICAO = "descricao";
		public final static String VENCIMENTO = "vencimento";
		public final static String PAGAMENTO = "pagamento";
		public final static String VALOR = "valor";
		public final static String TIPO = "tipo";
	}
	
	public static class Views {
		public final static String RECEITAS = "receitas";
		public final static String RECEITAS_ABERTAS = "receitas_abertas";
		public final static String RECEITAS_ABERTAS_VENCIDAS = "receitas_abertas_vencidas";
		public final static String RECEITAS_ABERTAS_NAO_VENCIDAS = "receitas_abertas_nao_vencidas";
		public final static String RECEITAS_PAGAS = "receitas_pagas";
		public final static String DESPESAS = "despesas";
		public final static String DESPESAS_ABERTAS = "despesas_abertas";
		public final static String DESPESAS_ABERTAS_VENCIDAS = "despesas_abertas_vencidas";
		public final static String DESPESAS_ABERTAS_NAO_VENCIDAS = "despesas_abertas_nao_vencidas";
		public final static String DESPESAS_PAGAS = "despesas_pagas";		
		public final static String RECEITAS_ABERTAS_VENCIDAS_TOTAL = "receitas_abertas_vencidas_total";
		public final static String RECEITAS_ABERTAS_NAO_VENCIDAS_TOTAL = "receitas_abertas__nao_vencidas_total";
		public final static String RECEITAS_PAGAS_TOTAL = "receitas_pagas_total";
		public final static String DESPESAS_ABERTAS_VENCIDAS_TOTAL = "despesas_abertas_vencidas_total";
		public final static String DESPESAS_ABERTAS_NAO_VENCIDAS_TOTAL = "despesas_abertas_nao_vencidas_total";
		public final static String DESPESAS_PAGAS_TOTAL = "despesas_pagas_total";
	}
	
	public static String[] TABLE_COLUMNS = new String[]{
		Conta.Columns.ID,
		Conta.Columns.DESCRICAO,
		Conta.Columns.VENCIMENTO,
		Conta.Columns.PAGAMENTO,
		Conta.Columns.VALOR
	};

	public static class Colors {		
		public static int ABERTA_VENCIDA = 0xFFFFAE00;
		public static int ABERTA_NAO_VENCIDA = 0xFF00FFF1;
		public static int PAGA = 0xFF00FF00;
	}
	
	private Long id;
	private String descricao;
	private Date vencimento;
	private Date pagamento;
	private double valor;
	private Tipo tipo;
	
	public Conta() { }
	
	public Conta(Conta conta) {
		this.tipo = conta.getTipo();
		this.vencimento = conta.getVencimento();
		this.descricao = conta.getDescricao();
		this.valor = conta.getValor();
	}
	
	public Conta(Cursor cursor, Tipo tipo) {
		this(tipo);
		
		int idIndex = cursor.getColumnIndex(Columns.ID);
		int descricaoIndex = cursor.getColumnIndex(Columns.DESCRICAO);
		int emissaoIndex = cursor.getColumnIndex(Columns.VENCIMENTO);
		int pagamentoIndex = cursor.getColumnIndex(Columns.PAGAMENTO);
		int valorIndex = cursor.getColumnIndex(Columns.VALOR);
		
		setId(cursor.getLong(idIndex));
		setDescricao(cursor.getString(descricaoIndex));
		Date vencimento = new Date(cursor.getLong(emissaoIndex));
		setVencimento(vencimento);
		Long pagamentoTime = cursor.getLong(pagamentoIndex);		
		Date pagamento = pagamentoTime == 0 ? null : new Date(pagamentoTime);
		setPagamento(pagamento);
		setValor(cursor.getDouble(valorIndex));
	}
	
	public Conta(Bundle bundle, Tipo tipo) {
		this(tipo);
		if (bundle != null) {
			setId(bundle.getLong(Columns.ID));
			setDescricao(bundle.getString(Columns.DESCRICAO));
			Date vencimento = DateUtils.stringToDate(bundle.getString(Columns.VENCIMENTO), "dd/MM/yyyy"); 
			setVencimento(vencimento);
			Date pagamento = DateUtils.stringToDate(bundle.getString(Columns.PAGAMENTO), "dd/MM/yyyy");
			setPagamento(pagamento);
			setValor(bundle.getDouble(Columns.VALOR, 0));
		}
	}
	
	public Conta(ContentValues values) {
		setId(values.getAsLong(Columns.ID));
		setDescricao(values.getAsString(Columns.DESCRICAO));
		
		Date vencimento = new Date(values.getAsLong(Columns.VENCIMENTO));
		setVencimento(vencimento);
		Long pagamentoTime = values.getAsLong(Columns.PAGAMENTO);		
		Date pagamento = pagamentoTime == 0 ? null : new Date(pagamentoTime);
		setPagamento(pagamento);
		setValor(values.getAsDouble(Columns.VALOR));
		setTipo(Tipo.valueOf(values.getAsInteger(Columns.TIPO)));
	}
	
	public Conta(Tipo tipo) {
		setTipo(tipo);
	}

	public Long getId() {
		return Long.valueOf(0).equals(id) ? null : id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Date getVencimento() {
		return vencimento;
	}

	public void setVencimento(Date vencimento) {
		this.vencimento = vencimento;
	}

	public Date getPagamento() {
		return pagamento;
	}

	public void setPagamento(Date pagamento) {
		this.pagamento = pagamento;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Tipo getTipo() {
		return tipo;
	}

	protected void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public boolean isNew() {
		return getId() == null; 
	}
	
	public boolean valida() {
		return descricao != null && descricao.length() > 0 && 
			   vencimento != null && valor > 0;
	}
	
	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		values.put(Columns.ID, getId());
		values.put(Columns.DESCRICAO, getDescricao());
		values.put(Columns.VENCIMENTO, getVencimento().getTime());
		values.put(Columns.PAGAMENTO,  getPagamento() == null ? null : getPagamento().getTime());
		values.put(Columns.VALOR, getValor());
		values.put(Columns.TIPO, getTipo().ordinal());
		return values;
	}
	
	public boolean vencida() {
		if (vencimento == null)
			return false;
		Calendar now = Calendar.getInstance();
		return now.getTime().after(vencimento);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
		return result;
	}


	public boolean paga() {
		return pagamento != null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Conta other = (Conta) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Conta [" + (id != null ? "id=" + id + ", " : "")
				+ (descricao != null ? "descricao=" + descricao + ", " : "")
				+ (vencimento != null ? "vencimento=" + vencimento + ", " : "")
				+ (pagamento != null ? "pagamento=" + pagamento + ", " : "")
				+ "valor=" + valor + ", "
				+ (tipo != null ? "tipo=" + tipo : "") + "]";
	}

}

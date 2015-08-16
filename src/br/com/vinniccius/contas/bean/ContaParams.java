package br.com.vinniccius.contas.bean;

import java.util.Calendar;

import br.com.vinniccius.contas.util.DateUtils;

public class ContaParams {

	public enum Ordem {
		VENCIMENTO(Conta.Columns.VENCIMENTO), 
		PAGAMENTO(Conta.Columns.PAGAMENTO), 
		VALOR(Conta.Columns.VALOR);
		private String columnName;

		private Ordem(String coluna) {
			this.columnName = coluna;
		}
		public String getColumnName() {
			return columnName;
		}
	}
	
	private Calendar start;
	private Calendar end;
	private Ordem ordem;
	private boolean inicialized;
	
	public void inicializa() {
		ordem = Ordem.VENCIMENTO;
		Calendar inicio = Calendar.getInstance();
		inicio.set(Calendar.DAY_OF_MONTH, 1);
		start = inicio;
		
		Calendar fim = Calendar.getInstance();
		fim.set(Calendar.DAY_OF_MONTH, fim.getMaximum(Calendar.DAY_OF_MONTH));
		end = fim;
		
		inicialized = true;
	}
	
	public void setInterval(Calendar start, Calendar end, Ordem ordem) {
		check(start, end);
		this.start = start;
		this.end = end;
		this.ordem = ordem == null ? Ordem.VENCIMENTO : ordem;
		inicialized = true;
	}
	
	public void clear() {
		start = null;
		end = null;
		ordem = Ordem.VENCIMENTO;
		inicialized = false;
	}
	
	public boolean hasValue() {
		return inicialized;
	}

	private void check(Calendar start, Calendar end) {
		if (start == null)
			throw new IllegalArgumentException("parametro dataInicial nao pode ser null");
		
		if (end == null)
			throw new IllegalArgumentException("parametro dataInicial nao pode ser null");
		
		if (start.after(end))
			throw new IllegalArgumentException("A Data Inicial nao pode ser posterior a Data Final.");
	}

	public Calendar getStart() {
		return start;
	}

	public Calendar getEnd() {
		return end;
	}

	public Ordem getOrdem() {
		return ordem;
	}
	
	@Override
	public ContaParams clone() {
		ContaParams parametros = new ContaParams();
		parametros.setInterval(start, end, ordem);
		return parametros;
	}
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((end == null) ? 0 : end.hashCode());
		result = prime * result
				+ ((start == null) ? 0 : start.hashCode());
		result = prime * result + (ordem == null ? 0 : ordem.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContaParams other = (ContaParams) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (ordem != other.ordem)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return !inicialized ? "" : new StringBuilder()
			.append(DateUtils.dateToString(start.getTime(), DateUtils.DATE_VIEW))
			.append(" até ")		
			.append(DateUtils.dateToString(end.getTime(), DateUtils.DATE_VIEW))
			.toString();
	}
}

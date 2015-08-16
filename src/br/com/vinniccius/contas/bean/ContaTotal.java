package br.com.vinniccius.contas.bean;

public class ContaTotal {

	public static final String TABLE_NAME = "contas_totais";
	public static final String COLUMN_RECEITAS = "receitas";
	public static final String COLUMN_RECEITAS_ABERTAS_VENCIDAS = "receitas_abertas_vencidas";
	public static final String COLUMN_RECEITAS_ABERTAS_NAO_VENCIDAS = "receitas_abertas_nao_vencidas";
	public static final String COLUMN_RECEITAS_PAGAS = "receitas_pagas";
	public static final String COLUMN_DESPESAS = "despesas";
	public static final String COLUMN_DESPESAS_ABERTAS_VENCIDAS = "despesas_abertas_vencidas";
	public static final String COLUMN_DESPESAS_ABERTAS_NAO_VENCIDAS = "despesas_abertas_nao_vencidas";
	public static final String COLUMN_DESPESAS_PAGAS = "despesas_pagas";

	public static final String[] COLUMNS = new String[]{
		COLUMN_RECEITAS_ABERTAS_VENCIDAS, COLUMN_RECEITAS_ABERTAS_NAO_VENCIDAS, COLUMN_RECEITAS_PAGAS,
		COLUMN_DESPESAS_ABERTAS_VENCIDAS, COLUMN_DESPESAS_ABERTAS_NAO_VENCIDAS, COLUMN_DESPESAS_PAGAS
	};
	
	private final Double receitasAbertasVencidas;
	private final Double receitasAbertasNaoVencidas;
	private final Double receitasPagas;
	private final Double despesasAbertasVencidas;
	private final Double despesasAbertasNaoVencidas;
	private final Double despesasPagas;

	public ContaTotal(
			Double receitasAbertasVencidas, Double receitasAbertasNaoVencidas, Double receitasPagas,
			Double despesasAbertasVencidas, Double despesasAbertasNaoVencidas, Double despesasPagas) {
		this.receitasAbertasVencidas = receitasAbertasVencidas;
		this.receitasAbertasNaoVencidas = receitasAbertasNaoVencidas;
		this.receitasPagas = receitasPagas;
		this.despesasAbertasVencidas = despesasAbertasVencidas;
		this.despesasAbertasNaoVencidas = despesasAbertasNaoVencidas;
		this.despesasPagas = despesasPagas;
	}

	public Double getReceitas() {
		return getReceitasAbertas() + getReceitasPagas();
	}

	public Double getDespesas() {
		return getDespesasAbertas() + getDespesasPagas();
	}

	public Double getReceitasAbertasVencidas() {
		return receitasAbertasVencidas;
	}

	public Double getReceitasAbertasNaoVencidas() {
		return receitasAbertasNaoVencidas;
	}
	
	public Double getReceitasAbertas() {
		return getReceitasAbertasVencidas() + getReceitasAbertasNaoVencidas();
	}

	public Double getReceitasPagas() {
		return receitasPagas;
	}

	public Double getDespesasAbertasVencidas() {
		return despesasAbertasVencidas;
	}

	public Double getDespesasAbertasNaoVencidas() {
		return despesasAbertasNaoVencidas;
	}
	
	public Double getDespesasAbertas() {
		return getDespesasAbertasVencidas() + getDespesasAbertasNaoVencidas();
	}

	public Double getDespesasPagas() {
		return despesasPagas;
	}

	public Double getLucro() {
		return getReceitas() - getDespesas();
	}

	public long getReceitasTotal() {
		return 0;
	}

}

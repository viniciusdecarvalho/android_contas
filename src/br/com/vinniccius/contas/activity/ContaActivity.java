package br.com.vinniccius.contas.activity;

import static br.com.vinniccius.contas.util.DateUtils.DATE_VIEW;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import br.com.vinniccius.contas.R;
import br.com.vinniccius.contas.bean.Conta;
import br.com.vinniccius.contas.listener.Mask;
import br.com.vinniccius.contas.listener.TextWatcherAdapter;
import br.com.vinniccius.contas.util.DateUtils;

@ContentView(R.layout.conta_form)
public class ContaActivity extends Activity {

	private Conta conta;

	@InjectResource(R.id.descricao)
	private EditText descricaoEditText;
	@InjectResource(R.id.valor)
	private EditText valorEditText;
	@InjectResource(R.id.vencimento)
	private EditText vencimentoEditText;
	@InjectResource(R.id.pagamento)
	private EditText pagamentoEditText;
	@InjectResource(R.id.salvar)
	private Button salvarButton;
	@InjectResource(R.id.cancelar)
	private Button cancelarButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Mask.insert(DateUtils.DATE_MASK, vencimentoEditText);
		Mask.insert(DateUtils.DATE_MASK, pagamentoEditText);
		
		salvarButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				confirma();
			}
		});
		
		cancelarButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancela();
			}
		});
		
		preencheCampos();
		habilitaSalvar();
	}
	
	private void cancela() {
		setResult(RESULT_CANCELED);
		finish();
	}

	private void bind() {
		descricaoEditText.addTextChangedListener(new TextWatcherAdapter(){
			@Override
			public void afterTextChanged(Editable s) {
				conta.setDescricao(s.toString());
				habilitaSalvar();
			}
		});
		vencimentoEditText.addTextChangedListener(new TextWatcherAdapter(){
			@Override
			public void afterTextChanged(Editable s) {
				String vencimento = s.toString();
				if (DateUtils.isDate(vencimento, DATE_VIEW)) {
					conta.setVencimento(DateUtils.stringToDate(vencimento, DATE_VIEW));
				} else {
					conta.setVencimento(null);
				}
				habilitaSalvar();
			}
		});
		valorEditText.addTextChangedListener(new TextWatcherAdapter(){
			@Override
			public void afterTextChanged(Editable s) {
				try {
					conta.setValor(Double.parseDouble(s.toString()));
				} catch (NumberFormatException e) {
					conta.setValor(0.0);
				}
				habilitaSalvar();
			}
		});
		pagamentoEditText.addTextChangedListener(new TextWatcherAdapter(){
			@Override
			public void afterTextChanged(Editable s) {
				String pagamento = s.toString();
				if (DateUtils.isDate(pagamento, DATE_VIEW)) {
					conta.setPagamento(DateUtils.stringToDate(pagamento, DATE_VIEW));
				} else {
					conta.setPagamento(null);
				}
				habilitaSalvar();
			}
		});
	}

	private void habilitaSalvar() {
		if (conta != null && conta.valida()) {
			salvarButton.setEnabled(true);
			salvarButton.setTextColor(Color.BLUE);
		} else {
			salvarButton.setEnabled(false);
			salvarButton.setTextColor(Color.RED);
		}
	}
	
	private void confirma() {
		if (conta.valida()) {	
//			getIntent().putExtra(Columns.ID, conta.getId());
//			getIntent().putExtra(Columns.DESCRICAO, conta.getDescricao());
//			String vencimento = DateUtils.dateToString(conta.getVencimento(), DateUtils.DATE_VIEW);
//			getIntent().putExtra(Columns.VENCIMENTO, vencimento);
//			String pagamento = DateUtils.dateToString(conta.getPagamento(), DateUtils.DATE_VIEW);
//			getIntent().putExtra(Columns.PAGAMENTO, pagamento);
//			getIntent().putExtra(Columns.VALOR, conta.getValor());
			getIntent().putExtra("conta", conta);
			setResult(RESULT_OK, getIntent());
			finish();
		}
	}

	private void preencheCampos() {
//		Bundle extras = getIntent().getExtras();		
		conta = (Conta) getIntent().getSerializableExtra("conta");
		descricaoEditText.setText(conta.getDescricao());
		vencimentoEditText.setText(DateUtils.dateToString(conta.getVencimento(), DATE_VIEW));
		pagamentoEditText.setText(DateUtils.dateToString(conta.getPagamento(), DATE_VIEW));
		valorEditText.setText(conta.getValor().toString());
		bind();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		confirma();
		super.onPause();
	}
}

package br.com.vinniccius.contas.activity;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import br.com.vinniccius.contas.R;
import br.com.vinniccius.contas.bean.Conta;
import br.com.vinniccius.contas.util.DateUtils;

public class ContasAdapter extends BaseAdapter {

	private ContasActivity activity;
	protected AlertDialog dialog;
	
	public ContasAdapter(ContasActivity activity) {
		this.activity = activity;
	}
	
	@Override
	public int getCount() {
		return activity.getContas().size();
	}

	@Override
	public Object getItem(int position) {
		return activity.getContas().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		view = inflateView(view, parent);
		
		final Conta conta = activity.getContas().get(position);
		
		bindView(view, conta);
		
		view.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.editConta(conta);
				return true;
			}
		});
		
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Conta conta = activity.getContas().get(position);
				
				v.setBackgroundColor(Color.BLUE);
				AlertDialog.Builder builder = builAlert(conta);
				dialog = builder.show();
				setBackGroudColorConta(v, conta);
			}
			
		});
		
		return view;
	}

	private Builder builAlert(Conta conta) {
		Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.conta_detalhes);
		View view = bindAlertView(conta);
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		return builder;
	}
	
	private void bindView(View view, final Conta conta) {
		final TextView descricao = (TextView)view.findViewById(R.id.descricao_view);
		descricao.setText(conta.getDescricao());
		
		final TextView vencimento = (TextView)view.findViewById(R.id.vencimento_view);
		vencimento.setText(DateUtils.dateToString(conta.getVencimento(), DateUtils.DATE_VIEW));
		
		final TextView valor = (TextView)view.findViewById(R.id.valor_view);
		valor.setText("R$ " + conta.getValor().toString());
		
		final TextView pagamento = (TextView)view.findViewById(R.id.pagamento_view);
		pagamento.setText("");
		if (conta.paga()) {
			pagamento.setText(DateUtils.dateToString(conta.getPagamento(), "dd/MM/yyyy"));
		}

		setBackGroudColorConta(view, conta);
	}	
	
	private View bindAlertView(final Conta conta) {
		final View view = activity.getLayoutInflater().inflate(R.layout.conta_alert, null);
		
		boolean vencida = conta.vencida();
		boolean paga = conta.paga();
		
		TextView descricao = (TextView)view.findViewById(R.id.conta_descricao_alert);
		descricao.setText(conta.getDescricao());
		TextView valor = (TextView)view.findViewById(R.id.conta_valor_alert);
		Double valorConta = conta.getValor();
		valor.setText(valorConta == null ? "" : valorConta.toString());
		TextView vencimento = (TextView)view.findViewById(R.id.conta_vencimento_alert);
		vencimento.setText(DateUtils.dateToString(conta.getVencimento(), DateUtils.DATE_VIEW));
		TextView pagamento = (TextView)view.findViewById(R.id.conta_pagamento_alert);
		TextView situacao = (TextView)view.findViewById(R.id.conta_situacao_alert);	
		
		ImageButton editar = (ImageButton)view.findViewById(R.id.conta_editar);
		editar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.editConta(conta);	
				disposeDialog(dialog);
			}
		});
		ImageButton baixar = (ImageButton)view.findViewById(R.id.conta_baixar);
		baixar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.downConta(conta);
				disposeDialog(dialog);
			}
		});
		
		ImageButton estornar = (ImageButton)view.findViewById(R.id.conta_estornar);
		estornar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.upConta(conta);
				disposeDialog(dialog);
			}
		});
		
		ImageButton incluir = (ImageButton)view.findViewById(R.id.conta_incluir);
		incluir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GregorianCalendar data = new GregorianCalendar();
				data.setTime(conta.getVencimento());
				data.add(Calendar.MONTH, 1);
				conta.setVencimento(data.getTime());
				activity.editConta(new Conta(conta));
				disposeDialog(dialog);
			}
		});
		
		ImageButton excluir = (ImageButton)view.findViewById(R.id.conta_excluir);
		excluir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				activity.deleteConta(conta);
				disposeDialog(dialog);
			}
		});
		
		pagamento.setText("");
		
		if (paga) {
			pagamento.setText(DateUtils.dateToString(conta.getPagamento(), DateUtils.DATE_VIEW));
			situacao.setText(R.string.conta_situacao_paga);
			situacao.setBackgroundColor(Color.GREEN);
			baixar.setEnabled(false);
		} else {
			if (vencida && !paga) {
				situacao.setText(R.string.conta_situacao_vencida);
				situacao.setTextColor(Conta.Colors.ABERTA_VENCIDA);
			} else {
				situacao.setText(R.string.conta_situacao_aberto);
			}
			pagamento.setEnabled(false);
			estornar.setEnabled(false);
		}
		
		return view;
	}
	
	private void disposeDialog(AlertDialog dialog) {
		if (dialog != null) {
			dialog.cancel();
			dialog = null;
		}
	}
	
	private void setBackGroudColorConta(View view, final Conta conta) {
		if (conta.paga()) {
			view.setBackgroundColor(Conta.Colors.PAGA);			
		} else if (conta.vencida()) {
			view.setBackgroundColor(Conta.Colors.ABERTA_VENCIDA);		
		} else {
			view.setBackgroundColor(Conta.Colors.ABERTA_NAO_VENCIDA);	
		}
	}

	private View inflateView(View view, ViewGroup parent) {
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.conta_linha, parent, false);			
		}
		return view;
	}

}

package br.com.vinniccius.contas.activity;

import java.util.Locale;

import android.app.Application;

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Locale.setDefault(new Locale("pt", "BR"));
	}
}

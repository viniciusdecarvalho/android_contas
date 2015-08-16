package br.com.vinniccius.contas.bean;

import java.util.Locale;

public enum Tipo {
	
	RECEBER, PAGAR, INDEFINIDO;
	
	public static Tipo valueOf(int ordinal) {
		switch (ordinal) {
		case 0:
			return Tipo.RECEBER;
		case 1:
			return Tipo.PAGAR;
		case 2:
			return Tipo.INDEFINIDO;
		}
		return null;
	}
	
	public static String capitalize(Tipo tipo) {
		Locale locale = new Locale("pt", "BR");
		String name = tipo.name().toLowerCase(locale);
		return name.charAt(0) + "".toUpperCase(locale) + name.substring(1);
	}
}
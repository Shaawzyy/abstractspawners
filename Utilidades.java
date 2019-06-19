package abstractland.spawners;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Utilidades {

	public static void sendSom(Player p, Sound s) {
		p.playSound(p.getLocation(), s, 1.0F, 0.5F);
	}
	
	public static String prefixo = "§3§lABSTRACTLAND §8» ";
	public static NumberFormat nf = new DecimalFormat("#,##0", new DecimalFormatSymbols(new Locale("pt", "BR")));
	
	
	public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
	
	public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
	
	public static String getTempo(long time) {
		long variacao = time;
		long varsegundos = variacao / 1000 % 60;
		long varminutos = variacao / 60000 % 60;
		long varhoras = variacao / 3600000 % 24;
		long vardias = variacao / 86400000 % 30;

		String segundos = String.valueOf(varsegundos).replaceAll("-", "");
		String minutos = String.valueOf(varminutos).replaceAll("-", "");
		String horas = String.valueOf(varhoras).replaceAll("-", "");
		String dias = String.valueOf(vardias).replaceAll("-", "");
		if (dias.equals("0") && horas.equals("0") && minutos.equals("0")) {
			return "" + segundos + "s";
		}
		if (dias.equals("0") && horas.equals("0")) {
			return "" + minutos + "m " + segundos + "s";
		}
		if (dias.equals("0")) {
			return "" + horas + "h " + minutos + "m " + segundos + "s";
		}
		return "" + dias + "d " + horas + "h " + minutos + "m " + segundos + "s ";
	}
	
	public static String getDataFormatada(long time) {
		long tempo = time;
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return dateFormat.format(tempo).replace(" ", " ás ");
	}

	public static String randomString(int tamanho) {
        StringBuilder sb = new StringBuilder();
        String a = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        int i = 0;
        for (int t = 0; t < tamanho; t++) {
            i = new Random().nextInt(a.length());
            sb.append(a.substring(i, i + 1));
        }
        return sb.toString();
    }
	
}
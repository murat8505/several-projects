package com.jacr.textviewj.util;

import android.graphics.Paint;

public class Utils {

	public static double roundNumber(double number, int decimalDigits) {
		double potencia = Math.pow(10, decimalDigits);
		return (Math.round(number * potencia) / potencia);
	}

	public static boolean isEmptyOrNull(String str) {
		if (str == null) {
			return true;
		} else if (str.contentEquals("")) {
			return true;
		} else {
			return false;
		}
	}

	public static float getAverageCharacterWidth(Paint paint, String str) {
		float acum = 0;
		float totalCharc = 0;
		for (int i = 0; i < str.length(); i++) {
			String charc = String.valueOf(str.charAt(i));
			if (!isEmptyOrNull(charc)) {
				acum += paint.measureText(charc);
				totalCharc++;
			}
		}
		return acum / totalCharc;
	}

}

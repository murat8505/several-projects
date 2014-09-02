package com.jacr.textviewj.util;

import android.graphics.Paint;

public class Utils {

	public static float getAverageCharacterWidth(Paint paint, String string) {
		float accum = 0f;
		float totalWords = 0f;
		for (int i = 0; i < string.length(); i++) {
			String str = String.valueOf(string.charAt(i));
			if (!isEmptyOrNull(str)) {
				accum += paint.measureText(str);
				totalWords++;
			}
		}
		return accum / totalWords;
	}

	public static boolean isEmptyOrNull(String string) {
		if (string == null) {
			return true;
		} else if (string.contentEquals("")) {
			return true;
		} else {
			return false;
		}
	}

	public static double roundNumber(double number, int decimalDigits) {
		double potencia = Math.pow(10, decimalDigits);
		return Math.round(number * potencia) / potencia;
	}
}

/* (c) Jesus A. Castro R, 2014
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.jacr.textviewj;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jacr.textviewj.util.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * TextView with full support justification.
 * 
 * @author j.castro 31/08/2014
 * 
 */
public class TextViewJ extends TextView {

	private static final int DEFAULT_WORDS_BY_LINE = 5;

	private Paint mBackgroundPaint = new Paint();
	private int mDefaultBackgroundColor;
	private float mLineSpacing;
	private ArrayList<Line> mTextLinesList = new ArrayList<Line>();
	private TextPaint mTextPaint = null;
	private float mViewWidth;
	private int mWordsNum;

	public TextViewJ(Context paramContext) {
		super(paramContext);
		init();
	}

	public TextViewJ(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init();
		parseAttrs(paramContext, paramAttributeSet);
	}

	public TextViewJ(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init();
		parseAttrs(paramContext, paramAttributeSet);
	}

	/**
	 * Load default values.
	 */
	private void init() {
		Resources resources = getResources();
		if (resources == null)
			return;
		this.mLineSpacing = resources
				.getDimensionPixelSize(R.dimen.textviewj_default_line_spacing);
		this.mDefaultBackgroundColor = resources
				.getColor(R.color.textviewj_default_background_color);
		this.mWordsNum = DEFAULT_WORDS_BY_LINE;
	}

	private void parseAttrs(Context paramContext, AttributeSet paramAttributeSet) {
		TypedArray typedArray = paramContext.obtainStyledAttributes(
				paramAttributeSet, R.styleable.TextViewJ);
		if (typedArray == null)
			return;
		for (int i = 0; i < typedArray.getIndexCount(); i++) {
			int attrs = typedArray.getIndex(i);
			if (attrs == R.styleable.TextViewJ_lineSpacing) {
				mLineSpacing = typedArray.getDimension(attrs,
						R.dimen.textviewj_default_line_spacing);
			} else if (attrs == R.styleable.TextViewJ_wordsByLine) {
				mWordsNum = typedArray.getInteger(attrs, DEFAULT_WORDS_BY_LINE);
			}
		}
		typedArray.recycle();
	}

	private void justifyText() {
		String text = getText().toString();
		if (!Utils.isEmptyOrNull(text)) {
			mTextLinesList.clear();
			String[] lines = text.split("\n");
			float paddingWidth = getPaddingLeft() + getPaddingRight();
			float mAparentViewWidth = mViewWidth - paddingWidth;
			for (String line : lines) {
				String[] words = line.split(" ");
				int wordsNumAux = mWordsNum;
				List<Line> list = null;
				do {
					list = justifyIterator(mAparentViewWidth, words.clone(),
							wordsNumAux);
					/*
					 * En caso de que el limite de palabras sea demasiado alto,
					 * para el calculo de espacios, se disminuye. Aun asi, si no
					 * es posible, no se tendran en cuenta las lineas de texto.
					 */
					if ((--wordsNumAux) < 1) {
						break;
					}

				} while (list == null);
				for (Line lin : list) {
					mTextLinesList.add(lin);
				}
			}
		}
	}

	/**
	 * Justify the text lines according to expected words limit.
	 * 
	 * @param viewWidth
	 *            View width.
	 * @param words
	 *            Words to justify.
	 * @param wordsNumLimit
	 *            Expected limit for number of words in a text line.
	 * @return List with line text instances.
	 */
	private List<Line> justifyIterator(float viewWidth, String[] words,
			int wordsNumLimit) {
		boolean fExceedChars = false;
		boolean fLastPostWord = false;
		boolean fPostWord = false;
		List<Line> listLines = new ArrayList<Line>();
		Line lineBuffer = new Line();
		List<String> splitList = null;
		float newSpacing = 0f;
		final int pre_word = 0;
		final int post_word = 1;
		int numWords = 0;
		if (words.length < wordsNumLimit) {
			return null;
		}
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (Utils.isEmptyOrNull(word)) {
				continue;
			}
			// Limite esperado de numero de palabras para cada linea justificada
			if (lineBuffer.getWords().size() == wordsNumLimit) {
				newSpacing = calculateSpacing(viewWidth, lineBuffer,
						wordsNumLimit);
				// if spacing isnt enough, i request a new words number limit
				if (newSpacing == -1) {
					return null;
				}
				lineBuffer.setSpacing(newSpacing);
				listLines.add(lineBuffer);
				lineBuffer = new Line();
			}

			/*
			 * Loop if it's necessary to split a word (which exceeds the view
			 * witdh)
			 */
			while (true) {
				// Post-word cases
				if (splitList != null
						&& !Utils.isEmptyOrNull(splitList.get(post_word))) {
					word = splitList.get(post_word); // Postword
					if (!fLastPostWord) {
						fPostWord = true;
					}
					splitList.clear();
					splitList = null;
				} else {
					fPostWord = false;
					word = words[i];

				}

				// Cases for setting text lines
				float wPalabra = mTextPaint.measureText(word);
				float wordsAccumWidth = lineBuffer.getWordSize() + wPalabra;
				numWords = lineBuffer.getWords().size();
				if (wPalabra >= viewWidth) {
					if (wPalabra != viewWidth) {
						newSpacing = calculateSpacing(viewWidth, lineBuffer,
								numWords);
						lineBuffer.setSpacing(newSpacing);
						listLines.add(lineBuffer);
						lineBuffer = new Line();
					}
					splitList = splitWord(word, wPalabra, viewWidth);
					fExceedChars = true;
				} else if (wordsAccumWidth >= viewWidth) {
					if (wordsAccumWidth == viewWidth) {
						lineBuffer.addWord(word);
					}
					newSpacing = calculateSpacing(viewWidth, lineBuffer,
							numWords);
					lineBuffer.setSpacing(newSpacing);
					listLines.add(lineBuffer);
					lineBuffer = new Line();
					break;
				} else {
					lineBuffer.addWord(word);
					if (!fPostWord) {
						break;
					}
				}
				if (fExceedChars && splitList != null) {
					lineBuffer.addWord(splitList.get(pre_word)); // preword
					lineBuffer.setSpacing(0);
					listLines.add(lineBuffer);
					lineBuffer = new Line();
					fExceedChars = false;
					// Caso: ultima palabra y existe caso postword
					if (i == words.length - 1
							&& !Utils.isEmptyOrNull(splitList.get(post_word))) {
						fLastPostWord = true;
					} else {
						break;
					}
				}
			}
		}
		// Ultima linea de texto para agregar
		if (listLines != null) {
			newSpacing = calculateSpacing(viewWidth, lineBuffer, wordsNumLimit);
			if (newSpacing == -1) {
				return null;
			}
			lineBuffer.setSpacing(newSpacing);
			listLines.add(lineBuffer);
		}
		return listLines;
	}

	private List<String> splitWord(String word, float wordSize, float viewWidth) {
		List<String> list = new ArrayList<String>();
		float charWidth = Utils.getAverageCharacterWidth(mTextPaint, word);
		int numExceedChars = (int) Utils.roundNumber((wordSize - viewWidth)
				/ charWidth, 0);
		int idxExceedChars = word.length() - numExceedChars;
		list.add(word.substring(0, idxExceedChars)); // preword
		list.add(numExceedChars != 0 ? word.substring(idxExceedChars,
				word.length()) : ""); // postword
		return list;
	}

	/**
	 * Calculates spacing for each word in a text line.
	 * 
	 * @param viewWidth
	 *            View Width
	 * @param line
	 *            Instances for text line.
	 * @param wordsNumLimit
	 *            Expected limit for number of words in a text line.
	 * @return spacing value.
	 */
	private float calculateSpacing(float viewWidth, Line line,
			final int wordsNumLimit) {
		float newSpacing = 0f;
		if (wordsNumLimit > 1) {
			while (true) {
				/*
				 * Formula: spaceValue = (width - wordsWidth)/(NumWords - 1)
				 */
				newSpacing = (float) Utils.roundNumber(
						(viewWidth - line.getWordSize()) / (wordsNumLimit - 1),
						2);
				if (newSpacing < 0) {
					return -1;
				} else {
					break;
				}
			}
		}
		return newSpacing;
	}

	// TODO TextViewJ: More LayoutParams? Add it here.
	private void updateParentLayoutParams(int height) {
		ViewGroup.LayoutParams lp = getLayoutParams();
		int wParent = lp.width;
		if ((lp instanceof LinearLayout.LayoutParams)) {
			setLayoutParams(new LinearLayout.LayoutParams(wParent, height));
		} else if ((lp instanceof FrameLayout.LayoutParams)) {
			setLayoutParams(new FrameLayout.LayoutParams(wParent, height));

		} else if ((lp instanceof RelativeLayout.LayoutParams)) {
			setLayoutParams(new RelativeLayout.LayoutParams(wParent, height));

		} else if ((lp instanceof TableLayout.LayoutParams)) {
			setLayoutParams(new TableLayout.LayoutParams(wParent, height));

		} else if ((lp instanceof TableRow.LayoutParams)) {
			setLayoutParams(new TableRow.LayoutParams(wParent, height));
		}
	}

	@SuppressLint({ "NewApi" })
	private int getBackgroundColor() {
		ColorDrawable drawable = (ColorDrawable) getBackground();
		if (drawable == null)
			return this.mDefaultBackgroundColor;
		if (Build.VERSION.SDK_INT >= 11)
			return drawable.getColor();
		try {
			Field field1 = drawable.getClass().getDeclaredField("mState");
			field1.setAccessible(true);
			Object obj = field1.get(drawable);
			Field field2 = obj.getClass().getDeclaredField("mUseColor");
			field2.setAccessible(true);
			return field2.getInt(obj);
		} catch (NoSuchFieldException localNoSuchFieldException) {
			Log.e(getClass().toString(), localNoSuchFieldException.toString());
		} catch (IllegalAccessException localIllegalAccessException) {
			Log.e(getClass().toString(), localIllegalAccessException.toString());
		} catch (IllegalArgumentException localIllegalArgumentException) {
			Log.e(getClass().toString(),
					localIllegalArgumentException.toString());
		}
		return this.mDefaultBackgroundColor;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.mBackgroundPaint.setColor(getBackgroundColor());
		this.mBackgroundPaint.setStyle(Paint.Style.FILL);
		canvas.drawPaint(this.mBackgroundPaint);
		justifyText();
		int i = getPaddingTop();
		int j = getPaddingBottom();
		int k = getPaddingLeft();
		float f1 = getTextSize() + this.mLineSpacing;
		float f2 = f1 * this.mTextLinesList.size();
		float f3 = f2 + i + j;
		updateParentLayoutParams((int) Utils.roundNumber(f3, 0));
		// TODO TextViewJ: draw on center position (default)
		float yPos = 2.0F + ((f3 - f2) / 2.0F + (this.mTextPaint.descent() - this.mTextPaint
				.ascent()) / 2.0F);
		for (Line line : mTextLinesList) {
			float xPos = k;
			for (String s : line.getWords()) {
				canvas.drawText(s, xPos, yPos, mTextPaint);
				xPos += mTextPaint.measureText(s) + line.getSpacing();
			}
			yPos += f1;
		}
	}

	@SuppressLint({ "NewApi" })
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.mViewWidth = (Build.VERSION.SDK_INT >= 11) ? resolveSizeAndState(
				getSuggestedMinimumWidth(), widthMeasureSpec, 1)
				: widthMeasureSpec;
		this.mTextPaint = getPaint();
	}

	/**
	 * Subclass for text lines.
	 */
	private class Line {
		private float mSpacing = 0f;
		private ArrayList<String> mWords = new ArrayList<String>();

		public Line() {
		}

		public void addWord(String paramString) {
			this.mWords.add(paramString);
		}

		public ArrayList<String> getWords() {
			return this.mWords;
		}

		public float getSpacing() {
			return this.mSpacing;
		}

		public void setSpacing(float paramFloat) {
			this.mSpacing = paramFloat;
		}

		public float getWordSize() {
			float size = 0;
			for (int i = 0; i < mWords.size(); i++) {
				size += mTextPaint.measureText(mWords.get(i));
			}
			return size;
		}

	}
}
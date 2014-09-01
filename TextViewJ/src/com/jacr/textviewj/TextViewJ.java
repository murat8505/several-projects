/*
 * (c) Jesus A. Castro R, 2014
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
import android.graphics.Paint.Style;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jacr.textviewj.util.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * TextView with full justification support.
 * 
 * @author j.castro 27/08/2014
 * 
 */
public class TextViewJ extends TextView {

	private ArrayList<Line> mTextLinesList = new ArrayList<Line>();
	private TextPaint mTextPaint = null;
	private Paint mBackgroundPaint = new Paint();

	private float mWordSpacing;
	private float mLineSpacing;
	private float mViewWidth;
	private int mDefaultBackgroundColor;

	public TextViewJ(Context context) {
		super(context);
		init();
	}

	public TextViewJ(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		parseAttrs(context, attrs);
	}

	public TextViewJ(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		parseAttrs(context, attrs);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// Get Textview width
		mViewWidth = (Build.VERSION.SDK_INT >= 11) ? resolveSizeAndState(
				getSuggestedMinimumWidth(), widthMeasureSpec, 1)
				: widthMeasureSpec;
		mTextPaint = getPaint();
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		// "clean" view
		mBackgroundPaint.setColor(getBackgroundColor());
		mBackgroundPaint.setStyle(Style.FILL);
		canvas.drawPaint(mBackgroundPaint);

		// Justify text
		justifyText();

		// Text Block size
		final int topPadding = getPaddingTop();
		final int bottomPadding = getPaddingBottom();
		final int leftPadding = getPaddingLeft();

		final float lineHeight = getTextSize() + mLineSpacing;
		float textBlockHeight = (mTextLinesList.size()) * lineHeight;

		/*
		 * Update Height according to text block height.
		 */
		float parentHeight = topPadding + textBlockHeight + bottomPadding;
		updateParentLayoutParams((int) Utils.roundNumber(parentHeight, 0));

		/*
		 * TODO Only center positioning at moment.
		 * 
		 * Center: yPos = topPadding + (ContainerSize - TextParagraphSize)/2 +
		 * LetterBaseLineHeight/2
		 */
		float xPos = 0f;
		float yPos = (parentHeight - textBlockHeight) / 2
				+ (mTextPaint.descent() - mTextPaint.ascent()) / 2;
		yPos += 2f; // offset
		for (Line line : mTextLinesList) {
			xPos = leftPadding;
			for (String s : line.getWords()) {
				String str = s;
				canvas.drawText(str, xPos, yPos, mTextPaint);
				xPos += (mTextPaint.measureText(str) + line.getSpacing());

			}
			// Next Position on Y
			yPos += lineHeight;
		}

	}

	/**
	 * Init defaults values.
	 */
	private void init() {
		Resources resources = getResources();
		if (resources == null) {
			return;
		}
		mLineSpacing = resources
				.getDimensionPixelSize(R.dimen.textviewj_default_line_spacing);
		mWordSpacing = resources
				.getDimensionPixelSize(R.dimen.textviewj_default_max_wordspacing);
		mDefaultBackgroundColor = resources
				.getColor(R.color.textviewj_default_background_color);
	}

	private void parseAttrs(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.TextViewJ);
		if (typedArray == null) {
			return;
		}
		for (int i = 0; i < typedArray.getIndexCount(); i++) {
			int attr = typedArray.getIndex(i);
			if (attr == R.styleable.TextViewJ_lineSpacing) {
				mLineSpacing = typedArray.getDimensionPixelSize(attr,
						R.dimen.textviewj_default_line_spacing);
			} else if (attr == R.styleable.TextViewJ_maxWordSpacing) {
				mWordSpacing = typedArray.getDimensionPixelSize(attr,
						R.dimen.textviewj_default_max_wordspacing);
			}

		}
		typedArray.recycle();
	}

	@SuppressLint("NewApi")
	private int getBackgroundColor() {
		ColorDrawable drawable = (ColorDrawable) getBackground();
		if (drawable == null) {
			return mDefaultBackgroundColor;
		}
		if (Build.VERSION.SDK_INT >= 11) {
			return drawable.getColor();
		}
		try {
			Field field = drawable.getClass().getDeclaredField("mState");
			field.setAccessible(true);
			Object obj = field.get(drawable);
			field = obj.getClass().getDeclaredField("mUseColor");
			field.setAccessible(true);
			return field.getInt(obj);
		} catch (NoSuchFieldException e) {
			Log.e(getClass().toString(), e.toString());
		} catch (IllegalAccessException e) {
			Log.e(getClass().toString(), e.toString());
		} catch (IllegalArgumentException e) {
			Log.e(getClass().toString(), e.toString());
		}
		return mDefaultBackgroundColor;
	}

	// TODO More LayoutParams? Add it here
	private void updateParentLayoutParams(int height) {
		LayoutParams lp = getLayoutParams();
		int wParent = (int) lp.width;
		int hParent = height;
		if (lp instanceof LinearLayout.LayoutParams) {
			setLayoutParams(new LinearLayout.LayoutParams(wParent, hParent));
		} else if (lp instanceof FrameLayout.LayoutParams) {
			setLayoutParams(new FrameLayout.LayoutParams(wParent, hParent));
		} else if (lp instanceof RelativeLayout.LayoutParams) {
			setLayoutParams(new RelativeLayout.LayoutParams(wParent, hParent));
		} else if (lp instanceof TableLayout.LayoutParams) {
			setLayoutParams(new TableLayout.LayoutParams(wParent, hParent));
		} else if (lp instanceof TableRow.LayoutParams) {
			setLayoutParams(new TableRow.LayoutParams(wParent, hParent));
		}
	}

	private void justifyText() {
		String text = getText().toString();
		if (!Utils.isEmptyOrNull(text)) {
			boolean fNewLine = false;
			boolean fExceededWord = false;
			String postWord = null;
			String preWord = null;
			int paddingWidth = getPaddingLeft() + getPaddingRight();
			String[] lines = text.split("\n");
			mTextLinesList.clear();
			for (String line : lines) {
				String[] words = line.split(" ");
				Line lineBuffer = new Line(mTextPaint);
				lineBuffer.setSpacing(mWordSpacing);
				for (String word : words) {
					if (Utils.isEmptyOrNull(word)) {
						continue;
					} else if (!Utils.isEmptyOrNull(postWord)) {
						word = postWord + " " + word;
						postWord = null;
					}
					float wordWidth = mTextPaint.measureText(word);
					float accumLineWidth = lineBuffer.getSizeLine();
					float wLine = wordWidth + accumLineWidth;
					float mAparentViewWidth = mViewWidth - paddingWidth;
					if (wordWidth > mAparentViewWidth) {
						final float exceededCharsWidth = wordWidth
								- mAparentViewWidth;
						final float charWidth = Utils.getAverageCharacterWidth(
								mTextPaint, word);
						final int numWordExceededChars = (int) Utils
								.roundNumber(exceededCharsWidth / charWidth, 0);
						final int idxExceedChars = word.length()
								- numWordExceededChars;
						preWord = word.substring(0, idxExceedChars);
						postWord = word
								.substring(idxExceedChars, word.length());
						fExceededWord = true;
						fNewLine = true;
					} else if (wLine == mAparentViewWidth) {
						fNewLine = true;
					} else if (wLine > mAparentViewWidth) {
						float spacingExtra = (wLine - mAparentViewWidth)
								/ lineBuffer.getWords().size();
						float roundedSpacingExtra = (float) Utils.roundNumber(
								spacingExtra, 3);
						float newSpacing = mWordSpacing - roundedSpacingExtra;
						lineBuffer.setSpacing(newSpacing);
						fNewLine = true;
					}
					if (fExceededWord) {
						lineBuffer.setSpacing(0);
						lineBuffer.addWord(preWord);
						fExceededWord = false;
					} else {
						lineBuffer.addWord(word);
					}
					if (fNewLine) {
						mTextLinesList.add(lineBuffer);
						lineBuffer = new Line(mTextPaint);
						lineBuffer.setSpacing(mWordSpacing);
						fNewLine = false;
					}

				}
				if (lineBuffer.getWords().size() != 0) {
					this.mTextLinesList.add(lineBuffer);
				}
			}
		}
	}

	/**
	 * Inner class to add words and to calculate line size.
	 */
	private class Line {

		private ArrayList<String> mWords = new ArrayList<String>();
		private float mSpacing = 0f;
		private Paint mPaint;

		public Line(Paint mPaint) {
			this.mPaint = mPaint;
		}

		public void setSpacing(float spacing) {
			this.mSpacing = spacing;
		}

		public float getSpacing() {
			return mSpacing;
		}

		public void addWord(String s) {
			mWords.add(s);
		}

		public ArrayList<String> getWords() {
			return mWords;
		}

		/**
		 * Return size of accumulated text block (include spacing between
		 * words). Measure on pixels.
		 * 
		 * @return size.
		 */
		public int getSizeLine() {
			int size = 0;
			for (String word : mWords) {
				size += (mPaint.measureText(word) + mSpacing);
			}
			return size;
		}
	}

}

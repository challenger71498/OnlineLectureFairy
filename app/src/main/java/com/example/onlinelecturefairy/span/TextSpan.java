package com.example.onlinelecturefairy.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import androidx.annotation.NonNull;

public class TextSpan implements LineBackgroundSpan {
    public final String text;
    public final int textColor;
    public final int colorBackGround;
    public final int index;

    private final int offset = 0;
    private final int height = 60;

    public TextSpan(String text, int textColor, int colorBackGround, int index) {
        this.text = text;
        this.textColor = textColor;
        this.colorBackGround = colorBackGround;
        this.index = index;
    }

    @Override
    public void drawBackground(@NonNull Canvas canvas, @NonNull Paint paint, int left, int right, int top, int baseline, int bottom, @NonNull CharSequence charSequence, int start, int end, int lineNumber) {
        int oldColor = paint.getColor();
        float oldTextsize = paint.getTextSize();
        if (colorBackGround != 0) {
            paint.setColor(colorBackGround);
        }
        canvas.drawRect(left, bottom - offset + index * 60 + 5, right, bottom - offset + (index + 1) * 60, paint);

        paint.setTextSize(36);

        if (textColor != 0) {
            paint.setColor(textColor);
        }
        canvas.drawText(text, left, bottom - offset + (index + 1) * 60 - 10, paint);
        paint.setColor(oldColor);
        paint.setTextSize(oldTextsize);
    }
}

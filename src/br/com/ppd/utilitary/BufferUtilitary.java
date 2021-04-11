package br.com.ppd.utilitary;

import javafx.beans.binding.StringBinding;

public class BufferUtilitary extends StringBinding {

    private final StringBuffer buffer = new StringBuffer();

    @Override
    protected String computeValue() {
        return this.buffer.toString();
    }

    public void append(String text) {
        this.buffer.append(text);
        this.buffer.append("\n");
        invalidate();
    }

}

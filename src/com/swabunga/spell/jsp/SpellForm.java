package com.swabunga.spell.jsp;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;

public class SpellForm extends ActionForm {

    private String code;
    private String word;
    private int threshold = 200;

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        code = null;
        word = null;
        threshold = 200;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setThreshold(int num) {
        this.threshold = num;
    }

    public int getThreshold() {
        return threshold;
    }
}



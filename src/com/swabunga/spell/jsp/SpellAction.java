package com.swabunga.spell.jsp;

import com.swabunga.spell.engine.*;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.File;

public class SpellAction extends Action {

    private SpellDictionary dictionary;


    public SpellAction() {
      try {
        dictionary = new SpellDictionary(new File("/dict.dic"));
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    public ActionForward perform(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws IOException, ServletException {

        SpellForm spellform = (SpellForm) form;
        SpellerBean bean = new SpellerBean();
        String checkword = spellform.getWord().trim();
        bean.setSuggestions(
                dictionary.getSuggestions(checkword,
                        spellform.getThreshold()));
        bean.setWord(checkword);
        bean.setCode(dictionary.getCode(checkword));
        bean.setCorrect(dictionary.isCorrect(checkword));
        request.setAttribute("spellbean", bean);
        return mapping.findForward("spelltest");
    }
}


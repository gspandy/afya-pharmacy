/**
 * Nthdimenzion Solutions Private Ltd.
 */
package org.ofbiz.webapp.ftl;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateModel;

/**
 * @author Nth
 */
public class SMECurrencyTransform implements TemplateDirectiveModel {

    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) {
        try {
            String amt = (params.get("amount") == null ? "0" : params.get("amount")).toString();
            Object delegator = ((BeanModel) env.getGlobalVariable("delegator")).getWrappedObject();
            if (delegator != null) {
                Method m = delegator.getClass().getMethod("findByPrimaryKey", new Class[]{String.class, Map.class});
                Map partyAcctgPref = (Map) m.invoke(delegator,
                        new Object[]{"PartyAcctgPreference", UtilMisc.toMap("partyId", "Company")});
                String language = partyAcctgPref.get("language") == null ? "en" : (String) partyAcctgPref.get("language");
                String currencyUOM = partyAcctgPref.get("country") == null ? "in" : (String) partyAcctgPref.get("country");
                Locale locale = new Locale(language, currencyUOM);
                String currency = params.get("isoCode") != null ? params.get("isoCode").toString() : null;
                env.getConfiguration().setLocale(locale);
                if (UtilValidate.isEmpty(currency))
                    currency = (String) partyAcctgPref.get("baseCurrencyUomId");
                if ("INR".equals(currency)) {
                    env.getOut().write(
                            UtilFormatOut.formatCurrency(new BigDecimal(amt.toString()), currency, locale));
                } else {
                    String fs = UtilFormatOut.formatCurrency(new BigDecimal(amt.toString()), currency, locale);
                    String t = null;
                    String v = null;
                    String[] arrayq = fs.split(currency);
                    fs.replace("", currency);
                    t = currency.concat(" ");
                    if (arrayq != null && arrayq.length > 1)
                        v = (arrayq[0] + t).concat(arrayq[1]);
                    else
                        v = (arrayq[0]);
                    env.getOut().write(v);
                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

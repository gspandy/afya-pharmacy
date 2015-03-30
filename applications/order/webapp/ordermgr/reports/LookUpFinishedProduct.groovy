import org.ofbiz.base.util.UtilValidate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.wsdl.Import;

import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.apache.poi.hssf.record.formula.functions.Request;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.sme.order.util.OrderMgrUtil;

import com.sun.org.apache.xalan.internal.xsltc.compiler.If;

import javolution.util.FastList;

String productId =request.getParameter("productId");
productAssocType="MANUF_COMPONENT";

List conditions = FastList.newInstance();

if(UtilValidate.isNotEmptyMsg){
conditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
}
conditions.add(EntityCondition.makeCondition("productAssocTypeId", EntityOperator.EQUALS, productAssocType));
List productList = delegator.findList("ProductAssoc",EntityCondition.makeCondition(conditions, EntityOperator.OR), null, null, null, false);

context.resultList = productList;
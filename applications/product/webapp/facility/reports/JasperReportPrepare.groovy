import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityListIterator
import java.sql.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.content.report.*;
import javax.servlet.http.HttpSession;
import org.ofbiz.content.*;
            delegator = request.getAttribute("delegator");
            resultList=  [:];
            jrParameters = [:];
            HttpSession session = request.getSession();
            resultList =session.getAttribute("Inventory_Summary_List")
            String title =session.getAttribute("title");
              jrParameters.reportTitle = title;
                       org.ofbiz.entity.transaction.TransactionUtil.begin();
                             jrDataSource = new org.ofbiz.content.JRMapCollectionDataSource(resultList);
                                   if(jrDataSource){
											request.setAttribute("jrDataSource", jrDataSource);
											request.setAttribute("jrParameters", jrParameters);

									}
							
                       
					   org.ofbiz.entity.transaction.TransactionUtil.commit();
					    
            return "success";
  
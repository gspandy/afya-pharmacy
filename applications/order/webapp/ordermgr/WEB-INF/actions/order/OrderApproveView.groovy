import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;

GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
String partyId = userLogin.getString("partyId");
context.userLogin = userLogin;



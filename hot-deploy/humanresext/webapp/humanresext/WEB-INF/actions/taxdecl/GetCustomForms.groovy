import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;

List entities = delegator.findAll("CustomForms");

context.put("customForms",entities);

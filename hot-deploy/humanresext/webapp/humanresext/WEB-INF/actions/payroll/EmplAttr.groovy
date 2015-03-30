
import java.util.*;

List listIt = new ArrayList(2);

Map map = new HashMap();

map.put("attrName","CITY");
map.put("description","Employee Primary Address ");

listIt.add(map);

map = new HashMap();

map.put("attrName","GENDER");
map.put("description","Employee's Gender ");

listIt.add(map);

context.listIt = listIt;